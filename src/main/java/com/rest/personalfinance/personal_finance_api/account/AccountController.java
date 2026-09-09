package com.rest.personalfinance.personal_finance_api.account;

import com.rest.personalfinance.personal_finance_api.account.dto.CreateAccountRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    public  AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateAccountRequest request){
        Account account = accountService.create(request);

        URI location = URI.create("/api/v1/accounts/" + account.getId());

        return ResponseEntity
                .created(location)
                .body(account);
    }
}
