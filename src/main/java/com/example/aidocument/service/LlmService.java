package com.example.aidocument.service;

import com.example.aidocument.model.Invoice;

public interface LlmService {

    Invoice extractInvoice(String documentText);
}