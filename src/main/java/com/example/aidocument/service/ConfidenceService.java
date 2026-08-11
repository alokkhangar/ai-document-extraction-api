package com.example.aidocument.service;

import com.example.aidocument.model.InvoiceValidationResult;

public interface ConfidenceService {

    double calculate(InvoiceValidationResult validation);
}