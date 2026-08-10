package com.example.aidocument.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record Invoice(

        @NotNull
        @Valid
        Vendor vendor,

        @NotBlank
        String invoiceNumber,

        @NotNull
        LocalDate invoiceDate,

        @NotBlank
        String customer,

        @NotNull
        @Valid
        List<InvoiceItem> items,

        @NotNull
        @PositiveOrZero
        BigDecimal subtotal,

        @NotNull
        @PositiveOrZero
        BigDecimal taxAmount,

        @PositiveOrZero
        BigDecimal taxRate,

        @NotNull
        @PositiveOrZero
        BigDecimal grandTotal
) {
}