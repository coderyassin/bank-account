package org.yascode.bank_account.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yascode.bank_account.controller.api.AuthenticationApi;
import org.yascode.bank_account.controller.payload.TokenRequest;
import org.yascode.bank_account.security.JwtHelper;

@RestController
@RequestMapping(value = "/api/v1/authentication")
@Slf4j
public class AuthenticationController implements AuthenticationApi {
    private final JwtHelper jwtHelper;

    public AuthenticationController(JwtHelper jwtHelper) {
        this.jwtHelper = jwtHelper;
    }

    @Override
    public boolean isTokenValid(TokenRequest tokenRequest) {
        try {
            return jwtHelper.isTokenValid(tokenRequest.token());
        } catch (Exception e) {
            return false;
        }
    }
}
