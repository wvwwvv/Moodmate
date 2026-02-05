package com.example.backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "character_collection",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "character_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CharacterCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 성능을 위해 LAZY 로딩을 명시
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) //
    @JoinColumn(name = "character_id", nullable = false)
    private EmotionCharacter emotionCharacter;

    // 필드 초기값 설정
    @Column(name = "count", nullable = false)
    private int count = 1;

    // 최초 수집일
    @Column(name = "created_datetime", nullable = false, updatable = false)
    private LocalDateTime createdDatetime;

    // nullable=false 추가 및 필드 초기값 설정
    @Column(name = "favorite", nullable = false)
    private Boolean favorite = false;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isNew = true;

    // 엔티티 저장 전 날짜 자동 생성
    @PrePersist
    public void prePersist() {
        this.createdDatetime = LocalDateTime.now();
    }
}
