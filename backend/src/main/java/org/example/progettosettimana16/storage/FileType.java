package org.example.progettosettimana16.storage;

import java.util.Arrays;
import java.util.Optional;

/**
 * Formati di file riconosciuti dall'applicazione.
 * Il tipo viene determinato dal contenuto (magic bytes), mai dal nome o dal Content-Type dichiarato.
 */
public enum FileType {
    JPEG("image/jpeg", "jpg"),
    PNG("image/png", "png"),
    WEBP("image/webp", "webp"),
    PDF("application/pdf", "pdf"),
    TIFF("image/tiff", "tif");

    private final String mimeType;
    private final String extension;

    FileType(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public String mimeType() {
        return mimeType;
    }

    public String extension() {
        return extension;
    }

    public static Optional<FileType> fromExtension(String extension) {
        return Arrays.stream(values())
                .filter(type -> type.extension.equalsIgnoreCase(extension))
                .findFirst();
    }
}
