package com.example.resumeanalyser.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Computes similarity between a resume and a job description using
 * hand-rolled term-frequency vectors (unigrams + bigrams) and cosine
 * similarity, so the algorithm stays simple enough to explain in an
 * interview without relying on an external ML library.
 */
@Service
public class SimilarityService {

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "the", "and", "or", "but", "if", "then", "so", "of", "to", "in", "on",
            "for", "with", "at", "by", "from", "as", "is", "are", "was", "were", "be", "been",
            "being", "this", "that", "these", "those", "it", "its", "we", "you", "your", "our",
            "they", "their", "will", "would", "should", "can", "could", "may", "might", "must",
            "have", "has", "had", "do", "does", "did", "not", "no", "yes", "job", "role",
            "position", "candidate", "candidates", "experience", "years", "year", "work",
            "working", "team", "teams", "company", "about", "who", "what", "when", "where",
            "why", "how", "all", "any", "each", "other", "such", "than", "into", "out", "up",
            "down", "over", "under", "again", "further", "once", "here", "there", "both",
            "few", "more", "most", "some", "own", "same", "too", "very", "just", "also",
            "us", "him", "her", "them", "i", "me", "my", "mine", "he", "she", "his", "hers",
            "off", "above", "below", "between", "during", "after", "before", "while",
            "s", "t", "don", "now", "only", "one", "two", "including", "etc", "plus",
            "looking", "join", "responsibilities", "requirements", "preferred", "required",
            "ability", "strong"
    );

    /**
     * Returns the resume-to-JD match percentage, rounded to 2 decimals.
     */
    public double matchPercentage(String resumeText, String jdText) {
        List<String> resumeTokens = tokenize(resumeText);
        List<String> jdTokens = tokenize(jdText);

        Map<String, Integer> resumeVector = termFrequencyVector(resumeTokens);
        Map<String, Integer> jdVector = termFrequencyVector(jdTokens);

        double cosine = cosineSimilarity(resumeVector, jdVector);

        double percentage = cosine * 100;
        return Math.round(percentage * 100.0) / 100.0;
    }

    private List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String cleaned = text.toLowerCase().replaceAll("[^a-z0-9+#.\\s]", " ");
        String[] words = cleaned.trim().split("\\s+");

        List<String> tokens = new ArrayList<>();
        for (String word : words) {
            if (word.isBlank() || STOP_WORDS.contains(word)) {
                continue;
            }
            tokens.add(word);
        }

        List<String> ngrams = new ArrayList<>(tokens);
        for (int i = 0; i < tokens.size() - 1; i++) {
            ngrams.add(tokens.get(i) + " " + tokens.get(i + 1));
        }

        return ngrams;
    }

    private Map<String, Integer> termFrequencyVector(List<String> tokens) {
        Map<String, Integer> frequencies = new HashMap<>();
        for (String token : tokens) {
            frequencies.merge(token, 1, Integer::sum);
        }
        return frequencies;
    }

    private double cosineSimilarity(Map<String, Integer> vectorA, Map<String, Integer> vectorB) {
        if (vectorA.isEmpty() || vectorB.isEmpty()) {
            return 0.0;
        }

        Set<String> allTerms = new HashSet<>();
        allTerms.addAll(vectorA.keySet());
        allTerms.addAll(vectorB.keySet());

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (String term : allTerms) {
            int a = vectorA.getOrDefault(term, 0);
            int b = vectorB.getOrDefault(term, 0);
            dotProduct += (double) a * b;
            normA += (double) a * a;
            normB += (double) b * b;
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
