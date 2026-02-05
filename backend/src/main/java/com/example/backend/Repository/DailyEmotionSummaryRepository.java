package com.example.backend.Repository;

import com.example.backend.Entity.DailyEmotionSummary;
import com.example.backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyEmotionSummaryRepository extends JpaRepository<DailyEmotionSummary, Long> {
    // User 와 summaryDate 로 요약 정보를 찾는 메서드
    Optional<DailyEmotionSummary> findByUserAndSummaryDate(User user, LocalDate summaryDate);

    // User 별 월별(startDate, endDate 사이)로 일일 대표 감정 찾는 메서드
    List<DailyEmotionSummary> findAllByUserAndSummaryDateBetween(User user, LocalDate startDate, LocalDate endDate);

    void deleteAllByUser(User user);
}
