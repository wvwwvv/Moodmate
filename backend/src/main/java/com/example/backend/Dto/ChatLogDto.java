package com.example.backend.Dto;

import java.time.LocalDateTime;

//채팅 페이지 접근 시 오늘의 대화 기록 불러올 때 사용

public class ChatLogDto {
    private final String sender; // 메시지를 보낸 주체 ("USER" 또는 "BOT")
    private final String message; // 메시지 내용
    private final LocalDateTime timestamp; // 메시지 시간
    private final String characterImageUrl; // 캐릭터 이미지

    public ChatLogDto(String sender, String message, LocalDateTime timestamp, String characterImageUrl) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.characterImageUrl = characterImageUrl;
    }

    // Getters and Setters
    public String getSender() { return sender; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getCharacterImageUrl() { return characterImageUrl; }
}
