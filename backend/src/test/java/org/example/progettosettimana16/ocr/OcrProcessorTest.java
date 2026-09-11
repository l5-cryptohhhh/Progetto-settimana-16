package org.example.progettosettimana16.ocr;

import net.sourceforge.tess4j.TesseractException;
import org.example.progettosettimana16.config.AppProperties;
import org.example.progettosettimana16.entity.Document;
import org.example.progettosettimana16.entity.OcrStatus;
import org.example.progettosettimana16.repository.DocumentRepository;
import org.example.progettosettimana16.storage.FileStorageService;
import org.example.progettosettimana16.storage.FileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.util.unit.DataSize;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OcrProcessorTest {

    private static final long DOCUMENT_ID = 7L;

    @TempDir
    Path uploadDir;

    private final DocumentRepository repository = mock(DocumentRepository.class);
    private final TesseractOcrEngine engine = mock(TesseractOcrEngine.class);
    private OcrProcessor processor;

    @BeforeEach
    void setUp() {
        AppProperties properties = new AppProperties(null,
                new AppProperties.Storage(uploadDir, DataSize.ofMegabytes(10), DataSize.ofMegabytes(20)),
                null, null, null);
        processor = new OcrProcessor(repository, new FileStorageService(properties), engine);
        when(repository.save(any(Document.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Document storedDocument(String storedFilename) {
        Document document = new Document();
        document.setId(DOCUMENT_ID);
        document.setStoredFilename(storedFilename);
        document.setOcrStatus(OcrStatus.PENDING);
        when(repository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));
        return document;
    }

    @Test
    void storesTrimmedTextAndMarksDocumentCompleted() throws Exception {
        Document document = storedDocument("abc.pdf");
        AtomicReference<OcrStatus> statusDuringOcr = new AtomicReference<>();
        when(engine.extractText(argThat(path -> path.endsWith(Path.of("documents", "abc.pdf"))), eq(FileType.PDF)))
                .thenAnswer(invocation -> {
                    statusDuringOcr.set(document.getOcrStatus());
                    return "  Testo del documento\n";
                });

        processor.process(DOCUMENT_ID);

        assertThat(statusDuringOcr.get()).isEqualTo(OcrStatus.PROCESSING);
        assertThat(document.getOcrStatus()).isEqualTo(OcrStatus.COMPLETED);
        assertThat(document.getExtractedText()).isEqualTo("Testo del documento");
        assertThat(document.getOcrError()).isNull();
        assertThat(document.getProcessedAt()).isNotNull();
    }

    @Test
    void marksDocumentFailedWithReasonWhenOcrFails() throws Exception {
        Document document = storedDocument("scansione.png");
        when(engine.extractText(any(Path.class), eq(FileType.PNG)))
                .thenThrow(new TesseractException("immagine illeggibile"));

        processor.process(DOCUMENT_ID);

        assertThat(document.getOcrStatus()).isEqualTo(OcrStatus.FAILED);
        assertThat(document.getOcrError()).contains("immagine illeggibile");
        assertThat(document.getExtractedText()).isNull();
        assertThat(document.getProcessedAt()).isNotNull();
    }

    @Test
    void truncatesVeryLongErrorMessages() throws Exception {
        Document document = storedDocument("scansione.tif");
        when(engine.extractText(any(Path.class), eq(FileType.TIFF)))
                .thenThrow(new TesseractException("x".repeat(5000)));

        processor.process(DOCUMENT_ID);

        assertThat(document.getOcrError()).hasSizeLessThanOrEqualTo(Document.MAX_ERROR_LENGTH);
    }

    @Test
    void ignoresDocumentDeletedBeforeProcessing() {
        when(repository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());

        assertThatCode(() -> processor.process(DOCUMENT_ID)).doesNotThrowAnyException();
    }
}
