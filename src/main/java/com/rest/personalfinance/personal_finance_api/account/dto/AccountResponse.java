package com.rest.personalfinance.personal_finance_api.account.dto;

import com.rest.personalfinance.personal_finance_api.account.AccountType;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

public class AccountResponse extends RepresentationModel<AccountResponse> {

    private Long id;
    private String name;
    private AccountType type;
    private BigDecimal balance;

    public AccountResponse(
            Long id,
            String name,
            AccountType type,
            BigDecimal balance) {

        this.id = id;
        this.name = name;
        this.type = type;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AccountType getType() {
        return type;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}