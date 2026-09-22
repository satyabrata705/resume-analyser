# Resume Analyser

A stateless REST API that analyses a resume against a job description: it extracts text
from PDF/DOCX/TXT files, detects skills by category, scores the resume against a job
description using cosine similarity, and returns ATS-style suggestions to help the resume
pass automated screening.

## Features

- Extracts plain text from PDF, DOCX, and TXT resumes
- Detects skills grouped into 7 categories (languages, frameworks, databases, cloud/DevOps,
  data/ML, soft skills, tools)
- Computes a JD match percentage using a hand-rolled TF cosine-similarity algorithm
  (unigrams + bigrams, stopwords removed)
- Finds skills present in the job description but missing from the resume
- Generates ATS-style suggestions (word count, contact info, section headers, action verbs,
  quantified achievements, bullet points, missing skills)
- Extracts contact info (email, phone, LinkedIn, GitHub), education lines, and years of
  experience
- Recommends additional keywords worth adding to the resume
- Interactive API docs via Swagger UI

## Tech stack

| Layer              | Choice                              |
|---------------------|--------------------------------------|
| Language             | Java 17                              |
| Framework            | Spring Boot 3.2.5                    |
| Build tool           | Maven                                |
| PDF parsing          | Apache PDFBox 3.0.x                  |
| DOCX parsing         | Apache POI 5.2.x                     |
| API docs             | SpringDoc OpenAPI (Swagger UI) 2.3.x |
| Validation           | Jakarta Bean Validation              |
| Testing              | JUnit 5 + MockMvc                    |

No database, no security layer, and no Docker — this is a stateless, single-process API by design.

## Project structure

```
resume-analyser/
├── pom.xml
├── .gitignore
├── README.md
├── src/main/java/com/example/resumeanalyser/
│   ├── ResumeAnalyserApplication.java
│   ├── config/OpenApiConfig.java
│   ├── controller/ResumeController.java
│   ├── dto/
│   │   ├── AnalysisResponse.java
│   │   ├── HealthResponse.java
│   │   └── ExtractedTextResponse.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResumeProcessingException.java
│   │   └── ErrorResponse.java
│   ├── service/
│   │   ├── TextExtractionService.java
│   │   ├── SkillExtractionService.java
│   │   ├── SimilarityService.java
│   │   ├── AtsSuggestionService.java
│   │   ├── ResumeAnalyzerService.java
│   │   └── impl/
│   │       ├── FileTextExtractor.java   (interface)
│   │       ├── PdfTextExtractor.java
│   │       ├── DocxTextExtractor.java
│   │       └── TxtTextExtractor.java
│   └── util/
│       ├── ContactInfoExtractor.java
│       ├── EducationExtractor.java
│       └── ExperienceExtractor.java
├── src/main/resources/
│   ├── application.yml
│   └── skills.json
└── src/test/java/com/example/resumeanalyser/
    ├── controller/ResumeControllerTest.java
    └── service/
        ├── ResumeAnalyzerServiceTest.java
        └── TextExtractionServiceTest.java
```

## Running

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`. Swagger UI is available at
`http://localhost:8080/swagger-ui.html`.

## Testing

```bash
mvn test
```

## API examples

**Health check**

```bash
curl http://localhost:8080/api/v1/resumes/health
```

**Extract text from a resume**

```bash
curl -X POST http://localhost:8080/api/v1/resumes/extract \
  -F "resume=@/path/to/resume.pdf"
```

**Analyze a resume against a job description**

```bash
curl -X POST http://localhost:8080/api/v1/resumes/analyze \
  -F "resume=@/path/to/resume.pdf" \
  -F "jobDescription=We need a backend engineer skilled in Python, Java, AWS and Kubernetes."
```

## Screenshot

_Swagger UI screenshot placeholder — add a screenshot of `/swagger-ui.html` here._

## Future work

- Persisting analysis history to a database
- LLM-generated suggestions for higher-quality phrasing feedback
- A batch endpoint for analyzing multiple resumes against one job description
- Docker packaging for deployment
