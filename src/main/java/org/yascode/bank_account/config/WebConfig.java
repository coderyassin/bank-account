package org.yascode.bank_account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.yascode.bank_account.interceptor.JwtCookieInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final JwtCookieInterceptor jwtCookieInterceptor;

    public WebConfig(@Lazy JwtCookieInterceptor jwtCookieInterceptor) {
        this.jwtCookieInterceptor = jwtCookieInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtCookieInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/v1/auth/**");
    }
}
