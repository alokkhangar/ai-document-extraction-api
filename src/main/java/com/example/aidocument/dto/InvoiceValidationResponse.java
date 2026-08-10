package com.example.aidocument.dto;

import com.example.aidocument.model.Invoice;
import com.example.aidocument.model.InvoiceValidationResult;

public record InvoiceValidationResponse(
        Invoice invoice,
        InvoiceValidationResult validation
) {
}