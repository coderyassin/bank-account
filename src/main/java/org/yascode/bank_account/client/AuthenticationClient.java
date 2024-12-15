package org.yascode.bank_account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.yascode.bank_account.client.model.RefreshTokenResponse;
import org.yascode.bank_account.client.request.RefreshTokenRequest;
import org.yascode.bank_account.config.CustomBeanDecoder;

@FeignClient(name = "authentication-service",
        url = "${oauth.base-url}",
        path = "/api/v1/auth",
        configuration = {CustomBeanDecoder.class})
public interface AuthenticationClient {
    @PostMapping("/refresh-token")
    RefreshTokenResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest);
}
