package BankSdNd.example.BsDnD.adapter.in.cli.support;

import BankSdNd.example.BsDnD.adapter.in.cli.ui.ConsoleUI;
import BankSdNd.example.BsDnD.adapter.in.cli.util.InputUtils;
import BankSdNd.example.BsDnD.adapter.in.cli.util.PasswordUtils;
import BankSdNd.example.BsDnD.core.port.in.dto.CreatePersonCommand;
import BankSdNd.example.BsDnD.core.domain.validation.CpfValidator;
import BankSdNd.example.BsDnD.core.domain.validation.PhoneValidator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Scanner;


/**
 * A helper class responsible for collecting and validating all necessary user input
 * for creating a new person ({@code BankUser}).
 */
@Component
public class PersonInputCollector {

    private final ConsoleUI ui;
    private final InputUtils inputUtils;

    public PersonInputCollector(ConsoleUI ui, InputUtils inputUtils) {
        this.ui = ui;
        this.inputUtils = inputUtils;
    }

    /**
     * Orchestrates the step-by-step collection of a new user's data from the console.
     *
     * @param scanner The {@code Scanner} instance used to read user input.
     * @return A {@code CreatePersonCommand} containing the collected user data, or {@code null}
     * if the user cancels the operation.
     */
    public CreatePersonCommand collectUserInput(Scanner scanner) {
        String name = inputUtils.readString(scanner, ui.getMessage("prompt.name"));
        String lastName = inputUtils.readString(scanner, ui.getMessage("prompt.last_name"));

        String cpf = this.collectCpf(scanner);
        if (cpf == null) {
            ui.print(ui.getMessage("error.registration_cancelled"));
            return null;
        }

        String phoneNumber = this.collectPhoneNumber(scanner);
        if (phoneNumber == null) {
            ui.print(ui.getMessage("error.registration_cancelled"));
            return null;
        }

        BigDecimal income = inputUtils.readBigDecimal(scanner, ui.getMessage("prompt.income"));

        char[] rawLoginPassword = null;
        char[] rawTransactionPassword = null;

        try {
            rawLoginPassword = this.collectAndConfirmPassword(
                    ui.getMessage("prompt.login_password_create"),
                    ui.getMessage("prompt.login_password_confirm"),
                    "^\\d{6}$",
                    ui.getMessage("error.password_login_requirement")
            );
            if (rawLoginPassword == null) return null;

            rawTransactionPassword = this.collectAndConfirmPassword(
                    ui.getMessage("prompt.transaction_password_create"),
                    ui.getMessage("prompt.transaction_password_confirm"),
                    "^\\d{4}$",
                    ui.getMessage("error.password_transaction_requirement")
            );
            if (rawTransactionPassword == null) return null;

            String loginPasswordStr = new String(rawLoginPassword);
            String transactionPasswordStr = new String(rawTransactionPassword);

            return new CreatePersonCommand(name, lastName, cpf, phoneNumber, income, loginPasswordStr, transactionPasswordStr);
        } finally {
            if (rawLoginPassword != null) {
                Arrays.fill(rawLoginPassword, '\0');
            }
            if (rawTransactionPassword != null) {
                Arrays.fill(rawTransactionPassword, '\0');
            }
        }
    }

    private char[] collectAndConfirmPassword(String msgPrompt, String msgConfirm, String regex, String errorMsg) {
        char[] rawPassword = null;
        char[] confirmedRawPassword = null;

        try {
            do {
                if (rawPassword != null) Arrays.fill(rawPassword, '\0');
                if (confirmedRawPassword != null) Arrays.fill(confirmedRawPassword, '\0');

                rawPassword = PasswordUtils.catchPassword(msgPrompt);
                if (rawPassword == null) return null;

                String tempPassword = new String(rawPassword);
                if (!tempPassword.matches(regex)) {
                    ui.showError(ui.getMessage("error.password_format", errorMsg));
                    continue;
                }

                confirmedRawPassword = PasswordUtils.catchPassword(msgConfirm);
                if (confirmedRawPassword == null) return null;

                if (Arrays.equals(rawPassword, confirmedRawPassword)) {
                    return rawPassword;
                } else {
                    ui.showError(ui.getMessage("error.password_mismatch"));
                }
            } while (true);
        } finally {

            if (confirmedRawPassword != null) {
                Arrays.fill(confirmedRawPassword, '\0');
            }
        }
    }

    private String collectPhoneNumber(Scanner scanner) {
        String phoneNumber;
        do {
            ui.print(ui.getMessage("prompt.cancel_hint"));

            phoneNumber = inputUtils.readString(scanner, ui.getMessage("prompt.phone_number"));
            if (phoneNumber.trim().equals("0")) {
                return null;
            }
            if (PhoneValidator.isValidPhoneNumber(phoneNumber)) {
                return phoneNumber;
            }

            ui.showError(ui.getMessage("error.phone_invalid_format"));
            ui.print(ui.getMessage("error.phone_examples_fixed"));
            ui.print(ui.getMessage("error.phone_examples_mobile"));
        } while (true);
    }

    private String collectCpf(Scanner scanner) {
        String cpf;
        do {
            ui.print(ui.getMessage("prompt.cancel_hint"));

            cpf = inputUtils.readString(scanner, ui.getMessage("prompt.cpf"));
            if (cpf.trim().equals("0")) {
                return null;
            }
            cpf = cpf.replaceAll("\\D", "");

            if (CpfValidator.isValid(cpf)) {
                return cpf;
            }

            ui.showError(ui.getMessage("error.cpf_invalid"));
        } while (true);
    }

}
