package org.example.progettosettimana16.dto;

import org.example.progettosettimana16.entity.PostPhoto;

/**
 * Foto di un post. {@code url} e' relativo al backend e non richiede autenticazione (utilizzabile in un tag img).
 */
public record PhotoResponse(Long id, String url, String originalFilename, String contentType, long size) {

    public static final String PHOTO_URL_PREFIX = "/api/files/photos/";

    public static PhotoResponse from(PostPhoto photo) {
        return new PhotoResponse(photo.getId(), PHOTO_URL_PREFIX + photo.getStoredFilename(),
                photo.getOriginalFilename(), photo.getContentType(), photo.getSizeBytes());
    }
}
