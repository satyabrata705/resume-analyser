package com.example.resumeanalyser.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Plain text extracted from an uploaded resume file")
public record ExtractedTextResponse(
        @Schema(description = "Original uploaded filename", example = "resume.pdf") String filename,
        @Schema(description = "Extracted plain text") String text,
        @Schema(description = "Word count of the extracted text", example = "412") int wordCount
) {
}
