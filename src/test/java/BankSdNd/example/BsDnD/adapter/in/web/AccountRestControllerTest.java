package BankSdNd.example.BsDnD.adapter.in.web;

import BankSdNd.example.BsDnD.adapter.in.web.dto.AccountCreateRequest;
import BankSdNd.example.BsDnD.adapter.in.web.handler.GlobalExceptionHandler;
import BankSdNd.example.BsDnD.core.domain.exception.BusinessException;
import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.port.in.CreateAccountUseCase;
import BankSdNd.example.BsDnD.core.port.in.GetAccountUseCase;
import BankSdNd.example.BsDnD.core.port.in.ManageAccountUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccountRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateAccountUseCase createAccountUseCase;

    @Mock
    private GetAccountUseCase getAccountUseCase;

    @Mock
    private ManageAccountUseCase manageAccountUseCase;

    @InjectMocks
    private AccountRestController accountRestController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Long USER_ID = 1L;
    private final String USER_CPF = "12345678909";
    private final String ACCOUNT_NUMBER = "12345-6";
    private final String VALID_PASSWORD = "1234";
    private final String ACCOUNTS_URL = "/api/accounts";

    private BankUser defaultUser;
    private Account defaultAccount;

    @BeforeEach
    void setUp() {
        defaultUser = new BankUser();
        defaultUser.setId(USER_ID);
        defaultUser.setCpf(USER_CPF);
        defaultUser.setName("John");
        defaultUser.setLastName("Wick");
        defaultUser.setPhoneNumber("11322441000");

        defaultAccount = new Account(ACCOUNT_NUMBER, defaultUser);
        defaultAccount.setId(USER_ID);
        defaultAccount.setBalance(BigDecimal.ZERO);

        mockMvc = MockMvcBuilders.standaloneSetup(accountRestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(BankUser.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return defaultUser;
                    }
                })
                .build();
    }

    @Test
    @DisplayName("Should create account successfully and return 200 OK")
    void shouldCreateAccountSuccessfully() throws Exception {

        AccountCreateRequest request = new AccountCreateRequest(VALID_PASSWORD);
        when(createAccountUseCase.createAccount(eq(USER_CPF), eq(VALID_PASSWORD))).thenReturn(defaultAccount);

        mockMvc.perform(post(ACCOUNTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.accountNumber").value(ACCOUNT_NUMBER))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.holder.name").value("John"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when AccountCreateRequest is invalid")
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {

        AccountCreateRequest request = new AccountCreateRequest("12"); // Invalid size

        mockMvc.perform(post(ACCOUNTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("$$-Validation Error"))
                .andExpect(jsonPath("$.transactionPassword").exists());
    }

    @Test
    @DisplayName("Should return 422 Unprocessable Entity on BusinessException from GlobalExceptionHandler")
    void shouldReturnUnprocessableEntityWhenBusinessExceptionIsThrown() throws Exception {

        AccountCreateRequest request = new AccountCreateRequest(VALID_PASSWORD);
        String errorMessage = "Invalid CPF";
        when(createAccountUseCase.createAccount(any(String.class), any(String.class)))
                .thenThrow(new BusinessException(errorMessage));

        mockMvc.perform(post(ACCOUNTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Business Rule Violation"))
                .andExpect(jsonPath("$.detail").value(errorMessage));
    }
    
    @Test
    @DisplayName("Should list user accounts successfully")
    void shouldListUserAccountsSuccessfully() throws Exception {

        when(getAccountUseCase.findAllByUserCpf(USER_CPF)).thenReturn(List.of(defaultAccount));

        mockMvc.perform(get(ACCOUNTS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(USER_ID))
                .andExpect(jsonPath("$[0].accountNumber").value(ACCOUNT_NUMBER));
    }
    
    @Test
    @DisplayName("Should soft delete account successfully")
    void shouldSoftDeleteAccountSuccessfully() throws Exception {

        mockMvc.perform(delete(ACCOUNTS_URL + "/1"))
                .andExpect(status().isNoContent());

        verify(manageAccountUseCase).softDeleteAccount(USER_ID);
    }
}
