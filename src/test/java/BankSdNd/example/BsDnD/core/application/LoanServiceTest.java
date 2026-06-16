package BankSdNd.example.BsDnD.core.application;

import BankSdNd.example.BsDnD.core.domain.exception.*;
import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.domain.service.FinancialCalculator;
import BankSdNd.example.BsDnD.core.port.out.PasswordEncoderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoanServiceTest {

    private LoanService loanService;
    private AccountService accountService;
    private FinancialCalculator financialCalculator;
    private PasswordEncoderPort passwordEncoder;

    @BeforeEach
    void setUp() {
        accountService = mock(AccountService.class);
        financialCalculator = mock(FinancialCalculator.class);
        passwordEncoder = mock(PasswordEncoderPort.class);
        loanService = new LoanService(accountService, financialCalculator, passwordEncoder);
    }

    @Test
    @DisplayName("Should grant a loan when user and amount are valid")
    void grantLoan_ShouldGrantWhenValid() {
        BankUser user = BankUser.builder().cpf("12345678900").transactionPassword("encoded").build();
        Account account = new Account("1", user);
        char[] password = "1234".toCharArray();

        when(passwordEncoder.matches(any(CharBuffer.class), eq("encoded"))).thenReturn(true);
        when(accountService.searchClientAccount("12345678900")).thenReturn(Collections.singletonList(account));
        when(financialCalculator.calculateLoanLimit(eq(user), anyList())).thenReturn(new BigDecimal("5000"));

        Account result = loanService.grantLoan(user, new BigDecimal("1000"), password);

        assertNotNull(result);
        assertEquals(new BigDecimal("1000"), result.getBalance());
    }

    @Test
    @DisplayName("Should throw BusinessException when transaction password is wrong during loan request")
    void grantLoan_ShouldThrowBusinessExceptionWhenPasswordWrong() {
        BankUser user = BankUser.builder().transactionPassword("encoded").build();
        char[] password = "wrong".toCharArray();
        when(passwordEncoder.matches(any(CharBuffer.class), eq("encoded"))).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> loanService.grantLoan(user, new BigDecimal("1000"), password));
        assertEquals("error.password_incorrect", exception.getMessageKey());
    }

    @Test
    @DisplayName("Should throw LoanLimitExceededException when requested amount is above the limit")
    void grantLoan_ShouldThrowLoanLimitExceededExceptionWhenAmountTooHigh() {
        BankUser user = BankUser.builder().cpf("12345678900").transactionPassword("encoded").build();
        char[] password = "1234".toCharArray();

        when(passwordEncoder.matches(any(CharBuffer.class), eq("encoded"))).thenReturn(true);
        when(accountService.searchClientAccount("12345678900")).thenReturn(Collections.singletonList(new Account("1", user)));
        when(financialCalculator.calculateLoanLimit(eq(user), anyList())).thenReturn(new BigDecimal("500"));

        LoanLimitExceededException exception = assertThrows(LoanLimitExceededException.class, () -> loanService.grantLoan(user, new BigDecimal("1000"), password));
        assertEquals("error.loan_limit_exceeded", exception.getMessageKey());
    }

    @Test
    @DisplayName("Should throw ValidationException when loan amount is non-positive")
    void grantLoan_ShouldThrowValidationExceptionWhenAmountNonPositive() {
        BankUser user = BankUser.builder().cpf("12345678900").transactionPassword("encoded").build();
        char[] password = "1234".toCharArray();

        when(passwordEncoder.matches(any(CharBuffer.class), eq("encoded"))).thenReturn(true);
        when(accountService.searchClientAccount("12345678900")).thenReturn(Collections.singletonList(new Account("1", user)));
        when(financialCalculator.calculateLoanLimit(eq(user), anyList())).thenReturn(new BigDecimal("5000"));

        ValidationException exception = assertThrows(ValidationException.class, () -> loanService.grantLoan(user, BigDecimal.ZERO, password));
        assertEquals("error.amount_positive", exception.getMessageKey());
    }
}
