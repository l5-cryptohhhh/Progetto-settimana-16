package org.example.progettosettimana16.storage;

/** Sottocartelle della directory di upload. */
public enum StorageArea {
    PHOTOS("photos"),
    DOCUMENTS("documents");

    private final String folder;

    StorageArea(String folder) {
        this.folder = folder;
    }

    public String folder() {
        return folder;
    }
}
