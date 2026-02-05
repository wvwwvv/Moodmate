package com.example.backend.Dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

//콜렉션 페이지 -> 상세 정보 페이지 에서 사용할 레벨별 캐릭터 세부 정보

@Getter
@Builder
public class LevelSpecificDetailDto {
    private int level;
    private boolean isAcquired;
    private String name;
    private String imageUrl;
    private String description;

    //획득 해야 값을 가지는 필드
    private Integer count;
    private LocalDateTime createdDatetime;
    private boolean isNew;
    private Long collectionId; // new를 제거하는 checkCharacter api 호출에 사용
}
