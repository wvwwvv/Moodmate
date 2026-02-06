package com.example.backend.Controller;

import com.example.backend.Config.auth.PrincipalDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @Value("${app.frontend-url}")
    private String frontendLoginUrl;

    @GetMapping("/loginForm")
    public String loginForm() {
        return "login";
    }


    @GetMapping("/")
    public String home(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        // Spring Security의 인증 정보(Authentication)를 확인
        // String frontendLoginUrl = "https://www.moodmate.site"; 배포 도메인

        if (principalDetails == null) {
            // 인증되지 않은 사용자라면, 프론트엔드 로그인 페이지로 리디렉션
            return "redirect:" + frontendLoginUrl;
        }

        return "redirect:" + frontendLoginUrl + "/chat"; // 또는 이미 로그인되었다면 /chat 등으로 리디렉션하는 로직
    }


}
