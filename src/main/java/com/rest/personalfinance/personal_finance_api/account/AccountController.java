package com.rest.personalfinance.personal_finance_api.account;

import com.rest.personalfinance.personal_finance_api.account.dto.CreateAccountRequest;
import com.rest.personalfinance.personal_finance_api.account.dto.UpdateAccountPatchRequest;
import com.rest.personalfinance.personal_finance_api.account.dto.UpdateAccountRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateAccountRequest request) {
        Account account = accountService.create(request);

        URI location = URI.create("/api/v1/accounts/" + account.getId());

        return ResponseEntity
                .created(location)
                .body(account);
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getById(@PathVariable Long id) {
        return accountService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> update(@PathVariable Long id, @RequestBody UpdateAccountRequest request) {

        return accountService.update(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Account> partialUpdate(@PathVariable Long id, @RequestBody UpdateAccountPatchRequest request) {

        return accountService.partialUpdate(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        if (accountService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
