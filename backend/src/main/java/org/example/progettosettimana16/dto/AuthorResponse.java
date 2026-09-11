package org.example.progettosettimana16.dto;

import org.example.progettosettimana16.entity.User;

/** Dati pubblici dell'autore di un post (senza email). */
public record AuthorResponse(Long id, String username) {

    public static AuthorResponse from(User user) {
        return new AuthorResponse(user.getId(), user.getUsername());
    }
}
