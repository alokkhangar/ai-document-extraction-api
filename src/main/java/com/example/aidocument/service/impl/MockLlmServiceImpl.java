package com.example.aidocument.service.impl;

import com.example.aidocument.model.Invoice;
import com.example.aidocument.model.InvoiceItem;
import com.example.aidocument.model.Vendor;
import com.example.aidocument.service.LlmService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Profile("mock")
public class MockLlmServiceImpl implements LlmService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    @Override
    public Invoice extractInvoice(String documentText) {

        if (documentText == null || documentText.isBlank()) {
            throw new IllegalArgumentException(
                    "Document text cannot be empty"
            );
        }

        String normalizedText = documentText.replace("\r\n", "\n");

        String vendorName = extract(
                normalizedText,
                "(?m)^([A-Za-z0-9 .&]+(?:Pvt\\. Ltd\\.|Ltd\\.|Limited))$"
        );

        String gstin = extract(
                normalizedText,
                "(?m)^GSTIN:\\s*([A-Z0-9]+)"
        );

        String invoiceNumber = extract(
                normalizedText,
                "(?m)^Invoice Number\\s+([^\\s]+)"
        );

        String invoiceDateText = extract(
                normalizedText,
                "(?m)^Invoice Date\\s+([^\\s]+)"
        );

        String customer = extract(
                normalizedText,
                "(?m)^Customer\\s+(.+)$"
        );

        LocalDate invoiceDate = LocalDate.parse(
                invoiceDateText,
                DATE_FORMATTER
        );

        List<InvoiceItem> items = extractItems(normalizedText);

        BigDecimal subtotal = extractAmount(
                normalizedText,
                "(?m)^Subtotal\\s+([0-9,]+(?:\\.\\d{2})?)"
        );

        BigDecimal taxAmount = extractAmount(
                normalizedText,
                "(?m)^GST\\s*\\(([0-9.]+)%\\)\\s+([0-9,]+(?:\\.\\d{2})?)",
                2
        );

        BigDecimal taxRate = extractAmount(
                normalizedText,
                "(?m)^GST\\s*\\(([0-9.]+)%\\)",
                1
        );

        BigDecimal grandTotal = extractAmount(
                normalizedText,
                "(?m)^Grand Total\\s+([0-9,]+(?:\\.\\d{2})?)"
        );

        return new Invoice(
                new Vendor(
                        vendorName,
                        gstin
                ),
                invoiceNumber,
                invoiceDate,
                customer,
                items,
                subtotal,
                taxAmount,
                taxRate,
                grandTotal
        );
    }

    private List<InvoiceItem> extractItems(String text) {

        List<InvoiceItem> items = new ArrayList<>();

        Pattern pattern = Pattern.compile(
                "(?m)^(.+?)\\s+([0-9]+(?:\\.\\d+)?)\\s+" +
                        "(KG|HOUR|PCS|UNIT|LITER|METER)\\s+" +
                        "([0-9,]+(?:\\.\\d{2})?)\\s+" +
                        "([0-9,]+(?:\\.\\d{2})?)$"
        );

        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {

            String description = matcher.group(1).trim();

            BigDecimal quantity =
                    parseAmount(matcher.group(2));

            String unit = matcher.group(3);

            BigDecimal unitPrice =
                    parseAmount(matcher.group(4));

            BigDecimal amount =
                    parseAmount(matcher.group(5));

            items.add(
                    new InvoiceItem(
                            description,
                            quantity,
                            unit,
                            unitPrice,
                            amount
                    )
            );
        }

        if (items.isEmpty()) {
            throw new IllegalStateException(
                    "No invoice line items could be extracted"
            );
        }

        return items;
    }

    private String extract(String text, String regex) {

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (!matcher.find()) {
            throw new IllegalStateException(
                    "Unable to extract required invoice field"
            );
        }

        return matcher.group(1).trim();
    }

    private BigDecimal extractAmount(
            String text,
            String regex) {

        return extractAmount(text, regex, 1);
    }

    private BigDecimal extractAmount(
            String text,
            String regex,
            int group) {

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (!matcher.find()) {
            throw new IllegalStateException(
                    "Unable to extract invoice amount"
            );
        }

        return parseAmount(matcher.group(group));
    }

    private BigDecimal parseAmount(String value) {

        return new BigDecimal(
                value.replace(",", "").trim()
        );
    }
}