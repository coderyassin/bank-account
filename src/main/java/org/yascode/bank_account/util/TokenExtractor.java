package org.yascode.bank_account.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class TokenExtractor {
    private static final String JWT_COOKIE_NAME = "jwt_cookie";

    public String retrieveAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> JWT_COOKIE_NAME.equals(cookie.getName()))
                        .findFirst()
                        .map(cookie -> "Bearer " + cookie.getValue())
                )
                .orElse(null);
    }
}
