package com.example.resumeanalyser.util;

import java.util.ArrayList;
import java.util.List;

public final class EducationExtractor {

    private static final int MAX_LINES = 5;

    private static final List<String> EDUCATION_KEYWORDS = List.of(
            "b.s.", "b.a.", "b.tech", "m.s.", "m.a.", "m.tech", "mba", "ph.d",
            "bachelor", "master", "doctorate", "diploma"
    );

    private EducationExtractor() {
    }

    public static List<String> extract(String text) {
        List<String> matches = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return matches;
        }

        for (String line : text.split("\\r?\\n")) {
            String lowered = line.toLowerCase();
            boolean hasKeyword = EDUCATION_KEYWORDS.stream().anyMatch(lowered::contains);

            if (hasKeyword) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    matches.add(trimmed);
                }
            }

            if (matches.size() >= MAX_LINES) {
                break;
            }
        }

        return matches;
    }
}
