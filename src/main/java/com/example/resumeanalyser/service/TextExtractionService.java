package com.example.resumeanalyser.service;

import com.example.resumeanalyser.exception.ResumeProcessingException;
import com.example.resumeanalyser.service.impl.FileTextExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class TextExtractionService {

    private static final Logger log = LoggerFactory.getLogger(TextExtractionService.class);

    private final List<FileTextExtractor> extractors;

    public TextExtractionService(List<FileTextExtractor> extractors) {
        this.extractors = extractors;
    }

    public String extract(String filename, byte[] bytes) {
        String extension = extensionOf(filename);

        FileTextExtractor extractor = extractors.stream()
                .filter(e -> e.supports(extension))
                .findFirst()
                .orElseThrow(() -> new ResumeProcessingException("Unsupported file type: " + extension));

        try {
            return extractor.extract(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            log.error("Failed to extract text from {}", filename, e);
            throw new ResumeProcessingException("Failed to extract text from file: " + filename, e);
        }
    }

    private String extensionOf(String filename) {
        if (filename == null) {
            throw new ResumeProcessingException("Filename must not be null");
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            throw new ResumeProcessingException("File has no extension: " + filename);
        }
        return filename.substring(dotIndex + 1);
    }
}
