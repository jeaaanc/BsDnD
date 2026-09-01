package BankSdNd.example.BsDnD.adapter.out.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderAdapterTest {

    private PasswordEncoderAdapter adapter;
    private PasswordEncoder passwordEncoder;

    private final String RAW_PASSWORD = "password123";
    private final String WRONG_PASSWORD = "wrongPassword";

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        adapter = new PasswordEncoderAdapter(passwordEncoder);
    }

    @Test
    @DisplayName("Should encode password returning a hashed string different from the raw password")
    void shouldEncodePassword() {

        String encoded = adapter.encode(RAW_PASSWORD);

        assertNotEquals(RAW_PASSWORD, encoded);
        assertTrue(passwordEncoder.matches(RAW_PASSWORD, encoded));
    }

    @Test
    @DisplayName("Should return true when raw password matches the encoded password")
    void shouldMatchPasswords() {

        String encoded = passwordEncoder.encode(RAW_PASSWORD);

        boolean matches = adapter.matches(RAW_PASSWORD, encoded);

        assertTrue(matches);
    }

    @Test
    @DisplayName("Should return false when passwords do not match")
    void shouldReturnFalseWhenPasswordDoNotMatch() {

        String encoded = passwordEncoder.encode(RAW_PASSWORD);

        boolean matches = adapter.matches(WRONG_PASSWORD, encoded);

        assertFalse(matches);
    }
}
