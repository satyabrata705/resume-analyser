package com.example.resumeanalyser.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ContactInfoExtractor {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("(\\+?\\d{1,3}[\\s.-]?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}");
    private static final Pattern LINKEDIN_PATTERN =
            Pattern.compile("(https?://)?(www\\.)?linkedin\\.com/\\S+", Pattern.CASE_INSENSITIVE);
    private static final Pattern GITHUB_PATTERN =
            Pattern.compile("(https?://)?(www\\.)?github\\.com/\\S+", Pattern.CASE_INSENSITIVE);

    private ContactInfoExtractor() {
    }

    public static Map<String, String> extract(String text) {
        Map<String, String> contactInfo = new HashMap<>();
        contactInfo.put("email", find(EMAIL_PATTERN, text));
        contactInfo.put("phone", find(PHONE_PATTERN, text));
        contactInfo.put("linkedin", find(LINKEDIN_PATTERN, text));
        contactInfo.put("github", find(GITHUB_PATTERN, text));
        return contactInfo;
    }

    private static String find(Pattern pattern, String text) {
        if (text == null) {
            return null;
        }
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group().trim() : null;
    }
}
