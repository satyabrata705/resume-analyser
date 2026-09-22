package com.example.resumeanalyser.service;

import com.example.resumeanalyser.dto.AnalysisResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumeAnalyzerServiceTest {

    private ResumeAnalyzerService resumeAnalyzerService;

    @BeforeEach
    void setUp() {
        SkillExtractionService skillExtractionService = new SkillExtractionService();
        skillExtractionService.loadSkills();

        resumeAnalyzerService = new ResumeAnalyzerService(
                skillExtractionService,
                new SimilarityService(),
                new AtsSuggestionService());
    }

    @Test
    void missingSkillsContainsKubernetesWhenResumeLacksIt() {
        String resumeText = "Experienced engineer skilled in Python and Java development.";
        String jobDescription = "Looking for an engineer with Python, Java and Kubernetes experience.";

        AnalysisResponse response = resumeAnalyzerService.analyze(resumeText, jobDescription);

        assertEquals(1, response.missingSkills().size());
        assertTrue(response.missingSkills().contains("kubernetes"));
        assertTrue(response.matchPercentage() > 0);
    }
}
