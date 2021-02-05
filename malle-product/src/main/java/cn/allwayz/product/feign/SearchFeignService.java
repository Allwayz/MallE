package cn.allwayz.product.feign;

import cn.allwayz.common.to.es.SkuEsModel;
import cn.allwayz.common.utils.R;
import cn.allwayz.common.utils.ResultBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("MallE-SearchService")
public interface SearchFeignService {

    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> esSkuModels);

}
