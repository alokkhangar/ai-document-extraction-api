package com.example.aidocument.service;

import com.example.aidocument.model.Invoice;
import com.example.aidocument.model.InvoiceItem;
import com.example.aidocument.model.InvoiceValidationResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceValidator {

    private static final BigDecimal TOLERANCE = new BigDecimal("0.01");

    public InvoiceValidationResult validate(Invoice invoice) {

        List<String> errors = new ArrayList<>();

        if (invoice == null) {
            errors.add("Invoice cannot be null");
            return new InvoiceValidationResult(false, errors);
        }

        validateLineItems(invoice, errors);
        validateSubtotal(invoice, errors);
        validateGrandTotal(invoice, errors);
        validateTax(invoice, errors);

        return new InvoiceValidationResult(
                errors.isEmpty(),
                errors
        );
    }

    private void validateLineItems(
            Invoice invoice,
            List<String> errors) {

        if (invoice.items() == null || invoice.items().isEmpty()) {
            errors.add("Invoice must contain at least one item");
            return;
        }

        BigDecimal calculatedSubtotal = BigDecimal.ZERO;

        for (InvoiceItem item : invoice.items()) {

            BigDecimal calculatedAmount =
                    item.quantity().multiply(item.unitPrice());

            if (calculatedAmount.subtract(item.amount())
                    .abs()
                    .compareTo(TOLERANCE) > 0) {

                errors.add(
                        "Line item amount mismatch for: "
                                + item.description()
                );
            }

            calculatedSubtotal =
                    calculatedSubtotal.add(item.amount());
        }

        if (calculatedSubtotal.subtract(invoice.subtotal())
                .abs()
                .compareTo(TOLERANCE) > 0) {

            errors.add(
                    "Subtotal mismatch. Expected: "
                            + calculatedSubtotal
                            + ", Actual: "
                            + invoice.subtotal()
            );
        }
    }

    private void validateSubtotal(
            Invoice invoice,
            List<String> errors) {

        if (invoice.subtotal().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Subtotal cannot be negative");
        }
    }

    private void validateGrandTotal(
            Invoice invoice,
            List<String> errors) {

        BigDecimal expectedGrandTotal =
                invoice.subtotal()
                        .add(invoice.taxAmount());

        if (expectedGrandTotal.subtract(invoice.grandTotal())
                .abs()
                .compareTo(TOLERANCE) > 0) {

            errors.add(
                    "Grand total mismatch. Expected: "
                            + expectedGrandTotal
                            + ", Actual: "
                            + invoice.grandTotal()
            );
        }
    }

    private void validateTax(
            Invoice invoice,
            List<String> errors) {

        if (invoice.taxRate() != null &&
                invoice.taxRate().compareTo(BigDecimal.ZERO) < 0) {

            errors.add("Tax rate cannot be negative");
        }

        if (invoice.taxAmount().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Tax amount cannot be negative");
        }
    }
}