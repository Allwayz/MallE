package cn.allwayz.cart.service;

import cn.allwayz.cart.vo.CartItemVO;
import cn.allwayz.cart.vo.CartVO;
import cn.allwayz.common.to.CartItemTO;

import java.util.List;

public interface CartService {
    /**
     * 获取当前登录用户的全部购物项列表
     * @return
     */
    CartVO getCart();

    /**
     * 添加某个商品到当前登录用户的购物车
     * @param skuId
     * @param count
     * @return
     */
    CartItemVO addToCart(Long skuId, Integer count);

    /**
     * 获取当前登录用户购物车某个购物项
     * @param skuId
     * @return
     */
    CartItemVO getCartItem(Long skuId);

    void deleteBatch(List<String> skuIds);

    /**
     * 改变购物车中某个购物项选中状态
     */
    void changeItemStatus(String skuId, Boolean checked);

    /**
     * 改变购物车中某个购物项的数量
     */
    void changeItemCount(String skuId, Integer count);

    /**
     * 获取当前用户购物车中选中的购物项列表，更新最新价格
     */
    List<CartItemTO> getCheckedItems();
}
