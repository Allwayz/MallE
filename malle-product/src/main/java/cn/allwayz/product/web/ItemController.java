package cn.allwayz.product.web;

import cn.allwayz.product.service.SkuInfoService;
import cn.allwayz.product.vo.ItemDetailVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;

/**
 * @author allwayz
 */
@Controller
public class ItemController {

    @Resource
    SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId") Long skuId,
                       Model model) {
        ItemDetailVO detail = skuInfoService.detail(skuId);
        model.addAttribute("item", detail);
        return "item";
    }

}
