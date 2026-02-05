package com.example.backend.Repository;

import com.example.backend.Entity.EmotionCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmotionCharacterRepository extends JpaRepository<EmotionCharacter, Long> {
    // Emotion과 Level로 Character를 찾는 메서드
    Optional<EmotionCharacter> findByEmotionAndLevel(String emotion, int level);

    //특정 감정의 모든 레벨 캐릭터를 레벨 순으로 조회하는 메서드
    List<EmotionCharacter> findByEmotionOrderByLevelAsc(String emotion);

    //eng 기준 조회 메서드
    Optional<EmotionCharacter> findByEngAndLevel(String eng, int level);
    List<EmotionCharacter> findByEngOrderByLevelAsc(String eng);
    List<EmotionCharacter> findByLevelOrderByEmotionAsc(int level);

    //한글 emotion 으로 영어 emotion(eng) 찾는 메서드
    Optional<EmotionCharacter> findFirstByEmotion(String emotion);
}
