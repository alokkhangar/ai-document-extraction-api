package com.example.aidocument.config;

import com.google.genai.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("gemini")
public class GeminiConfig {

    @Bean
    public Client geminiClient() {
        return Client.builder()
                .apiKey(System.getenv("GEMINI_API_KEY"))
                .build();
    }
}