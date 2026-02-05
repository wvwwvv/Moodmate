package com.example.backend.Service;

import com.example.backend.Dto.ChatLogDto;
import com.example.backend.Dto.ChatResponseDto;
import com.example.backend.Entity.*;
import com.example.backend.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ChatService {
    // 필요한 Repository 주입

    private final UserRepository userRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final EmotionStatusRepository emotionStatusRepository;
    private final EmotionCharacterRepository emotionCharacterRepository;
    private final CharacterCollectionRepository characterCollectionRepository;
    private final DailyEmotionSummaryRepository dailyEmotionSummaryRepository;
    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String AiUrl;
    private static final int MAX_DAILY_MESSAGES = 50; //일일 메시지 제한 개수

    // 이모지 모음
    String emojiRegex = "["
            + "\uD83C\uDC04-\uD83C\uDFFF"  // U+1F004 - U+1F773 (Mahjong, DOMINO, etc)
            + "\uD83D\uDC00-\uD83D\uDFFF"  // U+1F800 - U+1F9FF (Supplemental Symbols and Pictographs)
            + "\uD83E\uDD00-\uD83E\uDFFF"  // U+1FA70 - U+1FAFF (Symbols and Pictographs Extended-A)
            + "\u2600-\u26FF"              // U+2600 - U+26FF (Miscellaneous Symbols)
            + "\u2700-\u27BF"              // U+2700 - U+27BF (Dingbats)
            + "]+";


    @Transactional
    public ChatResponseDto processMessage(User user, String userText) {

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX); // 23시 59분 59.99...초

        int todayChatCount = emotionLogRepository.countByUserAndCreatedDatetimeBetween(user, startOfDay, endOfDay);

        // 횟수 제한 체크
        if (todayChatCount >= MAX_DAILY_MESSAGES) {
            return createErrorResponse(
                    "오늘은 더이상 대화를 나눌 수 없어.. 내일 다시 이용해줘!",
                    "/images/characters/nothing.png"
            );
        }

        //입력값 유효성 검사
        Optional<ChatResponseDto> validationError = validateUserText(userText);
        if (validationError.isPresent()) {
            return validationError.get();
        }

        // userText 에서 이모지 제거
        String emoji = emojiRegex;
        String textOnly = userText.replaceAll(emoji, "");

        //Python AI 서버 호출
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("text", textOnly);

        //Map<String, String> aiResponse = restTemplate.postForObject("http://localhost:5000/api/ai/chat", requestBody, Map.class);
        Map<String, String> aiResponse = restTemplate.postForObject(AiUrl + "/api/ai/chat", requestBody, Map.class);

        String emotion = aiResponse.get("emotion");
        String chatbotAnswer = aiResponse.get("answer");

        //emotion "오류" 예외 처리
        if ("오류".equals(emotion)) {
            // DB 작업을 모두 건너뛰고, 즉시 DTO를 만들어 반환
            return createErrorResponse(chatbotAnswer, "/images/characters/nothing.png");
        }


        //채팅 로그 저장
        EmotionLog newLog = new EmotionLog();

        newLog.setUser(user);
        newLog.setText(userText);
        newLog.setAnswer(chatbotAnswer);
        newLog.setEmotion(emotion);

        emotionLogRepository.save(newLog);

        //감정 통계 update (emotion_status db)
        int totalEmotionCount = updateUserEmotionStatus(user, emotion);

        //일일 감정 요약 update (daily_emotion_summary db)
        updateDailySummary(user, emotion);

        //캐릭터 생성 및 강화 로직 실행
        processCharacterLogic(user, emotion, totalEmotionCount);

        //최고 레벨 캐릭터 이미지 조회
        Integer maxLevel = characterCollectionRepository.findMaxLevelByUserAndEmotion(user, emotion)
                .orElse(0); // 캐릭터 없으면 0 반환

        String characterImageUrl = null;
        if (maxLevel > 0) {
            characterImageUrl = emotionCharacterRepository.findByEmotionAndLevel(emotion, maxLevel)
                    .map(EmotionCharacter::getImage)
                    .orElse(null);
        }


        //프론트엔드로 보낼 응답 생성
        return ChatResponseDto.builder()
                .answer(chatbotAnswer)
                .emotion(emotion)
                .time(newLog.getCreatedDatetime())
                .characterImageUrl(characterImageUrl)
                .build();
    }


    // 사용자의 오늘 채팅 기록 조회 : 채팅 로그 DTO 리스트 반환
    @Transactional(readOnly = true) // 데이터를 읽기만 하는 메서드에는 readOnly=true 옵션을 권장
    public List<ChatLogDto> getTodayChatLogs(User user) {

        // 사용자의 커스텀 리셋 시간과 현재 시간을 가져옴
        LocalTime dailyResetTimeFromDb = user.getDailyResetTime();

        LocalTime dailyResetTime = dailyResetTimeFromDb.minusHours(9);

        LocalDateTime now = LocalDateTime.now();

        // 리셋 시간을 기준으로 '사용자별 하루 범위 '의 시작과 끝 시간을 계산
        LocalDateTime startOfDay;
        // 현재 시간이 리셋 시간보다 이전이라면, '화면에 보여질 오늘'은 어제 시작된 것 (day 고려 없이 HH:MM:SS 만 비교할 떄)
        // ex) 현재 7월 27일 01시 이고 리셋 시간이 02 시라면, 로그에 보여질 "오늘" 범위의 시작 시간은 "어제" 인 7월 26일
        if (now.toLocalTime().isBefore(dailyResetTime)) {
            startOfDay = LocalDate.now().minusDays(1).atTime(dailyResetTime);
        }
        // 현재 시간이 리셋 시간과 같거나 이후라면, '화면에 보여질 오늘'은 오늘 시작된 것
        // ex) 현재 7월 27일 03시 이고, 리셋 시간이 02 시라면, 리셋이 완료 되었으므로, "오늘" 범위의 시작 시간은 "오늘" 인 7월 27일
        else {
            startOfDay = LocalDate.now().atTime(dailyResetTime);
        }
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        //  Repository를 통해 '화면에 보여질 오늘' 동안의 EmotionLog 리스트를 조회
        // 오늘이 7월 27일 이고, daily_reset_time 이 00:00:00 이면
        // 2025-07-27 00:00:00 부터 2025-07-27 23:59:59.999999999 가 조회되는 범위 (끝 시간에서 1나노초 감소시켰기 때문)
        List<EmotionLog> todayLogs = emotionLogRepository.findAllByUserAndCreatedDatetimeBetweenOrderByCreatedDatetimeAsc(user, startOfDay, endOfDay.minusNanos(1));

        //  EmotionLog 리스트를 ChatLogDto 리스트로 변환
        List<ChatLogDto> chatLogs = new ArrayList<>();
        for (EmotionLog log : todayLogs) {
            // 사용자의 메시지
            chatLogs.add(new ChatLogDto("USER", log.getText(), log.getCreatedDatetime(), null));

            String characterImageUrl = null;
            if (log.getEmotion() != null) {
                Optional<CharacterCollection> character = characterCollectionRepository.
                        findTopByUserAndEmotionCharacter_EmotionOrderByEmotionCharacter_LevelDesc(user,log.getEmotion());

                if (character.isPresent()) {
                    characterImageUrl = character.get().getEmotionCharacter().getImage();
                }
            }
            // 챗봇의 답변
            chatLogs.add(new ChatLogDto("CHATBOT", log.getAnswer(), log.getCreatedDatetime().plusNanos(1), characterImageUrl)); // 챗봇 답변을 약간 뒤 시간으로 처리하여 순서 보장
        }

        return chatLogs;
    }


    // 감정 통계 update , 해당 감정의 누적 횟수 리턴
    private int updateUserEmotionStatus(User user, String emotion) {
        // 기존에 해당 감정 기록이 있는지 조회
        Optional<EmotionStatus> optionalStatus = emotionStatusRepository.findByUserAndEmotion(user, emotion);

        EmotionStatus status;
        if (optionalStatus.isPresent()) {
            // 기록이 있으면 count 를 1 증가
            status = optionalStatus.get();
            status.setCount(status.getCount() + 1);
        } else {
            status = new EmotionStatus(); // 기본 생성자로 객체 생성
            status.setUser(user);         // 필요한 값들을 setter 로 설정
            status.setEmotion(emotion);
        }
        emotionStatusRepository.save(status);
        return status.getCount();
    }

    /**
     * 일별 대표 감정 통계 (db : daily_emotion_summary) 를 찾아 업데이트하거나 새로 생성
     * emotion_log 와 emotion_status 가 업데이트 된 이후 호출
     */

    private void updateDailySummary(User user, String newEmotion) {
        // 데이터 저장은 00시 기준 하루로 잡음
        // daily_reset_time 은 단순히 채팅 기록 띄우기 위해서만 사용
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        // 오늘 날짜의 요약 정보가 daily_emotion_summary db 에 있는지 조회
        Optional<DailyEmotionSummary> optionalSummary = dailyEmotionSummaryRepository.findByUserAndSummaryDate(user, today);

        if (optionalSummary.isPresent()) {
            //  케이스 1: 오늘 자 요약이 이미 있는 경우
            DailyEmotionSummary summary = optionalSummary.get();
            int currentDominantCount = summary.getEmotionCount(); //오늘의 저장된 대표감정

            // 오늘 하루 동안 새로 들어온 감정(newEmotion)의 횟수를 DB 에서 직접 카운트
            int newEmotionTodayCount = emotionLogRepository.countByUserAndEmotionAndCreatedDatetimeBetween(user, newEmotion, startOfDay, endOfDay);

            // 규칙: 새로운 감정의 오늘 횟수가 기존 대표 감정의 횟수보다 크거나 같으면 대표 감정을 교체
            // 횟수가 같을 경우, 방금 들어온 최신 감정에 우선순위 부여
            if (newEmotionTodayCount >= currentDominantCount) {
                summary.setDominantEmotion(newEmotion);
                summary.setEmotionCount(newEmotionTodayCount);
            }
            // 그 외의 경우 db 업데이트 안함

            dailyEmotionSummaryRepository.save(summary);

        } else {
            //  케이스 2: 오늘 자 요약이 없는 경우 (오늘의 첫 감정 기록)
            DailyEmotionSummary newSummary = new DailyEmotionSummary();
            newSummary.setUser(user);
            newSummary.setSummaryDate(today);
            newSummary.setDominantEmotion(newEmotion);
            newSummary.setEmotionCount(1); // 오늘의 첫 감정이므로 횟수는 1

            dailyEmotionSummaryRepository.save(newSummary);
        }

    }

    /**
     * 감정 횟수에 따라 캐릭터를 생성하거나 레벨업합니다.
     * 모든 db 업데이트 완료 후 실행
     */

    private void processCharacterLogic(User user, String emotion, int totalEmotionCount) {

        // 이 감정에 해당하는 캐릭터를 이미 가지고 있는지 가장 높은 레벨로 확인
        Optional<CharacterCollection> optionalCollection = characterCollectionRepository
                .findTopByUserAndEmotionCharacter_EmotionOrderByEmotionCharacter_LevelDesc(user, emotion);

        if (optionalCollection.isEmpty()) {
            // 케이스 1 : 해당 감정의 캐릭터를 처음 획득하는 경우
            // EmotionStatus의 값이 1일 때만 생성
            int creationThreshold = calculateLevelUpThreshold(0); // 레벨 0 -> 1 기준치 (1)

            if (totalEmotionCount >= creationThreshold) {
                // 기준을 만족하면 레벨 1 캐릭터를 찾아서 컬렉션에 새로 추가
                emotionCharacterRepository.findByEmotionAndLevel(emotion, 1)
                        .ifPresent(levelOneCharacter -> {
                            CharacterCollection newCollection = new CharacterCollection();
                            newCollection.setUser(user);
                            newCollection.setEmotionCharacter(levelOneCharacter);
                            // emotion_status의 count를 그대로 가져와서 설정
                            newCollection.setCount(totalEmotionCount);
                            characterCollectionRepository.save(newCollection);
                        });
            }
        } else {
            // 케이스 2 : 캐릭터가 있는 경우 -> 레벨업 확인
            CharacterCollection latestCollection = optionalCollection.get();
            EmotionCharacter currentCharacter = latestCollection.getEmotionCharacter();
            int currentLevel = currentCharacter.getLevel();

            // 가장 높은 레벨 캐릭터의 count를 현재 emotion_status의 count와 동기화
            latestCollection.setCount(totalEmotionCount);

            if (currentLevel >= 5) {
                return; // 최고 레벨이므로 레벨업 로직 불필요
            }

            int levelUpThreshold = calculateLevelUpThreshold(currentLevel);

            if (totalEmotionCount >= levelUpThreshold) {
                // 기준을 만족하면 다음 레벨 캐릭터를 찾아서 컬렉션에 새로 추가
                emotionCharacterRepository.findByEmotionAndLevel(emotion, currentLevel + 1)
                        .ifPresent(nextLevelCharacter -> {
                            CharacterCollection newLevelCollection = new CharacterCollection();
                            newLevelCollection.setUser(user);
                            newLevelCollection.setEmotionCharacter(nextLevelCharacter);
                            // emotion_status의 count를 그대로 가져와서 설정
                            newLevelCollection.setCount(totalEmotionCount);
                            characterCollectionRepository.save(newLevelCollection);
                        });
            }
        }
    }


    // 레벨업 정책
    private int calculateLevelUpThreshold(int currentLevel) {
        return switch (currentLevel) {
            case 0 -> 1;
            case 1 -> 3;
            case 2 -> 6;
            case 3 -> 10;
            case 4 -> 15;
            default -> Integer.MAX_VALUE;
        };

    }

    // userText 유효성 검사
    private Optional<ChatResponseDto> validateUserText(String userText) {

        // null, 빈 문자열, 길이 검사
        if (userText == null || userText.trim().isEmpty()) {
            return Optional.of(createErrorResponse("메시지를 입력해줘..", "/images/characters/nothing.png"));
        }
        if (userText.length() > 200) {
            return Optional.of(createErrorResponse("200자가 넘는 문자열을 요청할 수 없어..", "/images/characters/nothing.png"));
        }

        // 이모지 사용 비율 조사
        Pattern emojiPattern = Pattern.compile(emojiRegex);
        Matcher emojiMatcher = emojiPattern.matcher(userText);
        int emojiCount = 0;
        while (emojiMatcher.find()) {
            emojiCount++;
        }

        // 이모지 비율 10% 이하만 허용
        if (emojiCount > 0 && (double) emojiCount / userText.length() > 0.1) {
            return Optional.of(createErrorResponse("이모지가 너무 많아! 감정 분석을 위해 이모지를 조금만 더 줄여줄래?", "/images/characters/nothing.png"));
        }

        // 이모지 제거 하고 언어 관련 유효성 처리
        userText = userText.replaceAll(emojiRegex, "");

        // 한글, 영문, 숫자, 공백, 기본 특수 문자만 허용
        Pattern allowedChar = Pattern.compile("[^a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\s.,?!'\"~@#$%^&*()=_+-]");
        if (allowedChar.matcher(userText).find()) {
            return Optional.of(createErrorResponse("한글, 알파벳, 숫자, 기본 특수문자만 사용할 수 있어..", "/images/characters/nothing.png"));
        }

        // 알파벳 개수와 비율 체크
        Matcher alphabet = Pattern.compile("[a-zA-Z]").matcher(userText);
        int alphabetCount = 0;
        while (alphabet.find()) {
            alphabetCount++;
        }

        // 알파벳 10개 넘거나 전체 비율 5% 초과시 감정 분석 거절
        if (alphabetCount > 10 || (double) alphabetCount / userText.length() > 0.1) {
            return Optional.of(createErrorResponse("영어가 너무 많아! 감정 분석을 위해 알파벳을 조금만 더 줄여줄래?", "/images/characters/nothing.png"));
        }

        //유효성 검사 통과
        return Optional.empty();
    }

    private ChatResponseDto createErrorResponse(String answer, String imageUrl) {
        return ChatResponseDto.builder()
                .answer(answer)
                .emotion("오류")
                .time(LocalDateTime.now())
                .characterImageUrl(imageUrl)
                .build();
    }
}

