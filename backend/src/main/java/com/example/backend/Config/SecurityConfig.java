package com.example.backend.Config;

import com.example.backend.Config.auth.OAuth2SuccessHandler;
import com.example.backend.Config.auth.PrincipalOauth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 필요 시 생성
                        .sessionFixation().none()
                        .maximumSessions(2) // 동시 세션 제한
                )
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // WebConfig에서 설정한 CORS가 적용되도록 추가
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/oauth2/**").permitAll() // Thymeleaf 테스트용 페이지는 허용
                        .requestMatchers("/login","/").permitAll() // 테스트용으로 허용
                        .requestMatchers("/api/**").authenticated() // /api/** 요청은 인증 필요
                        .anyRequest().permitAll() // 나머지 API 요청은 일단 허용
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(principalOauth2UserService)
                        )
                        .successHandler(oAuth2SuccessHandler) // 커스텀 성공 핸들러 등록
                )
                .logout(logout -> logout
                        .logoutUrl("/api/my-page/logout") // 프론트엔드가 호출할 로그아웃 API 경로
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // 로그아웃 성공 시 별도의 리디렉션 없이 200 OK 응답만 보냄
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .deleteCookies("JSESSIONID") // 세션 쿠키 삭제
                        .invalidateHttpSession(true) // 세션 무효
                );

        return http.build();
    }
}
