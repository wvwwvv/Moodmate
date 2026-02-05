package com.example.backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "daily_emotion_summary",
        uniqueConstraints = {
                // 한 유저당 특정 날짜의 요약은 유일해야 함
                @UniqueConstraint(columnNames = {"user_id", "summary_date"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyEmotionSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 요약의 대상이 되는 유저 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 요약 날짜 (YYYY-MM-DD)
    @Column(name = "summary_date", nullable = false)
    private LocalDate summaryDate;

    // 그날의 대표 감정
    @Column(name = "dominant_emotion", nullable = false, length = 20)
    private String dominantEmotion;

    // 대표 감정이 기록된 횟수
    @Column(name = "emotion_count", nullable = false)
    private int emotionCount;

    // 마지막 업데이트 시각
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    /**
     * 엔티티가 생성되거나 업데이트될 때마다 마지막 업데이트 시각을 자동으로 설정
     */
    @PrePersist
    @PreUpdate
    public void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}