package cn.allwayz.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.coupon.entity.CouponSpuCategoryRelationEntity;

import java.util.Map;

/**
 * 优惠券分类关联
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 21:14:44
 */
public interface CouponSpuCategoryRelationService extends IService<CouponSpuCategoryRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

