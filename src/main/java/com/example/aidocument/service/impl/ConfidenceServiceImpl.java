package com.example.aidocument.service.impl;

import com.example.aidocument.model.InvoiceValidationResult;
import com.example.aidocument.service.ConfidenceService;
import org.springframework.stereotype.Service;

@Service
public class ConfidenceServiceImpl implements ConfidenceService {

    private static final double MAX_CONFIDENCE = 0.98;
    private static final double MIN_CONFIDENCE = 0.30;
    private static final double ERROR_PENALTY = 0.15;

    @Override
    public double calculate(InvoiceValidationResult validation) {

        if (validation.valid()) {
            return MAX_CONFIDENCE;
        }

        int errorCount = validation.errors().size();

        double confidence =
                MAX_CONFIDENCE - (errorCount * ERROR_PENALTY);

        return Math.max(confidence, MIN_CONFIDENCE);
    }
}