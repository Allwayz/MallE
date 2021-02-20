package cn.allwayz.common.exception;

import lombok.Data;

@Data
public class BizException extends RuntimeException {

    private int errorCode;

    private String errorMsg;

    public BizException() {
        super();
    }

    public BizException(CommonError commonError) {
        super(commonError.getErrorMsg());
        this.errorCode = commonError.getErrorCode();
        this.errorMsg = commonError.getErrorMsg();
    }

    public BizException(CommonError commonError, String msg) {
        super(msg);
        this.errorCode = commonError.getErrorCode();
        this.errorMsg = msg;
    }

    public BizException(int code, String msg) {
        super(msg);
        this.errorCode = code;
        this.errorMsg = msg;
    }
}
