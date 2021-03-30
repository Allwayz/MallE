package cn.allwayz.member.service;

import cn.allwayz.common.to.MemberInfoTO;
import cn.allwayz.common.to.MemberLoginTO;
import cn.allwayz.common.to.MemberRegisterTO;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.member.entity.MemberEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 会员
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 21:13:21
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * 新用户注册
     * @param registerTO
     * @return
     */
    boolean register(MemberRegisterTO registerTO);

    /**
     * 账号密码登录
     * @param loginTO
     * @return
     */
    MemberInfoTO login(MemberLoginTO loginTO);

    boolean comparePasswd(String username, String password);
}

