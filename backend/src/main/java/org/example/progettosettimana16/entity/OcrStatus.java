package org.example.progettosettimana16.entity;

/**
 * Stato dell'elaborazione OCR di un documento: PENDING -> PROCESSING -> COMPLETED | FAILED.
 */
public enum OcrStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}
