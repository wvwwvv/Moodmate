package com.example.backend.Controller;


import com.example.backend.Config.auth.PrincipalDetails;
import com.example.backend.Dto.ChatLogDto;
import com.example.backend.Dto.ChatRequestDto;
import com.example.backend.Dto.ChatResponseDto;
import com.example.backend.Service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    // 새로운 채팅 메시지를 처리하는 API
    @PostMapping
    public ResponseEntity<ChatResponseDto> processChatMessage(
            @AuthenticationPrincipal PrincipalDetails principalDetails, //현재 로그인 사용자 정보
            @RequestBody ChatRequestDto requestDto) {

        // 받아온 사용자 정보에서 User 객체를 서비스로 전달
        ChatResponseDto responseDto = chatService.processMessage(principalDetails.getUser(), requestDto.getText());

        return ResponseEntity.ok(responseDto);
    }


    //오늘의 채팅 기록을 불러오는 API, 채팅 기록 return
    @GetMapping
    public ResponseEntity<List<ChatLogDto>> getTodayChatLogs(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        List<ChatLogDto> chatLogs = chatService.getTodayChatLogs(principalDetails.getUser());
        return ResponseEntity.ok(chatLogs);
    }
}


