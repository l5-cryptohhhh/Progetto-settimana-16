package org.example.progettosettimana16.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.progettosettimana16.config.AppProperties;
import org.example.progettosettimana16.entity.User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Genera e verifica i token JWT firmati con HMAC-SHA.
 * Il "subject" del token e' l'id dell'utente.
 */
@Service
public class JwtService {

    private static final int MIN_SECRET_BYTES = 32;

    private final SecretKey key;
    private final Duration expiration;

    public JwtService(AppProperties properties) {
        String secret = properties.jwt().secret();
        byte[] secretBytes = secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException("app.jwt.secret deve essere lungo almeno " + MIN_SECRET_BYTES + " byte");
        }
        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.expiration = properties.jwt().expiration();
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(key)
                .compact();
    }

    /**
     * Restituisce l'id utente se il token e' integro, firmato con la nostra chiave e non scaduto.
     */
    public Optional<Long> parseUserId(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        try {
            String subject = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return Optional.of(Long.valueOf(subject));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
