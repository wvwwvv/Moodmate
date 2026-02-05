package com.example.backend.Repository;


import com.example.backend.Entity.CharacterCollection;
import com.example.backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacterCollectionRepository extends JpaRepository<CharacterCollection, Long> {

    List<CharacterCollection> findByUser(User user);

    Optional<CharacterCollection> findByUserAndEmotionCharacterId(User user, Long characterId);

    // User 와 Character 의 Emotion 으로 Collection 을 찾는 메서드
    Optional<CharacterCollection> findFirstByUserAndEmotionCharacter_Emotion(User user, String emotion);

    //유저가 획득한 모든 캐릭터의 레벨 총 합 찾는 메서드
    @Query("SELECT SUM(cc.emotionCharacter.level) FROM CharacterCollection cc WHERE cc.user = :user")
    Integer findSumOfLevelsByUser(@Param("user") User user);

    //특정 유저와 감정에 대해 가장 높은 레벨값 찾는 메서드
    @Query("SELECT MAX(cc.emotionCharacter.level) FROM CharacterCollection cc WHERE cc.user = :user AND cc.emotionCharacter.emotion = :emotion")
    Optional<Integer> findMaxLevelByUserAndEmotion(@Param("user") User user, @Param("emotion") String emotion);

    //특정 유저의 감정에 해당 하는 모든 컬렉션 정보를 캐릭터 정보와 함께 조회 하는 메서드
    @Query("SELECT cc FROM CharacterCollection cc JOIN FETCH cc.emotionCharacter ec WHERE cc.user = :user AND ec.emotion = :emotion")
    List<CharacterCollection> findByUserAndEmotionWithCharacter(@Param("user") User user, @Param("emotion") String emotion);

    //유저와 감정으로 가장 높은 레벨의 캐릭터를 찾는 메서드
    Optional<CharacterCollection> findTopByUserAndEmotionCharacter_EmotionOrderByEmotionCharacter_LevelDesc(User user, String emotion);

    void deleteAllByUser(User user);

    //isNew 가 true 인 캐릭터가 1개라도 있으면 true
    boolean existsByUserAndEmotionCharacter_EmotionAndIsNewTrue(User user, String emotion);

    //특정 유저와 감정(eng)에 대해 가장 높은 레벨값 찾는 메서드
    @Query("SELECT MAX(cc.emotionCharacter.level) FROM CharacterCollection cc WHERE cc.user = :user AND cc.emotionCharacter.eng = :emotionEng")
    Optional<Integer> findMaxLevelByUserAndEmotionEng(@Param("user") User user, @Param("emotionEng") String emotionEng);

    //특정 유저의 감정에 해당 하는 모든 컬렉션 정보를 캐릭터 정보와 함께 조회 하는 메서드 (eng 로)
    @Query("SELECT cc FROM CharacterCollection cc JOIN FETCH cc.emotionCharacter ec WHERE cc.user = :user AND ec.eng = :emotionEng")
    List<CharacterCollection> findByUserAndEmotionEngWithCharacter(@Param("user") User user, @Param("emotionEng") String emotionEng);

    //isNew 가 true 인 캐릭터가 1개라도 있으면 true (eng 로)
    boolean existsByUserAndEmotionCharacter_EngAndIsNewTrue(User user, String emotionEng);

    //collectionId , userId 를 사용해 데이터 찾는 메서드
    Optional<CharacterCollection> findByIdAndUserId(Long id, Long userId);

}

