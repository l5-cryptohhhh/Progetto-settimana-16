package org.example.progettosettimana16.api;

import com.jayway.jsonpath.JsonPath;
import org.example.progettosettimana16.ocr.OcrProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base comune dei test d'integrazione: contesto Spring completo su H2, MockMvc e OCR simulato
 * (l'OCR reale e' coperto da TesseractOcrEngineTest).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class IntegrationTestSupport {

    static final String PASSWORD = "password123";

    private static final AtomicInteger COUNTER = new AtomicInteger();

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    OcrProcessor ocrProcessor;

    record TestUser(long id, String username, String token) {
        String email() {
            return username + "@example.com";
        }

        String bearer() {
            return "Bearer " + token;
        }
    }

    TestUser registerUser() throws Exception {
        String username = "utente" + COUNTER.incrementAndGet();
        String body = "{\"username\":\"%s\",\"email\":\"%s@example.com\",\"password\":\"%s\"}"
                .formatted(username, username, PASSWORD);
        String json = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return new TestUser(longAt(json, "$.user.id"), username, JsonPath.read(json, "$.token"));
    }

    static long longAt(String json, String path) {
        return ((Number) JsonPath.read(json, path)).longValue();
    }

    static byte[] pngBytes() {
        return imageBytes("png", BufferedImage.TYPE_INT_ARGB);
    }

    static byte[] jpegBytes() {
        return imageBytes("jpg", BufferedImage.TYPE_INT_RGB);
    }

    static byte[] pdfBytes() {
        return "%PDF-1.4\n1 0 obj\n<<>>\nendobj\ntrailer\n<<>>\n%%EOF".getBytes(StandardCharsets.US_ASCII);
    }

    /** File che inizia con la firma indicata ed e' lungo esattamente {@code size} byte. */
    static byte[] fileOfSize(byte[] header, int size) {
        byte[] content = new byte[size];
        System.arraycopy(header, 0, content, 0, header.length);
        return content;
    }

    private static byte[] imageBytes(String format, int imageType) {
        BufferedImage image = new BufferedImage(8, 8, imageType);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, format, out);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return out.toByteArray();
    }
}
