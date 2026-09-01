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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSessionHandlerTest {

    @Mock
    private AccountOperationHandler accountOperationHandler;

    @Mock
    private UserProfileHandler userProfileHandler;

    @Mock
    private LoanHandler loanHandler;

    @Mock
    private InputUtils inputUtils;

    @Mock
    private Scanner scanner;

    @Mock
    private ConsoleUI ui;

    @InjectMocks
    private UserSessionHandler userSessionHandler;

    private final BankUser defaultUser = new BankUser();

    private final int OPTION_CLEAR_SCREEN = 0;
    private final int OPTION_CREATE_ACCOUNT = 1;
    private final int OPTION_VIEW_BALANCE = 2;
    private final int OPTION_TRANSFER = 3;
    private final int OPTION_LOAN = 4;
    private final int OPTION_PROFILE = 5;
    private final int OPTION_DELETE_ACCOUNT = 6;
    private final int OPTION_EXIT = 9;
    private final int OPTION_INVALID = 99;

    @Test
    @DisplayName("Should process create account choice 1 and then exit")
    void shouldProcessCreateAccountChoice() {

        when(inputUtils.readInt(any(Scanner.class), any())).thenReturn(OPTION_CREATE_ACCOUNT, OPTION_EXIT);
        
        userSessionHandler.runUserSession(defaultUser);

        verify(accountOperationHandler).registerUserAccount(defaultUser);
        verify(ui).showUserSessionExpired();
    }

    @Test
    @DisplayName("Should process balance choice 2 and then exit")
    void shouldProcessBalanceChoice() {

        when(inputUtils.readInt(any(Scanner.class), any())).thenReturn(OPTION_VIEW_BALANCE, OPTION_EXIT);
        
        userSessionHandler.runUserSession(defaultUser);

        verify(accountOperationHandler).balance(defaultUser);
    }

    @Test
    @DisplayName("Should process transfer choice 3 and then exit")
    void shouldProcessTransferChoice() {

        when(inputUtils.readInt(any(Scanner.class), any())).thenReturn(OPTION_TRANSFER, OPTION_EXIT);
        
        userSessionHandler.runUserSession(defaultUser);

        verify(accountOperationHandler).showTransferForm(defaultUser);
    }

    @Test
    @DisplayName("Should process loan choice 4 and then exit")
    void shouldProcessLoanChoice() {

        when(inputUtils.readInt(any(Scanner.class), any())).thenReturn(OPTION_LOAN, OPTION_EXIT);
        
        userSessionHandler.runUserSession(defaultUser);

        verify(loanHandler).handleLoanRequest(defaultUser);
    }

    @Test
    @DisplayName("Should process profile choice 5 and logout if user becomes null")
    void shouldProcessProfileChoiceAndLogout() {

        when(inputUtils.readInt(any(Scanner.class), any())).thenReturn(OPTION_PROFILE);
        when(userProfileHandler.showUserProfile(defaultUser)).thenReturn(null);
        
        userSessionHandler.runUserSession(defaultUser);

        verify(userProfileHandler).showUserProfile(defaultUser);
        verify(ui).showUserSessionExpired();
    }
    
    @Test
    @DisplayName("Should process profile choice 5 and continue if user returned")
    void shouldProcessProfileChoiceAndContinue() {

        when(inputUtils.readInt(any(Scanner.class), any())).thenReturn(OPTION_PROFILE, OPTION_EXIT);
        when(userProfileHandler.showUserProfile(defaultUser)).thenReturn(defaultUser);
        
        userSessionHandler.runUserSession(defaultUser);

        verify(userProfileHandler).showUserProfile(defaultUser);
        verify(ui).showUserSessionExpired();
    }

    @Test
    @DisplayName("Should process delete account choice 6 and then exit")
    void shouldProcessDeleteAccountChoice() {

        when(inputUtils.readInt(any(Scanner.class), any())).thenReturn(OPTION_DELETE_ACCOUNT, OPTION_EXIT);
        
        userSessionHandler.runUserSession(defaultUser);

        verify(accountOperationHandler).handleAccountDeletion(defaultUser);
    }
    
    @Test
    @DisplayName("Should clear screen on choice 0")
    void shouldClearScreen() {

        when(inputUtils.readInt(any(Scanner.class), any())).thenReturn(OPTION_CLEAR_SCREEN, OPTION_EXIT);
        
        userSessionHandler.runUserSession(defaultUser);

        verify(ui).clearScreen();
    }
    
    @Test
    @DisplayName("Should show options on invalid choice")
    void shouldShowOptions() {

        when(inputUtils.readInt(any(Scanner.class), any())).thenReturn(OPTION_INVALID, OPTION_EXIT);
        
        userSessionHandler.runUserSession(defaultUser);

        verify(ui).showChooseOptions();
    }
}
