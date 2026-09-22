package com.example.resumeanalyser.service;

import com.example.resumeanalyser.dto.AnalysisResponse;
import com.example.resumeanalyser.util.ContactInfoExtractor;
import com.example.resumeanalyser.util.EducationExtractor;
import com.example.resumeanalyser.util.ExperienceExtractor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeAnalyzerService {

    private static final int MAX_KEYWORD_RECOMMENDATIONS = 20;
    private static final int MIN_KEYWORD_LENGTH = 3;
    private static final Pattern WORD_PATTERN = Pattern.compile("[a-z][a-z0-9+#.-]*");

    private final SkillExtractionService skillExtractionService;
    private final SimilarityService similarityService;
    private final AtsSuggestionService atsSuggestionService;

    public ResumeAnalyzerService(SkillExtractionService skillExtractionService,
                                  SimilarityService similarityService,
                                  AtsSuggestionService atsSuggestionService) {
        this.skillExtractionService = skillExtractionService;
        this.similarityService = similarityService;
        this.atsSuggestionService = atsSuggestionService;
    }

    public AnalysisResponse analyze(String resumeText, String jobDescription) {
        Map<String, List<String>> resumeSkills = skillExtractionService.extractGrouped(resumeText);
        Set<String> resumeSkillSet = skillExtractionService.flatten(resumeSkills);

        Map<String, List<String>> jdSkills;
        List<String> missing;
        double matchPercentage;
        List<String> keywordRecommendations;

        if (jobDescription != null && !jobDescription.isBlank()) {
            jdSkills = skillExtractionService.extractGrouped(jobDescription);
            Set<String> jdSkillSet = skillExtractionService.flatten(jdSkills);

            missing = new ArrayList<>(new TreeSet<>(difference(jdSkillSet, resumeSkillSet)));
            matchPercentage = similarityService.matchPercentage(resumeText, jobDescription);
            keywordRecommendations = buildKeywordRecommendations(resumeText, jobDescription, missing, resumeSkillSet, jdSkillSet);
        } else {
            jdSkills = Map.of();
            missing = List.of();
            matchPercentage = 0.0;
            keywordRecommendations = List.of();
        }

        List<String> suggestions = atsSuggestionService.generate(resumeText, resumeSkills, missing);

        Map<String, String> contactInfo = ContactInfoExtractor.extract(resumeText);
        List<String> education = EducationExtractor.extract(resumeText);
        double experienceYears = ExperienceExtractor.extract(resumeText);

        return new AnalysisResponse(
                resumeText,
                countWords(resumeText),
                resumeSkills,
                jdSkills,
                missing,
                matchPercentage,
                suggestions,
                contactInfo,
                education,
                experienceYears,
                keywordRecommendations
        );
    }

    private List<String> buildKeywordRecommendations(String resumeText, String jobDescription,
                                                       List<String> missingSkills,
                                                       Set<String> resumeSkillSet, Set<String> jdSkillSet) {
        Set<String> knownSkills = new HashSet<>(resumeSkillSet);
        knownSkills.addAll(jdSkillSet);

        Set<String> resumeTokens = tokenize(resumeText);

        Set<String> extraJdTokens = new TreeSet<>();
        for (String token : tokenize(jobDescription)) {
            if (token.length() <= MIN_KEYWORD_LENGTH) {
                continue;
            }
            if (resumeTokens.contains(token)) {
                continue;
            }
            if (knownSkills.contains(token)) {
                continue;
            }
            extraJdTokens.add(token);
        }

        Set<String> combined = new TreeSet<>(missingSkills);
        combined.addAll(extraJdTokens);

        return combined.stream().limit(MAX_KEYWORD_RECOMMENDATIONS).toList();
    }

    private Set<String> tokenize(String text) {
        Set<String> tokens = new HashSet<>();
        if (text == null) {
            return tokens;
        }
        Matcher matcher = WORD_PATTERN.matcher(text.toLowerCase());
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }

    private Set<String> difference(Set<String> a, Set<String> b) {
        Set<String> result = new HashSet<>(a);
        result.removeAll(b);
        return result;
    }

    private int countWords(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }
}
