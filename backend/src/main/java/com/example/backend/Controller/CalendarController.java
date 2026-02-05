package com.example.backend.Controller;

import com.example.backend.Config.auth.PrincipalDetails;
import com.example.backend.Dto.ChatLogDto;
import com.example.backend.Service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;


    //특정 "월" 의 "일"별 대표 감정들 조회
    //<날짜, 감정> 을 담은 Map 반환
    @GetMapping("/{year}/{month}")
    public ResponseEntity<Map<Integer, String>> getMonthlyEmotions(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("year") int year,
            @PathVariable("month") int month) {
        Map<Integer, String> monthlyEmotions = calendarService.getMonthlyEmotions(principalDetails.getUser(), year, month);
        return ResponseEntity.ok(monthlyEmotions);
    }


    //특정 "일"의 채팅 로그 조회
    //ChatLogDto 타입의 리스트를 반환
    @GetMapping("/logs/{year}/{month}/{day}")
    public ResponseEntity<List<ChatLogDto>> getDailyChatLogs(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("year") int year,
            @PathVariable("month") int month,
            @PathVariable("day") int day) {
        LocalDate date = LocalDate.of(year, month, day);
        List<ChatLogDto> chatLogs = calendarService.getDailyLogs(principalDetails.getUser(), date);
        return ResponseEntity.ok(chatLogs);
    }
}
