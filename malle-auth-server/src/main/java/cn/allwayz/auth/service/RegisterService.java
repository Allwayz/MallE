package cn.allwayz.auth.service;

import cn.allwayz.auth.vo.RegisterVO;

/**
 * @author allwayz
 */
public interface RegisterService {
    /**
     * 发送验证码
     * @param phone
     */
    @Deprecated
    void sendCode(String phone);

    /**
     * 用户注册
     * @param registerVO
     * @return
     */
    boolean register(RegisterVO registerVO);
}
