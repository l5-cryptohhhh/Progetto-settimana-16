package org.example.progettosettimana16.storage;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class FileTypeDetectorTest {

    private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = (byte) values[i];
        }
        return result;
    }

    @Test
    void detectsJpeg() {
        assertThat(FileTypeDetector.detect(bytes(0xFF, 0xD8, 0xFF, 0xE0, 0x00, 0x10, 'J', 'F', 'I', 'F', 0x00, 0x01)))
                .contains(FileType.JPEG);
    }

    @Test
    void detectsPng() {
        assertThat(FileTypeDetector.detect(bytes(0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D)))
                .contains(FileType.PNG);
    }

    @Test
    void detectsWebp() {
        assertThat(FileTypeDetector.detect(bytes('R', 'I', 'F', 'F', 0x24, 0x00, 0x00, 0x00, 'W', 'E', 'B', 'P')))
                .contains(FileType.WEBP);
    }

    @Test
    void rejectsRiffContainerThatIsNotWebp() {
        assertThat(FileTypeDetector.detect(bytes('R', 'I', 'F', 'F', 0x24, 0x00, 0x00, 0x00, 'W', 'A', 'V', 'E')))
                .isEmpty();
    }

    @Test
    void detectsPdf() {
        assertThat(FileTypeDetector.detect("%PDF-1.7\n%âãÏÓ".getBytes(StandardCharsets.ISO_8859_1)))
                .contains(FileType.PDF);
    }

    @Test
    void detectsLittleEndianTiff() {
        assertThat(FileTypeDetector.detect(bytes(0x49, 0x49, 0x2A, 0x00, 0x08, 0x00, 0x00, 0x00)))
                .contains(FileType.TIFF);
    }

    @Test
    void detectsBigEndianTiff() {
        assertThat(FileTypeDetector.detect(bytes(0x4D, 0x4D, 0x00, 0x2A, 0x00, 0x00, 0x00, 0x08)))
                .contains(FileType.TIFF);
    }

    @Test
    void doesNotRecognizePlainTextRenamedAsImage() {
        assertThat(FileTypeDetector.detect("questa non e' una foto".getBytes(StandardCharsets.UTF_8)))
                .isEmpty();
    }

    @Test
    void doesNotRecognizeTooShortHeader() {
        assertThat(FileTypeDetector.detect(bytes(0xFF, 0xD8))).isEmpty();
    }
}
