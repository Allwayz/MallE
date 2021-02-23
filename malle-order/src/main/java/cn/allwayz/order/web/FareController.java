package cn.allwayz.order.web;

import cn.allwayz.common.to.FareInfoTO;
import cn.allwayz.common.utils.R;
import cn.allwayz.order.service.FareService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author allwayz
 */
@Controller
@RequestMapping("/order/fare")
public class FareController {

    @Resource
    FareService fareService;

    @ResponseBody
    @GetMapping("/address/{id}")
    public R getFare(@PathVariable("id") Long addrId) {
        FareInfoTO fare = fareService.getFare(addrId);
        return R.ok().setData(fare);
    }
}