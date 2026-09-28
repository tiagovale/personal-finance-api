package com.rest.personalfinance.personal_finance_api.account;

import com.rest.personalfinance.personal_finance_api.account.dto.AccountResponse;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountModelAssembler
        implements RepresentationModelAssembler<Account, AccountResponse> {

    @Override
    public AccountResponse toModel(Account account) {

        AccountResponse response = new AccountResponse(
                account.getId(),
                account.getName(),
                account.getType(),
                account.getBalance()
        );

        response.add(
                linkTo(
                        methodOn(AccountController.class)
                                .getById(account.getId())
                ).withSelfRel()
        );

        response.add(
                linkTo(
                        methodOn(AccountController.class)
                                .getAll()
                ).withRel("all-accounts")
        );

        return response;
    }
}