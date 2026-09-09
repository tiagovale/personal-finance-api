package com.rest.personalfinance.personal_finance_api.account;

import com.rest.personalfinance.personal_finance_api.account.dto.CreateAccountRequest;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService (AccountRepository accountRepository) {
        this.accountRepository= accountRepository;
    }

    public Account create(CreateAccountRequest request) {

        Account account = new Account();

        account.setName(request.name());
        account.setType(request.type());
        account.setBalance(request.balance());

        return accountRepository.save(account);
    }
}
