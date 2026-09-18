package BankSdNd.example.BsDnD.adapter.in.cli;

import BankSdNd.example.BsDnD.adapter.in.cli.support.PersonInputCollector;
import BankSdNd.example.BsDnD.adapter.in.cli.ui.ConsoleUI;
import BankSdNd.example.BsDnD.adapter.in.cli.util.InputUtils;
import BankSdNd.example.BsDnD.adapter.in.cli.util.PasswordUtils;
import BankSdNd.example.BsDnD.core.domain.exception.DuplicateException;
import BankSdNd.example.BsDnD.core.domain.exception.InvalidPasswordException;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.port.in.AuthenticateUserUseCase;
import BankSdNd.example.BsDnD.core.port.in.CreatePersonUseCase;
import BankSdNd.example.BsDnD.core.port.in.GetAccountUseCase;
import BankSdNd.example.BsDnD.core.port.in.dto.CreatePersonCommand;
import BankSdNd.example.BsDnD.core.port.in.dto.LoginCommand;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationHandlerTest {

    @Mock
    private AuthenticateUserUseCase authenticateUserUseCase;

    @Mock
    private CreatePersonUseCase createPersonUseCase;

    @Mock
    private GetAccountUseCase getAccountUseCase;

    @Mock
    private InputUtils inputUtils;

    @Mock
    private PersonInputCollector personInputCollector;

    @Mock
    private Scanner scanner;

    @Mock
    private ConsoleUI ui;

    @InjectMocks
    private AuthenticationHandler authenticationHandler;

    private static MockedStatic<PasswordUtils> passwordUtilsMock;

    @BeforeAll
    static void beforeAll() {
        passwordUtilsMock = mockStatic(PasswordUtils.class);
    }

    @AfterAll
    static void afterAll() {
        passwordUtilsMock.close();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should return null when user chooses to go back in showCreate")
    void showCreate_ShouldReturnNullWhenGoBack() {

        when(inputUtils.readInt(any(), any())).thenReturn(9);

        BankUser result = authenticationHandler.showCreate();

        assertNull(result);
        verify(ui).showMenuGoBack();
    }

    @Test
    @DisplayName("Should clear screen on choice 0 in showCreate")
    void showCreate_ShouldClearScreen() {
        int optionClearScreen = 0;
        int optionExitLoop = 9;

        when(inputUtils.readInt(any(), any())).thenReturn(optionClearScreen, optionExitLoop);

        authenticationHandler.showCreate();

        verify(ui).clearScreen();
    }

    @Test
    @DisplayName("Should register successfully inside showCreate")
    void showCreate_ShouldRegisterSuccessfully() {

        int optionRegister = 1;
        String expectedCpf = "123";
        CreatePersonCommand command = new CreatePersonCommand("John", "Doe", expectedCpf, "119", BigDecimal.TEN, "pass", "1234");
        BankUser user = BankUser.builder().cpf(expectedCpf).password("enc").build();

        when(inputUtils.readInt(any(), any())).thenReturn(optionRegister);
        when(personInputCollector.collectUserInput(any())).thenReturn(command);
        when(createPersonUseCase.savePerson(command)).thenReturn(user);

        BankUser result = authenticationHandler.showCreate();

        assertNotNull(result);
        assertEquals(expectedCpf, result.getCpf());
        verify(ui).showUserCreatedSuccessfully();
    }

    @Test
    @DisplayName("Should return null when user cancels login")
    void performLogin_ShouldReturnNullWhenCancelled() {
        String cancelCommand = "sair";
        when(inputUtils.readString(any(), any())).thenReturn(cancelCommand);

        BankUser result = authenticationHandler.performLogin();

        assertNull(result);
        verify(ui).showLoginCancelled();
    }

    @Test
    @DisplayName("Should return null after max failed login attempts")
    void performLogin_ShouldFailAfterMaxAttempts() {

        String inputCpf = "12345678900";
        char[] wrongPassword = {'w','r', 'o', 'n', 'g'};
        int expectedMaxAttempts = 3;

        when(inputUtils.readString(any(), any())).thenReturn(inputCpf);
        passwordUtilsMock.when(() -> PasswordUtils.catchPassword(any())).thenReturn(wrongPassword);
        when(authenticateUserUseCase.login(any(LoginCommand.class))).thenThrow(new InvalidPasswordException("Invalid credentials"));

        BankUser result = authenticationHandler.performLogin();

        assertNull(result);
        verify(ui, times(expectedMaxAttempts)).showDisplayLogin();
        verify(ui).showMaxAttemptsReached();
    }

    @Test
    @DisplayName("Should login successfully on valid credentials")
    void performLogin_ShouldSucceed() {

        String inputCpf = "12345678900";
        char[] correctPassword = {'p', 'a', 's', 's'};
        BankUser user = BankUser.builder().cpf(inputCpf).password("enc").build();

        when(inputUtils.readString(any(), any())).thenReturn(inputCpf);
        passwordUtilsMock.when(() -> PasswordUtils.catchPassword(any())).thenReturn(correctPassword);
        when(authenticateUserUseCase.login(any(LoginCommand.class))).thenReturn(user);

        BankUser result = authenticationHandler.performLogin();

        assertNotNull(result);
        assertEquals(inputCpf, result.getCpf());
        verify(ui).showLoginSuccessfully();
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("registerUser should return null if input collection fails")
    void registerUser_ShouldReturnNullIfInputFails() {

        when(personInputCollector.collectUserInput(any())).thenReturn(null);

        BankUser result = authenticationHandler.registerUser();

        assertNull(result);
        verify(ui).showRegisterError();
    }

    @Test
    @DisplayName("registerUser should handle DuplicateException")
    void registerUser_ShouldHandleDuplicateException() {

        CreatePersonCommand command = new CreatePersonCommand("John", "Doe", "123", "119", BigDecimal.TEN, "pass", "1234");
        String errorMessage = "CPF exists";

        when(personInputCollector.collectUserInput(any())).thenReturn(command);
        when(createPersonUseCase.savePerson(command)).thenThrow(new DuplicateException(errorMessage));

        BankUser result = authenticationHandler.registerUser();

        assertNull(result);
        verify(ui).showValidationError(errorMessage);
    }
}
