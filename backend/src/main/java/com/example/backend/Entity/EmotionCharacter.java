package com.example.backend.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "emotion_character",
        uniqueConstraints = {
                // emotion + level은 유니크 조합
                @UniqueConstraint(columnNames = {"emotion", "level"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmotionCharacter {

    // 캐릭터의 고유한 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    // 캐릭터 이름
    @Column(name = "name", nullable = false, length = 30)
    private String name;

    // 캐릭터 감정 영어로
    @Column(name = "eng", nullable = false, length = 30)
    private String eng;

    // 대표 감정
    @Column(name = "emotion", nullable = false, length = 30)
    private String emotion;

    // 레벨 (1~5)
    @Column(name = "level", nullable = false)
    // @Min(1)
    // @Max(5)
    private int level;

    // 캐릭터 이미지 url
    @Column(name = "image", nullable = false, columnDefinition = "TEXT")
    private String image;


    // 캐릭터 설명
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

}
