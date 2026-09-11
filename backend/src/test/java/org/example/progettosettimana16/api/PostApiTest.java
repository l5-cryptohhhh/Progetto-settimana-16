package org.example.progettosettimana16.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostApiTest extends IntegrationTestSupport {

    private static final int TEN_MB = 10 * 1024 * 1024;

    private static MockMultipartFile photo(String filename, String contentType, byte[] content) {
        return new MockMultipartFile("photos", filename, contentType, content);
    }

    private String createSimplePost(TestUser user, String caption) throws Exception {
        return mockMvc.perform(multipart("/api/posts")
                        .file(photo("foto.png", "image/png", pngBytes()))
                        .param("source", "CAMERA")
                        .param("caption", caption)
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void createsCameraPostWithOnePhotoAndPostLevelLocation() throws Exception {
        TestUser user = registerUser();
        byte[] png = pngBytes();

        String json = mockMvc.perform(multipart("/api/posts")
                        .file(photo("scatto.png", "image/png", png))
                        .param("source", "CAMERA")
                        .param("caption", "Tramonto al Colosseo")
                        .param("latitude", "41.8902")
                        .param("longitude", "12.4922")
                        .param("address", "Piazza del Colosseo, Roma")
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.photoSource").value("CAMERA"))
                .andExpect(jsonPath("$.caption").value("Tramonto al Colosseo"))
                .andExpect(jsonPath("$.author.id").value(user.id()))
                .andExpect(jsonPath("$.author.username").value(user.username()))
                .andExpect(jsonPath("$.location.latitude").value(41.8902))
                .andExpect(jsonPath("$.location.longitude").value(12.4922))
                .andExpect(jsonPath("$.location.address").value("Piazza del Colosseo, Roma"))
                .andExpect(jsonPath("$.photos", hasSize(1)))
                .andExpect(jsonPath("$.photos[0].originalFilename").value("scatto.png"))
                .andExpect(jsonPath("$.photos[0].contentType").value("image/png"))
                .andExpect(jsonPath("$.photos[0].size").value(png.length))
                .andExpect(jsonPath("$.photos[0].location").doesNotExist())
                .andReturn().getResponse().getContentAsString();

        String photoUrl = JsonPath.read(json, "$.photos[0].url");
        assertThat(photoUrl).startsWith("/api/files/photos/");
        mockMvc.perform(get(photoUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(content().bytes(png));
    }

    @Test
    void createsUploadPostWithMultiplePhotosInOrderAndDetectsRealFormat() throws Exception {
        TestUser user = registerUser();

        mockMvc.perform(multipart("/api/posts")
                        .file(photo("prima.jpg", "image/jpeg", jpegBytes()))
                        .file(photo("seconda.jpg", "image/jpeg", pngBytes()))
                        .file(photo("terza.png", "image/png", pngBytes()))
                        .param("source", "UPLOAD")
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.photoSource").value("UPLOAD"))
                .andExpect(jsonPath("$.photos", hasSize(3)))
                .andExpect(jsonPath("$.photos[0].originalFilename").value("prima.jpg"))
                .andExpect(jsonPath("$.photos[0].contentType").value("image/jpeg"))
                .andExpect(jsonPath("$.photos[1].originalFilename").value("seconda.jpg"))
                .andExpect(jsonPath("$.photos[1].contentType").value("image/png"))
                .andExpect(jsonPath("$.photos[2].originalFilename").value("terza.png"))
                .andExpect(jsonPath("$.location").doesNotExist());
    }

    @Test
    void rejectsCameraPostWithMoreThanOnePhoto() throws Exception {
        TestUser user = registerUser();

        mockMvc.perform(multipart("/api/posts")
                        .file(photo("a.png", "image/png", pngBytes()))
                        .file(photo("b.png", "image/png", pngBytes()))
                        .param("source", "CAMERA")
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsFileThatIsNotReallyAnImageAndCreatesNothing() throws Exception {
        TestUser user = registerUser();

        mockMvc.perform(multipart("/api/posts")
                        .file(photo("valida.png", "image/png", pngBytes()))
                        .file(photo("finta.jpg", "image/jpeg", "questo e' testo, non una foto".getBytes()))
                        .param("source", "UPLOAD")
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(415));

        mockMvc.perform(get("/api/users/{id}/posts", user.id()).header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void rejectsPdfAsPhoto() throws Exception {
        TestUser user = registerUser();

        mockMvc.perform(multipart("/api/posts")
                        .file(photo("documento.pdf", "application/pdf", pdfBytes()))
                        .param("source", "UPLOAD")
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void rejectsPhotoLargerThanTenMegabytes() throws Exception {
        TestUser user = registerUser();
        byte[] tooLarge = fileOfSize(pngBytes(), TEN_MB + 1);

        mockMvc.perform(multipart("/api/posts")
                        .file(photo("enorme.png", "image/png", tooLarge))
                        .param("source", "CAMERA")
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isContentTooLarge());
    }

    @Test
    void rejectsMissingOrUnknownSource() throws Exception {
        TestUser user = registerUser();

        mockMvc.perform(multipart("/api/posts")
                        .file(photo("a.png", "image/png", pngBytes()))
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isBadRequest());

        mockMvc.perform(multipart("/api/posts")
                        .file(photo("a.png", "image/png", pngBytes()))
                        .param("source", "GALLERIA")
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsPostWithoutPhotosOrWithOnlyEmptyFiles() throws Exception {
        TestUser user = registerUser();

        mockMvc.perform(multipart("/api/posts")
                        .param("source", "UPLOAD")
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isBadRequest());

        mockMvc.perform(multipart("/api/posts")
                        .file(photo("vuota.png", "image/png", new byte[0]))
                        .param("source", "CAMERA")
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsIncompleteLocation() throws Exception {
        TestUser user = registerUser();

        mockMvc.perform(multipart("/api/posts")
                        .file(photo("a.png", "image/png", pngBytes()))
                        .param("source", "CAMERA")
                        .param("latitude", "41.89")
                        .header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void creatingPostRequiresAuthentication() throws Exception {
        mockMvc.perform(multipart("/api/posts")
                        .file(photo("a.png", "image/png", pngBytes()))
                        .param("source", "CAMERA"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void feedReturnsNewestPostsFirstWithPagination() throws Exception {
        TestUser user = registerUser();
        createSimplePost(user, "primo post");
        createSimplePost(user, "secondo post");

        mockMvc.perform(get("/api/posts").param("page", "0").param("size", "2").header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].caption").value("secondo post"))
                .andExpect(jsonPath("$.content[1].caption").value("primo post"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2));
    }

    @Test
    void feedPageSizeIsCappedAtFifty() throws Exception {
        TestUser user = registerUser();

        mockMvc.perform(get("/api/posts").param("size", "500").header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(50));
    }

    @Test
    void userPostsContainOnlyThatUsersPosts() throws Exception {
        TestUser alice = registerUser();
        TestUser bob = registerUser();
        createSimplePost(alice, "post di alice");
        createSimplePost(bob, "post di bob");

        mockMvc.perform(get("/api/users/{id}/posts", alice.id()).header(AUTHORIZATION, bob.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].caption").value("post di alice"));
    }

    @Test
    void getsPostByIdAndReturns404ForUnknownPost() throws Exception {
        TestUser user = registerUser();
        long postId = longAt(createSimplePost(user, "dettaglio"), "$.id");

        mockMvc.perform(get("/api/posts/{id}", postId).header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caption").value("dettaglio"));

        mockMvc.perform(get("/api/posts/{id}", 987654321L).header(AUTHORIZATION, user.bearer()))
                .andExpect(status().isNotFound());
    }

    @Test
    void onlyAuthorCanDeletePostAndItsPhotosAreRemoved() throws Exception {
        TestUser author = registerUser();
        TestUser other = registerUser();
        String json = createSimplePost(author, "da cancellare");
        long postId = longAt(json, "$.id");
        String photoUrl = JsonPath.read(json, "$.photos[0].url");

        mockMvc.perform(delete("/api/posts/{id}", postId).header(AUTHORIZATION, other.bearer()))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/posts/{id}", postId).header(AUTHORIZATION, author.bearer()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/posts/{id}", postId).header(AUTHORIZATION, author.bearer()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get(photoUrl))
                .andExpect(status().isNotFound());
    }

    @Test
    void unknownPhotoFileReturns404() throws Exception {
        mockMvc.perform(get("/api/files/photos/{filename}", "00000000-0000-0000-0000-000000000000.png"))
                .andExpect(status().isNotFound());
    }
}
