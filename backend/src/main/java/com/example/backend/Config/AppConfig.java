package com.example.backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    // Spring 어디서든 WebClient.Builder 주입 받을 수 있도록 함
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
