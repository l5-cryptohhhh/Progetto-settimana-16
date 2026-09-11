package org.example.progettosettimana16.dto;

import org.example.progettosettimana16.entity.Document;
import org.example.progettosettimana16.entity.OcrStatus;

import java.time.Instant;

public record DocumentResponse(
        Long id,
        String originalFilename,
        String contentType,
        long size,
        OcrStatus ocrStatus,
        String extractedText,
        String ocrError,
        Instant uploadedAt,
        Instant processedAt) {

    public static DocumentResponse from(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getOriginalFilename(),
                document.getContentType(),
                document.getSizeBytes(),
                document.getOcrStatus(),
                document.getExtractedText(),
                document.getOcrError(),
                document.getUploadedAt(),
                document.getProcessedAt());
    }
}
