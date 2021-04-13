package cn.allwayz.order.service.impl;

import cn.allwayz.common.constant.OrderConstant;
import cn.allwayz.common.exception.BizCodeEnum;
import cn.allwayz.common.exception.BizException;
import cn.allwayz.common.to.*;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.Query;
import cn.allwayz.common.utils.R;
import cn.allwayz.common.vo.MemberInfoVO;
import cn.allwayz.common.vo.UserCheckVO;
import cn.allwayz.order.config.AlipayTemplate;
import cn.allwayz.order.dao.OrderDao;
import cn.allwayz.order.entity.OrderEntity;
import cn.allwayz.order.entity.OrderItemEntity;
import cn.allwayz.order.entity.PaymentInfoEntity;
import cn.allwayz.order.feign.CartFeignService;
import cn.allwayz.order.feign.MemberFeignService;
import cn.allwayz.order.feign.ProductFeignService;
import cn.allwayz.order.feign.WareFeignService;
import cn.allwayz.order.interceptor.LoginInterceptor;
import cn.allwayz.order.service.FareService;
import cn.allwayz.order.service.OrderItemService;
import cn.allwayz.order.service.OrderService;
import cn.allwayz.order.service.PaymentInfoService;
import cn.allwayz.order.vo.*;
import com.alibaba.fastjson.TypeReference;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    MemberFeignService memberFeignService;

    @Resource
    ThreadPoolExecutor executor;

    @Resource
    WareFeignService wareFeignService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    ProductFeignService productFeignService;

    @Resource
    FareService fareService;

    @Resource
    OrderItemService orderItemService;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    AlipayTemplate alipayTemplate;

    @Resource
    PaymentInfoService paymentInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVO confirmOrder() {
        MemberInfoVO loginUser = LoginInterceptor.threadLocal.get();
        OrderConfirmVO orderConfirmVO = new OrderConfirmVO();
        // RequestAttributes of the original thread binding
        // Asynchronous to prevent FEIGN from losing headers
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> orderItemsTask = CompletableFuture.runAsync(() -> {
            // Assign a request to the new thread binding
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R res = cartFeignService.getCheckedItems();
            if (res.getCode() != 0) {
                log.error("Confirm order Remote call to shopping cart service failed to query shopping cart");
                throw new BizException(BizCodeEnum.CALL_FEIGN_SERVICE_FAILED, "Query for shopping cart failed");
            }
            List<CartItemTO> itemTOS = res.getData(new TypeReference<List<CartItemTO>>() {
            });
            orderConfirmVO.setItems(itemTOS.stream().map(to -> convertOrderItemTO2OrderSkuVO(to)).collect(Collectors.toList()));
        }, executor);
        CompletableFuture<Void> stockTask = orderItemsTask.thenRunAsync(() -> {
            List<Long> ids = orderConfirmVO.getItems().stream().map(OrderSkuVO::getSkuId).collect(Collectors.toList());
            R r = wareFeignService.getSkuStockBatch(ids);
            if (r.getCode() != 0) {
                log.error("Failed to call malle-ware to query commodity inventory: {}",r.getCode());
                throw new BizException(BizCodeEnum.CALL_FEIGN_SERVICE_FAILED, "Inventory query failed");
            }
            List<SkuStockTO> stockTOList = r.getData(new TypeReference<List<SkuStockTO>>() {
            });
            Map<Long, Long> collect = stockTOList.stream().collect(Collectors.toMap(SkuStockTO::getSkuId, SkuStockTO::getStock));
            orderConfirmVO.getItems().forEach(item -> item.setStock(collect.getOrDefault(item.getSkuId(), 0L)));
        }, executor);
        CompletableFuture<Void> memberAddressTask = CompletableFuture.runAsync(() -> {
            R r2 = memberFeignService.getMemberAddresses(loginUser.getId());
            if (r2.getCode() != 0) {
                log.error("Confirm order Remote call member service query address failed");
                throw new BizException(BizCodeEnum.CALL_FEIGN_SERVICE_FAILED, "Failed to query shipping address");
            }
            List<MemberAddressTO> addressTOS = r2.getData(new TypeReference<List<MemberAddressTO>>() {
            });
            orderConfirmVO.setAddresses(addressTOS.stream().map(to -> convert2MemberAddressVO(to)).collect(Collectors.toList()));
        }, executor);
        orderConfirmVO.setIntegration(loginUser.getIntegration());
        String token = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(OrderConstant.ORDER_TOKEN_PREFIX + loginUser.getId(), token, 30, TimeUnit.MINUTES);
        orderConfirmVO.setToken(token);

        // Wait for the asynchronous task to complete
        try {
            CompletableFuture.allOf(stockTask, memberAddressTask).get();
        } catch (Exception e) {
            log.error("Asynchronous Choreographer failed to obtain confirmation order page data: {}", e);
            throw new BizException(BizCodeEnum.THREAD_POOL_TASK_FAILED, "There was an error. Please try again");
        }

        return orderConfirmVO;
    }

    // With Seata distributed transactions, the Seata AT mode (auto-commit, global lock, concurrent serialization) is not suitable for high concurrency
    // @GlobalTransactional
    // Use message queues to achieve final consistency
    @Transactional
    @Override
    public OrderCreateVO submit(OrderSubmitVO submitVO) {
        MemberInfoVO loginUser = LoginInterceptor.threadLocal.get();
        OrderCreateVO createVO = new OrderCreateVO();

        String orderToken = submitVO.getOrderToken();
        String key = OrderConstant.ORDER_TOKEN_PREFIX + loginUser.getId();
        // Getting, validating, and deleting tokens should be atomic operations
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long res = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(key), orderToken);
        if (res == 0) {
            log.warn("Order token validation failed. Created or expired. Please reorder");
            throw new BizException(BizCodeEnum.ORDER_HAS_EXPIRED);
        }
        OrderEntity orderEntity = buildOrder(submitVO);
        createVO.setOrder(orderEntity);

        List<OrderItemEntity> items = buildOrderItems(orderEntity.getOrderSn());
        createVO.setItems(items);

        calculateOrder(orderEntity, items);

        BigDecimal totalPrice = submitVO.getTotalPrice();
        BigDecimal payAmount = orderEntity.getPayAmount();
        // 差距大于0.01验价失败
        if (!(Math.abs(totalPrice.subtract(payAmount).doubleValue()) < 0.01)) {
            log.warn("Create order, front and back end price test failed");
            throw new BizException(BizCodeEnum.UNKNOW_EXCEPTION, "Order failed, please try again");
        }

        lockStock(createVO);

        this.save(orderEntity);
        orderItemService.saveBatch(items);

        try {
            rabbitTemplate.convertAndSend(OrderConstant.ORDER_EVENT_EXCHANGE, OrderConstant.ORDER_CREATE_ROUTING_KEY, orderEntity);
        } catch (AmqpException e) {
            log.warn("Failed to send order new creation message to MQ, retry automatically");
            rabbitTemplate.convertAndSend(OrderConstant.ORDER_EVENT_EXCHANGE, OrderConstant.ORDER_CREATE_ROUTING_KEY, orderEntity);
        }

        // Error here, local transaction cannot control the remote inventory rollback, Seata distributed transaction can be resolved,
        // You can also select message queues for ultimate consistency
        List<Long> skuIds = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
        R r = cartFeignService.delBatch(skuIds);
        if (r.getCode() != 0) {
            log.error("The malle-order call to malle-cart failed to delete the shopping item：{}");
            throw new BizException(BizCodeEnum.CALL_FEIGN_SERVICE_FAILED, "Order failed, please try again");
        }
        return createVO;
    }

    @Override
    public OrderCreateVO getOrderDetail(String orderSn) {
        OrderEntity orderEntity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        if (orderEntity == null) {
            throw new BizException(BizCodeEnum.ORDER_CREATE_FAILED, "The order does not exist");
        }
        OrderCreateVO createVO = new OrderCreateVO();
        List<OrderItemEntity> items = orderItemService.listByOrderSn(orderSn);
        createVO.setOrder(orderEntity);
        createVO.setItems(items);
        return createVO;
    }

    @Override
    public OrderTO getOrderTOByOrderSn(String orderSn) {
        OrderEntity orderEntity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        if (orderEntity == null) {
            return null;
        } else {
            OrderTO orderTO = new OrderTO();
            BeanUtils.copyProperties(orderEntity, orderTO);
            return orderTO;
        }
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        return this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    @Transactional
    @Override
    public void closeOrder(OrderEntity orderEntity) {
        // Get the latest status of the order
        OrderEntity order = getOrderByOrderSn(orderEntity.getOrderSn());
        // It's still unpaid
        if (order.getStatus() == OrderConstant.OrderStatusEnum.CREATE_NEW.getCode()) {
            OrderEntity entity = new OrderEntity();
            entity.setId(order.getId());
            entity.setStatus(OrderConstant.OrderStatusEnum.CANCLED.getCode());
            this.updateById(entity);
            // Send an inventory release message to MQ
            rabbitTemplate.convertAndSend(OrderConstant.ORDER_EVENT_EXCHANGE, OrderConstant.ORDER_RELEASE_STOCK_ROUTING_KEY, getOrderTOByOrderSn(order.getOrderSn()));
        }
    }

    @Override
    public String payOrder(String orderSn) {
        OrderEntity order = getOrderByOrderSn(orderSn);
        if (order == null) {
            throw new BizException(BizCodeEnum.ORDER_PAY_FEILED, "Order Does not Exist");
        } else if(order.getStatus() == OrderConstant.OrderStatusEnum.PAYED.getCode()) {
            // The order has been paid
            throw new BizException(BizCodeEnum.ORDER_PAY_FEILED, "Already Paied");
        }
        AlipayVO alipayVO = new AlipayVO();
        alipayVO.setOutTradeNo(orderSn);
        // Alipay payment requirements must be two decimal places
        alipayVO.setTotalAmount(order.getPayAmount().setScale(2, BigDecimal.ROUND_UP).toString());
        alipayVO.setSubject("Malle Order");
        alipayVO.setBody("Malle Order");
        try {
            String response = alipayTemplate.pay(alipayVO);
            log.info("Alipay payment response：{}", response);
            return response;
        } catch (AlipayApiException e) {
            log.error("AliPay failed：{}", e.getMessage());
            throw new BizException(BizCodeEnum.ORDER_PAY_FEILED);
        }
    }

    @Override
    public PageUtils getCurrentUserOrderList(Map<String, Object> params) {
        System.out.println("Enter getCurrentUserOrderList() Method");
        MemberInfoVO memberInfoVO = LoginInterceptor.threadLocal.get();
        System.out.println("Get MemberId:"+memberInfoVO.getId());
        // Gets all orders for the current logged-in user
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id", memberInfoVO.getId()).orderByDesc("id")
        );
        // Find the order item under each order
        List<OrderEntity> collect = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> orderItemEntities = orderItemService.listByOrderSn(order.getOrderSn());
            order.setItems(orderItemEntities);
            return order;
        }).collect(Collectors.toList());

        System.out.println("Get Order List From Database:");
        for(int i = 0;i < collect.size();i++){
            System.out.println(collect.get(i).toString());
        }
        page.setRecords(collect);
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public String handleAlipayNotify(AlipayNotifyVO notifyVO, HttpServletRequest request) {
        boolean verified;
        // 1.attestation
        try {
            verified = alipayTemplate.signVerify(request);
        } catch (Exception e) {
            log.warn("AliPay Asynchronous Notification Checking Failure");
            return "error";
        }
        // 验签失败
        if (!verified) {
            return "error";
        }
        // 2.Save payment flow
        PaymentInfoEntity paymentInfo = new PaymentInfoEntity();
        paymentInfo.setAlipayTradeNo(notifyVO.getTrade_no());
        paymentInfo.setCallbackTime(notifyVO.getNotify_time());
        paymentInfo.setOrderSn(notifyVO.getOut_trade_no());
        paymentInfo.setPaymentStatus(notifyVO.getTrade_status());
        paymentInfo.setTotalAmount(new BigDecimal(notifyVO.getTotal_amount()));
        paymentInfo.setSubject(notifyVO.getSubject());
        paymentInfoService.save(paymentInfo);
        // 3.Modify the order status to Paid
        if (notifyVO.getTrade_status().equals("TRADE_SUCCESS") || notifyVO.getTrade_status().equals("TRADE_FINISHED")) {
            String orderSn = notifyVO.getOut_trade_no();
            updateOrderStatusByOrderSn(orderSn, OrderConstant.OrderStatusEnum.PAYED.getCode());
        }
        return "success";
    }

    @Transactional
    @Override
    public String updateOrder(UserCheckVO userCheckVO) {
        OrderEntity orderEntity = this.baseMapper.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn",userCheckVO.getOrderSN()));
        List<OrderItemEntity> orderItemEntityList = orderItemService.listByOrderSn(orderEntity.getOrderSn());
        String items = null;
        for (OrderItemEntity itemz: orderItemEntityList) {
            items += itemz.getSkuName();
        }
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setOrderSn(userCheckVO.getOrderSN());
        paymentInfoEntity.setPaymentStatus("TRADE_FINISH");
        paymentInfoEntity.setTotalAmount(orderEntity.getTotalAmount());
        paymentInfoEntity.setSubject(items);
        paymentInfoService.save(paymentInfoEntity);
        updateOrderStatusByOrderSn(orderEntity.getOrderSn(), OrderConstant.OrderStatusEnum.PAYED.getCode());
        //Unlock inventory and update it
        wareFeignService.reseaseStock(orderEntity.getOrderSn());
        return "success";
    }

    /**
     * Update order status
     * @param orderSn
     * @param status
     * @return
     */
    private boolean updateOrderStatusByOrderSn(String orderSn, Integer status) {
        UpdateWrapper<OrderEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_sn",orderSn).set("status", status);
        int i = this.baseMapper.update(null, updateWrapper);
        if(i > 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * Locking the inventory
     * @param createVO
     */
    private void lockStock(OrderCreateVO createVO) {
        OrderLockStockTO lockStockTO = new OrderLockStockTO();
        lockStockTO.setOrderSn(createVO.getOrder().getOrderSn());
        List<OrderLockStockTO.SkuLockStock> collect = createVO.getItems().stream().map(item -> {
            OrderLockStockTO.SkuLockStock skuLock = new OrderLockStockTO.SkuLockStock();
            skuLock.setSkuId(item.getSkuId());
            skuLock.setSkuName(item.getSkuName());
            skuLock.setCount(item.getSkuQuantity());
            return skuLock;
        }).collect(Collectors.toList());
        lockStockTO.setLocks(collect);

        R r = wareFeignService.lockStock(lockStockTO);
        if (r.getCode() != 0) {
            throw new BizException(BizCodeEnum.WARE_SKU_STOCK_NOT_ENOUGH, "Place the order failed：" + r.get("msg"));
        }
    }

    /**
     * Create Order
     * @param submitVO
     * @return
     */
    private OrderEntity buildOrder(OrderSubmitVO submitVO) {
        MemberInfoVO loginUser = LoginInterceptor.threadLocal.get();
        OrderEntity orderEntity = new OrderEntity();
        // 1.Order creator
        orderEntity.setMemberId(loginUser.getId());
        orderEntity.setMemberUsername(loginUser.getUsername());
        // 2.Shipping address and freight
        FareInfoTO fare = fareService.getFare(submitVO.getAddressId());
        MemberAddressTO address = fare.getAddress();
        orderEntity.setReceiverProvince(address.getProvince());
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverDetailAddress(address.getDetailAddress());
        // orderEntity.setReceiverRegion();
        // orderEntity.setReceiverPostCode();
        orderEntity.setReceiverName(address.getName());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setFreightAmount(fare.getFare());
        orderEntity.setAutoConfirmDay(7);
        orderEntity.setPayType(submitVO.getPayType());
        orderEntity.setOrderSn(IdWorker.getTimeId());
        orderEntity.setCreateTime(new Date());
        orderEntity.setModifyTime(new Date());
        orderEntity.setStatus(OrderConstant.OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setDeleteStatus(0);
        orderEntity.setConfirmStatus(0);
        return orderEntity;
    }

    /**
     * Build an order item
     * @param orderSn
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        // 1.获取购物车中所有已选购物项
        // TODO 1.异步查询购物车
        R res = cartFeignService.getCheckedItems();
        if (res.getCode() != 0) {
            log.error("Confirm order Remote call to shopping cart service failed to query shopping cart");
            throw new BizException(BizCodeEnum.CALL_FEIGN_SERVICE_FAILED, "Query for shopping cart failed");
        }
        List<CartItemTO> itemTOS = res.getData(new TypeReference<List<CartItemTO>>() {
        });
        List<OrderItemEntity> items = itemTOS.stream().map(cartItemTO -> {
            OrderItemEntity orderItem = buildOrderItemFromCartItem(cartItemTO);
            orderItem.setOrderSn(orderSn);
            return orderItem;
        }).collect(Collectors.toList());
        return items;
    }

    private OrderItemEntity buildOrderItemFromCartItem(CartItemTO cartItem) {
        OrderItemEntity orderItemVO = new OrderItemEntity();
        // 1. Sku part
        orderItemVO.setSkuId(cartItem.getSkuId());
        orderItemVO.setSkuName(cartItem.getSkuTitle());
        orderItemVO.setSkuPrice(cartItem.getPrice());
        orderItemVO.setSkuPic(cartItem.getSkuImg());
        orderItemVO.setSkuQuantity(cartItem.getCount());
        orderItemVO.setSkuAttrsVals(cartItem.getAttrs().stream().collect(Collectors.joining(";")));
        // 2. The spu part
        R r = productFeignService.getSpuBySkuId(cartItem.getSkuId());
        if (r.getCode() != 0) {
            log.error("Malle-order failed to call malle-product to query spuinfo");
            throw new BizException(BizCodeEnum.CALL_FEIGN_SERVICE_FAILED, "Please try again if the order fails");

        }
        SpuInfoTO spuInfoTO = r.getData(SpuInfoTO.class);
        orderItemVO.setSpuId(spuInfoTO.getId());
        orderItemVO.setSpuName(spuInfoTO.getSpuName());
        // orderItemVO.setSpuPic;
        orderItemVO.setSpuBrand(spuInfoTO.getBrandName());
        orderItemVO.setCategoryId(spuInfoTO.getCatelogId());

        orderItemVO.setGiftGrowth(spuInfoTO.getGrowBounds().intValue() * cartItem.getCount());
        orderItemVO.setGiftIntegration(spuInfoTO.getIntegration().intValue() * cartItem.getCount());

        BigDecimal zero = new BigDecimal("0");
        orderItemVO.setCouponAmount(zero);
        orderItemVO.setIntegrationAmount(zero);
        orderItemVO.setPromotionAmount(zero);
        BigDecimal origin = orderItemVO.getSkuPrice().multiply(new BigDecimal(orderItemVO.getSkuQuantity().toString()));
        BigDecimal subtract = origin.subtract(orderItemVO.getCouponAmount()).subtract(orderItemVO.getIntegrationAmount()).subtract(orderItemVO.getPromotionAmount());
        orderItemVO.setRealAmount(subtract);
        return orderItemVO;
    }

    /**
     * Some properties in the order require calculation of the sum of the order items
     * @param items
     * @return
     */
    private void calculateOrder(OrderEntity orderEntity, List<OrderItemEntity> items) {
        int growth = 0, integration = 0;
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal couponAmount = new BigDecimal("0");
        BigDecimal promotionAmount = new BigDecimal("0");
        BigDecimal integrationAmount = new BigDecimal("0");
        if (!CollectionUtils.isEmpty(items)) {
            for (OrderItemEntity item : items) {
                growth += item.getGiftGrowth();
                integration += item.getGiftIntegration();
                totalAmount = totalAmount.add(item.getRealAmount());
                couponAmount = couponAmount.add(item.getCouponAmount());
                promotionAmount = promotionAmount.add(item.getPromotionAmount());
                integrationAmount = integrationAmount.add(item.getIntegrationAmount());
            }
        }
        orderEntity.setIntegration(integration);
        orderEntity.setGrowth(growth);
        orderEntity.setPromotionAmount(promotionAmount);
        orderEntity.setCouponAmount(couponAmount);
        orderEntity.setIntegrationAmount(integrationAmount);
        orderEntity.setTotalAmount(totalAmount);
        orderEntity.setPayAmount(totalAmount.add(orderEntity.getFreightAmount()));
    }
    private OrderSkuVO convertOrderItemTO2OrderSkuVO(CartItemTO itemTO) {
        OrderSkuVO orderSkuVO = new OrderSkuVO();
        BeanUtils.copyProperties(itemTO, orderSkuVO);
        return orderSkuVO;
    }

    private MemberAddressVO convert2MemberAddressVO(MemberAddressTO addressTO) {
        MemberAddressVO addressVO = new MemberAddressVO();
        BeanUtils.copyProperties(addressTO, addressVO);
        return addressVO;
    }


}