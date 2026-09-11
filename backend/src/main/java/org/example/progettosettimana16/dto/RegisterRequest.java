package org.example.progettosettimana16.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "e' obbligatorio")
        @Size(min = 3, max = 30, message = "deve contenere da 3 a 30 caratteri")
        @Pattern(regexp = "^[A-Za-z0-9._]*$", message = "puo' contenere solo lettere, numeri, punto e underscore")
        String username,

        @NotBlank(message = "e' obbligatoria")
        @Email(message = "non e' un indirizzo email valido")
        @Size(max = 255, message = "e' troppo lunga")
        String email,

        @NotBlank(message = "e' obbligatoria")
        @Size(min = 8, max = 72, message = "deve contenere da 8 a 72 caratteri")
        String password) {
}
