package com.rest.personalfinance.personal_finance_api.account;

import com.rest.personalfinance.personal_finance_api.account.dto.AccountResponse;
import com.rest.personalfinance.personal_finance_api.account.dto.CreateAccountRequest;
import com.rest.personalfinance.personal_finance_api.account.dto.UpdateAccountPatchRequest;
import com.rest.personalfinance.personal_finance_api.account.dto.UpdateAccountRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;
    private final AccountModelAssembler accountModelAssembler;
    private final ETagGenerator eTagGenerator;


    public AccountController(AccountService accountService, AccountModelAssembler accountModelAssembler, ETagGenerator eTagGenerator) {
        this.accountService = accountService;
        this.accountModelAssembler = accountModelAssembler;
        this.eTagGenerator = eTagGenerator;
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
    public ResponseEntity<CollectionModel<AccountResponse>> getAll() {

        List<AccountResponse> accounts = accountService.findAll()
                .stream()
                .map(accountModelAssembler::toModel)
                .toList();

        CollectionModel<AccountResponse> collection =
                CollectionModel.of(accounts);

        collection.add(
                linkTo(
                        methodOn(AccountController.class)
                                .getAll()
                ).withSelfRel()
        );

        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id,
            @RequestHeader(
                    value = "If-None-Match",
                    required = false
            ) String ifNoneMatch) {

        Optional<Account> accountOptional =
                accountService.findById(id);

        if (accountOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Account account = accountOptional.get();

        AccountResponse response =
                accountModelAssembler.toModel(account);

        String etag = eTagGenerator.generate(response);

        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_MODIFIED)
                    .eTag(etag)
                    .build();
        }

        return ResponseEntity
                .ok()
                .eTag(etag)
                .body(response);
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
