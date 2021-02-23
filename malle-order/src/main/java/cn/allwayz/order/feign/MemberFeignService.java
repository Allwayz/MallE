package cn.allwayz.order.feign;

import cn.allwayz.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("MallE-MemberService")
public interface MemberFeignService {

    @RequestMapping("/member/memberreceiveaddress/listby/{memberId}")
    R getMemberAddresses(@PathVariable("memberId") Long memberId);

    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    R getAddress(@PathVariable("id") Long addressId);

    @RequestMapping("/member/memberreceiveaddress/default/{memberId}")
    R getMemberDefaultAddress(@PathVariable("memberId") Long memberId);
}
