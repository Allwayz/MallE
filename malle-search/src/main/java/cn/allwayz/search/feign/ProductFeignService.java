package cn.allwayz.search.feign;

import cn.allwayz.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author allwayz
 */
@FeignClient("MallE-ProductService")
public interface ProductFeignService {

    @GetMapping("/product/attr/info/{attrId}")
    R info(@PathVariable("attrId") Long attrId);

    @RequestMapping("/product/brand/info/batch")
    R getBatch(@RequestBody List<Long> ids);

    @GetMapping("/product/category/cateLogJson")
    R returnCategoryJson();

}
