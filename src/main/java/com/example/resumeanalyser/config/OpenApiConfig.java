package com.example.resumeanalyser.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Resume Analyser API",
                version = "1.0.0",
                description = "Stateless REST API that extracts resume text, detects skills, "
                        + "computes a job description match score, and returns ATS-style suggestions.",
                contact = @Contact(name = "Resume Analyser Team", email = "support@example.com")
        )
)
public class OpenApiConfig {
}
