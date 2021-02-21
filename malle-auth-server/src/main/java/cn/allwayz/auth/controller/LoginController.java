package cn.allwayz.auth.controller;

import cn.allwayz.auth.service.LoginService;
import cn.allwayz.auth.vo.LoginVO;
import cn.allwayz.common.constant.AuthServerConstant;
import cn.allwayz.common.vo.MemberInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    LoginService loginService;

    @PostMapping("/login")
    public String login(LoginVO loginVO, HttpSession session) {
        MemberInfoVO info = loginService.doLogin(loginVO);
        // 登录失败会被异常处理器处理
        // 登录成功要保存会话信息，返回主页面
        session.setAttribute(AuthServerConstant.LOGIN_USER_KEY, info);
        // System.out.println(infoTO);
        return "redirect:http://malle.com/";
    }
}
