package com.example.resumeanalyser.service;

import com.example.resumeanalyser.service.impl.DocxTextExtractor;
import com.example.resumeanalyser.service.impl.PdfTextExtractor;
import com.example.resumeanalyser.service.impl.TxtTextExtractor;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextExtractionServiceTest {

    @Test
    void extractsTextFromTxtBytes() {
        TextExtractionService service = new TextExtractionService(
                List.of(new PdfTextExtractor(), new DocxTextExtractor(), new TxtTextExtractor()));

        byte[] bytes = "Java Developer with Python experience".getBytes(StandardCharsets.UTF_8);

        String extracted = service.extract("resume.txt", bytes);

        assertEquals("Java Developer with Python experience", extracted);
    }
}
