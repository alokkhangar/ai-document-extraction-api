package com.example.aidocument.service.impl;

import com.example.aidocument.model.Invoice;
import com.example.aidocument.model.InvoiceItem;
import com.example.aidocument.model.Vendor;
import com.example.aidocument.model.llm.InvoiceExtraction;
import com.example.aidocument.service.LlmService;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Profile("openai")
public class OpenAILlmServiceImpl implements LlmService {

    private final OpenAIClient openAIClient;

    public OpenAILlmServiceImpl(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    @Override
    public Invoice extractInvoice(String documentText) {

        String prompt = buildPrompt(documentText);

        var params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_5_2)
                .responseFormat(InvoiceExtraction.class)
                .addUserMessage(prompt)
                .build();

        var response = openAIClient
                .chat()
                .completions()
                .create(params);

        InvoiceExtraction extraction = response.choices()
                .stream()
                .flatMap(choice -> choice.message().content().stream())
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No structured invoice returned by LLM"
                        ));

        return convertToInvoice(extraction);
    }

    private String buildPrompt(String documentText) {

        return """
                Extract invoice information from the following document text.

                Rules:

                1. Extract only information explicitly present in the document.
                2. Do not invent missing values.
                3. If GSTIN is not present, return null.
                4. Preserve the invoice number exactly.
                5. Convert the invoice date to ISO format yyyy-MM-dd.
                6. Extract every invoice line item.
                7. Extract quantity, unit, unit price and amount.
                8. Do not calculate or correct values.
                9. Return the values exactly as represented by the document.

                Document:

                %s
                """.formatted(documentText);
    }

    private Invoice convertToInvoice(
            InvoiceExtraction extraction) {

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

//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