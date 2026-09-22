package com.example.resumeanalyser.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Simple liveness response")
public record HealthResponse(
        @Schema(description = "Service status", example = "ok") String status,
        @Schema(description = "Application name", example = "Resume Analyser API") String app
) {
}
