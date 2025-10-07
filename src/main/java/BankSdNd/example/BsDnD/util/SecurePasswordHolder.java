package BankSdNd.example.BsDnD.util;



/**
 * Defines a contract for objects that temporarily hold a raw, unencrypted password.
 *
 * This interface provides a standard way to access the raw password as a char array
 * and a default method to securely clear it from memory. Classes holding sensitive
 * password data, such as DTOs, should implement this interface.
 */
public interface SecurePasswordHolder {

    /**
     * Retrieves the raw password.
     *
     * @return A {@code char[]} containing the raw password.
     */
    char[] getRawPassword();



    /**
     * Securely clears the raw password array from memory by overwriting it with zeros.
     * This default method should be called as soon as the password is no longer needed.
     */
    default void clearPassword(){
        if (getRawPassword() != null){
            java.util.Arrays.fill(getRawPassword(), '0');
        }
    }
}
