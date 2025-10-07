package BankSdNd.example.BsDnD.util.validation;


/**
 * Utility class for validating Brazilian phone numbers.
 * This class validates formats for both landlines (10 digits) and mobile phones (11 digits).
 * It cannot be instantiated or extended.
 */
public final class PhoneValidator {

    private PhoneValidator() {
    }

    /**
     * Validates a Brazilian phone number format.
     *
     * The method first strips all non-digit characters from the string. It then checks if the
     * resulting number is a valid landline (10 digits, e.g., DDD + 8 digits) or a valid
     * mobile number (11 digits, e.g., DDD + '9' + 8 digits).
     *
     * @param phoneNumber The phone number string to be validated. Can contain formatting.
     * @return {@code true} if the phone number matches a valid format, {@code false} otherwise.
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }

        String digits = phoneNumber.replaceAll("\\D", "");
        // Valid landline format (e.g., 1140028922)
        if (digits.length() == 10) {
            return true;
        }
        // Valid mobile format (e.g., 11987654321)
        else if (digits.length() == 11 && digits.charAt(2) == '9') {
            return true;
        }

        return false;
    }
}
