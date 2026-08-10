package com.example.aidocument.controller;

import com.example.aidocument.dto.HealthResponse;
import com.example.aidocument.dto.PdfExtractionResponse;
import com.example.aidocument.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private static final String SERVICE_NAME = "ai-document-extraction-api";

    private final DocumentService documentService;

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        HealthResponse response = HealthResponse.builder().status("UP").service(SERVICE_NAME).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/extract-text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PdfExtractionResponse> extractText(@RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(documentService.extractText(file));
    }
}
