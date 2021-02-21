package cn.allwayz.auth.exception;

import cn.allwayz.common.exception.BizException;
import cn.allwayz.common.exception.CommonError;

public class RegisterPageException extends BizException {

    public RegisterPageException() {
    }

    public RegisterPageException(CommonError commonError) {
        super(commonError);
    }

    public RegisterPageException(CommonError commonError, String msg) {
        super(commonError, msg);
    }

    public RegisterPageException(int code, String msg) {
        super(code, msg);
    }

}
