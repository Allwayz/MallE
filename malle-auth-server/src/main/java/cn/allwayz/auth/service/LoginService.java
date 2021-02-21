package cn.allwayz.auth.service;

import cn.allwayz.auth.vo.LoginVO;
import cn.allwayz.common.vo.MemberInfoVO;

public interface LoginService {
    MemberInfoVO doLogin(LoginVO loginVO);
}
