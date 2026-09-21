package com.rest.personalfinance.personal_finance_api.account.dto;

import com.rest.personalfinance.personal_finance_api.account.AccountType;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;

public record UpdateAccountPatchRequest(
        JsonNullable<String> name,
        JsonNullable<AccountType> type,
        JsonNullable<BigDecimal> balance
) {
}
