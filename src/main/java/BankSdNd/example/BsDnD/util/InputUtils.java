package BankSdNd.example.BsDnD.util;

import java.math.BigDecimal;
import java.util.Scanner;


/**
 * Utility class for handling and validating user input from the console.
 * Provides robust methods for reading specific data types like String, int, and BigDecimal.
 * This class cannot be instantiated or extended.
 */
public final class InputUtils {

    private InputUtils() {
    }


    /**
     * Reads a single line of text from the console after displaying a prompt.
     *
     * @param sc      The {@code Scanner} instance to read from.
     * @param message The prompt message to display to the user.
     * @return The {@code String} entered by the user.
     */
    public static String readString(Scanner sc, String message) {
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
    public static int readInt(Scanner sc, String message) {
        while (true) {

            try {

                System.out.print(message);
                int value = Integer.parseInt(sc.nextLine());
                return value;

            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Digite um número Inteiro. ");
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
    public static BigDecimal readBigDecimal(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine();
            try {
                String noPoint = input.replace(".", "");
                String formatJava = noPoint.replace(",", ".");
                return new BigDecimal(formatJava);

            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Digite um valor numérico.");
            }
        }
    }


}
