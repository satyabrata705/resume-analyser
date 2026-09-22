package com.example.resumeanalyser.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "Full resume analysis result")
public record AnalysisResponse(
        @Schema(description = "Full extracted resume text") String resumeText,
        @Schema(description = "Word count of the resume text", example = "512") int resumeWordCount,
        @Schema(description = "Skills detected in the resume, grouped by category") Map<String, List<String>> skillsDetected,
        @Schema(description = "Skills detected in the job description, grouped by category") Map<String, List<String>> jobDescriptionSkills,
        @Schema(description = "Skills present in the job description but missing from the resume") List<String> missingSkills,
        @Schema(description = "Cosine-similarity match percentage between resume and job description", example = "67.42") double matchPercentage,
        @Schema(description = "ATS-style improvement suggestions") List<String> atsSuggestions,
        @Schema(description = "Extracted contact info: email, phone, linkedin, github") Map<String, String> contactInfo,
        @Schema(description = "Lines from the resume mentioning education") List<String> education,
        @Schema(description = "Maximum years of experience mentioned in the resume", example = "5.0") double experienceYears,
        @Schema(description = "Recommended keywords to add to the resume") List<String> keywordRecommendations
) {
}
