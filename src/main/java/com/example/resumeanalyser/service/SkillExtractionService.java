package com.example.resumeanalyser.service;

import com.example.resumeanalyser.exception.ResumeProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

@Service
public class SkillExtractionService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, List<String>> skillsByCategory = Map.of();
    private Map<String, String> skillToCategory = Map.of();
    private Map<String, Pattern> skillPatterns = Map.of();

    @PostConstruct
    void loadSkills() {
        try (InputStream in = new ClassPathResource("skills.json").getInputStream()) {
            skillsByCategory = objectMapper.readValue(in, new TypeReference<Map<String, List<String>>>() {
            });
        } catch (IOException e) {
            throw new ResumeProcessingException("Failed to load skills.json", e);
        }

        Map<String, String> toCategory = new HashMap<>();
        Map<String, Pattern> patterns = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : skillsByCategory.entrySet()) {
            String category = entry.getKey();
            for (String skill : entry.getValue()) {
                toCategory.put(skill, category);
                patterns.put(skill, compileSkillPattern(skill));
            }
        }

        skillToCategory = Map.copyOf(toCategory);
        skillPatterns = Map.copyOf(patterns);
    }

    private Pattern compileSkillPattern(String skill) {
        String escaped = Pattern.quote(skill);
        return Pattern.compile("(?<![a-z0-9])" + escaped + "(?![a-z0-9])");
    }

    /**
     * Finds all known skills present in the given text, grouped by category.
     * Only categories with at least one match are included.
     */
    public Map<String, List<String>> extractGrouped(String text) {
        String lowered = text == null ? "" : text.toLowerCase();

        Map<String, List<String>> found = new TreeMap<>();

        for (Map.Entry<String, String> entry : skillToCategory.entrySet()) {
            String skill = entry.getKey();
            String category = entry.getValue();
            Pattern pattern = skillPatterns.get(skill);

            if (pattern.matcher(lowered).find()) {
                found.computeIfAbsent(category, c -> new ArrayList<>()).add(skill);
            }
        }

        return new LinkedHashMap<>(found);
    }

    public Set<String> flatten(Map<String, List<String>> grouped) {
        Set<String> all = new HashSet<>();
        grouped.values().forEach(all::addAll);
        return all;
    }
}
