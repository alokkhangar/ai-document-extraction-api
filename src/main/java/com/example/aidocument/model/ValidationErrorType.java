package com.example.aidocument.model;

public enum ValidationErrorType {

    GRAND_TOTAL_MISMATCH(0.15),

    SUBTOTAL_MISMATCH(0.15),

    TAX_MISMATCH(0.10),

    MISSING_REQUIRED_FIELD(0.25),

    LINE_ITEM_MISMATCH(0.10),

    UNKNOWN(0.10);

    private final double penalty;

    ValidationErrorType(double penalty) {
        this.penalty = penalty;
    }

    public double getPenalty() {
        return penalty;
    }
}