package com.example.backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "kakao_id", nullable = false, unique = true)
    private Long kakaoId;

    @Column(name = "kakao_name", nullable = false, length = 20)
    private String kakaoName; // DB 스키마에 맞춰 길이 20으로 수정

    @Builder.Default
    @Column(name = "nickname", nullable = false, length = 20)
    private String nickname = "사용자";

    //사용자가 앱 내에서 설정하는 프로필 이미지
    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;

    //카카오에서 동기화되는 원본 프로필 이미지
    @Column(name = "kakao_profile_image_url", columnDefinition = "TEXT")
    private String kakaoProfileImageUrl;

    @Column(name = "email", length = 50)
    private String email;

    @CreationTimestamp // 엔티티 생성 시 자동으로 현재 시간 저장
    @Column(name = "created_datetime", nullable = false, updatable = false)
    private LocalDateTime createdDatetime;

    //@UpdateTimestamp // 엔티티 업데이트 시 자동으로 현재 시간 저장 : 이렇게 하면 사용자 정보 업데이트 해도 lastLogin 바뀜
    @Column(name = "last_login", nullable = false)
    private LocalDateTime lastLogin;

    @Builder.Default
    @Column(name = "daily_reset_time")
    private LocalTime dailyResetTime = LocalTime.of(0,0,0); //(기본값 00:00)


    @Builder.Default
    @Column(name = "terms_agreed", nullable = false)
    private boolean termsAgreed = false;

    @Builder.Default
    @Column(name = "privacy_agreed", nullable = false)
    private boolean privacyAgreed = false;

    @Column(name = "terms_agreed_at")
    private LocalDateTime termsAgreedAt;

    @Column(name = "privacy_agreed_at")
    private LocalDateTime privacyAgreedAt;

    @Builder.Default
    @Column(name = "terms_version", length = 20)
    private String termsVersion = "1.0";

    @Builder.Default
    @Column(name = "privacy_version", length = 20)
    private String privacyVersion = "1.0";


    //카카오 정보 동기화 메서드
    public void updateProfile(String kakaoName, String kakaoProfileImageUrl) {
        this.kakaoName = kakaoName;
        this.kakaoProfileImageUrl = kakaoProfileImageUrl;
    }

}
