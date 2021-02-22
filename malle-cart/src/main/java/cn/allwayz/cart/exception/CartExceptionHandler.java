package cn.allwayz.cart.exception;

import cn.allwayz.common.exception.BizCodeEnum;
import cn.allwayz.common.exception.BizException;
import cn.allwayz.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author allwayz
 */
@Slf4j
@ControllerAdvice
public class CartExceptionHandler {

    /**
     * 业务异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler({BizException.class})
    public R bizException(BizException e) {
        // 其他业务异常则直接返回json数据
        return R.error(e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 运行期异常
     * @param e
     * @return
     */
    @ExceptionHandler(Throwable.class)
    public R throwableExceptionHandler(Throwable e) {
        log.error("RunTimeException：{}", e);
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(), "Unknow Execption");
    }

}

