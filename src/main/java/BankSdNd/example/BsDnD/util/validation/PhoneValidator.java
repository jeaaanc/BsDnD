package BankSdNd.example.BsDnD.util.validation;

public class PhoneValidator {

    public  static boolean isValidPhoneNumber(String phoneNumber){
        if (phoneNumber == null){
            return false;
        }

        String digits = phoneNumber.replaceAll("\\D", "");

        if (digits.length() == 10){
            return true; // fixo
        } else if (digits.length() == 11 && digits.charAt(2) == '9') {
            return true; // celular começa com 9
        }

        return false;
    }
}
