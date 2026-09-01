package BankSdNd.example.BsDnD.adapter.in.cli;

import BankSdNd.example.BsDnD.adapter.in.cli.ui.ConsoleUI;
import BankSdNd.example.BsDnD.adapter.in.cli.util.InputUtils;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Scanner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsoleControllerTest {

    @Mock
    private AuthenticationHandler authHandler;

    @Mock
    private UserSessionHandler userSessionHandler;

    @Mock
    private InputUtils inputUtils;

    @Mock
    private Scanner scanner;

    @Mock
    private ConsoleUI ui;

    @InjectMocks
    private ConsoleController consoleController;

    @Test
    @DisplayName("Should process clear screen choice 0 and re-prompt")
    void display_ShouldProcessChoice0AndReprompt() {
        int optionClearScreen = 0;
        int optionExit = 3;

        when(inputUtils.readInt(any(), any())).thenReturn(optionClearScreen, optionExit);

        consoleController.display();

        verify(ui).clearScreen();
    }

    @Test
    @DisplayName("Should process create user choice 1 and start session")
    void display_ShouldProcessChoice1AndStartSession() {
        int optionCreateUser = 1;
        int optionExit = 3;
        BankUser user = new BankUser();
        when(inputUtils.readInt(any(), any())).thenReturn(optionCreateUser, optionExit);
        when(authHandler.showCreate()).thenReturn(user);

        consoleController.display();

        verify(authHandler).showCreate();
        verify(userSessionHandler).runUserSession(user);
    }

    @Test
    @DisplayName("Should process login choice 2 and start session")
    void display_ShouldProcessChoice2AndStartSession() {
        int optionLogin = 2;
        int optionExit = 3;
        BankUser user = new BankUser();
        when(inputUtils.readInt(any(), any())).thenReturn(optionLogin, optionExit);
        when(authHandler.performLogin()).thenReturn(user);

        consoleController.display();

        verify(authHandler).performLogin();
        verify(userSessionHandler).runUserSession(user);
    }

    @Test
    @DisplayName("Should process invalid option default case")
    void display_ShouldProcessInvalidOption() {
        int optionInvalid = 99;
        int optionExit = 3;
        when(inputUtils.readInt(any(), any())).thenReturn(optionInvalid, optionExit);

        consoleController.display();

        verify(ui).showOptionInvalid();
    }

    @Test
    @DisplayName("Should process exit choice 3")
    void display_ShouldProcessChoice3AndExit() {
        int optionExit = 3;
        when(inputUtils.readInt(any(), any())).thenReturn(optionExit);

        consoleController.display();

        verify(ui).showMenuGoBack();
        verify(userSessionHandler, never()).runUserSession(any());
    }
}
