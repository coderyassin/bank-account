package org.yascode.bank_account.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class JwtCookieInterceptor implements HandlerInterceptor {
    private static final String JWT_COOKIE_NAME = "jwt_cookie";
    private static final String OAUTH_LOGIN_URL = "http://localhost:6689/login";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JWT_COOKIE_NAME.equals(cookie.getName())) {
                    return true;
                }
            }
        }

        String redirectUri = URLEncoder.encode(request.getRequestURL().toString(), StandardCharsets.UTF_8.name());
        String loginUrl = OAUTH_LOGIN_URL + "?redirect_uri=" + redirectUri;
        response.sendRedirect(loginUrl);
        return false;
    }
}
