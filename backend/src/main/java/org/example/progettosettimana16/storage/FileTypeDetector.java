package org.example.progettosettimana16.storage;

import java.util.Optional;

/**
 * Riconosce il formato reale di un file leggendo i primi byte (firma / magic bytes).
 * Un file di testo rinominato in ".jpg" non viene quindi scambiato per un'immagine.
 */
public final class FileTypeDetector {

    /** Numero di byte iniziali necessari per riconoscere tutti i formati supportati. */
    public static final int HEADER_LENGTH = 12;

    private FileTypeDetector() {
    }

    public static Optional<FileType> detect(byte[] header) {
        if (header == null || header.length < 4) {
            return Optional.empty();
        }
        if (startsWith(header, 0, 0xFF, 0xD8, 0xFF)) {
            return Optional.of(FileType.JPEG);
        }
        if (startsWith(header, 0, 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A)) {
            return Optional.of(FileType.PNG);
        }
        if (startsWith(header, 0, 'R', 'I', 'F', 'F') && startsWith(header, 8, 'W', 'E', 'B', 'P')) {
            return Optional.of(FileType.WEBP);
        }
        if (startsWith(header, 0, '%', 'P', 'D', 'F', '-')) {
            return Optional.of(FileType.PDF);
        }
        if (startsWith(header, 0, 'I', 'I', 0x2A, 0x00) || startsWith(header, 0, 'M', 'M', 0x00, 0x2A)) {
            return Optional.of(FileType.TIFF);
        }
        return Optional.empty();
    }

    private static boolean startsWith(byte[] data, int offset, int... signature) {
        if (data.length < offset + signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if ((data[offset + i] & 0xFF) != signature[i]) {
                return false;
            }
        }
        return true;
    }
}
