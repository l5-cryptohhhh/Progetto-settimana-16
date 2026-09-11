package org.example.progettosettimana16.controller;

import lombok.RequiredArgsConstructor;
import org.example.progettosettimana16.exception.ApiException;
import org.example.progettosettimana16.storage.FileStorageService;
import org.example.progettosettimana16.storage.FileType;
import org.example.progettosettimana16.storage.StorageArea;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Serve i file delle foto dei post. Endpoint pubblico: i nomi dei file sono UUID non indovinabili.
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService storage;

    @GetMapping("/photos/{filename}")
    public ResponseEntity<Resource> photo(@PathVariable("filename") String filename) {
        Path file = storage.load(StorageArea.PHOTOS, filename);
        FileType type = FileType.fromExtension(StringUtils.getFilenameExtension(filename))
                .filter(found -> Files.isRegularFile(file))
                .orElseThrow(() -> ApiException.notFound("Foto non trovata"));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(type.mimeType()))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .body(new FileSystemResource(file));
    }
}
