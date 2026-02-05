package com.example.backend.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

//마이 페이지 -> 계정 설정 -> 닉네임 수정에 사용

@Getter
@NoArgsConstructor
public class NicknameUpdateRequestDto {
    private String nickname;
}
