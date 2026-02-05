package com.example.backend.Dto;

//프론트엔드(사용자)가 보낼 메시지를 담을 클래스
//채팅 페이지 -> 채팅 입력에 사용

public class ChatRequestDto {
    private String text;


    // Getter and Setter

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
