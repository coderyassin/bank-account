package org.yascode.bank_account.controller.api;

import org.springframework.web.bind.annotation.RequestBody;
import org.yascode.bank_account.controller.payload.TokenRequest;

public interface AuthenticationApi {
    boolean isTokenValid(@RequestBody TokenRequest tokenRequest);
}
