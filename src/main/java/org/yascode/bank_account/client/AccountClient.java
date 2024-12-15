package org.yascode.bank_account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.yascode.bank_account.client.model.AccountResponse;
import org.yascode.bank_account.config.CustomBeanDecoder;
import org.yascode.bank_account.interceptor.AccountRequestInterceptor;

@FeignClient(name = "account-service",
        url = "${oauth.base-url}",
        path = "/api/v1/accounts",
        configuration = {CustomBeanDecoder.class, AccountRequestInterceptor.class})
public interface AccountClient {
    @GetMapping("/{accountId}")
    AccountResponse getAccount(@PathVariable Long accountId, @RequestHeader("Authorization") String customHeader);

    @GetMapping("/{accountId}")
    AccountResponse getAccount(@PathVariable Long accountId);
}
