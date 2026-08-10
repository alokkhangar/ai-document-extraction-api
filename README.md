# AI Document Extraction API

AI-powered document extraction and invoice validation API built with Java 17, Spring Boot, and Apache PDFBox.

## Overview

This project demonstrates a backend service for processing invoice documents and converting unstructured PDF content into structured, validated business data.

The application is being developed incrementally toward an AI-powered document intelligence platform.

### Current Processing Flow

```text
PDF Document
     |
     v
Spring Boot REST API
     |
     v
Apache PDFBox
     |
     v
Text Extraction
     |
     v
Invoice Domain Model
     |
     v
Java Validation
     |
     v
Validation Result
```

## Current Features

- PDF document upload
- PDF text extraction using Apache PDFBox
- Invoice domain model
- Vendor modeling
- Invoice item modeling
- Line-item amount validation
- Subtotal validation
- Tax validation
- Grand-total validation
- Invoice validation result
- REST APIs
- Global error handling
- Unit tests
- Java 17
- Spring Boot
- Maven
- Git/GitHub

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Backend development |
| Spring Boot | REST API and application framework |
| Apache PDFBox | PDF text extraction |
| Maven | Build and dependency management |
| JUnit 5 | Unit testing |
| Git | Version control |
| GitHub | Source code repository |

## API Documentation

### 1. Health Check

Checks whether the application is running.

```http
GET /api/v1/documents/health
```

Example response:

```json
{
  "status": "UP",
  "service": "ai-document-extraction-api"
}
```

### 2. Extract Text from PDF

Extracts text from a PDF document using Apache PDFBox.

```http
POST /api/v1/documents/extract-text
Content-Type: multipart/form-data
```

Request parameter:

```text
file = invoice.pdf
```

Example response:

```json
{
  "fileName": "sample_invoice.pdf",
  "pageCount": 1,
  "text": "SAMPLE INVOICE..."
}
```

## Invoice Validation

The application uses deterministic Java validation after invoice data is extracted.

### Line Item Validation

For each invoice item:

```text
Quantity × Unit Price = Amount
```

### Subtotal Validation

The system calculates the subtotal from all invoice line items and compares it with the extracted subtotal.

### Tax Validation

The system validates the extracted tax amount and tax rate.

### Grand Total Validation

The expected grand total is calculated as:

```text
Subtotal + Tax = Grand Total
```

Any mismatch is reported as a validation error.

## Architecture

```text
                    PDF Document
                         |
                         v
              +----------------------+
              | Spring Boot REST API |
              +----------------------+
                         |
                         v
              +----------------------+
              |  Document Controller |
              +----------------------+
                         |
                         v
              +----------------------+
              |   Document Service   |
              +----------------------+
                         |
                         v
              +----------------------+
              |     Apache PDFBox    |
              +----------------------+
                         |
                         v
                   Extracted Text
                         |
                         v
              +----------------------+
              |  Invoice Domain Model|
              +----------------------+
                         |
                         v
              +----------------------+
              |  Invoice Validator   |
              +----------------------+
                         |
                  +------+------+
                  |             |
                  v             v
                VALID        INVALID
                  |             |
                  v             v
              Continue      Review Error
```

## Design Principles

### AI for Unstructured Data Extraction

The future AI layer will interpret unstructured document text and convert it into structured invoice data.

### Java for Deterministic Validation

Financial calculations and business rules are handled by Java.

```text
AI
 |
 +-- Extract information
 |
 v
Structured Invoice
 |
 v
Java
 |
 +-- Validate calculations
 +-- Validate business rules
 +-- Detect discrepancies
```

## Error Handling

The application validates:

- Empty PDF files
- Unsupported file types
- Invalid invoice values
- Line-item calculation mismatches
- Subtotal mismatches
- Tax validation errors
- Grand-total mismatches

## Testing

The project includes unit tests for invoice validation.

Current test scenarios include:

- Valid invoice
- Line-item amount mismatch
- Subtotal mismatch
- Grand-total mismatch

Run all tests using:

```bash
mvn clean test
```

## Running Locally

### Prerequisites

- Java 17
- Maven 3.8+
- Git

### Clone the Repository

```bash
git clone https://github.com/alokkhangar/ai-document-extraction-api.git
```

### Build

```bash
mvn clean install
```

### Run Tests

```bash
mvn clean test
```

### Run the Application

```bash
mvn spring-boot:run
```

The application runs by default on:

```text
http://localhost:8080
```

## Testing with Postman

### Health API

```http
GET http://localhost:8080/api/v1/documents/health
```

### PDF Extraction API

```http
POST http://localhost:8080/api/v1/documents/extract-text
```

In Postman:

```text
Body
  -> form-data
      -> Key: file
      -> Type: File
      -> Value: invoice.pdf
```

## Roadmap

### Phase 1 — Core Document Processing

- [x] Spring Boot REST API
- [x] Health API
- [x] PDF upload
- [x] PDF text extraction
- [x] Invoice domain model
- [x] Invoice validation
- [x] Unit tests
- [x] GitHub repository

### Phase 2 — AI Document Extraction

- [ ] LLM integration
- [ ] Structured invoice extraction
- [ ] JSON schema validation
- [ ] AI extraction error handling
- [ ] LLM provider abstraction
- [ ] Prompt management

### Phase 3 — Advanced Document Intelligence

- [ ] OCR for scanned documents
- [ ] Purchase Order extraction
- [ ] PO vs Invoice comparison
- [ ] Price discrepancy detection
- [ ] Quantity discrepancy detection
- [ ] Duplicate invoice detection
- [ ] Vendor-specific document formats

### Phase 4 — Production Readiness

- [ ] PostgreSQL persistence
- [ ] Database migrations
- [ ] Authentication and authorization
- [ ] Docker support
- [ ] Integration tests
- [ ] API documentation with OpenAPI
- [ ] Observability
- [ ] CI/CD pipeline
- [ ] Cloud deployment

## Project Status

The project is actively under development.

Current milestone:

**PDF → Text → Structured Domain Model → Java Validation**

Upcoming milestone:

**PDF → Text → LLM → Structured Invoice → Java Validation**
