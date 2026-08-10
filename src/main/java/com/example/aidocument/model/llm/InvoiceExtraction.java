package com.example.aidocument.model.llm;

import java.util.List;

public class InvoiceExtraction {

    public VendorExtraction vendor;

    public String invoiceNumber;

    public String invoiceDate;

    public String customer;

    public List<ItemExtraction> items;

    public double subtotal;

    public double taxAmount;

    public double taxRate;

    public double grandTotal;

    public static class VendorExtraction {

        public String name;

        public String gstin;
    }

    public static class ItemExtraction {

        public String description;

        public double quantity;

        public String unit;

        public double unitPrice;

        public double amount;
    }
}