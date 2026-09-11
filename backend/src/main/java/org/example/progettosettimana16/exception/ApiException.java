package org.example.progettosettimana16.exception;

import org.springframework.http.HttpStatus;

/**
 * Errore applicativo con lo status HTTP da restituire al client.
 * Viene tradotto in {@code ErrorResponse} dal {@code GlobalExceptionHandler}.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, message);
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, message);
    }

    public static ApiException forbidden(String message) {
        return new ApiException(HttpStatus.FORBIDDEN, message);
    }

    public static ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, message);
    }

    public static ApiException conflict(String message) {
        return new ApiException(HttpStatus.CONFLICT, message);
    }

    public static ApiException contentTooLarge(String message) {
        return new ApiException(HttpStatus.CONTENT_TOO_LARGE, message);
    }

    public static ApiException unsupportedMediaType(String message) {
        return new ApiException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message);
    }

    public static ApiException badGateway(String message) {
        return new ApiException(HttpStatus.BAD_GATEWAY, message);
    }
}
