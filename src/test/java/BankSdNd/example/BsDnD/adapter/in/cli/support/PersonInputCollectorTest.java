package BankSdNd.example.BsDnD.adapter.in.cli.support;

import BankSdNd.example.BsDnD.adapter.in.cli.ui.ConsoleUI;
import BankSdNd.example.BsDnD.adapter.in.cli.util.InputUtils;
import BankSdNd.example.BsDnD.adapter.in.cli.util.PasswordUtils;
import BankSdNd.example.BsDnD.core.port.in.dto.CreatePersonCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonInputCollectorTest {

    @Mock
    private ConsoleUI ui;

    @Mock
    private InputUtils inputUtils;

    @Mock
    private Scanner scanner;

    @InjectMocks
    private PersonInputCollector collector;

    private final String VALID_NAME = "John";
    private final String VALID_LAST_NAME = "Doe";
    private final String VALID_CPF = "52998224725";
    private final String VALID_PHONE = "11999999999";
    private final BigDecimal VALID_INCOME = new BigDecimal("5000");

    @Test
    @DisplayName("Should successfully collect all user input and return a valid command")
    void collectUserInput_ShouldReturnCommandWhenValidInput() {

        char[] loginPassword = {'1', '2', '3', '4', '5', '6'};
        char[] txPassword = {'1', '2', '3', '4'};


        when(inputUtils.readString(any(), any()))
                .thenReturn(VALID_NAME)
                .thenReturn(VALID_LAST_NAME)
                .thenReturn(VALID_CPF)
                .thenReturn(VALID_PHONE);

        when(inputUtils.readBigDecimal(any(), any())).thenReturn(VALID_INCOME);


        try (MockedStatic<PasswordUtils> mockedPasswordUtils = mockStatic(PasswordUtils.class)) {
            mockedPasswordUtils.when(() -> PasswordUtils.catchPassword(any()))
                    .thenReturn(loginPassword)
                    .thenReturn(loginPassword)
                    .thenReturn(txPassword)
                    .thenReturn(txPassword);


            CreatePersonCommand command = collector.collectUserInput(scanner);


            assertNotNull(command);
            assertEquals(VALID_NAME, command.name());
            assertEquals(VALID_CPF, command.cpf());
            assertEquals(VALID_INCOME, command.income());
        }
    }

    @Test
    @DisplayName("Should return null when user cancels registration by typing '0' during CPF collection")
    void collectUserInput_ShouldReturnNullWhenCpfCanceled() {

        when(inputUtils.readString(any(), any()))
                .thenReturn(VALID_NAME)
                .thenReturn(VALID_LAST_NAME)
                .thenReturn("0");

        CreatePersonCommand command = collector.collectUserInput(scanner);

        assertNull(command);
        verify(ui).print(ui.getMessage("prompt.cancel_hint"));
        verify(ui).print(ui.getMessage("error.registration_cancelled"));
//        olha se nao passou falso positivo !@!@!@!@!!@@!@@@!@
//        verify(ui).print(any());
    }
    // verificar '0' ^ para phoneNumber
}