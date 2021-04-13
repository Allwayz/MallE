package cn.allwayz.product.feign;

import cn.allwayz.common.to.SkuReductionTo;
import cn.allwayz.common.to.SpuBoundTo;
import cn.allwayz.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("MallE-CouponService")
public interface CouponFeignService {

    /**
     * 1, CouponFeignService saveSpuBounds (spuBoundTo);
     *      1), @RequestBody converts this object to JSON.
     *      2), find the malle-coupon service and send a request to /coupon/spubounds/save.
     *          Send the request by placing the previous JSON in the request body;
     *      3) The service received the request. The request body contains JSON data.
     *          (@requestbody SpuBoundsEntity spuBounds); Converts the JSON of the request body to SPUBoundSentity;
     * As long as the JSON data model is compatible. Both services do not need to use the same TO
     * @param spuBoundTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);


    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);

    @RequestMapping("/coupon/spubounds/info/spuId/{spuId}")
    R getBySpuId(@PathVariable("spuId") Long spuId);

}
