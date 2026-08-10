package com.example.aidocument.model;

import java.util.List;

public record InvoiceValidationResult(
        boolean valid,
        List<String> errors
) {
}