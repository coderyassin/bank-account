package org.yascode.bank_account.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface AccountApi {
    @GetMapping(value = "/{accountId}")
    ResponseEntity<?> getAccount(@PathVariable Long accountId, HttpServletRequest request);
}
