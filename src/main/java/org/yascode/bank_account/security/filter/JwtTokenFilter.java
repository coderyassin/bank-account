package org.yascode.bank_account.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.yascode.bank_account.security.exception.JwtAuthenticationException;
import org.yascode.bank_account.security.provider.JwtTokenProvider;
import org.yascode.bank_account.util.TokenExtractor;

import java.io.IOException;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenExtractor tokenExtractor;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider,
                          TokenExtractor tokenExtractor) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenExtractor = tokenExtractor;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        try {
            if (token != null) {
                Authentication auth = jwtTokenProvider.validateToken(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (JwtAuthenticationException e) {
            SecurityContextHolder.clearContext();
            response.sendError(SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("access_token");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else {
            Optional<String> jwtCookieOptional = tokenExtractor.findTokenInCookie(req, "jwt_cookie");
            if (jwtCookieOptional.isPresent()) {
                return jwtCookieOptional.get();
            }
        }
        return null;
    }
}
