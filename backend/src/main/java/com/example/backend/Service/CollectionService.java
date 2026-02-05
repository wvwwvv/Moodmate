package com.example.backend.Service;

import com.example.backend.Dto.CharacterCollectionDto;
import com.example.backend.Dto.CharacterDetailDto;
import com.example.backend.Dto.LevelSpecificDetailDto;
import com.example.backend.Entity.CharacterCollection;
import com.example.backend.Entity.User;
import com.example.backend.Repository.CharacterCollectionRepository;
import com.example.backend.Repository.EmotionCharacterRepository;
import com.example.backend.Entity.EmotionCharacter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CollectionService {

    private final CharacterCollectionRepository characterCollectionRepository;
    private final EmotionCharacterRepository emotionCharacterRepository;

    //기본 감정 18개
    /*private static final List<String> BASE_EMOTIONS = List.of(
            "기쁨", "행복", "즐거움/신남", "고마움", "슬픔", "힘듦/지침",
            "불쌍함/연민", "두려움", "의심/불신", "분노", "화남/분노", "짜증",
            "불평/불만", "놀람", "당황/난처", "혐오", "중립/복합", "안심/신뢰"
    );*/


    //처음 페이지 접근 시 18개 캐릭터 정보 로드
    //획득한 감정 중 하나라도 isNew = true 이면 (도감 메인 페이지의) 대표 감정에 isNew = true 설정
    public List<CharacterCollectionDto> getCollectionForUser(User user) {
        // DB에서 레벨 1인 모든 캐릭터(기반 캐릭터)를 조회
        List<EmotionCharacter> baseCharacters = emotionCharacterRepository.findByLevelOrderByEmotionAsc(1);

        // DB에서 조회한 기반 캐릭터 리스트를 기준으로 DTO를 생성
        return baseCharacters.stream().map(baseCharacter -> {
            String emotion = baseCharacter.getEmotion(); // 현재 캐릭터의 감정 이름
            String emotionEng = baseCharacter.getEng();

            // 각 감정 별로 유저가 획득한 최고 레벨 조회 (없으면 0)
            int maxLevel = characterCollectionRepository.findMaxLevelByUserAndEmotion(user, emotion)
                    .orElse(0);

            boolean isAcquired = maxLevel > 0;
            String imageUrl;

            if (isAcquired) {
                // 획득한 경우: 최고 레벨 캐릭터의 이미지 URL 조회
                imageUrl = emotionCharacterRepository.findByEmotionAndLevel(emotion, maxLevel)
                        .map(EmotionCharacter::getImage)
                        .orElse(""); // 예외 처리
            } else {
                // 미획득인 경우: 기반 캐릭터(level 1)의 이미지를 사용 (이미 조회했으므로 DB 재접근 방지)
                imageUrl = baseCharacter.getImage();
            }

            // 해당 감정에 'NEW' 마커가 있는지 확인
            boolean isNew = characterCollectionRepository.existsByUserAndEmotionCharacter_EmotionAndIsNewTrue(user, emotion);

            // 최종 DTO 생성
            return CharacterCollectionDto.builder()
                    .emotion(emotion)
                    .level(maxLevel)
                    .imageUrl(imageUrl)
                    .isAcquired(isAcquired)
                    .isNew(isNew)
                    .emotionEng(emotionEng)
                    .build();

        }).collect(Collectors.toList());
    }


    //콜렉션 페이지 상세 페이지 로직
    //각 레벨별 isNew 상태와 collectionId 도 함께 반환
    public CharacterDetailDto getCharacterDetailForUser(User user, String emotionEng) {
        //해당 감정의 1 ~ 5 레벨 캐릭터 모두 가져옴
        List<EmotionCharacter> charactersByEmotion = emotionCharacterRepository.findByEngOrderByLevelAsc(emotionEng);

        //조회된 캐릭터 정보에서 '한글' 감정 이름(emotion)을 추출
        String koreanEmotion = charactersByEmotion.get(0).getEmotion();

        List<CharacterCollection> acquiredCharacters = characterCollectionRepository.findByUserAndEmotionWithCharacter(user, koreanEmotion);


        //획득한 컬렉션 정보를 레벨별 빠르게 찾기 위해 Map 으로 변환
        Map<Integer, CharacterCollection> acquiredMap = acquiredCharacters.stream()
                .collect(Collectors.toMap(cc -> cc.getEmotionCharacter().getLevel(), Function.identity()));

        // 1~5레벨별 상세 DTO 리스트 생성
        List<LevelSpecificDetailDto> levelDetails = charactersByEmotion.stream().map(character -> {
            boolean isAcquired = acquiredMap.containsKey(character.getLevel());
            CharacterCollection collection = acquiredMap.get(character.getLevel());

            return LevelSpecificDetailDto.builder()
                    .level(character.getLevel())
                    .isAcquired(isAcquired)
                    .name(character.getName())
                    .imageUrl(character.getImage())
                    .description(character.getDescription())
                    .count(isAcquired ? collection.getCount() : null)
                    .createdDatetime(isAcquired ? collection.getCreatedDatetime() : null)
                    .isNew(isAcquired && collection.getIsNew())
                    .collectionId(isAcquired ? collection.getId() : null)
                    .build();
        }).collect(Collectors.toList());

        // 유저가 획득한 최고 레벨 계산 (없으면 0)
        int maxAcquiredLevel = acquiredMap.keySet().stream().max(Integer::compare).orElse(0);

        // 최종 DTO를 빌드하여 반환
        return CharacterDetailDto.builder()
                .emotion(koreanEmotion)
                .initialDisplayLevel(maxAcquiredLevel)
                .levelDetails(levelDetails)
                .build();
    }

    //캐릭터의 isNew 를 false 로 변경 (new 마커를 확인한 경우)
    @Transactional
    public void checkCharacter(User user, Long collectionId) {

        CharacterCollection collection = characterCollectionRepository.findByIdAndUserId(collectionId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("요청한 캐릭터를 찾을 수 없거나, 해당 사용자의 캐릭터가 아닙니다."));

        collection.setIsNew(false);
    }



}