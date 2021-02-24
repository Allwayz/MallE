package cn.allwayz.order.expection;

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
public class OrderExceptionHandler {

    @ResponseBody
    @ExceptionHandler({BizException.class})
    public R bizExceptionHandler(BizException e) {

        return R.error(e.getErrorCode(), e.getErrorMsg());
    }

    @ExceptionHandler(RuntimeException.class)
    public String runtimeExceptionHandler(RuntimeException e) {
        log.error("Expection：{}", e.getMessage());
        // 重定向到购物车列表
        return "redirect:http://cart.malle.com/cart/list.html";
    }
}
