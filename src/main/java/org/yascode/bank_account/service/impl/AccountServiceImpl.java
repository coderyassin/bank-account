package org.yascode.bank_account.service.impl;

import org.springframework.stereotype.Service;
import org.yascode.bank_account.client.AccountClient;
import org.yascode.bank_account.client.model.AccountResponse;
import org.yascode.bank_account.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountClient accountClient;

    public AccountServiceImpl(AccountClient accountClient) {
        this.accountClient = accountClient;
    }

    @Override
    public AccountResponse getAccount(Long accountId) {
        return accountClient.getAccount(accountId, "token");
    }
}
