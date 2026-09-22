package com.example.resumeanalyser.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ResumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String RESUME_TEXT =
            "Jane Doe jane.doe@example.com (555) 123-4567 linkedin.com/in/janedoe\n"
                    + "Summary: Backend engineer with 5 years of experience in Python and Java.\n"
                    + "Experience: Led, built, designed, developed and implemented services, reducing "
                    + "latency by 30% and serving 10k requests.\n"
                    + "Education: B.Tech in Computer Science.\n"
                    + "Skills: Python, Java, Spring Boot, Docker, Kubernetes, Git.";

    private static final String JOB_DESCRIPTION =
            "We are looking for a backend engineer skilled in Python, Java, AWS and MongoDB, "
                    + "with strong communication and problem solving skills.";

    @Test
    void healthReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/resumes/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void extractReturnsTextFromTxtFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "resume", "resume.txt", "text/plain", RESUME_TEXT.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/v1/resumes/extract").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filename").value("resume.txt"))
                .andExpect(jsonPath("$.text").isNotEmpty());
    }

    @Test
    void analyzeReturnsSkillsMatchAndMissingSkills() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "resume", "resume.txt", "text/plain", RESUME_TEXT.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/v1/resumes/analyze")
                        .file(file)
                        .param("jobDescription", JOB_DESCRIPTION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skillsDetected.programming_languages", hasItem("python")))
                .andExpect(jsonPath("$.matchPercentage", greaterThan(0.0)))
                .andExpect(jsonPath("$.missingSkills", hasItem("aws")));
    }
}
