package BankSdNd.example.BsDnD.adapter.out.security;

import BankSdNd.example.BsDnD.core.domain.exception.InvalidTokenException;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    private final String secret = "my-very-secret-key-that-must-be-long-enough";
    private final String VALID_CPF = "12345678900";
    private final String ISSUER = "bsdnd-api";

    private BankUser defaultUser;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", secret);

        defaultUser = BankUser.builder()
                .cpf(VALID_CPF)
                .build();
    }

    @Test
    @DisplayName("Should generate a valid JWT token for a given user")
    void shouldGenerateToken() {

        String token = tokenService.generateToken(defaultUser);

        assertNotNull(token);
        assertFalse(token.trim().isEmpty());
    }

    @Test
    @DisplayName("Should validate token and return the user CPF subject")
    void shouldValidateToken() {

        String token = tokenService.generateToken(defaultUser);

        String subjectCpf = tokenService.validateToken(token);

        assertEquals(VALID_CPF, subjectCpf);
    }

    // ajustar mensagem logo menos !@@#%#¨$¨%&$@#$
    @Test
    @DisplayName("Should throw InvalidTokenException when token string is completely invalid")
    void shouldThrowExceptionWhenTokenIsInvalid() {

        String completelyInvalidToken = "invalid-token-format";

        assertThrows(InvalidTokenException.class, () -> tokenService.validateToken(completelyInvalidToken));

        // TODO: Se a sua exception tiver uma mensagem padrão, adicione a validação abaixo:
        // assertEquals("Mensagem de erro esperada", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when token signature is tampered")
    void shouldThrowExceptionWhenSignatureIsInvalid() {

        String validToken = tokenService.generateToken(defaultUser);
        String tamperedToken = validToken + "modified";

        assertThrows(InvalidTokenException.class, () -> tokenService.validateToken(tamperedToken));
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when token is expired")
    void shouldThrowExceptionWhenTokenIsExpired() {

        String expiredToken = createExpiredToken();

        assertThrows(InvalidTokenException.class, () -> tokenService.validateToken(expiredToken));
    }

    private String createExpiredToken() {

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        long currentTime = System.currentTimeMillis();
        Date pastIssueDate = new Date(currentTime - 10000);
        Date pastExpirationDate = new Date(currentTime - 5000);

        return Jwts.builder()
                .issuer(ISSUER)
                .subject(VALID_CPF)
                .issuedAt(pastIssueDate)
                .expiration(pastExpirationDate)
                .signWith(key)
                .compact();
    }
}
