package cn.allwayz.cart.interceptor;

import cn.allwayz.common.constant.AuthServerConstant;
import cn.allwayz.common.constant.CartConstant;
import cn.allwayz.common.vo.MemberInfoVO;
import cn.allwayz.cart.vo.UserLoginStatusTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author allwayz
 */
public class CartInterceptor implements HandlerInterceptor {

    public static final ThreadLocal<UserLoginStatusTO> threadLocal = new ThreadLocal<>();

    /**
     * Determine the user's login status before the target method executes
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserLoginStatusTO loginStatusTO = new UserLoginStatusTO();
        // Determine whether the user is logged in
        MemberInfoVO loginUser = (MemberInfoVO) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER_KEY);
        // Lggined
        if (loginUser != null) {
            // Record its login status, ID as identification
            loginStatusTO.setId(loginUser.getId());
        }
        // don't care if you're not logged in, because casual users can do that
        // Check if the request cookie has a user-key, that is, whether the user has already obtained the user-key
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(CartConstant.COOKIE_TEMP_USER_KEY)) {
                    // record user-key
                    loginStatusTO.setUserKey(cookie.getValue());
                }
            }
        }
        // If the user is neither logged in nor has a User-key, it must be assigned to ensure that the business can be handled normally
        if (StringUtils.isEmpty(loginStatusTO.getUserKey())) {
            // Assign the user - key
            String key = UUID.randomUUID().toString().replace("-", "");
            loginStatusTO.setUserKey(key);
            // Mark it as his first visit
            loginStatusTO.setFirstVisit(true);
        }

        // Store the user login status in a ThreadLocal
        threadLocal.set(loginStatusTO);
        return true;
    }

    /**
     * At the end of the service, if the user is "visiting for the first time", the system will generate a temporary key as its identity, and all subsequent operations will be based on this temporary key
     * In this case, be sure to tell the browser this key when you return, and let it be stored in a cookie, which will be automatically carried by the browser when you revisit it
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserLoginStatusTO userLoginStatusTO = threadLocal.get();
        // The system assigns a user-key to the user who is visiting for the first time and tells the browser to save it
        if (userLoginStatusTO.isFirstVisit()) {
            // User-key assigned by the system
            String key = userLoginStatusTO.getUserKey();
            // Command browser to save, the next visit will be automatically carried
            Cookie cookie = new Cookie(CartConstant.COOKIE_TEMP_USER_KEY, key);
            // Set expiration date
            cookie.setMaxAge(CartConstant.COOKIE_TEMP_USER_KEY_TIMEOUT);
            // Set the scope. This key is only used for the shopping cart service, so the default is the current scope
            // cookie.setDomain();
            // Tell the browser to save
            response.addCookie(cookie);
        }
    }
}
