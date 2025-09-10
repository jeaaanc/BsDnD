package BankSdNd.example.BsDnD.util.validation;

import BankSdNd.example.BsDnD.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
@Component
public class AccountNumberGenerator {

    private final AccountRepository accountRepository;
    private static final SecureRandom random = new SecureRandom();

    public AccountNumberGenerator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public static String generateBase(){

        int number = 10000000 + random.nextInt(90000000);
        return String.valueOf(number);
    }

    public String generateUniqueAccountNumber(){

        String accountNumber;
        do {

            String base = generateBase();
            char checkDigit = calculateCheckDigit(base);
            accountNumber = base + checkDigit;

        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
    public static char calculateCheckDigit(String base){
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

    public static String generateAccountNumber(){
        String base = generateBase();
        char checkDigit = calculateCheckDigit(base);
        return base + "-" + checkDigit;
    }
}
