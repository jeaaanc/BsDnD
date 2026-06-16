package BankSdNd.example.BsDnD.adapter.out.security;

import BankSdNd.example.BsDnD.core.domain.exception.InvalidTokenException;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;
    private final String secret = "my-very-secret-key-that-must-be-long-enough";

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", secret);
    }

    @Test
    void shouldGenerateToken() {
        BankUser user = BankUser.builder().cpf("12345678900").build();
        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldValidateToken() {
        BankUser user = BankUser.builder().cpf("12345678900").build();
        String token = tokenService.generateToken(user);

        String cpf = tokenService.validateToken(token);

        assertEquals("12345678900", cpf);
    }

    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() {
        assertThrows(InvalidTokenException.class, () -> tokenService.validateToken("invalid-token"));
    }

    @Test
    void shouldThrowExceptionWhenSignatureIsInvalid() {
        BankUser user = BankUser.builder().cpf("12345678900").build();
        String token = tokenService.generateToken(user);
        
        // Tamper with the token
        String tamperedToken = token + "modified";

        assertThrows(InvalidTokenException.class, () -> tokenService.validateToken(tamperedToken));
    }

    @Test
    void shouldThrowExceptionWhenTokenIsExpired() {
        // Manually create an expired token
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        
        String expiredToken = Jwts.builder()
                .issuer("bsdnd-api")
                .subject("12345678900")
                .issuedAt(new Date(System.currentTimeMillis() - 10000))
                .expiration(new Date(System.currentTimeMillis() - 5000)) // Expired 5 seconds ago
                .signWith(key)
                .compact();

        assertThrows(InvalidTokenException.class, () -> tokenService.validateToken(expiredToken));
    }
}
