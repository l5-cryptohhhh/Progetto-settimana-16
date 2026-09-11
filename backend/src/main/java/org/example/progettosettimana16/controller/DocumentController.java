package org.example.progettosettimana16.controller;

import lombok.RequiredArgsConstructor;
import org.example.progettosettimana16.dto.DocumentResponse;
import org.example.progettosettimana16.entity.User;
import org.example.progettosettimana16.service.DocumentService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Documenti del profilo dell'utente autenticato.
 */
@RestController
@RequestMapping("/api/users/me/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /** Carica un documento (campo multipart "file"): risponde 202 perche' l'OCR prosegue in background. */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DocumentResponse upload(@AuthenticationPrincipal User user, @RequestParam("file") MultipartFile file) {
        return documentService.upload(user, file);
    }

    @GetMapping
    public List<DocumentResponse> list(@AuthenticationPrincipal User user) {
        return documentService.list(user);
    }

    @GetMapping("/{id}")
    public DocumentResponse get(@AuthenticationPrincipal User user, @PathVariable("id") Long id) {
        return documentService.get(user, id);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> download(@AuthenticationPrincipal User user, @PathVariable("id") Long id) {
        DocumentService.DocumentDownload download = documentService.download(user, id);
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(download.originalFilename(), StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(new FileSystemResource(download.file()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal User user, @PathVariable("id") Long id) {
        documentService.delete(user, id);
    }
}
