package BankSdNd.example.BsDnD.util;

public class CpfValidator {
    public static boolean isValid(String cpf){
        if (cpf == null) return false;

        cpf = cpf.replace("\\D", "");
        if (!cpf.matches("\\d{11}") || cpf.matches("(\\d)\\1{10}")) return false;

            int sum = 0, weight = 10;
            for (int i = 0; i < 9; i++) {
                int d = cpf.charAt(i) - '0';
                sum += d * weight --;
            }
            int r = 11 - (sum % 11);
            int dig1 = (r >= 10) ? 0 : r;

            sum = 0; weight = 11;
            for (int i = 0; i < 10; i++) {
                int d = cpf.charAt(i) - '0';
                sum += d * weight--;
            }
            r = 11 - (sum % 11);
            int dig2 = (r >= 10) ? 0 : r;

            return (cpf.charAt(9) - '0') == dig1 && (cpf.charAt(10) - '0') == dig2;
    }
}
