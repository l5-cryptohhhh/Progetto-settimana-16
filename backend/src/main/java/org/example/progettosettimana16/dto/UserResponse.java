package org.example.progettosettimana16.dto;

import org.example.progettosettimana16.entity.User;

import java.time.Instant;

public record UserResponse(Long id, String username, String email, Instant createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt());
    }
}
