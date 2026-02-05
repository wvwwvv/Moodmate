package com.example.backend.Dto;

import com.example.backend.Entity.EmotionLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor



public class EmotionLogDto {
    private String text;
    private String answer;
    private String emotion;
    private LocalDateTime createdDatetime;

    public static EmotionLogDto fromEntity(EmotionLog entity) {
        return new EmotionLogDto(
                entity.getText(),
                entity.getAnswer(),
                entity.getEmotion(),
                entity.getCreatedDatetime()
        );
    }
}
