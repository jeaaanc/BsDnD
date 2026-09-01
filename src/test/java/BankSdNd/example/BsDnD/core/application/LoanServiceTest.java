package BankSdNd.example.BsDnD.core.application;

import BankSdNd.example.BsDnD.core.domain.exception.*;
import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.domain.service.FinancialCalculator;
import BankSdNd.example.BsDnD.core.port.out.PasswordEncoderPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private AccountService accountService;
    @Mock
    private FinancialCalculator financialCalculator;
    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private LoanService loanService;

    private final String DEFAULT_CPF = "12345678900";
    private final String DEFAULT_ACCOUNT_ID = "1";
    private final String ENCODED_PASSWORD = "encoded";
    private final char[] VALID_PASSWORD_CHARS = "1234".toCharArray();
    private final char[] WRONG_PASSWORD_CHARS = "wrong".toCharArray();

    @Test
    @DisplayName("Should grant a loan when user and amount are valid")
    void grantLoan_ShouldGrantWhenValid() {

        BankUser user = BankUser.builder().cpf(DEFAULT_CPF).transactionPassword(ENCODED_PASSWORD).build();
        Account account = new Account(DEFAULT_ACCOUNT_ID, user);
        BigDecimal loanAmount = new BigDecimal("1000");
        BigDecimal calculatedLimit = new BigDecimal("5000");

        when(passwordEncoder.matches(any(CharBuffer.class), eq("encoded"))).thenReturn(true);
        when(accountService.searchClientAccount("12345678900")).thenReturn(Collections.singletonList(account));
        when(financialCalculator.calculateLoanLimit(eq(user), anyList())).thenReturn((calculatedLimit));

        Account result = loanService.grantLoan(user, loanAmount, VALID_PASSWORD_CHARS);

        assertNotNull(result);
        assertEquals(0,loanAmount.compareTo(result.getBalance()), "Loan balance mismatch");
    }

    @Test
    @DisplayName("Should throw BusinessException when transaction password is wrong during loan request")
    void grantLoan_ShouldThrowBusinessExceptionWhenPasswordWrong() {

        BankUser user = BankUser.builder().transactionPassword("encoded").build();
        BigDecimal loanAmount = new BigDecimal("1000");

        when(passwordEncoder.matches(any(CharBuffer.class), eq(ENCODED_PASSWORD))).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                loanService.grantLoan(user, loanAmount, WRONG_PASSWORD_CHARS)
        );

        assertEquals("error.password_incorrect", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw LoanLimitExceededException when requested amount is above the limit")
    void grantLoan_ShouldThrowLoanLimitExceededExceptionWhenAmountTooHigh() {

        BankUser user = BankUser.builder().cpf(DEFAULT_CPF).transactionPassword(ENCODED_PASSWORD).build();
        BigDecimal loanAmount = new BigDecimal("1000");
        BigDecimal calculatedLimit = new BigDecimal("500");

        when(passwordEncoder.matches(any(CharBuffer.class), eq(ENCODED_PASSWORD))).thenReturn(true);
        when(accountService.searchClientAccount(DEFAULT_CPF))
                .thenReturn(Collections.singletonList(new Account(DEFAULT_ACCOUNT_ID, user)));
        when(financialCalculator.calculateLoanLimit(eq(user), anyList()))
                .thenReturn((calculatedLimit));

        LoanLimitExceededException exception = assertThrows(LoanLimitExceededException.class, () ->
                loanService.grantLoan(user, loanAmount, VALID_PASSWORD_CHARS));

        assertEquals("error.loan_limit_exceeded", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ValidationException when loan amount is non-positive")
    void grantLoan_ShouldThrowValidationExceptionWhenAmountNonPositive() {

        BankUser user = BankUser.builder().cpf(DEFAULT_CPF).transactionPassword(ENCODED_PASSWORD).build();
        BigDecimal loanAmount = BigDecimal.ZERO;
        BigDecimal calculatedLimit = new BigDecimal("5000");

        when(passwordEncoder.matches(any(CharBuffer.class), eq(ENCODED_PASSWORD))).thenReturn(true);
        when(accountService.searchClientAccount(DEFAULT_CPF)).
                thenReturn(Collections.singletonList(new Account(DEFAULT_ACCOUNT_ID, user)));
        when(financialCalculator.calculateLoanLimit(eq(user), anyList())).thenReturn((calculatedLimit));

        ValidationException exception = assertThrows(ValidationException.class, () ->
                loanService.grantLoan(user, loanAmount, VALID_PASSWORD_CHARS));
        assertEquals("error.amount_positive", exception.getMessage());
    }
}
