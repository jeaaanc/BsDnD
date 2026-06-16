package BankSdNd.example.BsDnD.adapter.out.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordEncoderAdapterTest {

    private PasswordEncoderAdapter adapter;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        adapter = new PasswordEncoderAdapter(passwordEncoder);
    }

    @Test
    void shouldEncodePassword() {
        String rawPassword = "password123";
        String encoded = adapter.encode(rawPassword);

        assertTrue(passwordEncoder.matches(rawPassword, encoded));
    }

    @Test
    void shouldMatchPasswords() {
        String rawPassword = "password123";
        String encoded = passwordEncoder.encode(rawPassword);

        assertTrue(adapter.matches(rawPassword, encoded));
    }
}
