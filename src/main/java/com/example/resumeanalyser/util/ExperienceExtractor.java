package com.example.resumeanalyser.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ExperienceExtractor {

    private static final Pattern YEARS_PATTERN =
            Pattern.compile("(\\d{1,2})\\+?\\s*(years?|yrs?)", Pattern.CASE_INSENSITIVE);

    private ExperienceExtractor() {
    }

    /**
     * Finds all "N years" / "N+ yrs" style mentions and returns the maximum
     * value found, or 0.0 if none are present.
     */
    public static double extract(String text) {
        if (text == null || text.isBlank()) {
            return 0.0;
        }

        double max = 0.0;
        Matcher matcher = YEARS_PATTERN.matcher(text);
        while (matcher.find()) {
            double value = Double.parseDouble(matcher.group(1));
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
