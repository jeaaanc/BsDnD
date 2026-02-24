package BankSdNd.example.BsDnD.util.validation;

import BankSdNd.example.BsDnD.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;


/**
 * A Spring component responsible for generating unique bank account numbers.
 *
 * This class creates an 8-digit base number and appends a 1-digit check digit
 * based on the "Módulo 11" algorithm. It ensures the final, generated number
 * is unique by checking against the {@code AccountRepository}.
 */
@Component
public class AccountNumberGenerator {

    private final AccountRepository accountRepository;
    private static final SecureRandom random = new SecureRandom();

    public AccountNumberGenerator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    /**
     * Generates a unique 9-digit bank account number as a String.
     *
     * The process loops until a number is found that does not already exist
     * in the database, guaranteeing uniqueness.
     *
     * @return A {@code String} representing the unique 9-digit account number.
     */
    public String generateUniqueAccountNumber() {

        String accountNumber;
        do {

            String base = generateBase();
            char checkDigit = calculateCheckDigit(base);
            accountNumber = base + checkDigit;

        } while (accountRepository.existsByAccountNumberAndActiveTrue(accountNumber));

        return accountNumber;
    }

    private static String generateBase() {

        int number = 10_000_000 + random.nextInt(90_000_000);
        return String.valueOf(number);
    }

    private static char calculateCheckDigit(String base) {
        int sum = 0;
        int weight = 2;

        for (int i = base.length() - 1; i >= 0; i--) {

            int digit = base.charAt(i) - '0';
            sum += digit * weight;
            weight++;

            if (weight > 9) weight = 2;
        }

        int remainder = sum % 11;
        if (remainder == 0 || remainder == 1) return '0';

        return (char) ((11 - remainder) + '0');

    }


}
