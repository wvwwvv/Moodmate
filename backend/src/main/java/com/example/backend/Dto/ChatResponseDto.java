package com.example.backend.Dto;

//백엔드(서버)가 보낼 정보를 담은 클래스
//챗봇의 답변과 분석한 감정을 전달

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatResponseDto {
    private final String answer;
    private final String emotion;
    private final LocalDateTime time;
    private final String characterImageUrl; //emotion 의 캐릭터 중 가장 레벨이 높은 캐릭터 이미지

}