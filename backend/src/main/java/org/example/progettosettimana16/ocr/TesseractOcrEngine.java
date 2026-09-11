package org.example.progettosettimana16.ocr;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.example.progettosettimana16.config.AppProperties;
import org.example.progettosettimana16.storage.FileType;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Estrae il testo da immagini e PDF con Tesseract (tramite tess4j).
 * I PDF vengono prima renderizzati pagina per pagina in immagini con PDFBox.
 */
@Component
public class TesseractOcrEngine {

    private static final float PDF_RENDER_DPI = 300;

    private final String tessdataPath;
    private final String language;

    public TesseractOcrEngine(AppProperties properties) {
        this.tessdataPath = properties.ocr().tessdataPath();
        this.language = properties.ocr().language();
    }

    public String extractText(Path file, FileType type) throws IOException, TesseractException {
        // Tesseract non e' thread-safe: una nuova istanza per ogni elaborazione
        ITesseract tesseract = newTesseract();
        if (type == FileType.PDF) {
            return extractFromPdf(tesseract, file);
        }
        return tesseract.doOCR(file.toFile());
    }

    private String extractFromPdf(ITesseract tesseract, Path file) throws IOException, TesseractException {
        StringBuilder text = new StringBuilder();
        try (PDDocument document = Loader.loadPDF(file.toFile())) {
            PDFRenderer renderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage image = renderer.renderImageWithDPI(page, PDF_RENDER_DPI, ImageType.GRAY);
                text.append(tesseract.doOCR(image)).append(System.lineSeparator());
            }
        }
        return text.toString();
    }

    private ITesseract newTesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessdataPath);
        tesseract.setLanguage(language);
        return tesseract;
    }
}
