package cn.allwayz.order.web;

import cn.allwayz.common.to.OrderTO;
import cn.allwayz.common.utils.Constant;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.R;
import cn.allwayz.order.service.OrderService;
import cn.allwayz.order.vo.OrderConfirmVO;
import cn.allwayz.order.vo.OrderCreateVO;
import cn.allwayz.order.vo.OrderSubmitVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author allwayz
 */
@Controller
public class OrderWebController {

    @Resource
    OrderService orderService;

    /**
     * User order list
     * @return
     */
    @GetMapping("/center/list.html")
    public String orderList(@RequestParam(value = "pageNum", required = false, defaultValue = "1") String pageNum,
                            @RequestParam(value = "pageSize", required = false, defaultValue = "4") String pageSize,
                            Model model) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.PAGE, pageNum);
        params.put(Constant.LIMIT, pageSize);
        PageUtils page = orderService.getCurrentUserOrderList(params);
        model.addAttribute("page", page);
        return "list";
    }

    /**
     * The user clicks from the shopping cart page to check out
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model, HttpServletRequest request) {
        // 得到用户本次结算时购物车中选中的那些购物项
        OrderConfirmVO orderConfirmVO = orderService.confirmOrder();
        model.addAttribute("order", orderConfirmVO);
        return "confirm";
    }

    /**
     * Submit the order, create the order
     */
    @PostMapping("/submit")
    public String submit(OrderSubmitVO submitVO) {
        System.out.println(submitVO.toString());
        OrderCreateVO resp = orderService.submit(submitVO);
        return "redirect:http://order.malle.com/topay?orderSn=" + resp.getOrder().getOrderSn();
    }

    @GetMapping("/topay")
    public String topay(@RequestParam("orderSn") String orderSn,
                        Model model) {
        OrderCreateVO resp = orderService.getOrderDetail(orderSn);
        model.addAttribute("orderCreateVO", resp);
        return "pay";
    }

    /**
     * Remote calls that start with/API need to be cleared by interceptors
     * @param orderSn
     * @return
     */
    @ResponseBody
    @GetMapping("/api/info/{orderSn}")
    public R getOrderDetail(@PathVariable("orderSn") String orderSn) {
        OrderTO resp = orderService.getOrderTOByOrderSn(orderSn);
        return R.ok().setData(resp);
    }
}
