package org.yascode.bank_account.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yascode.bank_account.client.AccountClient;
import org.yascode.bank_account.client.model.AccountResponse;
import org.yascode.bank_account.service.AccountService;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountClient accountClient;

    public AccountServiceImpl(AccountClient accountClient) {
        this.accountClient = accountClient;
    }

    @Override
    public AccountResponse getAccount(Long accountId) {
        try {
            return accountClient.getAccount(accountId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
