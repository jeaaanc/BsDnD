package BankSdNd.example.BsDnD.adapter.in.cli.util;

import java.math.BigDecimal;
import java.util.Scanner;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;


/**
 * Utility class for handling and validating user input from the console.
 * Provides robust methods for reading specific data types like String, int, and BigDecimal.
 */
@Component
public class InputUtils {

    private final MessageSource messageSource;

    public InputUtils(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }


    /**
     * Reads a single line of text from the console after displaying a prompt.
     *
     * @param sc      The {@code Scanner} instance to read from.
     * @param message The prompt message to display to the user.
     * @return The {@code String} entered by the user.
     */
    public String readString(Scanner sc, String message) {
        System.out.print(message);
        return sc.nextLine();
    }


    /**
     * Reads an integer from the console, ensuring the input is a valid integer.
     * It will loop and re-prompt the user with an error message until a valid integer is entered.
     *
     * @param sc      The {@code Scanner} instance to read from.
     * @param message The prompt message to display to the user.
     * @return The validated {@code int} value entered by the user.
     */
    public int readInt(Scanner sc, String message) {
        while (true) {

            try {

                System.out.print(message);
                int value = Integer.parseInt(sc.nextLine());
                return value;

            } catch (NumberFormatException e) {
                System.out.print(getMessage("prompt.input_invalid_int"));
            }
        }
    }

    /**
     * Reads a {@code BigDecimal} value from the console, handling Brazilian number formats.
     * It correctly parses numbers with '.' as a thousands separator and ',' as a decimal separator.
     * The method will loop and re-prompt until a valid number is entered.
     *
     * @param sc      The {@code Scanner} instance to read from.
     * @param message The prompt message to display to the user.
     * @return The validated {@code BigDecimal} value entered by the user.
     */
    public BigDecimal readBigDecimal(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();

            if (input.contains(",")){
                System.out.println(getMessage("prompt.input_invalid_bigdecimal_comma"));
                System.out.println(getMessage("prompt.input_invalid_bigdecimal_hint"));
                System.out.println(getMessage("prompt.input_invalid_bigdecimal_example"));
                continue;
            }
            try {
                BigDecimal value = new BigDecimal(input);

                if (value.compareTo(BigDecimal.ZERO) < 0){
                    System.out.println(getMessage("prompt.input_negative_value"));
                    continue;
                }
                return value;

            } catch (NumberFormatException e) {
                System.out.print(getMessage("prompt.input_invalid_numeric"));
            }
        }
    }


}
