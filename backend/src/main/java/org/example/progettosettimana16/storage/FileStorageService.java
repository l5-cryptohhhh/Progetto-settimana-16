package org.example.progettosettimana16.storage;

import lombok.extern.slf4j.Slf4j;
import org.example.progettosettimana16.config.AppProperties;
import org.example.progettosettimana16.exception.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Validazione e salvataggio dei file caricati sul filesystem locale.
 * I file vengono salvati con un nome generato (UUID + estensione del formato reale), mai con il nome del client.
 */
@Slf4j
@Service
public class FileStorageService {

    private static final int MAX_ORIGINAL_NAME_LENGTH = 255;

    private final Path root;

    public FileStorageService(AppProperties properties) {
        this.root = properties.storage().uploadDir().toAbsolutePath().normalize();
    }

    /**
     * Verifica che il file non sia vuoto, rispetti la dimensione massima e che il suo contenuto
     * (magic bytes) corrisponda a uno dei formati ammessi.
     *
     * @return il formato reale del file
     */
    public FileType validate(MultipartFile file, Set<FileType> allowedTypes, DataSize maxSize) {
        if (file == null || file.isEmpty()) {
            throw ApiException.badRequest("Il file e' vuoto");
        }
        String name = originalName(file);
        if (file.getSize() > maxSize.toBytes()) {
            throw ApiException.contentTooLarge(
                    "Il file '" + name + "' supera la dimensione massima di " + maxSize.toMegabytes() + " MB");
        }
        byte[] header;
        try (InputStream in = file.getInputStream()) {
            header = in.readNBytes(FileTypeDetector.HEADER_LENGTH);
        } catch (IOException e) {
            throw ApiException.badRequest("Impossibile leggere il file '" + name + "'");
        }
        return FileTypeDetector.detect(header)
                .filter(allowedTypes::contains)
                .orElseThrow(() -> ApiException.unsupportedMediaType(
                        "Il formato del file '" + name + "' non e' supportato. Formati ammessi: " + describe(allowedTypes)));
    }

    public StoredFile store(MultipartFile file, StorageArea area, FileType type) {
        String storedFilename = UUID.randomUUID() + "." + type.extension();
        Path target = directory(area).resolve(storedFilename);
        try {
            Files.createDirectories(target.getParent());
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Salvataggio del file non riuscito", e);
        }
        return new StoredFile(storedFilename, originalName(file), type, file.getSize());
    }

    /**
     * Restituisce il percorso di un file salvato, impedendo di uscire dalla cartella (path traversal).
     */
    public Path load(StorageArea area, String filename) {
        Path directory = directory(area);
        try {
            Path file = directory.resolve(filename).normalize();
            if (!directory.equals(file.getParent())) {
                throw ApiException.badRequest("Nome file non valido");
            }
            return file;
        } catch (InvalidPathException e) {
            throw ApiException.badRequest("Nome file non valido");
        }
    }

    public void delete(StorageArea area, String filename) {
        try {
            Files.deleteIfExists(load(area, filename));
        } catch (IOException | RuntimeException e) {
            log.warn("Impossibile eliminare il file {} dall'area {}", filename, area, e);
        }
    }

    private Path directory(StorageArea area) {
        return root.resolve(area.folder());
    }

    private static String originalName(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null) {
            return "file";
        }
        name = name.substring(Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\')) + 1).strip();
        if (name.isEmpty()) {
            return "file";
        }
        return name.length() > MAX_ORIGINAL_NAME_LENGTH ? name.substring(name.length() - MAX_ORIGINAL_NAME_LENGTH) : name;
    }

    private static String describe(Set<FileType> types) {
        return types.stream().map(Enum::name).sorted().collect(Collectors.joining(", "));
    }
}
