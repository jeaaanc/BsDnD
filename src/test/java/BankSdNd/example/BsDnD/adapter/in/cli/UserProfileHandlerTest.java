package BankSdNd.example.BsDnD.adapter.in.cli;

import BankSdNd.example.BsDnD.adapter.in.cli.ui.ConsoleUI;
import BankSdNd.example.BsDnD.adapter.in.cli.util.InputUtils;
import BankSdNd.example.BsDnD.adapter.in.cli.util.PasswordUtils;
import BankSdNd.example.BsDnD.core.domain.exception.UserNotFoundException;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.port.in.ManageCredentialsUseCase;
import BankSdNd.example.BsDnD.core.port.in.ManagePersonUseCase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileHandlerTest {

    @Mock
    private ManagePersonUseCase managePersonUseCase;

    @Mock
    private ManageCredentialsUseCase manageCredentialsUseCase;

    @Mock
    private AccountOperationHandler accountOperationHandler;

    @Mock
    private InputUtils inputUtils;

    @Mock
    private Scanner scanner;

    @Mock
    private ConsoleUI ui;

    @InjectMocks
    private UserProfileHandler userProfileHandler;

    private static MockedStatic<PasswordUtils> passwordUtilsMock;

    private final long USER_ID = 1L;
    private final BankUser defaultUSer = BankUser.builder().id(USER_ID).build();

    private final int OPTION_CLEAR_SCREEN = 0;
    private final int OPTION_VIEW_DATA = 1;
    private final int OPTION_VIEW_BALACE = 2;
    private final int OPTION_CHANGE_NAME = 3;
    private final int OPTION_CHANGE_PASSWORD = 4;
    private final int OPTION_CHANGE_TX_PASSWORD = 5;
    private final int OPTION_CHANGE_PHONE = 6;
    private final int OPTION_GO_BACK = 9;
    private final int OPTION_INVALID = 99;

    @BeforeAll
    static void beforeAll() {
        passwordUtilsMock = mockStatic(PasswordUtils.class);
    }

    @AfterAll
    static void afterAll() {
        passwordUtilsMock.close();
    }

    @Test
    @DisplayName("Should process personal data view and go back")
    void shouldProcessPersonalDataView() {

        when(inputUtils.readInt(any(), any())).thenReturn(OPTION_VIEW_DATA, OPTION_GO_BACK);
        when(inputUtils.readString(any(), any())).thenReturn("");

        BankUser result = userProfileHandler.showUserProfile(defaultUSer);

        assertEquals(defaultUSer, result);
        verify(ui).displayPersonalData(defaultUSer);
        verify(ui).showMenuGoBack();
    }

    @Test
    @DisplayName("Should process account balance view and go back")
    void shouldProcessAccountBalanceView() {

        when(inputUtils.readInt(any(), any())).thenReturn(OPTION_VIEW_BALACE, OPTION_GO_BACK);
        when(inputUtils.readString(any(), any())).thenReturn("");

        BankUser result = userProfileHandler.showUserProfile(defaultUSer);

        assertEquals(defaultUSer, result);
        verify(accountOperationHandler).balance(defaultUSer);
        verify(ui).showMenuGoBack();
    }

    @Test
    @DisplayName("Should process name change successfully")
    void shouldProcessNameChangeSuccessfully() {

        String newFirstName = "New";
        String newLastName = "Name";
        BankUser updatedUser = BankUser.builder().id(USER_ID).name(newFirstName).build();
        
        when(inputUtils.readInt(any(), any())).thenReturn(OPTION_CHANGE_NAME, OPTION_GO_BACK);
        when(inputUtils.readString(eq(scanner), any())).thenReturn(newFirstName, newLastName);
        when(managePersonUseCase.updateName(USER_ID, newFirstName, newLastName)).thenReturn(updatedUser);

        BankUser result = userProfileHandler.showUserProfile(defaultUSer);

        assertEquals(updatedUser, result);
        verify(ui).showNameChangeSuccess();
    }

    @Test
    @DisplayName("Should handle name change error")
    void shouldHandleNameChangeError() {

        String newFirstName = "New";
        String newLastName = "Name";
        String errorMessage = "User not found";
        
        when(inputUtils.readInt(any(), any())).thenReturn(OPTION_CHANGE_NAME, OPTION_GO_BACK);
        when(inputUtils.readString(eq(scanner), any())).thenReturn(newFirstName, newLastName);
        when(managePersonUseCase.updateName(USER_ID, newFirstName, newLastName)).thenThrow(new UserNotFoundException(errorMessage));

        BankUser result = userProfileHandler.showUserProfile(defaultUSer);

        assertEquals(defaultUSer, result); // original user returned on error
        verify(ui).showNameChangeError(errorMessage);
    }

    @Test
    @DisplayName("Should process phone change successfully")
    void shouldProcessPhoneChangeSuccessfully() {

        String newPhoneNumber = "11999999999";
        BankUser updatedUser = BankUser.builder().id(USER_ID).phoneNumber(newPhoneNumber).build();
        
        when(inputUtils.readInt(any(), any())).thenReturn(OPTION_CHANGE_PHONE, OPTION_GO_BACK);
        when(inputUtils.readString(eq(scanner), any())).thenReturn(newPhoneNumber);
        when(managePersonUseCase.updatePhoneNumber(USER_ID, newPhoneNumber)).thenReturn(updatedUser);

        BankUser result = userProfileHandler.showUserProfile(updatedUser);

        assertEquals(updatedUser, result);
        verify(ui).showProfilePhoneChangeSuccess();
    }

    @Test
    @DisplayName("Should process password change and return null")
    void shouldProcessPasswordChangeAndLogout() {

        String oldPassword = "old";
        String newPassword = "new";
        char[] oldPassArray = {'o', 'l', 'd'};
        char[] newPassArray = {'n', 'e', 'w'};

        when(inputUtils.readInt(any(), any())).thenReturn(OPTION_CHANGE_PASSWORD);
        passwordUtilsMock.when(() -> PasswordUtils.catchPassword(any()))
                .thenReturn(oldPassArray)
                .thenReturn(newPassArray)
                .thenReturn(newPassArray);

        BankUser result = userProfileHandler.showUserProfile(defaultUSer);

        assertNull(result); // returns null because password change requires logout
        verify(manageCredentialsUseCase).updatePassword(USER_ID, oldPassword, newPassword);
        verify(ui).showProfilePasswordChangeSuccess();
    }
    
    @Test
    @DisplayName("Should handle transaction password change successfully")
    void shouldProcessTransactionPasswordChangeSuccessfully() {

        String oldPassword = "old";
        String newPassword = "new";
        char[] oldPassArray = {'o', 'l', 'd'};
        char[] newPassArray = {'n', 'e', 'w'};
        
        when(inputUtils.readInt(any(), any())).thenReturn(OPTION_CHANGE_TX_PASSWORD, OPTION_GO_BACK);
        passwordUtilsMock.when(() -> PasswordUtils.catchPassword(any()))
                .thenReturn(oldPassArray)
                .thenReturn(newPassArray)
                .thenReturn(newPassArray);

        BankUser result = userProfileHandler.showUserProfile(defaultUSer);

        assertEquals(defaultUSer, result);
        verify(manageCredentialsUseCase).updateTransactionPassword(USER_ID, oldPassword, newPassword);
        verify(ui, times(2)).print(any());
    }

    @Test
    @DisplayName("Should process choice 0 clear screen")
    void shouldProcessClearScreen() {

        when(inputUtils.readInt(any(), any())).thenReturn(OPTION_CLEAR_SCREEN, OPTION_GO_BACK);

        userProfileHandler.showUserProfile(defaultUSer);

        verify(ui).clearScreen();
    }

    @Test
    @DisplayName("Should process invalid choice")
    void shouldProcessInvalidChoice() {

        when(inputUtils.readInt(any(), any())).thenReturn(OPTION_INVALID, OPTION_GO_BACK);

        userProfileHandler.showUserProfile(defaultUSer);

        verify(ui).showChooseOptions();
    }
}
