package cn.allwayz.member.service.impl;

import cn.allwayz.common.to.MemberAddressTO;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.Query;
import cn.allwayz.member.dao.MemberReceiveAddressDao;
import cn.allwayz.member.entity.MemberReceiveAddressEntity;
import cn.allwayz.member.service.MemberReceiveAddressService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberReceiveAddressEntity> page = this.page(
                new Query<MemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<MemberReceiveAddressEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<MemberAddressTO> listByMemberId(Long memberId) {

        List<MemberReceiveAddressEntity> addressEntities = this.list(new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id", memberId));
        return addressEntities.stream().map(entity -> convertAddressEntity2AddressTO(entity)).collect(Collectors.toList());
    }

    @Override
    public MemberAddressTO getMemberDefaultAddress(Long memberId) {
        MemberReceiveAddressEntity addressEntity = this.getOne(new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id", memberId).eq("default_status", "1"));
        return convertAddressEntity2AddressTO(addressEntity);
    }

    private  MemberAddressTO convertAddressEntity2AddressTO(MemberReceiveAddressEntity entity) {
        MemberAddressTO memberAddressTO = new MemberAddressTO();
        BeanUtils.copyProperties(entity, memberAddressTO);
        return memberAddressTO;
    }

}