package com.example.backend.Repository;

import com.example.backend.Entity.EmotionLog;
import com.example.backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmotionLogRepository extends JpaRepository<EmotionLog, Long> {
    //추후 삭제
    //List<EmotionLog> findByUserAndCreatedDatetimeBetweenOrderByCreatedDatetimeAsc(User user, LocalDateTime start, LocalDateTime end);

    //사용자의 특정 기간 동안 특정 감정이 몇 번 나타났는지 세는 메서드
    int countByUserAndEmotionAndCreatedDatetimeBetween(User user, String emotion, LocalDateTime start, LocalDateTime end);

    //사용자의 특정 기간 동안 모든 로그를 생성 시간순으로 조회하는 메서드
    List<EmotionLog> findAllByUserAndCreatedDatetimeBetweenOrderByCreatedDatetimeAsc(User user, LocalDateTime start, LocalDateTime end);

    //사용자가 기간 동안 기록한 모든 메시지의 총 개수 세는 메서드
    int countByUserAndCreatedDatetimeBetween(User user, LocalDateTime start, LocalDateTime end);

    void deleteAllByUser(User user);
}
