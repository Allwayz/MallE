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
     * 用户访问其购物车
     * @return
     */
    @GetMapping("/list.html")
    public String cartList(Model model) {
        CartVO cart = cartService.getCart();
        model.addAttribute("cart", cart);

        return "cartList";
    }

    /**
     * 用户将某个商品加入其购物车，成功后返回成功页面，
     * 为了避免表单重复提交，应该使用重定向
     *
     * 如果使用  redirectAttributes.addFlashAttribute("item", itemVO); 重定向回成功页面，只能取一次数据，用户刷新后就无了
     *
     * 所以应该重定向到另一个请求，单上当次的skuId，控制器重新获取这个购物项数据，放入model中，再返回到成功页
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
     * 结合上面那个方法，共同完成添加商品到购物车功能，即可刷新页面，又不会重复提交表单数据
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
     * 改变购物车中某个购物项选中状态
     */
    @ResponseBody
    @GetMapping("/change/status")
    public R changeItemStatus(@RequestParam("skuId") String skuId,
                              @RequestParam("checked") Boolean checked) {
        cartService.changeItemStatus(skuId, checked);
        return R.ok();
    }

    /**
     * 改变购物车中某个购物项的数量
     */
    @ResponseBody
    @GetMapping("/change/count")
    public R changeItemCount(@RequestParam("skuId") String skuId,
                             @RequestParam("count") Integer count) {
        cartService.changeItemCount(skuId, count);
        return R.ok();
    }

    /**
     * 获取当前用户购物车中选中的购物项列表
     */
    @ResponseBody
    @GetMapping("/checked")
    public R getCheckedItems() {
        List<CartItemTO> itemTOS = cartService.getCheckedItems();
        return R.ok().setData(itemTOS);
    }

}

