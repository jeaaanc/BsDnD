package BankSdNd.example.BsDnD.core.port.out;

/**
 * Port for password encryption and verification.
 * Decouples the core from specific security framework implementations.
 */
public interface PasswordEncoderPort {
    String encode(CharSequence rawPassword);
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
