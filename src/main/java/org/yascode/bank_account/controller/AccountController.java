package org.yascode.bank_account.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yascode.bank_account.controller.api.AccountApi;
import org.yascode.bank_account.service.AccountService;
import org.yascode.bank_account.util.TokenExtractor;

@RestController
@RequestMapping(value = "/api/v1/accounts")
@Slf4j
public class AccountController implements AccountApi {
    private final AccountService accountService;
    private final TokenExtractor tokenExtractor;

    public AccountController(AccountService accountService,
                             TokenExtractor tokenExtractor) {
        this.accountService = accountService;
        this.tokenExtractor = tokenExtractor;
    }

    @Override
    public ResponseEntity<?> getAccount(Long accountId, HttpServletRequest request) {
        return ResponseEntity.ok(accountService.getAccount(accountId, tokenExtractor.retrieveAccessToken(request)));
    }
}
