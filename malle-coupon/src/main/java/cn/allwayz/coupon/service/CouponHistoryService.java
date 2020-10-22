package cn.allwayz.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.coupon.entity.CouponHistoryEntity;

import java.util.Map;

/**
 * 优惠券领取历史记录
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 21:14:46
 */
public interface CouponHistoryService extends IService<CouponHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

