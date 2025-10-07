package BankSdNd.example.BsDnD.util.validation;

/**
 * Utility class for validating Brazilian CPF numbers (Cadastro de Pessoas Físicas).
 * This class contains static methods and cannot be instantiated.
 */
public final class CpfValidator {

    private CpfValidator() {
    }

    /**
     * Validates a Brazilian CPF number based on the official "Módulo 11" algorithm.
     * This method automatically removes common formatting characters (like '.', '-', '/')
     * before applying the validation logic. It also checks for sequences of identical digits.
     *
     * @param cpf The CPF string to be validated. Can be formatted or contain only digits.
     * @return {@code true} if the CPF is valid according to the algorithm, {@code false} otherwise.
     */
    public static boolean isValid(String cpf) {
        if (cpf == null) return false;

        cpf = cpf.replaceAll("\\D", "");

        if (!cpf.matches("\\d{11}") || cpf.matches("(\\d)\\1{10}")) return false;

        int sum = 0, weight = 10;
        for (int i = 0; i < 9; i++) {

            int d = cpf.charAt(i) - '0';
            sum += d * weight--;

        }

        int r = 11 - (sum % 11);
        int dig1 = (r >= 10) ? 0 : r;

        sum = 0;
        weight = 11;

        for (int i = 0; i < 10; i++) {

            int d = cpf.charAt(i) - '0';
            sum += d * weight--;

        }

        r = 11 - (sum % 11);
        int dig2 = (r >= 10) ? 0 : r;

        return (cpf.charAt(9) - '0') == dig1 && (cpf.charAt(10) - '0') == dig2;
    }
}
