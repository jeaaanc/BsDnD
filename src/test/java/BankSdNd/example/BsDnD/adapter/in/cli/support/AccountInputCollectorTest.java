package BankSdNd.example.BsDnD.adapter.in.cli.support;

import BankSdNd.example.BsDnD.adapter.in.cli.ui.ConsoleUI;
import BankSdNd.example.BsDnD.adapter.in.cli.util.InputUtils;
import BankSdNd.example.BsDnD.adapter.in.cli.util.PasswordUtils;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.port.in.GetAccountUseCase;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountInputCollectorTest {

    @Mock
    private Scanner scanner;

    @Mock
    private ConsoleUI ui;

    @Mock
    private InputUtils inputUtils;

    @Mock
    private GetAccountUseCase getAccountUseCase;

    @InjectMocks
    private AccountInputCollector collector;

    private final Long DEFAULT_USER_ID = 1L;


    @Test
    @DisplayName("Should successfully collect origin account when user is the owner")
    void collectOriginAccount_ShouldReturnAccountWhenOwner() {

        BankUser user = BankUser.builder().id(DEFAULT_USER_ID).build();
        String expectedAccount = "12345-6";

        when(inputUtils.readString(any(), any())).thenReturn(expectedAccount);
        when(getAccountUseCase.isAccountNumberOwner(expectedAccount, DEFAULT_USER_ID)).thenReturn(true);

        String result = collector.collectOriginAccount(getAccountUseCase, user);

        assertEquals(expectedAccount, result);
        verify(getAccountUseCase).isAccountNumberOwner(expectedAccount, DEFAULT_USER_ID);
    }

    @Test
    @DisplayName("Should capture transaction password successfully")
    void captureTransactionPassword_ShouldReturnPasswordWhenValid() {

        char[] expectedPassword = {'1', '2', '3', '4'};

        try (MockedStatic<PasswordUtils> mockedPasswordUtils = mockStatic(PasswordUtils.class)) {

            mockedPasswordUtils.when(() -> PasswordUtils.catchPassword(any())).thenReturn(expectedPassword);

            char[] result = collector.captureTransactionPassword();

            assertArrayEquals(expectedPassword, result);
            verify(ui).showConfirmPassword();
        }
    }

}