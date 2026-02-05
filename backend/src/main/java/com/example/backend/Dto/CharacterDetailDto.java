package com.example.backend.Dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

//콜렉션 페이지 -> 상세 페이지 에서 사용할 전체 정보

@Getter
@Builder
public class CharacterDetailDto {
    private String emotion; //현재 보고 있는 감정
    private int initialDisplayLevel; //처음에 상단에 표시할 캐릭터 레벨
    private List<LevelSpecificDetailDto> levelDetails; //1 ~ 5 레벨의 모든 캐릭터 정보

}