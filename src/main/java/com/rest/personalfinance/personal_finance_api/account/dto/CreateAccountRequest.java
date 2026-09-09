package com.rest.personalfinance.personal_finance_api.account.dto;

import com.rest.personalfinance.personal_finance_api.account.AccountType;

import java.math.BigDecimal;

public record CreateAccountRequest(
        String name,
        AccountType type,
        BigDecimal balance
) {
}
