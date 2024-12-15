package org.yascode.bank_account.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.yascode.bank_account.client.AuthenticationClient;
import org.yascode.bank_account.client.model.RefreshTokenResponse;
import org.yascode.bank_account.client.request.RefreshTokenRequest;
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
    private static final String REFRESH_TOKEN = "refresh_token";
    @Value("${oauth.base-url}")
    private String OAUTH_LOGIN_URL;
    @Value("${application.security.jwt.expiration}")
    private int accessTokenExpiration;
    @Value("${application.security.jwt.refresh_token.expiration}")
    private int refreshTokenExpiration;
    private final JwtHelper jwtHelper;
    private final AuthenticationClient authenticationClient;

    public JwtCookieInterceptor(JwtHelper jwtHelper,
                                AuthenticationClient authenticationClient) {
        this.jwtHelper = jwtHelper;
        this.authenticationClient = authenticationClient;
    }

    /*@Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        Optional<String> tokenOptional = findCookie(request, JWT_COOKIE_NAME);

        if (tokenOptional.isEmpty()) {
            Optional<String> refreshTokenOptional = findCookie(request, REFRESH_TOKEN);
            if (refreshTokenOptional.isPresent()) {
                RefreshTokenResponse refreshTokenResponse = refreshToken(refreshTokenOptional.get());
                if (Objects.nonNull(refreshTokenResponse.getRefreshToken())) {
                    addAccessTokenToCookie(response,
                            JWT_COOKIE_NAME,
                            refreshTokenResponse.getAccessToken(),
                            accessTokenExpiration);
                    addAccessTokenToCookie(response,
                            REFRESH_TOKEN,
                            refreshTokenResponse.getRefreshToken(),
                            refreshTokenExpiration);
                    tokenOptional = Optional.of(refreshTokenResponse.getAccessToken());
                }
            } else {
                redirectToLogin(request, response);
                return false;
            }
        }

        String token = tokenOptional.get();

        try {
            if (jwtHelper.isTokenValid(token)) {
                Optional<String> refreshTokenOptional = findCookie(request, REFRESH_TOKEN);
                if (refreshTokenOptional.isPresent()) {
                    RefreshTokenResponse refreshTokenResponse = refreshToken(refreshTokenOptional.get());
                    if (Objects.nonNull(refreshTokenResponse.getRefreshToken())) {
                        addAccessTokenToCookie(response,
                                JWT_COOKIE_NAME,
                                refreshTokenResponse.getAccessToken(),
                                accessTokenExpiration);
                        addAccessTokenToCookie(response,
                                REFRESH_TOKEN,
                                refreshTokenResponse.getRefreshToken(),
                                refreshTokenExpiration);
                    }
                } else {
                    removeJwtCookie(response);
                    redirectToLogin(request, response);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Token validation error", e);
            removeJwtCookie(response);
            redirectToLogin(request, response);
            return false;
        }
    }*/

    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        try {
            Optional<String> tokenOptional = findCookie(request, JWT_COOKIE_NAME);
            Optional<String> refreshTokenOptional = findCookie(request, REFRESH_TOKEN);

            if (tokenOptional.isEmpty()) {
                if (refreshTokenOptional.isPresent()) {
                    return handleTokenRefresh(request, response, refreshTokenOptional.get());
                }

                redirectToLogin(request, response);
                return false;
            }

            String token = tokenOptional.get();

            if (!jwtHelper.isTokenValid(token)) {
                if (refreshTokenOptional.isPresent()) {
                    return handleTokenRefresh(request, response, refreshTokenOptional.get());
                }

                removeJwtCookie(response);
                redirectToLogin(request, response);
                return false;
            }

            return handlePreemptiveRefresh(request, response, refreshTokenOptional);

        } catch (Exception e) {
            log.error("Token validation error", e);
            removeJwtCookie(response);
            redirectToLogin(request, response);
            return false;
        }
    }

    private boolean handleTokenRefresh(HttpServletRequest request,
                                       HttpServletResponse response,
                                       String refreshToken) throws Exception {
        RefreshTokenResponse refreshTokenResponse = refreshToken(refreshToken);

        if (Objects.isNull(refreshTokenResponse.getRefreshToken())) {
            redirectToLogin(request, response);
            return false;
        }

        addAccessTokenToCookie(response,
                JWT_COOKIE_NAME,
                refreshTokenResponse.getAccessToken(),
                accessTokenExpiration);
        addAccessTokenToCookie(response,
                REFRESH_TOKEN,
                refreshTokenResponse.getRefreshToken(),
                refreshTokenExpiration);

        return true;
    }

    private boolean handlePreemptiveRefresh(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Optional<String> refreshTokenOptional) throws Exception {
        if (refreshTokenOptional.isPresent()) {
            RefreshTokenResponse refreshTokenResponse = refreshToken(refreshTokenOptional.get());
            if (Objects.nonNull(refreshTokenResponse.getRefreshToken())) {
                addAccessTokenToCookie(response,
                        JWT_COOKIE_NAME,
                        refreshTokenResponse.getAccessToken(),
                        accessTokenExpiration);
                addAccessTokenToCookie(response,
                        REFRESH_TOKEN,
                        refreshTokenResponse.getRefreshToken(),
                        refreshTokenExpiration);
            }
        }

        return true;
    }

    private void addAccessTokenToCookie(HttpServletResponse response,
                                                String cookieName,
                                                String cookieValue,
                                                int maxAge) {
        Cookie jwtCookie = new Cookie(cookieName, cookieValue);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setMaxAge(maxAge);
        response.addCookie(jwtCookie);
    }

    private Optional<String> findCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();

        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue);
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirectUri = URLEncoder.encode(request.getRequestURL().toString(), StandardCharsets.UTF_8.name());
        String loginUrl = OAUTH_LOGIN_URL + "?redirect_uri=" + redirectUri;
        response.sendRedirect(loginUrl);
    }

    private RefreshTokenResponse refreshToken(String refreshToken) {
        RefreshTokenResponse refreshTokenResponse = null;
        try {
            RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(refreshToken);
            refreshTokenResponse = authenticationClient.refreshToken(refreshTokenRequest);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            return refreshTokenResponse;
        }
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
