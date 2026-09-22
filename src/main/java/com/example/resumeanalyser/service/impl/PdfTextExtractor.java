package com.example.resumeanalyser.service.impl;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class PdfTextExtractor implements FileTextExtractor {

    @Override
    public boolean supports(String extension) {
        return "pdf".equalsIgnoreCase(extension);
    }

    @Override
    public String extract(InputStream in) throws IOException {
        byte[] bytes = in.readAllBytes();
        try (PDDocument document = Loader.loadPDF(bytes)) {
            StringBuilder text = new StringBuilder();
            PDFTextStripper stripper = new PDFTextStripper();
            int pageCount = document.getNumberOfPages();
            for (int page = 1; page <= pageCount; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                text.append(stripper.getText(document));
            }
            return text.toString();
        }
    }
}
