package cn.allwayz.cart.service;

import cn.allwayz.cart.vo.CartItemVO;
import cn.allwayz.cart.vo.CartVO;
import cn.allwayz.common.to.CartItemTO;

import java.util.List;

public interface CartService {
    /**
     * Gets a list of all shopping items for the current logged-in user
     * @return
     */
    CartVO getCart();

    /**
     * Adds an item to the current logged-in user's shopping cart
     * @param skuId
     * @param count
     * @return
     */
    CartItemVO addToCart(Long skuId, Integer count);

    /**
     * Gets an item in the current logged-in user's shopping cart
     * @param skuId
     * @return
     */
    CartItemVO getCartItem(Long skuId);

    void deleteBatch(List<String> skuIds);

    /**
     * Changes the selection status of a shopping item in the shopping cart
     */
    void changeItemStatus(String skuId, Boolean checked);

    /**
     * Change the amount of an item in your shopping cart
     */
    void changeItemCount(String skuId, Integer count);

    /**
     * Gets the list of selected items in the current user's shopping cart and updates the latest prices
     */
    List<CartItemTO> getCheckedItems();
}
