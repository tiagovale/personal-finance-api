package com.rest.personalfinance.personal_finance_api.account;

import aj.org.objectweb.asm.commons.Remapper;
import com.rest.personalfinance.personal_finance_api.account.dto.CreateAccountRequest;
import com.rest.personalfinance.personal_finance_api.account.dto.UpdateAccountPatchRequest;
import com.rest.personalfinance.personal_finance_api.account.dto.UpdateAccountRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account create(CreateAccountRequest request) {

        Account account = new Account();

        account.setName(request.name());
        account.setType(request.type());
        account.setBalance(request.balance());

        return accountRepository.save(account);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    public Optional<Account> update(Long id, UpdateAccountRequest request) {

        return findById(id).map(account -> {
            account.setBalance(request.balance());
            account.setName(request.name());
            account.setType(request.type());
            return accountRepository.save(account);
        });
    }

    public Optional<Account> partialUpdate(Long id, UpdateAccountPatchRequest request) {

        return findById(id).map(account -> {
            if(request.name().isPresent()){
                account.setName(request.name().orElse(null));
            }
            if(request.balance().isPresent()){
                account.setBalance(request.balance().orElse(null));
            }
            if(request.type().isPresent()){
                account.setType(request.type().orElse(null));
            }

            return accountRepository.save(account);
        });
    }

    public boolean delete(Long id) {

        Optional<Account> account = findById(id);

        if (account.isEmpty()) {
            return false;
        }

        accountRepository.delete(account.get());
        return true;
    }
}
