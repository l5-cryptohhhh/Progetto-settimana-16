package org.example.progettosettimana16.storage;

/** Risultato del salvataggio di un file su disco. */
public record StoredFile(String storedFilename, String originalFilename, FileType type, long size) {
}
