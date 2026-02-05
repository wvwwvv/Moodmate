package com.example.backend.Repository;

import com.example.backend.Entity.EmotionStatus;
import com.example.backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmotionStatusRepository extends JpaRepository<EmotionStatus, Long> {

    // User 와 Emotion 으로 Status 를 찾는 메서드
    Optional<EmotionStatus> findByUserAndEmotion(User user, String emotion);

    void deleteAllByUser(User user);
}
