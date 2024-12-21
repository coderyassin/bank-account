package org.yascode.bank_account.controller.api;

import org.springframework.graphql.data.method.annotation.Argument;
import org.yascode.bank_account.controller.response.BankResponse;

import java.util.List;

public interface BankApi {
    List<BankResponse> banks();
    BankResponse bankById(@Argument String id);
    BankResponse addBank(@Argument String name, @Argument String address);
}
