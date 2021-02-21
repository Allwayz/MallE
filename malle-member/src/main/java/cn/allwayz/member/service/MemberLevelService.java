package cn.allwayz.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 21:13:21
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);

    MemberLevelEntity getDefaultLevel();
}

