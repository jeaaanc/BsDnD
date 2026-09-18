package BankSdNd.example.BsDnD.adapter.in.web;

import BankSdNd.example.BsDnD.adapter.in.web.dto.TransferRequest;
import BankSdNd.example.BsDnD.adapter.in.web.handler.GlobalExceptionHandler;
import BankSdNd.example.BsDnD.core.domain.exception.AccountNotFoundException;
import BankSdNd.example.BsDnD.core.port.in.TransferMoneyUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransferRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransferMoneyUseCase transferMoneyUseCase;

    @InjectMocks
    private TransferRestController transferRestController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String BASE_URL = "/api/transfers";
    private final String SOURCE_ACCOUNT = "12345-6";
    private final String TARGET_ACCOUNT = "65432-1";
    private final BigDecimal VALID_AMOUNT = new BigDecimal("100");
    private final String VALID_PASSWORD = "1234";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transferRestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should return 200 OK on successful transfer")
    void shouldTransferSuccessfully() throws Exception {

        TransferRequest request = new TransferRequest(SOURCE_ACCOUNT, TARGET_ACCOUNT,VALID_AMOUNT, VALID_PASSWORD);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transfer completed successfully!"));

        verify(transferMoneyUseCase).transfer(eq(SOURCE_ACCOUNT), eq(TARGET_ACCOUNT), eq(VALID_AMOUNT), eq(VALID_PASSWORD));
    }

    @Test
    @DisplayName("Should return 400 Bad Request on validation failure")
    void shouldReturnBadRequestOnValidationFailure() throws Exception {

        TransferRequest request = new TransferRequest("", TARGET_ACCOUNT, new BigDecimal("-10"), "");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("$$-Validation Error"))
                .andExpect(jsonPath("$.sourceAccountNumber").exists())
                .andExpect(jsonPath("$.amount").exists())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    @DisplayName("Should return 404 Not Found when account does not exist")
    void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {

        TransferRequest request = new TransferRequest(SOURCE_ACCOUNT, TARGET_ACCOUNT, VALID_AMOUNT, VALID_PASSWORD);
        String errorMessage = "error.account_not_found";

        doThrow(new AccountNotFoundException(errorMessage))
                .when(transferMoneyUseCase).transfer(any(), any(), any(), any(String.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(errorMessage));
    }
}
