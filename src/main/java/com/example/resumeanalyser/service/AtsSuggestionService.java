package com.example.resumeanalyser.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Generates ATS (Applicant Tracking System) style suggestions for a resume
 * based on a fixed set of heuristic rules.
 */
@Service
public class AtsSuggestionService {

    private static final int MIN_WORD_COUNT = 300;
    private static final int MAX_WORD_COUNT = 1200;
    private static final int MIN_ACTION_VERBS = 3;
    private static final int MIN_BULLET_POINTS = 5;
    private static final int MAX_MISSING_SKILLS_LISTED = 8;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("(\\+?\\d{1,3}[\\s.-]?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}");
    private static final Pattern LINKEDIN_PATTERN =
            Pattern.compile("linkedin\\.com/", Pattern.CASE_INSENSITIVE);
    private static final Pattern QUANTIFIED_ACHIEVEMENT_PATTERN = Pattern.compile(
            "\\d+%|\\$\\d+|\\d+\\s*(users|customers|requests|ms|k|m)", Pattern.CASE_INSENSITIVE);
    private static final Pattern BULLET_LINE_PATTERN = Pattern.compile("(?m)^\\s*[•\\-*]\\s*");

    private static final List<String> REQUIRED_SECTIONS =
            List.of("experience", "education", "skills", "projects", "summary");

    private static final List<String> ACTION_VERBS = List.of(
            "led", "built", "designed", "developed", "implemented", "created", "improved", "increased",
            "reduced", "managed", "launched", "delivered", "optimized", "automated", "architected",
            "mentored", "shipped"
    );

    public List<String> generate(String resumeText, Map<String, List<String>> foundSkills, List<String> missingSkills) {
        List<String> suggestions = new ArrayList<>();
        String text = resumeText == null ? "" : resumeText;
        String lowered = text.toLowerCase();

        addWordCountSuggestions(text, suggestions);
        addContactSuggestions(text, suggestions);
        addSectionSuggestions(lowered, suggestions);
        addActionVerbSuggestion(lowered, suggestions);
        addQuantifiedAchievementSuggestion(text, suggestions);
        addBulletPointSuggestion(text, suggestions);
        addMissingSkillsSuggestion(missingSkills, suggestions);

        return suggestions;
    }

    private void addWordCountSuggestions(String text, List<String> suggestions) {
        int wordCount = countWords(text);
        if (wordCount < MIN_WORD_COUNT) {
            suggestions.add("Resume too short, aim for 400–800 words.");
        } else if (wordCount > MAX_WORD_COUNT) {
            suggestions.add("Resume too long, trim to 1–2 pages.");
        }
    }

    private void addContactSuggestions(String text, List<String> suggestions) {
        if (!EMAIL_PATTERN.matcher(text).find()) {
            suggestions.add("No email address found – add one so recruiters can reach you.");
        }
        if (!PHONE_PATTERN.matcher(text).find()) {
            suggestions.add("No phone number found – add one for recruiter contact.");
        }
        if (!LINKEDIN_PATTERN.matcher(text).find()) {
            suggestions.add("No LinkedIn URL found – add your LinkedIn profile link.");
        }
    }

    private void addSectionSuggestions(String loweredText, List<String> suggestions) {
        for (String section : REQUIRED_SECTIONS) {
            if (!loweredText.contains(section)) {
                suggestions.add("Missing a \"" + capitalize(section) + "\" section – consider adding one.");
            }
        }
    }

    private void addActionVerbSuggestion(String loweredText, List<String> suggestions) {
        long verbCount = ACTION_VERBS.stream()
                .filter(loweredText::contains)
                .count();

        if (verbCount < MIN_ACTION_VERBS) {
            suggestions.add("Use more strong action verbs (e.g. led, built, designed, developed, "
                    + "implemented, improved) to describe your achievements.");
        }
    }

    private void addQuantifiedAchievementSuggestion(String text, List<String> suggestions) {
        if (!QUANTIFIED_ACHIEVEMENT_PATTERN.matcher(text).find()) {
            suggestions.add("Add quantified achievements (e.g. \"reduced latency by 20%\", "
                    + "\"served 10k users\") to make impact measurable.");
        }
    }

    private void addBulletPointSuggestion(String text, List<String> suggestions) {
        long bulletCount = BULLET_LINE_PATTERN.matcher(text).results().count();
        if (bulletCount < MIN_BULLET_POINTS) {
            suggestions.add("Use more bullet points (•, -, or *) to make achievements scannable.");
        }
    }

    private void addMissingSkillsSuggestion(List<String> missingSkills, List<String> suggestions) {
        if (missingSkills != null && !missingSkills.isEmpty()) {
            String preview = String.join(", ", missingSkills.subList(0, Math.min(MAX_MISSING_SKILLS_LISTED, missingSkills.size())));
            suggestions.add("Consider adding: " + preview + ".");
        }
    }

    private int countWords(String text) {
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return 0;
        }
        return trimmed.split("\\s+").length;
    }

    private String capitalize(String word) {
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }
}
