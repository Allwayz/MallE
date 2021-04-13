package cn.allwayz.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import cn.allwayz.common.constant.CartConstant;
import cn.allwayz.common.exception.BizCodeEnum;
import cn.allwayz.common.exception.BizException;
import cn.allwayz.common.to.CartItemTO;
import cn.allwayz.common.to.SkuInfoTO;
import cn.allwayz.common.utils.R;
import cn.allwayz.cart.feign.ProductFeignService;
import cn.allwayz.cart.interceptor.CartInterceptor;
import cn.allwayz.cart.service.CartService;
import cn.allwayz.cart.vo.CartItemVO;
import cn.allwayz.cart.vo.CartVO;
import cn.allwayz.cart.vo.UserLoginStatusTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author allwayz
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * Gets the current user's shopping cart
     * - If the user is not logged in, he only has the user-key(temporary shopping cart) to get all the items
     * - If the user is logged in, he has both an ID (official shopping cart) and a User-key (temporary shopping cart),
     *      If its temporary shopping cart is not empty, it merges the items in its temporary shopping cart into its formal shopping cart and empties the temporary cart
     * @return
     */
    @Override
    public CartVO getCart() {
        CartVO cartVO = new CartVO();
        UserLoginStatusTO userLoginStatusTO = CartInterceptor.threadLocal.get();
        String tempCartKey = CartConstant.CART_REDIS_KEY_PREFIX + userLoginStatusTO.getUserKey();
        List<CartItemVO> tempCartItems = getCartItems(tempCartKey);
        if (userLoginStatusTO.getId() == null) {
            cartVO.setItems(tempCartItems);
        } else {
            String userCartKey = CartConstant.CART_REDIS_KEY_PREFIX + userLoginStatusTO.getId();
            if (!CollectionUtils.isEmpty(tempCartItems)) {
                tempCartItems.forEach(tempCartItem -> moveToCertainCart(tempCartItem, userCartKey));
                clearCart(tempCartKey);
            }
            List<CartItemVO> cartItems = getCartItems(userCartKey);
            cartVO.setItems(cartItems);
        }
        return cartVO;
    }

    /**
     * Adds an item to the current user's shopping cart
     * @param skuId
     * @param count
     * @return
     */
    @Override
    public CartItemVO addToCart(Long skuId, Integer count) {
        String cartKey = getCurrentUserCartKey();
        return addToCertainCart(skuId, count, cartKey);
    }

    /**
     * Gets the detailed data for an item in the current user's shopping cart
     * @param skuId
     * @return
     */
    @Override
    public CartItemVO getCartItem(Long skuId) {
        BoundHashOperations<String, String, String> op = getCurrentUserCartOps();
        String dataStr = op.get(skuId.toString());
        if (!StringUtils.isEmpty(dataStr)) {
            CartItemVO itemVO = JSON.parseObject(dataStr, CartItemVO.class);
            return itemVO;
        }
        return null;
    }

    @Override
    public void deleteBatch(List<String> skuIds) {
        String currentUserCartKey = getCurrentUserCartKey();
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(currentUserCartKey);
        skuIds.forEach(skuId -> ops.delete(skuId));
    }

    @Override
    public void changeItemStatus(String skuId, Boolean checked) {
        BoundHashOperations<String, String, String> ops = getCurrentUserCartOps();
        String s = ops.get(skuId);
        if (!StringUtils.isEmpty(s)) {
            CartItemVO cartItem = JSON.parseObject(s, CartItemVO.class);
            cartItem.setChecked(checked);
            ops.put(skuId, JSON.toJSONString(cartItem));
        }
    }

    @Override
    public void changeItemCount(String skuId, Integer count) {
        BoundHashOperations<String, String, String> ops = getCurrentUserCartOps();
        String s = ops.get(skuId);
        if (!StringUtils.isEmpty(s)) {
            CartItemVO cartItem = JSON.parseObject(s, CartItemVO.class);
            cartItem.setCount(count);
            ops.put(skuId, JSON.toJSONString(cartItem));
        }
    }


    /**
     * Gets the key of the current user's shopping cart in Redis
     * @return
     */
    private String getCurrentUserCartKey() {
        UserLoginStatusTO userLoginStatusTO = CartInterceptor.threadLocal.get();
        if (userLoginStatusTO.getId() == null) {
            return CartConstant.CART_REDIS_KEY_PREFIX + userLoginStatusTO.getUserKey();
        } else {
            return CartConstant.CART_REDIS_KEY_PREFIX + userLoginStatusTO.getId().toString();
        }
    }

    /**
     * Gets the operator for the current user's shopping cart in Redis
     */
    private BoundHashOperations<String, String, String> getCurrentUserCartOps() {
        return redisTemplate.boundHashOps(getCurrentUserCartKey());
    }

    /**
     * Gets the data for the specified shopping cart
     * @param cartKey
     * @return
     */
    private List<CartItemVO> getCartItems(String cartKey) {
        BoundHashOperations<String, String, String> operations = redisTemplate.boundHashOps(cartKey);
        List<String> values = operations.values();
        List<CartItemVO> collect = values.stream().map(value -> JSON.parseObject(value, CartItemVO.class)).collect(Collectors.toList());
        return collect;
    }

    /**
     * According to the SKU 'ID to find out the basic information of the commodity, sales attributes, construct the shopping cart item
     * @param skuId
     * @return
     */
    private CartItemVO buildCartItemVO(Long skuId) {
        CartItemVO itemVO = new CartItemVO();
        itemVO.setSkuId(skuId);
        // Use thread pool asynchronous choreography for efficiency
        CompletableFuture<Void> skuInfoTask = CompletableFuture.runAsync(() -> {
            // Invoke the remote service to query the SKU details
            R res = productFeignService.getSkuInfo(skuId);
            if (res.getCode() != 0) {
                log.error("远程调用malle-product查询skuinfo失败");
                throw new BizException(BizCodeEnum.CALL_FEIGN_SERVICE_FAILED, "加入购物车失败");
            }
            SkuInfoTO skuInfo = res.getData("skuInfo",new TypeReference<SkuInfoTO>(){});
            itemVO.setPrice(skuInfo.getPrice());
            itemVO.setSkuTitle(skuInfo.getSkuTitle());
            itemVO.setSkuImg(skuInfo.getSkuDefaultImg());
        }, executor);

        CompletableFuture<Void> saleAttrTask = CompletableFuture.runAsync(() -> {
            // Invoke the remote service to query SKU SaleatTRS
            R r = productFeignService.getSaleAttrStringList(skuId);
            if (r.getCode() != 0) {
                log.error("The remote call to malle-product query SKU saleattr failed");
                throw new BizException(BizCodeEnum.CALL_FEIGN_SERVICE_FAILED);
            } else {
                itemVO.setAttrs(r.getData(new TypeReference<List<String>>() {
                }));
            }
        }, executor);
        // Wait for the asynchronous task to complete
        try {
            CompletableFuture.allOf(skuInfoTask, saleAttrTask).get();
        } catch (Exception e) {
            log.error("Failed to construct shopping item details by thread pool asynchronous orchestration");
            throw new BizException(BizCodeEnum.THREAD_POOL_TASK_FAILED);
        }
        return itemVO;
    }

    /**
     * Add items to the specified shopping cart
     * @return
     */
    private CartItemVO addToCertainCart(Long skuId, Integer count, String cartKey) {
        // Determine if the item exists in the shopping cart
        // If the item is not in the shopping cart, it is a new addition; If so, that is the number of modifications
        BoundHashOperations<String, String, String> op = redisTemplate.boundHashOps(cartKey);
        String dataStr = op.get(skuId.toString());
        // yes
        if (!StringUtils.isEmpty(dataStr)) {
            CartItemVO itemVO = JSON.parseObject(dataStr, CartItemVO.class);
            itemVO.setCount(itemVO.getCount() + count);
            op.put(skuId.toString(), JSON.toJSONString(itemVO));
            return itemVO;
        }
        // no
        CartItemVO itemVO = buildCartItemVO(skuId);
        itemVO.setCount(count);
        itemVO.setChecked(true);
        op.put(skuId.toString(), JSON.toJSONString(itemVO));
        return itemVO;
    }

    /**
     * Merges the items into the specified shopping cart
     *
     * Mainly used to merge the temporary shopping cart into the formal shopping cart
     * @return
     */
    private void moveToCertainCart(CartItemVO itemVO, String cartKey) {
        // Determine if the item exists in the shopping cart
        // If the item is not in the shopping cart, it is a new addition; If so, that is the number of modifications
        BoundHashOperations<String, String, String> op = redisTemplate.boundHashOps(cartKey);
        String dataStr = op.get(itemVO.getSkuId().toString());
        // yes
        if (!StringUtils.isEmpty(dataStr)) {
            CartItemVO cartItem = JSON.parseObject(dataStr, CartItemVO.class);
            cartItem.setCount(cartItem.getCount() + itemVO.getCount());
            op.put(cartItem.getSkuId().toString(), JSON.toJSONString(cartItem));
        } else {
            // no
            op.put(itemVO.getSkuId().toString(), JSON.toJSONString(itemVO));
        }
    }

    /**
     * Clear cart
     * @param cartKey
     */
    private void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    /**
     * Remote call, must be the logged-in user
     * @return
     */
    @Override
    public List<CartItemTO> getCheckedItems() {
        UserLoginStatusTO userLoginStatusTO = CartInterceptor.threadLocal.get();
        if (userLoginStatusTO.getId() == null) {
            throw new BizException(BizCodeEnum.AUTH_USER_NOT_LOGIN);
        }
        BoundHashOperations<String, String, String> ops = getCurrentUserCartOps();
        List<CartItemTO> collect = ops.values().stream()
                .map(val -> JSON.parseObject(val, CartItemVO.class))
                // Filter the selected shopping items
                .filter(item -> item.getChecked())
                .map(itemVO -> {
                    // Re-query to get the latest price
                    R res = productFeignService.getSkuInfo(itemVO.getSkuId());
                    if (res.getCode() != 0) {
                        log.error("The remote call to malle-product query skuinfo failed");
                        throw new BizException(BizCodeEnum.CALL_FEIGN_SERVICE_FAILED, "Query for shopping cart failed");
                    }
                    SkuInfoTO skuInfo = res.getData("skuInfo",new TypeReference<SkuInfoTO>(){});
                    itemVO.setPrice(skuInfo.getPrice());
                    // Convert to a transfer object
                    return convertCartItem2CartItemTO(itemVO);
                }).collect(Collectors.toList());
        return collect;
    }

    private CartItemTO convertCartItem2CartItemTO(CartItemVO cartItemVO) {
        CartItemTO cartItemTO = new CartItemTO();
        BeanUtils.copyProperties(cartItemVO, cartItemTO);
        return cartItemTO;
    }

}

