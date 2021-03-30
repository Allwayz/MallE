package cn.allwayz.auth.controller;

import cn.allwayz.auth.service.RegisterService;
import cn.allwayz.auth.vo.RegisterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author allwayz
 */
@Controller
@RequestMapping("register")
public class RegisterController {

    @Autowired
    RegisterService registerService;

    /**
     * @param registerVO
     * @return
     */
    @PostMapping("/register")
    public String register(@Validated RegisterVO registerVO) {
        boolean res = registerService.register(registerVO);
        return "redirect://auth.malle.com/login.html";
    }

}
