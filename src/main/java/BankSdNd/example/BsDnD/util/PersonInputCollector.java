package BankSdNd.example.BsDnD.util;

import BankSdNd.example.BsDnD.dto.PersonDto;
import BankSdNd.example.BsDnD.menu.ConsoleUI;
import BankSdNd.example.BsDnD.util.validation.CpfValidator;
import BankSdNd.example.BsDnD.util.validation.PhoneValidator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Scanner;


/**
 * A helper class responsible for collecting and validating all necessary user input
 * for creating a new person ({@code BankUser}).
 * It handles the entire interactive process via the terminal, including fail-fast
 * data validation and secure password capture, before packaging the data into a
 * {@code PersonDto}.
 * * This class is not a Spring bean and must be instantiated manually, typically
 * receiving a {@link ConsoleUI} to standardize output messages.
 */
public class PersonInputCollector {

    private final ConsoleUI ui;

    public PersonInputCollector(ConsoleUI ui) {
        this.ui = ui;
    }

    /**
     * Orchestrates the step-by-step collection of a new user's data from the console.
     * Prompts for personal details, along with a 6-digit login password and a 4-digit
     * transaction password (both validated and confirmed). Raw passwords are securely
     * handled and cleared from memory upon completion.
     *
     * @param scanner The {@code Scanner} instance used to read user input.
     * @return A {@code PersonDto} containing the collected user data, or {@code null}
     * if the user cancels the operation.
     */
    public PersonDto collectUserInput(Scanner scanner) {
        String name = InputUtils.readString(scanner, "Nome: ");
        String lastName = InputUtils.readString(scanner, "Sobrenome: ");

        String cpf = this.collectCpf(scanner);
        if (cpf == null) {
            ui.print("\n Operação cancelada pelo usuário");
            return null;
        }

        String phoneNumber = this.collectPhoneNumber(scanner);
        if (phoneNumber == null) {
            ui.print("\nOperação cancelada pelo usuário.");
            return null;
        }

        BigDecimal income = InputUtils.readBigDecimal(scanner, "Renda: (Ex: 1500.50): ");

        char[] rawLoginPassword = null;
        char[] rawTransactionPassword = null;

        try {
            rawLoginPassword = this.collectAndConfirmPassword(
                    "Crie um senha de acesso (6 números):",
                    "Confirme a senha de acesso",
                    "^\\d{6}$",
                    "A senha de acesso deve conter exatamente 6 números."
            );
            if (rawLoginPassword == null) return null;

            rawTransactionPassword = this.collectAndConfirmPassword(
                    "Crie uma senha de transação (4 números):",
                    "Confirme a senha de transação:",
                    "^\\d{4}$",
                    "A senha de transação deve conter exatamente 4 números"
            );
            if (rawTransactionPassword == null) return null;

            String loginPasswordStr = new String(rawLoginPassword);
            String transactionPasswordStr = new String(rawTransactionPassword);

            return new PersonDto(name, lastName, cpf, phoneNumber, income, loginPasswordStr, transactionPasswordStr);
        } finally {
            if (rawLoginPassword != null) {
                Arrays.fill(rawLoginPassword, '\0');
            }
            if (rawTransactionPassword != null) {
                Arrays.fill(rawTransactionPassword, '\0');
            }
        }
    }

    /**
     * Securely collects, validates, and confirms a password from the console.
     * Uses a regex for fail-fast validation and ensures both inputs match.
     * Temporary data is cleared from memory to maintain security.
     *
     * @param msgPrompt  The initial prompt displayed to the user.
     * @param msgConfirm The prompt displayed for password confirmation.
     * @param regex      The regular expression the input must match.
     * @param errorMsg   The error message shown if regex validation fails.
     * @return A {@code char[]} containing the validated password, or {@code null} if canceled.
     */
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
                    ui.showError("\nFormato inválido: " + errorMsg);
                    continue;
                }

                confirmedRawPassword = PasswordUtils.catchPassword(msgConfirm);
                if (confirmedRawPassword == null) return null;

                if (Arrays.equals(rawPassword, confirmedRawPassword)) {
                    return rawPassword;
                } else {
                    ui.showError("\nAs senha não coincidem. Tente novamente.");
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
            ui.print("\n(Digite '0' para cencelar o cadastro)");

            phoneNumber = InputUtils.readString(scanner, "Celular (DDD + Número): ");
            if (phoneNumber.trim().equals("0")) {
                return null;
            }
            if (PhoneValidator.isValidPhoneNumber(phoneNumber)) {
                return phoneNumber;
            }

            ui.showError("\nFormato inválido! Digite apenas os números.");
            ui.print("Exemplos Fixos: 1140028922 (10 dígitos)");
            ui.print("Exemplos Celular: 11987654321 (11 dígitos, começado com 9)");
        } while (true);
    }

    private String collectCpf(Scanner scanner) {
        String cpf;
        do {
            ui.print("\n(Digite '0' para cancelar o cadastro)");

            cpf = InputUtils.readString(scanner, "CPF: ");
            if (cpf.trim().equals("0")) {
                return null;
            }
            cpf = cpf.replaceAll("\\D", "");

            if (CpfValidator.isValid(cpf)) {
                return cpf;
            }

            ui.showError("\n[Erro] CPF inválido 11 números.");
        } while (true);
    }

}
