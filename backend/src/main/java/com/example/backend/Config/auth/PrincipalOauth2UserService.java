package com.example.backend.Config.auth;

import com.example.backend.Entity.User;
import com.example.backend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
@Transactional
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository; // @RequiredArgsConstructor를 통해 자동 주입

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Object idAttribute = attributes.get("id");
        Long kakaoId;

        if (idAttribute instanceof Number) {
            kakaoId = ((Number) idAttribute).longValue();
        } else if (idAttribute instanceof String) {
            kakaoId = Long.parseLong((String) idAttribute);
        } else {
            throw new OAuth2AuthenticationException("Kakao ID attribute type mismatch: " + idAttribute.getClass().getName());
        }

        // kakao_account 맵과 profile 맵이 null이 아닌지 확인
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (kakaoAccount != null) ? (Map<String, Object>) kakaoAccount.get("profile") : null;

        String rawKakaoName = (profile != null) ? (String) profile.get("nickname") : null;
        String kakaoName = (rawKakaoName != null && !rawKakaoName.isEmpty()) ? rawKakaoName : "사용자";
        String rawKakaoProfileImageUrl = (profile != null) ? (String) profile.get("profile_image_url") : null;
        String kakaoProfileImageUrl = (rawKakaoProfileImageUrl != null) ? rawKakaoProfileImageUrl : "";

        Optional<User> userOptional = userRepository.findByKakaoId(kakaoId);

        User user;
        if (userOptional.isPresent()) {
            // 사용자가 이미 존재하면 로그인
            user = userOptional.get();
            // 닉네임, 프로필 사진이 변경되었을 수 있으니 업데이트
            user.updateProfile(kakaoName, kakaoProfileImageUrl);
            user.setLastLogin(LocalDateTime.now()); // 마지막 로그인 시간 업데이트
            userRepository.save(user);
            System.out.println("기존 회원 로그인: " + user.getKakaoName());
        } else {
            // 사용자가 존재하지 않으면 최초 로그인 -> 자동 회원가입
            user = User.builder()
                    .kakaoId(kakaoId)
                    .kakaoName(kakaoName)
                    .profileImageUrl(kakaoProfileImageUrl)
                    .kakaoProfileImageUrl(kakaoProfileImageUrl)
                    .lastLogin(LocalDateTime.now()) // 최초 로그인이므로 마지막 로그인 시간도 현재로 설정
                    .build();
            userRepository.save(user);
            System.out.println("신규 회원 가입: " + user.getKakaoName());
        }

        // Spring Security의 인증 세션에 저장할 Principal 객체 생성
        // oAuth2User를 반환하는 대신, 우리 User 엔티티를 포함한 PrincipalDetails 객체를 반환
        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}
