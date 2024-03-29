package cn.allwayz.common.utils;

import cn.allwayz.common.exception.BizCodeEnum;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResultBody<T> {
    private static final long serialVersionUID = 1L;
    private int code;
    private String msg;
    private T data;

    public ResultBody() {
        this.code = 0;
        this.msg = "success";
    }

    public ResultBody(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public ResultBody( T data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }
    public ResultBody(BizCodeEnum e, T data) {
        this.code = e.getCode();
        this.msg = e.getMsg();
        this.data = data;
    }

    public ResultBody(BizCodeEnum e) {
        this.code = e.getCode();
        this.msg = e.getMsg();
    }


}
