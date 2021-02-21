package cn.allwayz.auth.feign;

import cn.allwayz.common.to.MemberLoginTO;
import cn.allwayz.common.to.MemberRegisterTO;
import cn.allwayz.common.to.WeiboUserAuthTO;
import cn.allwayz.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("MallE-MemberService")
public interface MemberFeignService {

    @RequestMapping("/member/member/register")
    R register(@RequestBody MemberRegisterTO registerTO);

    @RequestMapping("/member/member/login")
    R login(@RequestBody MemberLoginTO loginTO);

    @RequestMapping("/member/member/weibo/login")
    R weiboLogin(@RequestBody WeiboUserAuthTO authTO);
}
