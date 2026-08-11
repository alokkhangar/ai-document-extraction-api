package com.example.aidocument.dto;

import com.example.aidocument.model.Invoice;
import com.example.aidocument.model.InvoiceValidationResult;

import java.util.List;

public record InvoiceExtractionResponse(
        Invoice invoice,
        InvoiceValidationResult validation,
        String status,
        boolean reviewRequired,
        double confidenceScore,
        List<String> reviewReasons
) {
}