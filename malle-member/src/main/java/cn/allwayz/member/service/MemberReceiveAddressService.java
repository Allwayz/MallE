package cn.allwayz.member.service;

import cn.allwayz.common.to.MemberAddressTO;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.member.entity.MemberReceiveAddressEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 21:13:21
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<MemberAddressTO> listByMemberId(Long memberId);

    MemberAddressTO getMemberDefaultAddress(Long memberId);
}

