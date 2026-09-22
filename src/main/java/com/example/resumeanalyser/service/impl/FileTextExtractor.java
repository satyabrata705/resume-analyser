package com.example.resumeanalyser.service.impl;

import java.io.IOException;
import java.io.InputStream;

/**
 * Strategy interface for extracting plain text from a specific file type.
 */
public interface FileTextExtractor {

    boolean supports(String extension);

    String extract(InputStream in) throws IOException;
}
