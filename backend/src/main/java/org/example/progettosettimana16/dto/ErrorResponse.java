package org.example.progettosettimana16.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatusCode;

import java.time.Instant;
import java.util.List;

/**
 * Formato unico degli errori restituiti dalle API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(int status, String error, String message, List<String> details, Instant timestamp) {

    public static ErrorResponse of(HttpStatusCode status, String message) {
        return of(status, message, null);
    }

    public static ErrorResponse of(HttpStatusCode status, String message, List<String> details) {
        String reason = status instanceof org.springframework.http.HttpStatus httpStatus
                ? httpStatus.getReasonPhrase()
                : String.valueOf(status.value());
        return new ErrorResponse(status.value(), reason, message, details, Instant.now());
    }
}
