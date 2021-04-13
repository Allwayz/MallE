package cn.allwayz.order.web;

import cn.allwayz.common.utils.R;
import cn.allwayz.common.vo.UserCheckVO;
import cn.allwayz.order.config.AlipayTemplate;
import cn.allwayz.order.feign.MemberFeignService;
import cn.allwayz.order.service.OrderService;
import cn.allwayz.order.vo.AlipayNotifyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author allwayz
 */
@Slf4j
@Controller
public class PayController {

    @Resource
    OrderService orderService;

    @Resource
    AlipayTemplate alipayTemplate;

    @Autowired
    MemberFeignService memberFeignService;

    /**
     * Tells the browser that the string returned is text/ HTML
     * @param orderSn
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/pay/{orderSn}", produces = "text/html;charset=UTF-8")
    public String pay(@PathVariable("orderSn") String orderSn) {
        return orderSn;
        //return orderService.payOrder(orderSn);
    }

    /**
     *  @return
     */
    @ResponseBody
    @PostMapping("/alipay/notify")
    public String payNotify(AlipayNotifyVO notifyVO,
                            HttpServletRequest request) {
        log.info("Asynchronous notification message：" + notifyVO);
        return orderService.handleAlipayNotify(notifyVO, request);
    }

    @PostMapping("/pay/passwd")
    public String payByPasswd(UserCheckVO userCheckVO){
        R r = memberFeignService.checkPasswd(userCheckVO);
        System.out.println(userCheckVO.getOrderSN());
        System.out.println(r.getData(boolean.class));
        if(r.getData(boolean.class)){
            orderService.updateOrder(userCheckVO);
            return "redirect://order.malle.com/center/list.html";
        }
        return "redirect://order.malle.com/topay?orderSn="+userCheckVO.getOrderSN();
    }
}
