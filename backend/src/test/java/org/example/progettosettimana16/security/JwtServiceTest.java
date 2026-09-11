package org.example.progettosettimana16.security;

import org.example.progettosettimana16.config.AppProperties;
import org.example.progettosettimana16.entity.User;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "una-chiave-segreta-di-test-lunga-almeno-32-byte";

    private static JwtService service(String secret, Duration expiration) {
        return new JwtService(new AppProperties(new AppProperties.Jwt(secret, expiration), null, null, null, null));
    }

    private static User user(long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("mario");
        user.setEmail("mario@example.com");
        return user;
    }

    @Test
    void generatedTokenCarriesTheUserId() {
        JwtService jwtService = service(SECRET, Duration.ofHours(1));

        String token = jwtService.generateToken(user(42L));

        assertThat(jwtService.parseUserId(token)).contains(42L);
    }

    @Test
    void rejectsTokenSignedWithAnotherSecret() {
        String foreignToken = service("un-altro-segreto-completamente-diverso-0123456789", Duration.ofHours(1))
                .generateToken(user(42L));

        assertThat(service(SECRET, Duration.ofHours(1)).parseUserId(foreignToken)).isEmpty();
    }

    @Test
    void rejectsTokenWhosePayloadWasTampered() {
        JwtService jwtService = service(SECRET, Duration.ofHours(1));
        String[] parts = jwtService.generateToken(user(42L)).split("\\.");
        String forgedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"1\"}".getBytes(StandardCharsets.UTF_8));

        String forgedToken = parts[0] + "." + forgedPayload + "." + parts[2];

        assertThat(jwtService.parseUserId(forgedToken)).isEmpty();
    }

    @Test
    void rejectsExpiredToken() {
        JwtService jwtService = service(SECRET, Duration.ofSeconds(-5));

        String token = jwtService.generateToken(user(42L));

        assertThat(jwtService.parseUserId(token)).isEmpty();
    }

    @Test
    void rejectsMalformedOrMissingToken() {
        JwtService jwtService = service(SECRET, Duration.ofHours(1));

        assertThat(jwtService.parseUserId("non-e-un-jwt")).isEmpty();
        assertThat(jwtService.parseUserId(null)).isEmpty();
    }

    @Test
    void refusesToStartWithSecretShorterThan32Bytes() {
        assertThatThrownBy(() -> service("troppo-corto", Duration.ofHours(1)))
                .isInstanceOf(IllegalStateException.class);
    }
}
