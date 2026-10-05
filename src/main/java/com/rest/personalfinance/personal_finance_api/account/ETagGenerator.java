package com.rest.personalfinance.personal_finance_api.account;

import com.rest.personalfinance.personal_finance_api.account.dto.AccountResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Component
public class ETagGenerator {

    public String generate(AccountResponse response) {

        String content = response.getId()
                + response.getName()
                + response.getType()
                + response.getBalance();

        return "\"" +
                DigestUtils.md5DigestAsHex(
                        content.getBytes(StandardCharsets.UTF_8)
                ) +
                "\"";
    }
}