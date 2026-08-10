package com.example.aidocument.service;

import com.example.aidocument.model.Invoice;
import com.example.aidocument.model.InvoiceItem;
import com.example.aidocument.model.InvoiceValidationResult;
import com.example.aidocument.model.Vendor;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceValidatorTest {

    private final InvoiceValidator validator = new InvoiceValidator();

    @Test
    void shouldValidateCorrectInvoice() {

        Invoice invoice = new Invoice(
                new Vendor(
                        "ABC Industrial Supplies Pvt. Ltd.",
                        "09ABCDE1234F1Z5"
                ),
                "INV-2026-00124",
                LocalDate.of(2026, 8, 10),
                "XYZ Engineering Works",
                List.of(
                        new InvoiceItem(
                                "Mild Steel Sheet 10mm",
                                new BigDecimal("100"),
                                "KG",
                                new BigDecimal("72"),
                                new BigDecimal("7200")
                        ),
                        new InvoiceItem(
                                "Mild Steel Sheet 6mm",
                                new BigDecimal("150"),
                                "KG",
                                new BigDecimal("68"),
                                new BigDecimal("10200")
                        ),
                        new InvoiceItem(
                                "Stainless Steel 304",
                                new BigDecimal("50"),
                                "KG",
                                new BigDecimal("245"),
                                new BigDecimal("12250")
                        ),
                        new InvoiceItem(
                                "Cutting Service",
                                new BigDecimal("10"),
                                "HOUR",
                                new BigDecimal("500"),
                                new BigDecimal("5000")
                        )
                ),
                new BigDecimal("34650"),
                new BigDecimal("6237"),
                new BigDecimal("18"),
                new BigDecimal("40887")
        );

        InvoiceValidationResult result = validator.validate(invoice);

        assertTrue(result.valid());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    void shouldDetectLineItemAmountMismatch() {

        Invoice invoice = new Invoice(
                new Vendor("ABC Traders", "09ABCDE1234F1Z5"),
                "INV-001",
                LocalDate.of(2026, 8, 10),
                "XYZ Engineering",
                List.of(
                        new InvoiceItem(
                                "Steel",
                                new BigDecimal("100"),
                                "KG",
                                new BigDecimal("72"),
                                new BigDecimal("72000")
                        )
                ),
                new BigDecimal("72000"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("72000")
        );

        InvoiceValidationResult result = validator.validate(invoice);

        assertFalse(result.valid());
        assertTrue(
                result.errors().stream()
                        .anyMatch(error ->
                                error.contains("Line item amount mismatch"))
        );
    }

    @Test
    void shouldDetectSubtotalMismatch() {

        Invoice invoice = new Invoice(
                new Vendor("ABC Traders", "09ABCDE1234F1Z5"),
                "INV-002",
                LocalDate.of(2026, 8, 10),
                "XYZ Engineering",
                List.of(
                        new InvoiceItem(
                                "Steel",
                                new BigDecimal("100"),
                                "KG",
                                new BigDecimal("72"),
                                new BigDecimal("7200")
                        )
                ),
                new BigDecimal("8000"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("8000")
        );

        InvoiceValidationResult result = validator.validate(invoice);

        assertFalse(result.valid());
        assertTrue(
                result.errors().stream()
                        .anyMatch(error ->
                                error.contains("Subtotal mismatch"))
        );
    }

    @Test
    void shouldDetectGrandTotalMismatch() {

        Invoice invoice = new Invoice(
                new Vendor("ABC Traders", "09ABCDE1234F1Z5"),
                "INV-003",
                LocalDate.of(2026, 8, 10),
                "XYZ Engineering",
                List.of(
                        new InvoiceItem(
                                "Steel",
                                new BigDecimal("100"),
                                "KG",
                                new BigDecimal("72"),
                                new BigDecimal("7200")
                        )
                ),
                new BigDecimal("7200"),
                new BigDecimal("1296"),
                new BigDecimal("18"),
                new BigDecimal("9000")
        );

        InvoiceValidationResult result = validator.validate(invoice);

        assertFalse(result.valid());
        assertTrue(
                result.errors().stream()
                        .anyMatch(error ->
                                error.contains("Grand total mismatch"))
        );
    }
}