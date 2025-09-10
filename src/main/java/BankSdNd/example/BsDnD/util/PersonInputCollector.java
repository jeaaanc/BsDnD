package BankSdNd.example.BsDnD.util;

import BankSdNd.example.BsDnD.dto.PersonDto;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Scanner;

public class PersonInputCollector {
    private final PasswordEncoder passwordEncoder;

    public PersonInputCollector(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

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

            String encodedPassword = passwordEncoder.encode(new String(rawPassword));
            return new PersonDto(name, lastName, cpf, phoneNumber, income, encodedPassword);

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
