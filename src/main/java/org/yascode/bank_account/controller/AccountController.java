package org.yascode.bank_account.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yascode.bank_account.controller.api.AccountApi;
import org.yascode.bank_account.service.AccountService;

@RestController
@RequestMapping(value = "/api/v1/accounts")
@Slf4j
public class AccountController implements AccountApi {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<?> getAccount(Long accountId, HttpServletRequest request) {
        return ResponseEntity.ok(accountService.getAccount(accountId));
    }
}
