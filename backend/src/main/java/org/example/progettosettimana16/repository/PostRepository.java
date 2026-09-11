package org.example.progettosettimana16.repository;

import org.example.progettosettimana16.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    /** Feed paginato; l'autore viene caricato nella stessa query, le foto a blocchi (batch fetch). */
    @EntityGraph(attributePaths = "author")
    Page<Post> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = "author")
    Page<Post> findByAuthorId(Long authorId, Pageable pageable);
}
