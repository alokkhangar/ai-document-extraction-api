package com.example.aidocument.service.impl;

import com.example.aidocument.model.Invoice;
import com.example.aidocument.model.InvoiceItem;
import com.example.aidocument.model.Vendor;
import com.example.aidocument.service.LlmService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Profile("mock")
public class MockLlmServiceImpl implements LlmService {

    @Override
    public Invoice extractInvoice(String documentText) {

        return new Invoice(
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
                                BigDecimal.valueOf(100),
                                "KG",
                                BigDecimal.valueOf(72.00),
                                BigDecimal.valueOf(7200.00)
                        ),
                        new InvoiceItem(
                                "Mild Steel Sheet 6mm",
                                BigDecimal.valueOf(150),
                                "KG",
                                BigDecimal.valueOf(68.00),
                                BigDecimal.valueOf(10200.00)
                        ),
                        new InvoiceItem(
                                "Stainless Steel 304",
                                BigDecimal.valueOf(50),
                                "KG",
                                BigDecimal.valueOf(245.00),
                                BigDecimal.valueOf(12250.00)
                        ),
                        new InvoiceItem(
                                "Cutting Service",
                                BigDecimal.valueOf(10),
                                "HOUR",
                                BigDecimal.valueOf(500.00),
                                BigDecimal.valueOf(5000.00)
                        )
                ),

                BigDecimal.valueOf(34650.00),
                BigDecimal.valueOf(6237.00),
                BigDecimal.valueOf(18.00),
                BigDecimal.valueOf(40887.00)
        );
    }
}