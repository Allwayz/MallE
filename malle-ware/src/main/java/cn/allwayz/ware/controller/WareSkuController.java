package cn.allwayz.ware.controller;

import cn.allwayz.common.to.OrderLockStockTO;
import cn.allwayz.common.to.SkuHasStockVO;
import cn.allwayz.common.to.SkuStockTO;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.R;
import cn.allwayz.ware.entity.WareSkuEntity;
import cn.allwayz.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 商品库存
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 20:13:03
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * List
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * Info
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * Save
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * Update
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * Delete
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/hasstock")
    public R getSkusHasStock(@RequestBody List<Long> skuIds) {
        List<SkuHasStockVO> vos = wareSkuService.getSkuHasStock(skuIds);
        return R.ok().setData(vos);
    }

    @RequestMapping("/stock/batch")
    public R getSkuStockBatch(@RequestBody List<Long> skuIds) {
        List<SkuStockTO> skuStockTOS = wareSkuService.getSkusStock(skuIds);
        R ok = R.ok();
        ok.setData(skuStockTOS);
        return ok;
    }

    @RequestMapping("/stock/{skuId}")
    public R getSkuStock(@PathVariable("skuId") Long skuId) {
        Long stock  = wareSkuService.getSkuStock(skuId);
        return R.ok().setData(stock);
    }

    /**
     * 为订单锁定库存
     */
    @PostMapping("/lockStock")
    public R lockStock(@RequestBody OrderLockStockTO lockStockTO) {
        wareSkuService.lockOrderStock(lockStockTO);
        return R.ok();
    }

}
