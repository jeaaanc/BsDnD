package BankSdNd.example.BsDnD.adapter.out.security;

import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.domain.exception.*;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        if (secret == null || secret.isBlank()) {
            throw new TokenGenerationException("error.token_generation", null);
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(BankUser user) {
        try {
            return Jwts.builder()
                    .issuer("bsdnd-api")
                    .subject(user.getCpf())
                    .issuedAt(new Date())
                    .expiration(Date.from(genExpirationDate()))
                    .signWith(getSigningKey())
                    .compact();
        } catch (Exception e) {
            throw new TokenGenerationException("error.token_generation", e);
        }
    }

    public String validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();

        } catch (JwtException e) {
            throw new InvalidTokenException("error.token_invalid", e);
        } catch (Exception e) {
            throw new InvalidTokenException("error.token_invalid", e);
        }
    }

    private Instant genExpirationDate() {

        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
