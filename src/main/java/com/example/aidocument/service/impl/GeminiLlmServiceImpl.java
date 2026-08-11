package com.example.aidocument.service.impl;

import com.example.aidocument.model.Invoice;
import com.example.aidocument.model.InvoiceItem;
import com.example.aidocument.model.Vendor;
import com.example.aidocument.model.llm.InvoiceExtraction;
import com.example.aidocument.service.LlmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Schema;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Profile("gemini")
public class GeminiLlmServiceImpl implements LlmService {

    private final Client geminiClient;
    private final ObjectMapper objectMapper;

    public GeminiLlmServiceImpl(
            Client geminiClient,
            ObjectMapper objectMapper) {

        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Invoice extractInvoice(String documentText) {

        if (documentText == null || documentText.isBlank()) {
            throw new IllegalArgumentException(
                    "Document text cannot be empty"
            );
        }

        String prompt = buildPrompt(documentText);

        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .responseMimeType("application/json")
                        .responseSchema(buildInvoiceSchema())
                        .candidateCount(1)
                        .build();

        GenerateContentResponse response =
                geminiClient.models.generateContent(
                        "gemini-3.6-flash",
                        prompt,
                        config
                );

        String json = response.text();

        if (json == null || json.isBlank()) {
            throw new IllegalStateException(
                    "Gemini returned an empty response"
            );
        }

        try {

            InvoiceExtraction extraction =
                    objectMapper.readValue(
                            json,
                            InvoiceExtraction.class
                    );

            return convertToInvoice(extraction);

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Unable to parse Gemini invoice response",
                    e
            );
        }
    }

    private String buildPrompt(String documentText) {

        return """
                You are an invoice data extraction system.

                Extract invoice information from the document text below.

                Rules:

                1. Extract only information explicitly present in the document.
                2. Do not invent missing values.
                3. Do not calculate or correct financial values.
                4. Preserve the invoice number exactly.
                5. Extract every invoice line item.
                6. Extract quantity, unit, unit price and amount.
                7. Extract subtotal, tax amount, tax rate and grand total.
                8. Convert the invoice date to yyyy-MM-dd.
                9. If a text field is missing, return an empty string.
                10. If a numeric field is missing, return 0.

                Document text:

                %s
                """.formatted(documentText);
    }

    private Schema buildInvoiceSchema() {

        Schema itemSchema = Schema.builder()
                .type("OBJECT")
                .properties(
                        ImmutableMap.of(
                                "description",
                                Schema.builder()
                                        .type("STRING")
                                        .build(),

                                "quantity",
                                Schema.builder()
                                        .type("NUMBER")
                                        .build(),

                                "unit",
                                Schema.builder()
                                        .type("STRING")
                                        .build(),

                                "unitPrice",
                                Schema.builder()
                                        .type("NUMBER")
                                        .build(),

                                "amount",
                                Schema.builder()
                                        .type("NUMBER")
                                        .build()
                        )
                )
                .required(
                        ImmutableList.of(
                                "description",
                                "quantity",
                                "unit",
                                "unitPrice",
                                "amount"
                        )
                )
                .build();

        Schema vendorSchema = Schema.builder()
                .type("OBJECT")
                .properties(
                        ImmutableMap.of(
                                "name",
                                Schema.builder()
                                        .type("STRING")
                                        .build(),

                                "gstin",
                                Schema.builder()
                                        .type("STRING")
                                        .build()
                        )
                )
                .required(
                        ImmutableList.of(
                                "name",
                                "gstin"
                        )
                )
                .build();

        return Schema.builder()
                .type("OBJECT")
                .properties(
                        ImmutableMap.of(
                                "vendor",
                                vendorSchema,

                                "invoiceNumber",
                                Schema.builder()
                                        .type("STRING")
                                        .build(),

                                "invoiceDate",
                                Schema.builder()
                                        .type("STRING")
                                        .build(),

                                "customer",
                                Schema.builder()
                                        .type("STRING")
                                        .build(),

                                "items",
                                Schema.builder()
                                        .type("ARRAY")
                                        .items(itemSchema)
                                        .build(),

                                "subtotal",
                                Schema.builder()
                                        .type("NUMBER")
                                        .build(),

                                "taxAmount",
                                Schema.builder()
                                        .type("NUMBER")
                                        .build(),

                                "taxRate",
                                Schema.builder()
                                        .type("NUMBER")
                                        .build(),

                                "grandTotal",
                                Schema.builder()
                                        .type("NUMBER")
                                        .build()
                        )
                )
                .required(
                        ImmutableList.of(
                                "vendor",
                                "invoiceNumber",
                                "invoiceDate",
                                "customer",
                                "items",
                                "subtotal",
                                "taxAmount",
                                "taxRate",
                                "grandTotal"
                        )
                )
                .build();
    }

    private Invoice convertToInvoice(
            InvoiceExtraction extraction) {

        if (extraction.vendor == null) {
            throw new IllegalStateException(
                    "Gemini response does not contain vendor information"
            );
        }

        if (extraction.items == null) {
            throw new IllegalStateException(
                    "Gemini response does not contain invoice items"
            );
        }

        Vendor vendor = new Vendor(
                extraction.vendor.name,
                extraction.vendor.gstin
        );

        List<InvoiceItem> items =
                extraction.items.stream()
                        .map(item -> new InvoiceItem(
                                item.description,
                                BigDecimal.valueOf(item.quantity),
                                item.unit,
                                BigDecimal.valueOf(item.unitPrice),
                                BigDecimal.valueOf(item.amount)
                        ))
                        .toList();

        return new Invoice(
                vendor,
                extraction.invoiceNumber,
                LocalDate.parse(extraction.invoiceDate),
                extraction.customer,
                items,
                BigDecimal.valueOf(extraction.subtotal),
                BigDecimal.valueOf(extraction.taxAmount),
                BigDecimal.valueOf(extraction.taxRate),
                BigDecimal.valueOf(extraction.grandTotal)
        );
    }
}