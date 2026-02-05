package com.example.backend.Controller;

import com.example.backend.Config.auth.PrincipalDetails;
import com.example.backend.Dto.*;
import com.example.backend.Service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/my-page")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    //마이 페이지 정보 조회
    @GetMapping
    public ResponseEntity<MyPageInfoDto> getMyPageInfo(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        MyPageInfoDto info = myPageService.getMyPageInfo(principalDetails.getUser());
        return ResponseEntity.ok(info);
    }

    //마이 페이지 -> 프로필 수정
    //삭제
    /*@PutMapping("settings/profile")
    public ResponseEntity<Void> updateProfileImageUrl(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody ProfileImageUrlUpdateRequestDto requestDto) {

        myPageService.updateProfileImageUrl(principalDetails.getUser(), requestDto.getProfileImageUrl());
        return ResponseEntity.ok().build();
    }*/


    //계정 설정 -> 닉네임 수정
    @PutMapping("/settings/nickname")
    public ResponseEntity<Void> updateNickname(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody NicknameUpdateRequestDto requestDto) {

        myPageService.updateNickname(principalDetails.getUser(), requestDto.getNickname());
        return ResponseEntity.ok().build();
    }

    //계정 설정 -> 이메일 수정
    @PutMapping("/settings/email")
    public ResponseEntity<Void> updateEmail(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody EmailUpdateRequestDto requestDto) {

        myPageService.updateEmail(principalDetails.getUser(), requestDto.getEmail());
        return ResponseEntity.ok().build();
    }


    //앱 설정 -> 일일 리셋 시간 수정
    @PutMapping("/settings/reset-time")
    public ResponseEntity<Void> updateDailyResetTime(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody ResetTimeUpdateRequestDto requestDto) {

        myPageService.updateDailyResetTime(principalDetails.getUser(), requestDto.getDailyResetTime());
        return ResponseEntity.ok().build();
    }

    //계정 설정 -> 회원 탈퇴
    @DeleteMapping("/settings/withdraw")
    public ResponseEntity<Void> withdrawAccount(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        myPageService.withdraw(principalDetails.getUser());
        return ResponseEntity.ok().build();
    }



}
