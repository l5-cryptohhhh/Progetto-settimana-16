package org.example.progettosettimana16.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

/**
 * Configurazione applicativa letta dalle proprieta' "app.*" di application.properties.
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, Storage storage, Ocr ocr, Geocoding geocoding, Cors cors) {

    public record Jwt(String secret, Duration expiration) {
    }

    public record Storage(Path uploadDir, DataSize maxPhotoSize, DataSize maxDocumentSize) {
    }

    public record Ocr(String tessdataPath, String language) {
    }

    public record Geocoding(String baseUrl, String userAgent) {
    }

    public record Cors(List<String> allowedOrigins) {
    }
}
