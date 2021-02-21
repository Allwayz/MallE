package cn.allwayz.member.dao;

import cn.allwayz.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 会员
 * 
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 21:13:21
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

    MemberEntity getByAccount(@Param("account") String account);
}
