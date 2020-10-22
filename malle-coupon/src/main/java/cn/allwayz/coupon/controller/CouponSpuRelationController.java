package cn.allwayz.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.allwayz.coupon.entity.CouponSpuRelationEntity;
import cn.allwayz.coupon.service.CouponSpuRelationService;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.R;



/**
 * 优惠券与产品关联
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 21:14:44
 */
@RestController
@RequestMapping("coupon/couponspurelation")
public class CouponSpuRelationController {
    @Autowired
    private CouponSpuRelationService couponSpuRelationService;

    /**
     * List
     */
    @RequestMapping("/list")
    //@RequiresPermissions("coupon:couponspurelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = couponSpuRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * Info
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("coupon:couponspurelation:info")
    public R info(@PathVariable("id") Long id){
		CouponSpuRelationEntity couponSpuRelation = couponSpuRelationService.getById(id);

        return R.ok().put("couponSpuRelation", couponSpuRelation);
    }

    /**
     * Save
     */
    @RequestMapping("/save")
    //@RequiresPermissions("coupon:couponspurelation:save")
    public R save(@RequestBody CouponSpuRelationEntity couponSpuRelation){
		couponSpuRelationService.save(couponSpuRelation);

        return R.ok();
    }

    /**
     * Update
     */
    @RequestMapping("/update")
    //@RequiresPermissions("coupon:couponspurelation:update")
    public R update(@RequestBody CouponSpuRelationEntity couponSpuRelation){
		couponSpuRelationService.updateById(couponSpuRelation);

        return R.ok();
    }

    /**
     * Delete
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("coupon:couponspurelation:delete")
    public R delete(@RequestBody Long[] ids){
		couponSpuRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
