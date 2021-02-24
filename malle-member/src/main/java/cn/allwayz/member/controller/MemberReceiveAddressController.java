package cn.allwayz.member.controller;

import cn.allwayz.common.to.MemberAddressTO;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.R;
import cn.allwayz.member.entity.MemberReceiveAddressEntity;
import cn.allwayz.member.service.MemberReceiveAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 会员收货地址
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 21:13:21
 */
@RestController
@RequestMapping("member/memberreceiveaddress")
public class MemberReceiveAddressController {
    @Autowired
    private MemberReceiveAddressService memberReceiveAddressService;

    /**
     * List
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:memberreceiveaddress:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberReceiveAddressService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * Info
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:memberreceiveaddress:info")
    public R info(@PathVariable("id") Long id){
		MemberReceiveAddressEntity memberReceiveAddress = memberReceiveAddressService.getById(id);

        return R.ok().put("memberReceiveAddress", memberReceiveAddress);
    }

    /**
     * Save
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:memberreceiveaddress:save")
    public R save(@RequestBody MemberReceiveAddressEntity memberReceiveAddress){
		memberReceiveAddressService.save(memberReceiveAddress);

        return R.ok();
    }

    /**
     * Update
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:memberreceiveaddress:update")
    public R update(@RequestBody MemberReceiveAddressEntity memberReceiveAddress){
		memberReceiveAddressService.updateById(memberReceiveAddress);

        return R.ok();
    }

    /**
     * Delete
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:memberreceiveaddress:delete")
    public R delete(@RequestBody Long[] ids){
		memberReceiveAddressService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @RequestMapping("/listby/{memberId}")
    public R listBy(@PathVariable("memberId") Long memberId){
        List<MemberAddressTO> list = memberReceiveAddressService.listByMemberId(memberId);

        return R.ok().setData(list);
    }

    /**
     * 获取用户默认地址
     * @param memberId
     * @return
     */
    @RequestMapping("/default/{memberId}")
    R getMemberDefaultAddress(@PathVariable("memberId") Long memberId) {
        MemberAddressTO memberReceiveAddress = memberReceiveAddressService.getMemberDefaultAddress(memberId);
        return R.ok().setData(memberReceiveAddress);
    }


}
