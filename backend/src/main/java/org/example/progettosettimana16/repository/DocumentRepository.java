package org.example.progettosettimana16.repository;

import org.example.progettosettimana16.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByOwnerIdOrderByUploadedAtDescIdDesc(Long ownerId);

    /** Cerca un documento solo tra quelli dell'utente: i documenti altrui risultano "non trovati". */
    Optional<Document> findByIdAndOwnerId(Long id, Long ownerId);
}
