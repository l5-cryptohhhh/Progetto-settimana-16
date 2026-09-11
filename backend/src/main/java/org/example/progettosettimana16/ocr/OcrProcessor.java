package org.example.progettosettimana16.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.progettosettimana16.entity.Document;
import org.example.progettosettimana16.entity.OcrStatus;
import org.example.progettosettimana16.repository.DocumentRepository;
import org.example.progettosettimana16.storage.FileStorageService;
import org.example.progettosettimana16.storage.FileType;
import org.example.progettosettimana16.storage.StorageArea;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.time.Instant;

/**
 * Elabora in background l'OCR di un documento aggiornandone lo stato:
 * PENDING -> PROCESSING -> COMPLETED (con il testo) oppure FAILED (con il motivo).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OcrProcessor {

    private final DocumentRepository documentRepository;
    private final FileStorageService storage;
    private final TesseractOcrEngine engine;

    @Async("ocrExecutor")
    public void process(Long documentId) {
        Document document = documentRepository.findById(documentId).orElse(null);
        if (document == null) {
            log.info("Documento {} eliminato prima dell'elaborazione OCR", documentId);
            return;
        }
        document.setOcrStatus(OcrStatus.PROCESSING);
        documentRepository.save(document);

        try {
            FileType type = FileType.fromExtension(StringUtils.getFilenameExtension(document.getStoredFilename()))
                    .orElseThrow(() -> new IllegalStateException("Formato del documento non riconosciuto"));
            Path file = storage.load(StorageArea.DOCUMENTS, document.getStoredFilename());
            String text = engine.extractText(file, type);
            document.setExtractedText(text == null ? "" : text.strip());
            document.setOcrStatus(OcrStatus.COMPLETED);
        } catch (Exception e) {
            log.warn("OCR non riuscito per il documento {}", documentId, e);
            document.setOcrStatus(OcrStatus.FAILED);
            document.setOcrError(truncate(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
        }
        document.setProcessedAt(Instant.now());

        try {
            documentRepository.save(document);
        } catch (OptimisticLockingFailureException e) {
            log.info("Documento {} eliminato durante l'elaborazione OCR", documentId);
        }
    }

    private static String truncate(String message) {
        return message.length() <= Document.MAX_ERROR_LENGTH ? message : message.substring(0, Document.MAX_ERROR_LENGTH);
    }
}
