package cn.allwayz.ware.feign;

import cn.allwayz.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author allwayz
 */
@FeignClient("MallE-OrderService")
public interface OrderFeignService {

    @GetMapping("/api/info/{orderSn}")
    R getOrderDetail(@PathVariable("orderSn") String orderSn);
}

