package com.example.aidocument.service;

import com.example.aidocument.dto.PdfExtractionResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    PdfExtractionResponse extractText(MultipartFile file);
}