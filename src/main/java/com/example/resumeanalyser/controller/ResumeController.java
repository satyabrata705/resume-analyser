package com.example.resumeanalyser.controller;

import com.example.resumeanalyser.dto.AnalysisResponse;
import com.example.resumeanalyser.dto.ExtractedTextResponse;
import com.example.resumeanalyser.dto.HealthResponse;
import com.example.resumeanalyser.service.ResumeAnalyzerService;
import com.example.resumeanalyser.service.TextExtractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/resumes")
@Tag(name = "Resumes", description = "Resume text extraction and analysis")
public class ResumeController {

    private static final Logger log = LoggerFactory.getLogger(ResumeController.class);

    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;
    private static final List<String> ALLOWED_EXTENSIONS = List.of("pdf", "docx", "txt");

    private final TextExtractionService textExtractionService;
    private final ResumeAnalyzerService resumeAnalyzerService;

    public ResumeController(TextExtractionService textExtractionService,
                             ResumeAnalyzerService resumeAnalyzerService) {
        this.textExtractionService = textExtractionService;
        this.resumeAnalyzerService = resumeAnalyzerService;
    }

    @Operation(summary = "Analyze a resume against a job description")
    @PostMapping(value = "/analyze", consumes = "multipart/form-data")
    public AnalysisResponse analyze(@RequestPart("resume") MultipartFile resume,
                                     @RequestParam("jobDescription") String jobDescription) {
        validateFile(resume);
        log.info("Analyzing resume '{}' ({} bytes)", resume.getOriginalFilename(), resume.getSize());

        String resumeText = extractText(resume);
        return resumeAnalyzerService.analyze(resumeText, jobDescription);
    }

    @Operation(summary = "Extract plain text from a resume file")
    @PostMapping(value = "/extract", consumes = "multipart/form-data")
    public ExtractedTextResponse extract(@RequestPart("resume") MultipartFile resume) {
        validateFile(resume);
        log.info("Extracting text from resume '{}' ({} bytes)", resume.getOriginalFilename(), resume.getSize());

        String text = extractText(resume);
        int wordCount = text.isBlank() ? 0 : text.trim().split("\\s+").length;

        return new ExtractedTextResponse(resume.getOriginalFilename(), text, wordCount);
    }

    @Operation(summary = "Health check")
    @GetMapping("/health")
    public HealthResponse health() {
        return new HealthResponse("ok", "Resume Analyser API");
    }

    private String extractText(MultipartFile file) {
        try {
            return textExtractionService.extract(file.getOriginalFilename(), file.getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read uploaded file: " + file.getOriginalFilename(), e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Resume file must not be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("Resume file exceeds the 5MB size limit");
        }

        String extension = extensionOf(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "Unsupported file extension: " + extension + ". Allowed: " + ALLOWED_EXTENSIONS);
        }
    }

    private String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("File must have an extension");
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
