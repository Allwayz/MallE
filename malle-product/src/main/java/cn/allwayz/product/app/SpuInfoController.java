package cn.allwayz.product.app;

import cn.allwayz.common.to.SpuInfoTO;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.R;
import cn.allwayz.product.entity.SpuInfoEntity;
import cn.allwayz.product.service.SpuInfoService;
import cn.allwayz.product.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * spu信息
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 19:24:14
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SpuSaveVo vo){
        //spuInfoService.save(spuInfo);

        spuInfoService.saveSpuInfo(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
        spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/{spuId}/up")
    public R spuUp(@PathVariable("spuId") Long spuId){
        spuInfoService.up(spuId);
        return R.ok();
    }

    /**
     * 远程调用，根据skuId查找
     */
    @GetMapping("/skuId/{skuId}")
    public R getBySkuId(@PathVariable("skuId") Long skuId) {
        SpuInfoTO spuInfoTO = spuInfoService.getBySkuId(skuId);
        return R.ok().setData(spuInfoTO);
    }
}