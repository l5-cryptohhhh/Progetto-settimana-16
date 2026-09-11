package org.example.progettosettimana16.service;

import lombok.RequiredArgsConstructor;
import org.example.progettosettimana16.config.AppProperties;
import org.example.progettosettimana16.dto.DocumentResponse;
import org.example.progettosettimana16.entity.Document;
import org.example.progettosettimana16.entity.OcrStatus;
import org.example.progettosettimana16.entity.User;
import org.example.progettosettimana16.exception.ApiException;
import org.example.progettosettimana16.ocr.OcrProcessor;
import org.example.progettosettimana16.repository.DocumentRepository;
import org.example.progettosettimana16.storage.FileStorageService;
import org.example.progettosettimana16.storage.FileType;
import org.example.progettosettimana16.storage.StorageArea;
import org.example.progettosettimana16.storage.StoredFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private static final Set<FileType> ALLOWED_DOCUMENT_TYPES =
            EnumSet.of(FileType.PDF, FileType.JPEG, FileType.PNG, FileType.TIFF);

    private final DocumentRepository documentRepository;
    private final FileStorageService storage;
    private final OcrProcessor ocrProcessor;
    private final AppProperties properties;

    /** File di un documento pronto per il download. */
    public record DocumentDownload(Path file, String originalFilename, String contentType) {
    }

    /**
     * Salva il documento con stato PENDING e avvia l'OCR in background.
     * Il metodo non e' transazionale di proposito: il record deve essere gia' salvato nel database
     * quando il thread OCR lo va a leggere.
     */
    public DocumentResponse upload(User owner, MultipartFile file) {
        FileType type = storage.validate(file, ALLOWED_DOCUMENT_TYPES, properties.storage().maxDocumentSize());
        StoredFile stored = storage.store(file, StorageArea.DOCUMENTS, type);

        Document document = new Document();
        document.setOwner(owner);
        document.setStoredFilename(stored.storedFilename());
        document.setOriginalFilename(stored.originalFilename());
        document.setContentType(type.mimeType());
        document.setSizeBytes(stored.size());
        document.setOcrStatus(OcrStatus.PENDING);
        try {
            document = documentRepository.save(document);
        } catch (RuntimeException e) {
            storage.delete(StorageArea.DOCUMENTS, stored.storedFilename());
            throw e;
        }

        ocrProcessor.process(document.getId());
        return DocumentResponse.from(document);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> list(User owner) {
        return documentRepository.findByOwnerIdOrderByUploadedAtDescIdDesc(owner.getId()).stream()
                .map(DocumentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentResponse get(User owner, Long id) {
        return DocumentResponse.from(findOwned(owner, id));
    }

    @Transactional(readOnly = true)
    public DocumentDownload download(User owner, Long id) {
        Document document = findOwned(owner, id);
        Path file = storage.load(StorageArea.DOCUMENTS, document.getStoredFilename());
        if (!Files.isRegularFile(file)) {
            throw ApiException.notFound("File del documento non trovato");
        }
        return new DocumentDownload(file, document.getOriginalFilename(), document.getContentType());
    }

    public void delete(User owner, Long id) {
        Document document = findOwned(owner, id);
        documentRepository.delete(document);
        storage.delete(StorageArea.DOCUMENTS, document.getStoredFilename());
    }

    private Document findOwned(User owner, Long id) {
        return documentRepository.findByIdAndOwnerId(id, owner.getId())
                .orElseThrow(() -> ApiException.notFound("Documento non trovato"));
    }
}
