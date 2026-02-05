package com.example.backend.Service;

import com.example.backend.Dto.MyPageInfoDto;
import com.example.backend.Entity.User;
import com.example.backend.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final CharacterCollectionRepository characterCollectionRepository;
    private final DailyEmotionSummaryRepository dailyEmotionSummaryRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final EmotionStatusRepository emotionStatusRepository;

    private final WebClient.Builder webClientBuilder;

    @Value("${kakao.admin.key}")
    private String kakaoAdminKey;

    @Transactional(readOnly = true)
    public MyPageInfoDto getMyPageInfo(User user) {

        // 획득한 모든 캐릭터 레벨 총 합
        Integer sumOfLevels = characterCollectionRepository.findSumOfLevelsByUser(user);

        // 수집한 캐릭터가 없어 합계가 null 이면 레벨 0 설정
        int level = (sumOfLevels == null) ? 0 : sumOfLevels;

        return MyPageInfoDto.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .dailyResetTime(user.getDailyResetTime())
                .level(level)
                .termsAgreedAt(user.getTermsAgreedAt())
                .termsVersion(user.getTermsVersion())
                .privacyAgreedAt(user.getPrivacyAgreedAt())
                .privacyVersion(user.getPrivacyVersion())
                .build();
    }

    //프로필 기능 삭제
   /* @Transactional
    public void updateProfileImageUrl(User user, String newProfileImageUrl) {
        user.setProfileImageUrl(newProfileImageUrl);
        userRepository.save(user);
    }*/

    @Transactional
    public void updateNickname(User user, String newNickname) {
        if (newNickname == null || newNickname.isEmpty() || newNickname.length() > 20) {
            throw new IllegalArgumentException("20자 이하로 설정해주세요.");
        }

        user.setNickname(newNickname);
        userRepository.save(user);
    }

    @Transactional
    public void updateEmail(User user, String newEmail) {
        user.setEmail(newEmail);
        userRepository.save(user);
    }

    @Transactional
    public void updateDailyResetTime(User user, LocalTime newResetTime) {
        user.setDailyResetTime(newResetTime);
        userRepository.save(user);
    }

    @Transactional
    public void withdraw(User user) {
        unlinkKakao(user.getKakaoId());
        characterCollectionRepository.deleteAllByUser(user);
        dailyEmotionSummaryRepository.deleteAllByUser(user);
        emotionLogRepository.deleteAllByUser(user);
        emotionStatusRepository.deleteAllByUser(user);

        userRepository.delete(user);
    }

    private void unlinkKakao(Long kakaoId) {
        String url = "https://kapi.kakao.com/v1/user/unlink";
        WebClient webClient = webClientBuilder.baseUrl(url).build();

        String response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoAdminKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue("target_id_type=user_id&target_id=" + kakaoId)
                .retrieve() // 응답 수신
                .bodyToMono(String.class) // 응답 본문을 String 으로 변환
                .block(); // 작업이 완료될 때까지 대기

        System.out.println("Kakao unlink response: " + response);
    }


}
