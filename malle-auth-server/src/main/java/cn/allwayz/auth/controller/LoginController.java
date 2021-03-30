package cn.allwayz.auth.controller;

import cn.allwayz.auth.service.LoginService;
import cn.allwayz.auth.vo.LoginVO;
import cn.allwayz.common.constant.AuthServerConstant;
import cn.allwayz.common.vo.MemberInfoVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {
    @Resource
    LoginService loginService;

    @PostMapping("/login")
    public String login(LoginVO loginVO, HttpSession session) {
        MemberInfoVO info = loginService.doLogin(loginVO);
        session.setAttribute(AuthServerConstant.LOGIN_USER_KEY, info);
        return "redirect:http://malle.com/";
    }
}
