package cn.allwayz.order.interceptor;

import cn.allwayz.common.constant.AuthServerConstant;
import cn.allwayz.common.vo.MemberInfoVO;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author allwayz
 */
public class LoginInterceptor implements HandlerInterceptor {

    public static final ThreadLocal<MemberInfoVO> threadLocal = new ThreadLocal<>();

    /**
     * 目标资源访问前拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        // Some remote calls do not require a login and simply let go
        boolean match = new AntPathMatcher().match("/api/**", uri);
        // Alipay asynchronous notification does not need to intercept
        boolean match1 = new AntPathMatcher().match("/alipay/notify", uri);
        if (match || match1) {
            return true;
        }
        HttpSession session = request.getSession();
        MemberInfoVO attribute = (MemberInfoVO) session.getAttribute(AuthServerConstant.LOGIN_USER_KEY);
        if (attribute == null) {
            // User not logged in
            response.sendRedirect("http://auth.malle.com/login.html");
            return false;
        } else {
            // Log in and share data with ThreadLocal
            threadLocal.set(attribute);
            return true;
        }
    }
}
