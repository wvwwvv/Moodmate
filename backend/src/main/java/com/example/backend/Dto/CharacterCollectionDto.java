package com.example.backend.Dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

//콜렉션 페이지 접근 시 18 감정의 각 캐릭터 정보
public class CharacterCollectionDto {
    private String emotion;
    private String emotionEng; // 상세 페이지 이동에 사용할 pathVariable
    private int level;
    private String imageUrl;
    private boolean isAcquired;
    private boolean isNew;
}
