package org.yascode.bank_account.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.yascode.bank_account.util.TokenExtractor;

import java.util.Objects;

@Component
public class AccountRequestInterceptor implements RequestInterceptor {
    private final TokenExtractor tokenExtractor;

    public AccountRequestInterceptor(TokenExtractor tokenExtractor) {
        this.tokenExtractor = tokenExtractor;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request != null) {
            String token = request.getHeader("access_token");
            token = Objects.isNull(token) ? tokenExtractor.retrieveAccessToken(request, "jwt_cookie") : token;
            if (token != null) {
                requestTemplate.header("Authorization", token);
            }
        }
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
