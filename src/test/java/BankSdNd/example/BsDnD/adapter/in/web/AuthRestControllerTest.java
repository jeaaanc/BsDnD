package BankSdNd.example.BsDnD.adapter.in.web;

import BankSdNd.example.BsDnD.adapter.in.web.dto.LoginRequest;
import BankSdNd.example.BsDnD.adapter.in.web.handler.GlobalExceptionHandler;
import BankSdNd.example.BsDnD.adapter.out.security.TokenService;
import BankSdNd.example.BsDnD.core.domain.exception.UserNotFoundException;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.port.in.AuthenticateUserUseCase;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticateUserUseCase authenticateUserUseCase;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthRestController authRestController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String LOGIN_URL = "/api/auth/login";
    private final String VALID_CPF = "32545068047";
    private final String VALID_PASSWORD = "123456";
    private final String MOCK_TOKEN = "mock-token-xyz";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authRestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should return 200 OK and token on successful login")
    void shouldLoginSuccessfully() throws Exception {

        LoginRequest request = new LoginRequest(VALID_CPF, VALID_PASSWORD);
        BankUser user = BankUser.builder().cpf(VALID_CPF).build();

        when(authenticateUserUseCase.login(any())).thenReturn(user);
        when(tokenService.generateToken(user)).thenReturn(MOCK_TOKEN);

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(MOCK_TOKEN));
    }

    @Test
    @DisplayName("Should return 404 Not Found when user is not found")
    void shouldReturnNotFoundWhenUserNotFound() throws Exception {

        LoginRequest request = new LoginRequest(VALID_CPF, VALID_PASSWORD);
        String errorMessage = "error.user_not_found";

        when(authenticateUserUseCase.login(any())).thenThrow(new UserNotFoundException(errorMessage));

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(errorMessage));
    }

    @Test
    @DisplayName("Should return 400 Bad Request on validation failure")
    void shouldReturnBadRequestOnValidationFailure() throws Exception {

        LoginRequest request = new LoginRequest("", ""); // Blank CPF and password

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("$$-Validation Error"))
                .andExpect(jsonPath("$.cpf").exists())
                .andExpect(jsonPath("$.password").exists());
    }
}
