package com.example.backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "emotion_status",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "emotion"}) // 유저별 감정 중복 방지
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmotionStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "emotion", nullable = false, length = 20)
    private String emotion;

    // 필드 초기값 설정
    @Column(name = "count", nullable = false)
    private int count = 1;

    // 생성 및 업데이트 시 모두 자동으로 시간 기록
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    public void prePersistAndPreUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
