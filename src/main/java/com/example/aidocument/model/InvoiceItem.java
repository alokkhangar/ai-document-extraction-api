package com.example.aidocument.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record InvoiceItem(

        @NotBlank
        String description,

        @NotNull
        @PositiveOrZero
        BigDecimal quantity,

        @NotBlank
        String unit,

        @NotNull
        @PositiveOrZero
        BigDecimal unitPrice,

        @NotNull
        @PositiveOrZero
        BigDecimal amount
) {
}