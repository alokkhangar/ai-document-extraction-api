package com.example.aidocument.service.impl;

import com.example.aidocument.dto.InvoiceExtractionResponse;
import com.example.aidocument.dto.PdfExtractionResponse;
import com.example.aidocument.model.Invoice;
import com.example.aidocument.model.InvoiceValidationResult;
import com.example.aidocument.service.DocumentService;
import com.example.aidocument.service.InvoiceValidator;
import com.example.aidocument.service.LlmService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private final LlmService llmService;
    private final InvoiceValidator invoiceValidator;

    public DocumentServiceImpl(
            LlmService llmService,
            InvoiceValidator invoiceValidator) {

        this.llmService = llmService;
        this.invoiceValidator = invoiceValidator;
    }

    @Override
    public PdfExtractionResponse extractText(MultipartFile file) {

        validateFile(file);

        try {
            byte[] pdfBytes = file.getBytes();

            try (PDDocument document = Loader.loadPDF(pdfBytes)) {

                PDFTextStripper textStripper = new PDFTextStripper();

                String text = textStripper.getText(document);

                return new PdfExtractionResponse(
                        file.getOriginalFilename(),
                        document.getNumberOfPages(),
                        text
                );
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to extract text from PDF",
                    e
            );
        }
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "PDF file cannot be empty"
            );
        }

        if (!PDF_CONTENT_TYPE.equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException(
                    "Only PDF files are supported"
            );
        }
    }

    @Override
    public InvoiceExtractionResponse extractInvoice(MultipartFile file) {

        validateFile(file);

        try {
            byte[] pdfBytes = file.getBytes();

            try (PDDocument document = Loader.loadPDF(pdfBytes)) {

                PDFTextStripper textStripper = new PDFTextStripper();

                String text = textStripper.getText(document);

                Invoice invoice = llmService.extractInvoice(text);

                InvoiceValidationResult validation =
                        invoiceValidator.validate(invoice);

                boolean reviewRequired = !validation.valid();

                String status = reviewRequired
                        ? "REVIEW_REQUIRED"
                        : "COMPLETED";

                double confidenceScore = reviewRequired
                        ? calculateConfidence(validation)
                        : 0.98;

                List<String> reviewReasons = reviewRequired
                        ? validation.errors()
                        : List.of();

                return new InvoiceExtractionResponse(
                        invoice,
                        validation,
                        status,
                        reviewRequired,
                        confidenceScore,
                        reviewReasons
                );
            }

        } catch (IOException e) {
            throw new IllegalStateException("Unable to process PDF", e);
        }
    }

    private double calculateConfidence(InvoiceValidationResult validation) {

        int errorCount = validation.errors().size();
        double confidence = 0.98 - (errorCount * 0.15);
        return Math.max(confidence, 0.30);
    }
}