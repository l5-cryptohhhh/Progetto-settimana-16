package org.example.progettosettimana16.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Documento caricato sul profilo di un utente, con il testo estratto tramite OCR.
 */
@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
public class Document {

    public static final int MAX_ERROR_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, unique = true)
    private String storedFilename;

    @Column(nullable = false)
    private String originalFilename;

    /** MIME type rilevato dai magic bytes. */
    @Column(nullable = false, length = 50)
    private String contentType;

    @Column(nullable = false)
    private long sizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OcrStatus ocrStatus;

    @Column(columnDefinition = "TEXT")
    private String extractedText;

    @Column(length = MAX_ERROR_LENGTH)
    private String ocrError;

    @Column(nullable = false, updatable = false)
    private Instant uploadedAt;

    private Instant processedAt;

    @PrePersist
    void onCreate() {
        uploadedAt = Instant.now();
    }
}
