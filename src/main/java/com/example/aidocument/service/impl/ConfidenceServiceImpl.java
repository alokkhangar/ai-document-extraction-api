package com.example.aidocument.service.impl;

import com.example.aidocument.model.InvoiceValidationResult;
import com.example.aidocument.model.ValidationErrorType;
import com.example.aidocument.service.ConfidenceService;
import org.springframework.stereotype.Service;

@Service
public class ConfidenceServiceImpl implements ConfidenceService {

    private static final double MAX_CONFIDENCE = 0.98;
    private static final double MIN_CONFIDENCE = 0.30;

    @Override
    public double calculate(InvoiceValidationResult validation) {

        if (validation.valid()) {
            return MAX_CONFIDENCE;
        }

        double totalPenalty = validation.errors()
                .stream()
                .mapToDouble(this::getPenalty)
                .sum();

        double confidence =
                MAX_CONFIDENCE - totalPenalty;

        return Math.max(confidence, MIN_CONFIDENCE);
    }

    private double getPenalty(String error) {

        ValidationErrorType errorType =
                classifyError(error);

        return errorType.getPenalty();
    }

    private ValidationErrorType classifyError(String error) {

        String normalizedError =
                error.toLowerCase();

        if (normalizedError.contains("grand total")) {
            return ValidationErrorType.GRAND_TOTAL_MISMATCH;
        }

        if (normalizedError.contains("subtotal")) {
            return ValidationErrorType.SUBTOTAL_MISMATCH;
        }

        if (normalizedError.contains("tax")) {
            return ValidationErrorType.TAX_MISMATCH;
        }

        if (normalizedError.contains("missing")) {
            return ValidationErrorType.MISSING_REQUIRED_FIELD;
        }

        if (normalizedError.contains("line item")) {
            return ValidationErrorType.LINE_ITEM_MISMATCH;
        }

        return ValidationErrorType.UNKNOWN;
    }
}