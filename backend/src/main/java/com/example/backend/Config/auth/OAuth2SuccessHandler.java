package com.example.backend.Config.auth;

import com.example.backend.Entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {


        // 인증된 Principal 객체를 받아옴
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
        log.info("OAuth2 Login a success. User: {}", user.getKakaoName());

        // 백엔드 서버로 요청이 들어온 Host를 기반으로 프론트엔드 주소를 동적으로 구성
        // String frontendHost = "https://moodmate-red.vercel.app";
        // String frontendHost = "https://www.moodmate.site";

        String targetUrl;

        if (user.isTermsAgreed() && user.isPrivacyAgreed()) {
            //두 약관에 모두 동의한 경우
            targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/chat")
                    .build().toUriString();
        } else {
            //두 약관에 모두 동의 하지 않은 경우
            targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/terms-agreement")
                    .build().toUriString();
        }

        log.info("Successfully authenticated user. Redirecting to {}", targetUrl);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
