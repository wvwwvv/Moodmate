package com.example.backend.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // /api/** 경로에 대해서
                .allowedOrigins(
                        "http://localhost:3000",
                        //"https://moodmate-red.vercel.app"
                        "https://www.moodmate.site"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // 쿠키 인증 요청 허용

        // OAuth2 로그인 시작 경로 CORS
        registry.addMapping("/oauth2/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "https://www.moodmate.site"
                )
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        // OAuth2 콜백 경로 CORS
        registry.addMapping("/login/oauth2/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "https://www.moodmate.site"
                )
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
