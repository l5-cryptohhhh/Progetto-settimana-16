package org.example.progettosettimana16.api;

import org.example.progettosettimana16.entity.Document;
import org.example.progettosettimana16.entity.OcrStatus;
import org.example.progettosettimana16.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DocumentApiTest extends IntegrationTestSupport {

    private static final int TWENTY_MB = 20 * 1024 * 1024;

    @Autowired
    DocumentRepository documentRepository;

    private static MockMultipartFile document(String filename, String contentType, byte[] content) {
        return new MockMultipartFile("file", filename, contentType, content);
    }

    private String upload(TestUser user, String filename, byte[] content) throws Exception {
        return mockMvc.perform(multipart("/api/users/me/documents")
                        .file(document(filename, "application/octet-stream", content))
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isAccepted())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void uploadingPdfReturnsPendingDocumentAndStartsOcr() throws Exception {
        TestUser user = registerUser();

        String json = mockMvc.perform(multipart("/api/users/me/documents")
                        .file(document("contratto.pdf", "application/pdf", pdfBytes()))
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.originalFilename").value("contratto.pdf"))
                .andExpect(jsonPath("$.contentType").value("application/pdf"))
                .andExpect(jsonPath("$.size").value(pdfBytes().length))
                .andExpect(jsonPath("$.ocrStatus").value("PENDING"))
                .andExpect(jsonPath("$.extractedText").doesNotExist())
                .andReturn().getResponse().getContentAsString();

        verify(ocrProcessor).process(longAt(json, "$.id"));
    }

    @Test
    void acceptsScannedImagesAsDocuments() throws Exception {
        TestUser user = registerUser();

        mockMvc.perform(multipart("/api/users/me/documents")
                        .file(document("scansione.jpg", "image/jpeg", jpegBytes()))
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.contentType").value("image/jpeg"));
    }

    @Test
    void rejectsUnsupportedDocumentFormatWithoutStartingOcr() throws Exception {
        TestUser user = registerUser();

        mockMvc.perform(multipart("/api/users/me/documents")
                        .file(document("appunti.pdf", "application/pdf", "solo testo".getBytes()))
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoInteractions(ocrProcessor);
    }

    @Test
    void rejectsDocumentLargerThanTwentyMegabytes() throws Exception {
        TestUser user = registerUser();

        mockMvc.perform(multipart("/api/users/me/documents")
                        .file(document("enorme.pdf", "application/pdf", fileOfSize(pdfBytes(), TWENTY_MB + 1)))
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isContentTooLarge());
    }

    @Test
    void listsOnlyOwnDocumentsNewestFirst() throws Exception {
        TestUser alice = registerUser();
        TestUser bob = registerUser();
        upload(alice, "primo.pdf", pdfBytes());
        upload(alice, "secondo.pdf", pdfBytes());
        upload(bob, "di-bob.pdf", pdfBytes());

        mockMvc.perform(get("/api/users/me/documents").header(AUTHORIZATION, alice.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].originalFilename").value("secondo.pdf"))
                .andExpect(jsonPath("$[1].originalFilename").value("primo.pdf"));
    }

    @Test
    void detailShowsTextExtractedByOcr() throws Exception {
        TestUser user = registerUser();
        long id = longAt(upload(user, "lettera.pdf", pdfBytes()), "$.id");
        Document document = documentRepository.findById(id).orElseThrow();
        document.setOcrStatus(OcrStatus.COMPLETED);
        document.setExtractedText("Gentile cliente, le confermiamo l'ordine.");
        document.setProcessedAt(Instant.now());
        documentRepository.save(document);

        mockMvc.perform(get("/api/users/me/documents/{id}", id).header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ocrStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.extractedText").value("Gentile cliente, le confermiamo l'ordine."))
                .andExpect(jsonPath("$.processedAt").isNotEmpty());
    }

    @Test
    void otherUsersCannotReadDownloadOrDeleteDocument() throws Exception {
        TestUser owner = registerUser();
        TestUser other = registerUser();
        long id = longAt(upload(owner, "privato.pdf", pdfBytes()), "$.id");

        mockMvc.perform(get("/api/users/me/documents/{id}", id).header(AUTHORIZATION, other.bearer()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/users/me/documents/{id}/file", id).header(AUTHORIZATION, other.bearer()))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/users/me/documents/{id}", id).header(AUTHORIZATION, other.bearer()))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/users/me/documents/{id}", id).header(AUTHORIZATION, owner.bearer()))
                .andExpect(status().isOk());
    }

    @Test
    void downloadReturnsOriginalFileAsAttachment() throws Exception {
        TestUser user = registerUser();
        byte[] pdf = pdfBytes();
        long id = longAt(upload(user, "fattura.pdf", pdf), "$.id");

        mockMvc.perform(get("/api/users/me/documents/{id}/file", id).header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string(CONTENT_DISPOSITION, containsString("attachment")))
                .andExpect(header().string(CONTENT_DISPOSITION, containsString("fattura.pdf")))
                .andExpect(content().bytes(pdf));
    }

    @Test
    void ownerCanDeleteDocument() throws Exception {
        TestUser user = registerUser();
        long id = longAt(upload(user, "vecchio.pdf", pdfBytes()), "$.id");

        mockMvc.perform(delete("/api/users/me/documents/{id}", id).header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/me/documents/{id}", id).header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isNotFound());
    }

    @Test
    void documentsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/users/me/documents"))
                .andExpect(status().isUnauthorized());
    }
}
