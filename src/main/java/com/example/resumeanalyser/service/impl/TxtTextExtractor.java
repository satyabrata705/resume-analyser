package com.example.resumeanalyser.service.impl;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class TxtTextExtractor implements FileTextExtractor {

    @Override
    public boolean supports(String extension) {
        return "txt".equalsIgnoreCase(extension);
    }

    @Override
    public String extract(InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
