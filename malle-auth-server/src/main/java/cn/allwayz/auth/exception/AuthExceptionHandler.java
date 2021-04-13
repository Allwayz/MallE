package cn.allwayz.auth.exception;

import cn.allwayz.common.exception.BizException;
import cn.allwayz.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author allwayz
 */
@Slf4j
@ControllerAdvice
public class AuthExceptionHandler {

    /**
     * Registration, the form submission data format verification failed,
     * jump to the registration page again, with a prompt message
     * To prevent duplicate submissions, function use a redirect,
     * because using view resolution, so unable add @responseBody
     * @param e
     * @return
     */
    @ExceptionHandler({BindException.class})
    public String bindingExceptionHandler(BindException e, RedirectAttributes redirectAttributes) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> map = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        redirectAttributes.addFlashAttribute("errMap", map);
        return "redirect:http://auth.malle.com/reg.html";
    }

    /**
     * Encapsulates the error message and returns to the registration page
     * @param e
     * @return
     */
    @ExceptionHandler({RegisterPageException.class})
    public String registerPageException(RegisterPageException e,
                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("regErrMsg", e.getMessage());
        return "redirect:http://auth.malle.com/reg.html";
    }

    /**
     * Encapsulates the error message and returns to the login page
     * @param e
     * @return
     */
    @ExceptionHandler({LoginPageException.class})
    public String loginPageException(LoginPageException e,
                                     RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("loginErrMsg", e.getMessage());
        return "redirect:http://auth.malle.com/login.html";
    }

    /**
     * Business exceptions
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler({BizException.class})
    public R bizException(BizException e) {
        return R.error(e.getErrorCode(), e.getErrorMsg());
    }
}

