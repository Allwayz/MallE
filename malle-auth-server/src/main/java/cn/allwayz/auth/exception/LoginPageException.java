package cn.allwayz.auth.exception;

import cn.allwayz.common.exception.BizException;
import cn.allwayz.common.exception.CommonError;

/**
 * @author allwayz
 */
public class LoginPageException extends BizException {

    public LoginPageException() {
    }

    public LoginPageException(CommonError commonError) {
        super(commonError);
    }

    public LoginPageException(CommonError commonError, String msg) {
        super(commonError, msg);
    }

    public LoginPageException(int code, String msg) {
        super(code, msg);
    }
}
