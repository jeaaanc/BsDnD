package BankSdNd.example.BsDnD.util;

import BankSdNd.example.BsDnD.dto.PersonDto;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Scanner;


/**
 * A helper class responsible for collecting all necessary user input
 * for creating a new person ({@code BankUser}).
 *
 * It handles the entire interactive process, including secure password
 * capture and confirmation, before packaging the data into a {@code PersonDto}.
 * This class is not a Spring bean and must be instantiated manually.
 */
public class PersonInputCollector {


    /**
     * Orchestrates the step-by-step collection of a new user's data from the console.
     *
     * This method prompts the user for personal details and a confirmed password.
     * The raw password is securely handled and cleared from memory upon completion.
     *
     * @param scanner The {@code Scanner} instance used to read user input.
     * @return A {@code PersonDto} containing the collected user data with an already
     * encoded password, or {@code null} if the user cancels the operation.
     */
    public PersonDto collectUserInput(Scanner scanner) {
        String name = InputUtils.readString(scanner, "Nome: ");
        String lastName = InputUtils.readString(scanner, "Sobrenome: ");
        String cpf = InputUtils.readString(scanner, "CPF: (Apenas números)");
        String phoneNumber = InputUtils.readString(scanner, "Celular: ");
        BigDecimal income = InputUtils.readBigDecimal(scanner, "Renda: ");

        char[] rawPassword = null;

        try {
            rawPassword = this.collectAndConfirmPassword();

            if (rawPassword == null) {
                return null;
            }

            String rawPasswordString = new String(rawPassword);

            return new PersonDto(name, lastName, cpf, phoneNumber, income, rawPasswordString);

        } finally {

            if (rawPassword != null) {
                Arrays.fill(rawPassword, '\0');
            }
        }
    }


    private char[] collectAndConfirmPassword() {
        char[] rawPassword = null;
        char[] confirmedRawPassword = null;

        try {
            do {

                if (rawPassword != null) Arrays.fill(rawPassword, '\0');
                if (confirmedRawPassword != null) Arrays.fill(confirmedRawPassword, '\0');

                rawPassword = PasswordUtils.catchPassword("Digite sua senha:");
                if (rawPassword == null) return null;

                confirmedRawPassword = PasswordUtils.catchPassword("Confirme a sua senha:");
                if (confirmedRawPassword == null) return null;

                if (Arrays.equals(rawPassword, confirmedRawPassword)) {
                    return rawPassword;
                } else {
                    System.out.println("\nAs senha não coincidem.");
                }
            } while (true);
        } finally {

            if (confirmedRawPassword != null) {
                Arrays.fill(confirmedRawPassword, '\0');
            }
        }
    }

}
