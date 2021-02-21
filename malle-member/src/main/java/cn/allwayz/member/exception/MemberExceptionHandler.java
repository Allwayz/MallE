package cn.allwayz.member.exception;

import cn.allwayz.common.exception.BizCodeEnum;
import cn.allwayz.common.exception.BizException;
import cn.allwayz.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author allwayz
 */
@Slf4j
@RestControllerAdvice
public class MemberExceptionHandler {

    @ExceptionHandler({BizException.class})
    public R bizException(BizException e) {
        return R.error(e.getErrorCode(), e.getErrorMsg());
    }

    @ExceptionHandler(Throwable.class)
    public R throwableExceptionHandler(Throwable e) {
        log.error("运行期异常：{}", e);
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(), BizCodeEnum.UNKNOW_EXCEPTION.getMsg());
    }
}
