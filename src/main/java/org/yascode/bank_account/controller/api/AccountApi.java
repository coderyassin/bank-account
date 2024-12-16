package org.yascode.bank_account.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface AccountApi {
    @GetMapping(value = "/{accountId}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE')")
    ResponseEntity<?> getAccount(@PathVariable Long accountId, HttpServletRequest request);
}
