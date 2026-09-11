package org.example.progettosettimana16.ocr;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.example.progettosettimana16.config.AppProperties;
import org.example.progettosettimana16.storage.FileType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test con il vero Tesseract installato sulla macchina: viene saltato se tessdata non e' presente.
 */
@EnabledIf("tesseractIsInstalled")
class TesseractOcrEngineTest {

    private static final String TESSDATA = System.getenv().getOrDefault(
            "TESSDATA_PATH", "C:/Program Files/Tesseract-OCR/tessdata");

    @TempDir
    Path tempDir;

    private final TesseractOcrEngine engine = new TesseractOcrEngine(
            new AppProperties(null, null, new AppProperties.Ocr(TESSDATA, "ita+eng"), null, null));

    static boolean tesseractIsInstalled() {
        return Files.isRegularFile(Path.of(TESSDATA, "ita.traineddata"))
                && Files.isRegularFile(Path.of(TESSDATA, "eng.traineddata"));
    }

    private static BufferedImage imageWithText(String... lines) {
        BufferedImage image = new BufferedImage(1600, 160 + 120 * lines.length, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.setColor(Color.BLACK);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 72));
        for (int i = 0; i < lines.length; i++) {
            graphics.drawString(lines[i], 80, 140 + 120 * i);
        }
        graphics.dispose();
        return image;
    }

    private static String normalize(String text) {
        return text.toUpperCase().replaceAll("\\s+", " ");
    }

    @Test
    void extractsTextFromImage() throws Exception {
        Path png = tempDir.resolve("scansione.png");
        ImageIO.write(imageWithText("CIAO MONDO", "SOCIAL NETWORK 2026"), "png", png.toFile());

        String text = engine.extractText(png, FileType.PNG);

        assertThat(normalize(text)).contains("CIAO MONDO").contains("SOCIAL NETWORK 2026");
    }

    @Test
    void extractsTextFromEveryPageOfPdf() throws Exception {
        Path pdf = tempDir.resolve("documento.pdf");
        try (PDDocument document = new PDDocument()) {
            for (String pageText : new String[]{"PRIMA PAGINA", "SECONDA PAGINA"}) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                PDImageXObject image = LosslessFactory.createFromImage(document, imageWithText(pageText));
                try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                    float width = PDRectangle.A4.getWidth() - 40;
                    float height = width * image.getHeight() / image.getWidth();
                    stream.drawImage(image, 20, PDRectangle.A4.getHeight() - height - 20, width, height);
                }
            }
            document.save(pdf.toFile());
        }

        String text = engine.extractText(pdf, FileType.PDF);

        assertThat(normalize(text)).contains("PRIMA PAGINA").contains("SECONDA PAGINA");
    }
}
