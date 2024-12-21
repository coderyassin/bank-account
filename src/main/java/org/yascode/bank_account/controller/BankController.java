package org.yascode.bank_account.controller;

import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.yascode.bank_account.controller.api.BankApi;
import org.yascode.bank_account.controller.response.BankResponse;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BankController implements BankApi {
    private List<BankResponse> bankResponses =  new ArrayList<>();

    public BankController() {
        bankResponses.add(new BankResponse("1", null, "address_1"));
        bankResponses.add(new BankResponse("2", "Bank_2", "address_2"));
    }

    @Override
    @QueryMapping
    public List<BankResponse> banks() {
        return bankResponses;
    }

    @Override
    @QueryMapping
    public BankResponse bankById(String id) {
        return bankResponses.stream()
                .filter(bank -> bank.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    @MutationMapping
    public BankResponse addBank(String name, String address) {
        String id = String.valueOf(bankResponses.size() + 1);
        BankResponse bank = new BankResponse(id, name, address);
        bankResponses.add(bank);
        return bank;
    }
}
