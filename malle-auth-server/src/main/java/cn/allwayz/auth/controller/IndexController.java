package cn.allwayz.auth.controller;

import cn.allwayz.common.constant.AuthServerConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

/**
 * @author allwayz
 */
@Controller
public class IndexController {

    @GetMapping("/login.html")
    public String loginPage(HttpSession httpSession) {
        if (httpSession.getAttribute(AuthServerConstant.LOGIN_USER_KEY) != null) {
            // 用户已登录
            return "redirect:http://malle.com";
        }
        return "index";
    }

    @GetMapping("/reg.html")
    public String regPage() {
        return "reg";
    }

}
