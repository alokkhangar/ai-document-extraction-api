package com.example.aidocument.model;

import jakarta.validation.constraints.NotBlank;

public record Vendor(

        @NotBlank
        String name,

        String gstin
) {
}