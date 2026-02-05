package com.example.backend.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

//마이 페이지 -> 앱 설정 -> 하루 초기화 시간 수정에 사용

@Getter
@NoArgsConstructor
public class ResetTimeUpdateRequestDto {
    private LocalTime dailyResetTime;
}
