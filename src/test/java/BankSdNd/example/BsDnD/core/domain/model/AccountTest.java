package BankSdNd.example.BsDnD.core.domain.model;

import BankSdNd.example.BsDnD.core.domain.exception.InsufficientBalanceException;
import BankSdNd.example.BsDnD.core.domain.exception.InvalidDepositAmountException;
import BankSdNd.example.BsDnD.core.domain.exception.InvalidWithdrawalAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private BankUser user;
    private Account account;

    private final String ACCOUNT_NUMBER = "12345-6";
    private final String DESTINATION_ACCOUNT_NUMBER = "98765-4";
    private final Long DEFAULT_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        user = BankUser.builder()
                .id(DEFAULT_USER_ID)
                .name("John")
                .build();
        account = new Account(ACCOUNT_NUMBER, user);
    }

    @Test
    @DisplayName("Should successfully accumulate balance after multiple deposits")
    void shouldAccumulateBalanceAfterMultipleDeposits() {

        BigDecimal firstDeposit = new BigDecimal("100.50");
        BigDecimal secondDeposit = new BigDecimal("50.00");
        BigDecimal expectedFinalBalance = new BigDecimal("150.50");

        account.deposit(firstDeposit);
        account.deposit(secondDeposit);

        assertEquals(0,expectedFinalBalance.compareTo(account.getBalance()),"Balance mismatch after deposits");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "-10"})
    @DisplayName("Should throw exception when depositing invalid amounts")
    void shouldThrowExceptionWhenDepositingInvalidAmounts(String invalidAmountStr) {

        BigDecimal invalidAmount = (invalidAmountStr == null) ? null : new BigDecimal(invalidAmountStr);
        String expectedErrorMessage = "Deposit must be greater than 0";

        InvalidDepositAmountException exception = assertThrows(
                InvalidDepositAmountException.class,
                () -> account.deposit(invalidAmount)
        );

        assertEquals(expectedErrorMessage, exception.getMessage());
    }


    @Test
    @DisplayName("Should successfully withdraw a valid amount")
    void shouldWithdrawValidAmount() {

        BigDecimal initialDeposit = new BigDecimal("100.00");
        BigDecimal withdrawalAmount = new BigDecimal("40.00");
        BigDecimal expectedBalance = new BigDecimal("60.00");

        account.deposit(initialDeposit);
        account.withdraw(withdrawalAmount);

        assertEquals(0, expectedBalance.compareTo(account.getBalance()));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "-50"})
    @DisplayName("Should throw exception when withdrawing invalid amounts")
    void shouldThrowExceptionWhenWithdrawingInvalidAmounts(String invalidAmountStr) {

        BigDecimal invalidAmount = (invalidAmountStr == null) ? null : new BigDecimal(invalidAmountStr);
        String expectedErrorMessage = "Withdrawal must be greater than 0";

        InvalidWithdrawalAmountException exception = assertThrows(
                InvalidWithdrawalAmountException.class,
                () -> account.withdraw(invalidAmount)
        );

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InsufficientBalanceException when withdrawing more than balance")
    void shouldThrowInsufficientBalanceException() {

        BigDecimal initialDeposit = new BigDecimal("100.00");
        BigDecimal excessiveWithdrawal = new BigDecimal("100.01");
        String expectedErrorMessage = "Insufficient balance.";

        account.deposit(initialDeposit);

        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> account.withdraw(excessiveWithdrawal)
        );

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully transfer to another account")
    void shouldTransferToAnotherAccount() {

        BigDecimal initialBalance = new BigDecimal("200.00");
        BigDecimal transferAmount = new BigDecimal("50.00");
        BigDecimal expectedSourceBalance = new BigDecimal("150.00");

        account.deposit(initialBalance);
        Account destination = new Account(DESTINATION_ACCOUNT_NUMBER, BankUser.builder().id(2L).build());
        account.transferTo(destination, transferAmount);

        assertEquals(0,expectedSourceBalance.compareTo(account.getBalance()),"Soucer Balance mismatch after tranfer");
        assertEquals(0,transferAmount.compareTo(destination.getBalance()), "Destination Balance mismatch after tranfer");
    }

    @Test
    @DisplayName("Should return true when checking ownership with the correct user ID")
    void shouldReturnTrueWhenCheckingOwnershipWithCorrectUserId() {

        boolean isOwner = account.isOwnedBy(DEFAULT_USER_ID);

        assertTrue(isOwner);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {2L, 99L})
    @DisplayName("Should return false when checking ownership with incorrect or null user ID")
    void shouldReturnFalseWhenCheckingOwnershipWithIncorrectOrNullUserID(Long invalidOwnerID) {

        boolean isOwner = account.isOwnedBy(invalidOwnerID);

        assertFalse(isOwner);
    }

    @Test
    @DisplayName("Should return false when checking ownership and account has no holder")
    void shouldReturnFalseWhenCheckingOwnershipAndAccountHasNoHolder() {

        account.setHolder(null);

        boolean isOwner = account.isOwnedBy(DEFAULT_USER_ID);

        assertFalse(isOwner);
    }

}
