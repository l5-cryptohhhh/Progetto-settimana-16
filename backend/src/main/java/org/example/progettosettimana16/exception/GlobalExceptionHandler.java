package org.example.progettosettimana16.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.progettosettimana16.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * Traduce tutte le eccezioni in risposte JSON con formato {@link ErrorResponse}.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        return build(ex.getStatus(), ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .sorted()
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Dati non validi", details);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        return build(HttpStatus.BAD_REQUEST, "Parametro obbligatorio mancante: " + ex.getParameterName(), null);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    ResponseEntity<ErrorResponse> handleMissingPart(MissingServletRequestPartException ex) {
        return build(HttpStatus.BAD_REQUEST, "File obbligatorio mancante: " + ex.getRequestPartName(), null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "Valore non valido per il parametro '" + ex.getName() + "'", null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Corpo della richiesta mancante o non valido", null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<ErrorResponse> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return build(HttpStatus.CONTENT_TOO_LARGE,"La richiesta supera la dimensione massima consentita per l'upload", null);
    }

    @ExceptionHandler(MultipartException.class)
    ResponseEntity<ErrorResponse> handleMultipart(MultipartException ex) {
        return build(HttpStatus.BAD_REQUEST, "Richiesta multipart non valida", null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Content-Type della richiesta non supportato", null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Risorsa non trovata", null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return build(HttpStatus.CONFLICT, "I dati inviati sono in conflitto con dati gia' esistenti", null);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        // Le altre eccezioni standard di Spring MVC (es. metodo HTTP non supportato) portano gia' il proprio status
        if (ex instanceof org.springframework.web.ErrorResponse springError) {
            return build(springError.getStatusCode(), springError.getBody().getDetail(), null);
        }
        log.error("Errore non gestito", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Errore interno del server", null);
    }

    private static ResponseEntity<ErrorResponse> build(HttpStatusCode status, String message, List<String> details) {
        return ResponseEntity.status(status).body(ErrorResponse.of(status, message, details));
    }
}
