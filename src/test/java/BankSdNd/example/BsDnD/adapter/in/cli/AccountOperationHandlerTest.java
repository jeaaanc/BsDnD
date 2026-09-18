package BankSdNd.example.BsDnD.adapter.in.cli;

import BankSdNd.example.BsDnD.adapter.in.cli.support.AccountInputCollector;
import BankSdNd.example.BsDnD.adapter.in.cli.ui.ConsoleUI;
import BankSdNd.example.BsDnD.core.domain.exception.BusinessException;
import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.port.in.CreateAccountUseCase;
import BankSdNd.example.BsDnD.core.port.in.GetAccountUseCase;
import BankSdNd.example.BsDnD.core.port.in.ManageAccountUseCase;
import BankSdNd.example.BsDnD.core.port.in.TransferMoneyUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountOperationHandlerTest {

    @Mock
    private CreateAccountUseCase createAccountUseCase;
    @Mock
    private GetAccountUseCase getAccountUseCase;
    @Mock
    private ManageAccountUseCase manageAccountUseCase;
    @Mock
    private TransferMoneyUseCase transferMoneyUseCase;
    @Mock
    private AccountInputCollector inputCollector;
    @Mock
    private ConsoleUI ui;

    @InjectMocks
    private AccountOperationHandler handler;

    @Test
    @DisplayName("Should create account successfully")
    void shouldCreateAccountSuccessfully() {

        BankUser user = new BankUser();
        String cpf = "12345678909";
        char[] password = {'1', '2', '3', '4'};
        Account account = new Account("123", user);

        when(inputCollector.collectCpf()).thenReturn(cpf);
        when(inputCollector.captureTransactionPassword()).thenReturn(password);
        when(createAccountUseCase.createAccount(cpf, password)).thenReturn(account);

        handler.registerUserAccount(user);

        verify(ui).showCreateAccount();
        verify(createAccountUseCase).createAccount(cpf, password);
        verify(ui).accountCreatedSuccessfully(account);
    }

    @Test
    @DisplayName("Should display validation error when creating account fails with BusinessException")
    void shouldDisplayValidationErrorWhenCreatingAccountFails() {

        BankUser user = new BankUser();
        String cpf = "12345678909";
        char[] password = {'1', '2', '3', '4'};
        String errorMessage = "Invalid password";

        when(inputCollector.collectCpf()).thenReturn(cpf);
        when(inputCollector.captureTransactionPassword()).thenReturn(password);
        when(createAccountUseCase.createAccount(cpf, password)).thenThrow(new BusinessException(errorMessage));

        handler.registerUserAccount(user);

        verify(ui).showAccountValidationError(errorMessage);
        verify(ui, never()).accountCreatedSuccessfully(any());
    }

    @Test
    @DisplayName("Should transfer money successfully")
    void shouldTransferMoneySuccessfully() {

        BankUser user = new BankUser();
        user.setCpf("12345678909");
        char[] password = {'1', '2', '3', '4'};
        String originAccount = "12345-6";
        String destAccount = "65432-1";
        BigDecimal amount = BigDecimal.TEN;
        Account activeAccount = new Account("123", user);

        when(getAccountUseCase.searchClientAccount(user.getCpf())).thenReturn(List.of(activeAccount));
        when(inputCollector.captureTransactionPassword()).thenReturn(password);
        when(inputCollector.collectOriginAccount(getAccountUseCase, user)).thenReturn(originAccount);
        when(inputCollector.collectDestinationAccount()).thenReturn(destAccount);
        when(inputCollector.collectTransferAmount()).thenReturn(amount);


        handler.showTransferForm(user);

        verify(transferMoneyUseCase).transfer(originAccount, destAccount, amount, password);
        verify(ui).showTransferSuccess();
    }

    @Test
    @DisplayName("Should display error when transferring money fails with BusinessException")
    void shouldDisplayErrorWhenTransferringMoneyFails() {

        BankUser user = new BankUser();
        user.setCpf("12345678909");
        char[] password = {'1', '2', '3', '4'};
        String originAccount = "12345-6";
        String destAccount = "65432-1";
        BigDecimal amount = BigDecimal.TEN;
        Account activeAccount = new Account("123", user);
        String errorMessage = "Insufficient balance";

        when(getAccountUseCase.searchClientAccount(user.getCpf())).thenReturn(List.of(activeAccount));
        when(inputCollector.captureTransactionPassword()).thenReturn(password);
        when(inputCollector.collectOriginAccount(getAccountUseCase, user)).thenReturn(originAccount);
        when(inputCollector.collectDestinationAccount()).thenReturn(destAccount);
        when(inputCollector.collectTransferAmount()).thenReturn(amount);
        doThrow(new BusinessException(errorMessage)).when(transferMoneyUseCase).transfer(originAccount, destAccount, amount, password);

        handler.showTransferForm(user);

        verify(ui).showTransferError(errorMessage);
        verify(ui, never()).showTransferSuccess();
    }

    @Test
    @DisplayName("Should return false when user has no active account")
    void requireActiveAccountShouldReturnFalseWhenNoAccount() {

        BankUser user = new BankUser();
        user.setCpf("12345678909");
        when(getAccountUseCase.searchClientAccount(user.getCpf())).thenReturn(Collections.emptyList());

        boolean result = handler.requireActiveAccount(user);

        assertFalse(result);
        verify(ui).showAccessDeniedNoActiveAccount();
    }

    @Test
    @DisplayName("Should return true when user has active account")
    void requireActiveAccountShouldReturnTrueWhenHasAccount() {

        BankUser user = new BankUser();
        user.setCpf("12345678909");
        when(getAccountUseCase.searchClientAccount(user.getCpf())).thenReturn(List.of(new Account("123", user)));

        boolean result = handler.requireActiveAccount(user);

        assertTrue(result);
        verify(ui, never()).showAccessDeniedNoActiveAccount();
    }
}
