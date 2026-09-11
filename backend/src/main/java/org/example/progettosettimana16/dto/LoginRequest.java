package org.example.progettosettimana16.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "e' obbligatoria") String email,
        @NotBlank(message = "e' obbligatoria") String password) {
}
