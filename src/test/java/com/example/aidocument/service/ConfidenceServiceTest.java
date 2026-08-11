package com.example.aidocument.service;

import com.example.aidocument.model.InvoiceValidationResult;
import com.example.aidocument.service.impl.ConfidenceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfidenceServiceTest {

    private ConfidenceService confidenceService;

    @BeforeEach
    void setUp() {
        confidenceService = new ConfidenceServiceImpl();
    }

    @Test
    void shouldReturnMaximumConfidenceForValidInvoice() {

        InvoiceValidationResult validation =
                new InvoiceValidationResult(
                        true,
                        List.of()
                );

        double result =
                confidenceService.calculate(validation);

        assertEquals(0.98, result, 0.0001);
    }

    @Test
    void shouldReduceConfidenceForOneValidationError() {

        InvoiceValidationResult validation =
                new InvoiceValidationResult(
                        false,
                        List.of("Grand total mismatch")
                );

        double result =
                confidenceService.calculate(validation);

        assertEquals(0.83, result, 0.0001);
    }

    @Test
    void shouldReduceConfidenceForTwoValidationErrors() {

        InvoiceValidationResult validation =
                new InvoiceValidationResult(
                        false,
                        List.of(
                                "Grand total mismatch",
                                "Subtotal mismatch"
                        )
                );

        double result =
                confidenceService.calculate(validation);

        assertEquals(0.68, result, 0.0001);
    }

    @Test
    void shouldNeverGoBelowMinimumConfidence() {

        InvoiceValidationResult validation =
                new InvoiceValidationResult(
                        false,
                        List.of(
                                "Error 1",
                                "Error 2",
                                "Error 3",
                                "Error 4",
                                "Error 5",
                                "Error 6",
                                "Error 7",
                                "Error 8",
                                "Error 9"
                        )
                );

        double result =
                confidenceService.calculate(validation);

        assertEquals(0.30, result, 0.0001);
    }
}