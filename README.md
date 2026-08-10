# ai-document-extraction-api

## Project Purpose

`ai-document-extraction-api` is the foundational service for an AI-powered document
extraction platform. This initial version establishes a clean, production-ready
Spring Boot skeleton: it accepts PDF uploads, validates them, and persists upload
metadata. **Actual AI-based text/data extraction from PDFs is intentionally not
implemented yet** — this project only lays the groundwork (API contracts, validation,
persistence, error handling) that extraction logic will be built on top of in a
future iteration.

## Technology Stack

| Component        | Technology                          |
|-------------------|--------------------------------------|
| Language          | Java 17                              |
| Framework         | Spring Boot 3.3.5                    |
| Build tool        | Maven                                |
| Web layer         | Spring Web (REST)                    |
| Validation        | Spring Validation                    |
| Persistence       | Spring Data JPA                      |
| Database          | PostgreSQL                           |
| Boilerplate       | Lombok                               |
| Observability     | Spring Boot Actuator                 |
| Testing           | JUnit 5, Mockito, Spring MockMvc     |

## Project Structure

```
com.example.aidocument
├── controller   # REST controllers (DocumentController)
├── service      # Business logic (DocumentService / DocumentServiceImpl)
├── dto          # Request/response payloads
├── entity       # JPA entities (Document, DocumentStatus)
├── repository   # Spring Data JPA repositories
└── exception    # Custom exceptions + global exception handler
```

## Prerequisites

* JDK 17+
* Maven 3.8+
* A running PostgreSQL instance

## Database Setup

Create a local database (defaults assume this name/user, override via env vars below):

```sql
CREATE DATABASE ai_document_extraction;
```

The application uses `spring.jpa.hibernate.ddl-auto=update`, so the `documents`
table is created automatically on startup — no manual migration is required for
this initial version.

## Configuration

Connection settings are read from environment variables, with sensible local
defaults defined in `src/main/resources/application.yml`:

| Variable       | Default      | Description             |
|----------------|--------------|--------------------------|
| `DB_USERNAME`  | `postgres`   | PostgreSQL username      |
| `DB_PASSWORD`  | `postgres`   | PostgreSQL password      |

The datasource URL defaults to `jdbc:postgresql://localhost:5432/ai_document_extraction`.

## How to Run Locally

1. Start PostgreSQL and ensure the database above exists.
2. From the project root, run:

   ```bash
   mvn spring-boot:run
   ```

   Or build a jar and run it directly:

   ```bash
   mvn clean package
   java -jar target/ai-document-extraction-api.jar
   ```

3. The service starts on **http://localhost:8080**.

## Running Tests

```bash
mvn test
```

## Available APIs

Base path: `/api/v1/documents`

### 1. Health Check

```
GET /api/v1/documents/health
```

**Response `200 OK`:**

```json
{
  "status": "UP",
  "service": "ai-document-extraction-api"
}
```

### 2. Upload Document for Extraction

```
POST /api/v1/documents/extract
Content-Type: multipart/form-data
```

**Form parameter:**

| Name   | Type | Required | Description                  |
|--------|------|----------|-------------------------------|
| `file` | file | yes      | PDF file to upload (max 10MB) |

**Example (curl):**

```bash
curl -X POST http://localhost:8080/api/v1/documents/extract \
  -F "file=@/path/to/document.pdf"
```

**Response `201 Created`:**

```json
{
  "documentId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "filename": "document.pdf",
  "status": "RECEIVED",
  "message": "File received successfully. Extraction is not yet implemented."
}
```

**Validation rules:**

* File must not be empty → `400 Bad Request`
* File must be a PDF (`.pdf` extension + `application/pdf` content type) → `400 Bad Request`
* File must not exceed 10MB → `413 Payload Too Large`

> Note: This endpoint currently only validates and records the upload. It does not
> yet extract text or data from the PDF — that logic will be added in a subsequent
> version.

### Actuator Endpoints

| Endpoint            | Description         |
|----------------------|----------------------|
| `GET /actuator/health` | Application health |
| `GET /actuator/info`   | Application info   |

## Error Response Format

All errors follow a consistent shape, produced by the global exception handler:

```json
{
  "timestamp": "2026-08-10T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Only PDF files are supported",
  "path": "/api/v1/documents/extract"
}
```

## Out of Scope (for this version)

The following are explicitly **not** part of this initial foundation:

* PDF text/data extraction logic
* AI/LLM integration (OpenAI, Claude, etc.)
* Authentication / authorization
* Frontend (React) or workflow tooling (n8n)
* Docker packaging
* Microservices decomposition
