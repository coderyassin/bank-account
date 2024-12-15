package org.yascode.bank_account.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.yascode.bank_account.security.JwtHelper;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class JwtCookieInterceptor implements HandlerInterceptor {
    private static final String JWT_COOKIE_NAME = "jwt_cookie";
    private static final String OAUTH_LOGIN_URL = "http://localhost:6689/login";
    private final JwtHelper jwtHelper;

    public JwtCookieInterceptor(JwtHelper jwtHelper) {
        this.jwtHelper = jwtHelper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        Optional<String> tokenOptional = findJwtCookie(request);

        if (tokenOptional.isEmpty()) {
            redirectToLogin(request, response);
            return false;
        }

        String token = tokenOptional.get();

        try {
            if (jwtHelper.isTokenValid(token)) {
                removeJwtCookie(response);
                redirectToLogin(request, response);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Token validation error", e);
            removeJwtCookie(response);
            redirectToLogin(request, response);
            return false;
        }
    }

    private Optional<String> findJwtCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();

        return Arrays.stream(cookies)
                .filter(cookie -> JWT_COOKIE_NAME.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue);
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirectUri = URLEncoder.encode(request.getRequestURL().toString(), StandardCharsets.UTF_8.name());
        String loginUrl = OAUTH_LOGIN_URL + "?redirect_uri=" + redirectUri;
        response.sendRedirect(loginUrl);
    }

    private static void removeJwtCookie(HttpServletResponse response) {
        Objects.requireNonNull(response, "HttpServletResponse cannot be null");

        Cookie invalidCookie = new Cookie(JWT_COOKIE_NAME, null);
        invalidCookie.setPath("/");
        invalidCookie.setMaxAge(0);
        invalidCookie.setHttpOnly(true);
        invalidCookie.setSecure(true);

        response.addCookie(invalidCookie);
    }
}
