package cn.allwayz.cart.controller;

import cn.allwayz.cart.service.CartService;
import cn.allwayz.cart.vo.CartItemVO;
import cn.allwayz.cart.vo.CartVO;
import cn.allwayz.common.to.CartItemTO;
import cn.allwayz.common.utils.R;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/cart")
public class CartController {

    @Resource
    CartService cartService;

    /**
     * The user accesses his shopping cart
     * @return
     */
    @GetMapping("/list.html")
    public String cartList(Model model) {
        CartVO cart = cartService.getCart();
        model.addAttribute("cart", cart);

        return "cartList";
    }

    /**
     * When a user adds an item to his or her shopping cart and returns to the success page,
     * a redirect should be used to avoid double submission of the form
     *
     * If use redirectAttributes.addFlashAttribute("item", itemVO); Redirect back to the success page, can only fetch data once, user refresh will be lost
     *
     * So it should redirect to another request, the skuId, and the controller will retrieve the purchase item data, put it in the model, and return to the success page
     *
     * @return
     */
    @GetMapping("/additem")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("count") Integer count) {
        CartItemVO itemVO = cartService.addToCart(skuId, count);
        return "redirect:http://cart.malle.com/cart/addToCart?skuId=" + skuId.toString();
    }

    /**
     * Combined with the above method, add an item to your shopping cart to refresh the page without submitting the form data twice
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            Model model) {
        CartItemVO itemVO = cartService.getCartItem(skuId);
        model.addAttribute("item", itemVO);
        return "success";
    }

    @ResponseBody
    @PostMapping("/del/batch")
    public R delBatch(@RequestBody List<String> skuIds) {
        cartService.deleteBatch(skuIds);
        return R.ok();
    }
    /**
     * Changes the selection status of a shopping item in the shopping cart
     */
    @ResponseBody
    @GetMapping("/change/status")
    public R changeItemStatus(@RequestParam("skuId") String skuId,
                              @RequestParam("checked") Boolean checked) {
        cartService.changeItemStatus(skuId, checked);
        return R.ok();
    }

    /**
     * Change the amount of an item in your shopping cart
     */
    @ResponseBody
    @GetMapping("/change/count")
    public R changeItemCount(@RequestParam("skuId") String skuId,
                             @RequestParam("count") Integer count) {
        cartService.changeItemCount(skuId, count);
        return R.ok();
    }

    /**
     * Gets a list of selected items in the current user's shopping cart
     */
    @ResponseBody
    @GetMapping("/checked")
    public R getCheckedItems() {
        List<CartItemTO> itemTOS = cartService.getCheckedItems();
        return R.ok().setData(itemTOS);
    }

}

