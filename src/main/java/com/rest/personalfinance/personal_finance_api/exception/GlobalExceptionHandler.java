package com.rest.personalfinance.personal_finance_api.exception;

import com.rest.personalfinance.personal_finance_api.account.AccountNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ProblemDetail handleAccountNotFound(
            AccountNotFoundException ex) {

        ProblemDetail problem = ProblemDetail.forStatus(
                HttpStatus.NOT_FOUND);

        problem.setType(
                URI.create("https://api.example.com/problems/account-not-found")
        );
        problem.setTitle("Account not found");
        problem.setDetail(ex.getMessage());

        return problem;
    }
}