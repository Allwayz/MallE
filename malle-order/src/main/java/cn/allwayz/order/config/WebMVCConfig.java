package cn.allwayz.order.config;

import cn.allwayz.order.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author allwayz
 */
@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    /**
     * Add an interceptor
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有请求
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**");
    }
}
