package org.example.progettosettimana16.entity;

import org.example.progettosettimana16.exception.ApiException;

/**
 * Origine delle foto di un post.
 * CAMERA: una singola foto scattata con la fotocamera. UPLOAD: una o piu' foto selezionate dal dispositivo.
 */
public enum PhotoSource {
    CAMERA,
    UPLOAD;

    public static final int MAX_UPLOAD_PHOTOS = 10;

    public void validatePhotoCount(int count) {
        switch (this) {
            case CAMERA -> {
                if (count != 1) {
                    throw ApiException.badRequest("Un post scattato con la fotocamera deve contenere esattamente una foto");
                }
            }
            case UPLOAD -> {
                if (count < 1 || count > MAX_UPLOAD_PHOTOS) {
                    throw ApiException.badRequest(
                            "Un post con upload deve contenere da 1 a " + MAX_UPLOAD_PHOTOS + " foto");
                }
            }
        }
    }
}
