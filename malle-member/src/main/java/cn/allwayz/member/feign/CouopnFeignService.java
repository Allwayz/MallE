package cn.allwayz.member.feign;

import cn.allwayz.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author allwayz
 */
@FeignClient("MallE-CouponService")
public interface CouopnFeignService {
    /**
     * Remote invocation Coupon Function
     * @return
     */
    @RequestMapping("coupon/coupon/member/coupon")
    R memberCoupons();

}
