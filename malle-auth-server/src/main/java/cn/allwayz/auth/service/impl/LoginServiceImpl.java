package cn.allwayz.auth.service.impl;

import cn.allwayz.auth.exception.LoginPageException;
import cn.allwayz.auth.feign.MemberFeignService;
import cn.allwayz.auth.service.LoginService;
import cn.allwayz.auth.vo.LoginVO;
import cn.allwayz.common.to.MemberInfoTO;
import cn.allwayz.common.to.MemberLoginTO;
import cn.allwayz.common.utils.R;
import cn.allwayz.common.vo.MemberInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author allwayz
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    MemberFeignService memberFeignService;

    @Override
    public MemberInfoVO doLogin(LoginVO loginVO) {
        MemberLoginTO memberLoginTO = new MemberLoginTO();
        BeanUtils.copyProperties(loginVO, memberLoginTO);
        R r = memberFeignService.login(memberLoginTO);
        // 登录失败
        if (r.getCode() != 0) {
            throw new LoginPageException(r.getCode(), r.getData(String.class));
        }
        return convertMemberInfoTO2MemberInfoVO(r.getData(MemberInfoTO.class));
    }

    private MemberInfoVO convertMemberInfoTO2MemberInfoVO(MemberInfoTO infoTO) {
        MemberInfoVO infoVO = new MemberInfoVO();
        BeanUtils.copyProperties(infoTO, infoVO);
        return infoVO;
    }
}
