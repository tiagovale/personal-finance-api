package com.rest.personalfinance.personal_finance_api.account;

import com.rest.personalfinance.personal_finance_api.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private AccountModelAssembler accountModelAssembler;

    @MockitoBean
    private ETagGenerator eTagGenerator;

    @Test
    void shouldReturnProblemDetailWhenAccountDoesNotExist()
            throws Exception {

        // Arrange
        when(accountService.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title")
                        .value("Account not found"));
    }
}