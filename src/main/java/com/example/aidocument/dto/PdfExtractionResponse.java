package com.example.aidocument.dto;

public record PdfExtractionResponse(
        String fileName,
        int pageCount,
        String text
) {
}