package cn.allwayz.member.controller;

import cn.allwayz.common.to.MemberInfoTO;
import cn.allwayz.common.to.MemberLoginTO;
import cn.allwayz.common.to.MemberRegisterTO;
import cn.allwayz.common.to.WeiboUserAuthTO;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.R;
import cn.allwayz.member.entity.MemberEntity;
import cn.allwayz.member.feign.CouopnFeignService;
import cn.allwayz.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 会员
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 21:13:21
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    CouopnFeignService couopnFeignService;

    /**
     * List
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * Info
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * Save
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * Update
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * Delete
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @RequestMapping("/coupons")
    public R getCoupons(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("Joker");
        R memberCoupons = couopnFeignService.memberCoupons();
        return R.ok().put("Member",memberEntity).put("Coupons",memberCoupons.get("Coupons"));
    }

    /**
     * 新用户注册
     * @param registerTO
     * @return
     */
    @RequestMapping("/register")
    public R register(@RequestBody MemberRegisterTO registerTO){
        memberService.register(registerTO);
        return R.ok();
    }

    /**
     * 用户登录
     */
    @RequestMapping("/login")
    public R login(@RequestBody MemberLoginTO loginTO){
        // 登录失败会由异常处理机制处理
        MemberInfoTO infoTO = memberService.login(loginTO);
        return R.ok().setData(infoTO);
    }

    /**
     * 社交登录--微博
     */
    @RequestMapping("/weibo/login")
    public R weiboLogin(@RequestBody WeiboUserAuthTO authTO){
        // 登录失败会由异常处理机制处理
        MemberInfoTO infoTO = memberService.weiboLogin(authTO);
        return R.ok().setData(infoTO);
    }

}
