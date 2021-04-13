package cn.allwayz.auth.service;

import cn.allwayz.auth.vo.RegisterVO;

/**
 * @author allwayz
 */
public interface RegisterService {
    /**
     * Register
     * @param registerVO
     * @return
     */
    boolean register(RegisterVO registerVO);
}
