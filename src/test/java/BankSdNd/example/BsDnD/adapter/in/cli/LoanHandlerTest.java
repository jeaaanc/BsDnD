package BankSdNd.example.BsDnD.adapter.in.cli;

import BankSdNd.example.BsDnD.adapter.in.cli.support.AccountInputCollector;
import BankSdNd.example.BsDnD.adapter.in.cli.ui.ConsoleUI;
import BankSdNd.example.BsDnD.adapter.in.cli.util.InputUtils;
import BankSdNd.example.BsDnD.core.domain.exception.LoanLimitExceededException;
import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.port.in.RequestLoanUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Scanner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanHandlerTest {

    @Mock
    private RequestLoanUseCase requestLoanUseCase;

    @Mock
    private AccountOperationHandler accountOperationHandler;

    @Mock
    private AccountInputCollector inputCollector;

    @Mock
    private InputUtils inputUtils;

    @Mock
    private Scanner scanner;

    @Mock
    private ConsoleUI ui;

    @InjectMocks
    private LoanHandler loanHandler;

    private final char[] VALID_PASSWORD = {'1', '2', '3', '4'};
    private final BigDecimal LOAN_LIMIT = new BigDecimal("1000");
    private final BigDecimal REQUESTD_AMOUNT = new BigDecimal("500");

    @Test
    @DisplayName("Should return if user does not have active account")
    void handleLoanRequest_ShouldReturnIfNoActiveAccount() {

        BankUser user = new BankUser();

        when(accountOperationHandler.requireActiveAccount(user)).thenReturn(false);

        loanHandler.handleLoanRequest(user);

        verify(requestLoanUseCase, never()).calculateLoanLimit(any());
    }

    @Test
    @DisplayName("Should return if user requests zero amount")
    void handleLoanRequest_ShouldReturnIfZeroAmount() {

        BankUser user = new BankUser();

        when(accountOperationHandler.requireActiveAccount(user)).thenReturn(true);
        when(requestLoanUseCase.calculateLoanLimit(user)).thenReturn(LOAN_LIMIT);
        when(inputUtils.readBigDecimal(any(), any())).thenReturn(BigDecimal.ZERO);

        loanHandler.handleLoanRequest(user);

        verify(ui).loanRequestShowCanceled();
        verify(inputCollector, never()).captureTransactionPassword();
    }

    @Test
    @DisplayName("Should return if password collection fails")
    void handleLoanRequest_ShouldReturnIfPasswordFails() {

        BankUser user = new BankUser();

        when(accountOperationHandler.requireActiveAccount(user)).thenReturn(true);
        when(requestLoanUseCase.calculateLoanLimit(user)).thenReturn(LOAN_LIMIT);
        when(inputUtils.readBigDecimal(any(), any())).thenReturn(REQUESTD_AMOUNT);
        when(inputCollector.captureTransactionPassword()).thenReturn(null);

        loanHandler.handleLoanRequest(user);

        verify(ui).showPasswordValidationError();
        verify(requestLoanUseCase, never()).grantLoan(any(), any(), any());
    }

    @Test
    @DisplayName("Should process successful loan request")
    void handleLoanRequest_ShouldProcessSuccessfully() {

        BankUser user = new BankUser();

        Account account = new Account("123", user);

        when(accountOperationHandler.requireActiveAccount(user)).thenReturn(true);
        when(requestLoanUseCase.calculateLoanLimit(user)).thenReturn(LOAN_LIMIT);
        when(inputUtils.readBigDecimal(any(), any())).thenReturn(REQUESTD_AMOUNT);
        when(inputCollector.captureTransactionPassword()).thenReturn(VALID_PASSWORD);
        when(requestLoanUseCase.grantLoan(eq(user), eq(REQUESTD_AMOUNT), eq(VALID_PASSWORD))).thenReturn(account);

        loanHandler.handleLoanRequest(user);

        verify(ui).showLoanSuccess(account, REQUESTD_AMOUNT);
    }

    @Test
    @DisplayName("Should show error on exception")
    void handleLoanRequest_ShouldShowErrorOnException() {

        BankUser user = new BankUser();
        String errorMessage = "Limit exceeded";

        when(accountOperationHandler.requireActiveAccount(user)).thenReturn(true);
        when(requestLoanUseCase.calculateLoanLimit(user)).thenReturn(LOAN_LIMIT);
        when(inputUtils.readBigDecimal(any(), any())).thenReturn(REQUESTD_AMOUNT);
        when(inputCollector.captureTransactionPassword()).thenReturn(VALID_PASSWORD);
        when(requestLoanUseCase.grantLoan(any(), any(), any())).thenThrow(new LoanLimitExceededException(errorMessage));

        loanHandler.handleLoanRequest(user);

        verify(ui).showLoanRequestError(errorMessage);
    }
}
