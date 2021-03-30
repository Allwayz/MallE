package cn.allwayz.auth.service;

import cn.allwayz.auth.vo.RegisterVO;

/**
 * @author allwayz
 */
public interface RegisterService {
    /**
     * 用户注册
     * @param registerVO
     * @return
     */
    boolean register(RegisterVO registerVO);
}
