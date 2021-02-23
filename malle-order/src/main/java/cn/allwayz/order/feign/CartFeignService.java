package cn.allwayz.order.feign;

import cn.allwayz.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author allwayz
 */
@FeignClient("MallE-CartService")
public interface CartFeignService {

    @GetMapping("/cart/checked")
    R getCheckedItems();

    @PostMapping("/cart/del/batch")
    R delBatch(@RequestBody List<Long> skuIds);
}
