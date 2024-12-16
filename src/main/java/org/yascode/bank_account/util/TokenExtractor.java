package org.yascode.bank_account.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class TokenExtractor {
    private static final String JWT_COOKIE_NAME = "jwt_cookie";

    public String retrieveAccessToken(HttpServletRequest request, String tokenName) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> tokenName.equals(cookie.getName()))
                        .findFirst()
                        .map(cookie -> "Bearer " + cookie.getValue())
                )
                .orElse(null);
    }

    public Optional<String> findTokenInCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();

        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue);
    }
}
