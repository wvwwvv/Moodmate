package com.example.backend.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "emotion_mapping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmotionMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //고마움, 만족, 무기력과 같은 세부 감정
    @Column(name = "detail_emotion", nullable = false, length = 20)
    private String detailEmotion;

    //기쁨, 슬픔과 같은 대표 감정
    @Column(name = "core_emotion", nullable = false, length = 20)
    private String coreEmotion;
}
