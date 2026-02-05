package com.example.backend.Dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

//마이 페이지 접근할 때 사용자 정보 가져올 때 사용

@Getter
@Builder
public class MyPageInfoDto {
    private String nickname;
    //private String profileImageUrl; 프로필 기능 삭제
    private String email;
    private int level;
    private LocalTime dailyResetTime;
    private LocalDateTime termsAgreedAt;
    private LocalDateTime privacyAgreedAt;
    private String termsVersion;
    private String privacyVersion;

}
