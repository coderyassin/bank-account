package org.yascode.bank_account.security.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.yascode.bank_account.security.exception.JwtAuthenticationException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    @Value("${application.security.jwt.algorithm}")
    private String algorithm;
    @Value("${application.security.jwt.secret_key}")
    private String secretKey;

    public Authentication validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            List<GrantedAuthority> authorities = extractAuthorities(claims);

            return new UsernamePasswordAuthenticationToken(username, null, authorities);
        } catch (Exception e) {
            throw new JwtAuthenticationException("Token invalid", e);
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    private List<GrantedAuthority> extractAuthorities(Claims claims) {
        List<String> roles = (List<String>) claims.get("authorities");

        return roles == null ? Collections.emptyList() :
                roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
    }
}
