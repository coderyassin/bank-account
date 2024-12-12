package org.yascode.bank_account.service;

import org.yascode.bank_account.client.model.AccountResponse;

public interface AccountService {
    AccountResponse getAccount(Long accountId);
}
