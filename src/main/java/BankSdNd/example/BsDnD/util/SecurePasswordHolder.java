package BankSdNd.example.BsDnD.util;

public interface SecurePasswordHolder {
    char[] getRawPassword();

    default void clearPassword(){
        if (getRawPassword() != null){
            java.util.Arrays.fill(getRawPassword(), '0');
        }
    }
}
