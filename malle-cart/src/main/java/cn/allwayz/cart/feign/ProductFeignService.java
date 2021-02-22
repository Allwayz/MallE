package cn.allwayz.cart.feign;

import cn.allwayz.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author allwayz
 */
@FeignClient("MallE-ProductService")
public interface ProductFeignService {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

    @RequestMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    R getSaleAttrStringList(@PathVariable("skuId") Long skuId);
}
