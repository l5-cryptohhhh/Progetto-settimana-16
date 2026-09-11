package org.example.progettosettimana16.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Metadati di una foto di un post. Il file vero e proprio e' salvato su disco con nome {@code storedFilename}.
 */
@Entity
@Table(name = "post_photos")
@Getter
@Setter
@NoArgsConstructor
public class PostPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, unique = true)
    private String storedFilename;

    @Column(nullable = false)
    private String originalFilename;

    /** MIME type rilevato dai magic bytes, non quello dichiarato dal client. */
    @Column(nullable = false, length = 50)
    private String contentType;

    @Column(nullable = false)
    private long sizeBytes;

    @Column(nullable = false)
    private int displayOrder;
}
