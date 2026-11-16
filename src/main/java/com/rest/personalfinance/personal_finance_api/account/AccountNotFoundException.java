package com.rest.personalfinance.personal_finance_api.account;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(Long id) {
        super("Account with id " + id + " was not found");
    }
}