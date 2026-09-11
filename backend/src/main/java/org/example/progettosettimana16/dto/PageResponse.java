package org.example.progettosettimana16.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/** Pagina di risultati con un formato JSON stabile, indipendente dalla serializzazione interna di Spring Data. */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
