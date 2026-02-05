package com.example.backend.Service;

import com.example.backend.Dto.ChatLogDto;
import com.example.backend.Entity.CharacterCollection;
import com.example.backend.Entity.DailyEmotionSummary;
import com.example.backend.Entity.EmotionLog;
import com.example.backend.Entity.User;
import com.example.backend.Repository.CharacterCollectionRepository;
import com.example.backend.Repository.DailyEmotionSummaryRepository;
import com.example.backend.Repository.EmotionLogRepository;
import com.example.backend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final UserRepository userRepository;
    private final DailyEmotionSummaryRepository dailyEmotionSummaryRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final CharacterCollectionRepository characterCollectionRepository;


    //"년", "월" 에 대해 대표 감정 리스트로 반환
    @Transactional(readOnly = true)
    public Map<Integer, String> getMonthlyEmotions(User user, int year, int month) {

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<DailyEmotionSummary> summaries = dailyEmotionSummaryRepository.findAllByUserAndSummaryDateBetween(user, startDate, endDate);

        return summaries.stream()
                .collect(Collectors.toMap(
                        summary -> summary.getSummaryDate().getDayOfMonth(),
                        // Value: 최고 레벨 캐릭터 이미지 URL
                        summary -> {
                            // 해당 날짜의 대표 감정 이름(예: "기쁨")을 가져옴
                            String dominantEmotion = summary.getDominantEmotion();

                            // Repository를 사용해 해당 감정의 최고 레벨 캐릭터 조회
                            Optional<CharacterCollection> topCharacterOpt = characterCollectionRepository
                                    .findTopByUserAndEmotionCharacter_EmotionOrderByEmotionCharacter_LevelDesc(user, dominantEmotion);

                            // 캐릭터가 존재하면 이미지 URL을, 없으면 빈 문자열을 반환
                            return topCharacterOpt
                                    .map(cc -> cc.getEmotionCharacter().getImage())
                                    .orElse("");
                        }
                ));
    }


    // "년", "월", "일" 에 대해 해당 날의 대화 로그 반환
    @Transactional(readOnly = true)
    public List<ChatLogDto> getDailyLogs(User user, LocalDate date) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59, 999999999);

        // chat 에는 사용자가 설정한 로그 초기화 기준 시간으로 보여주지만 calendar 는 00시 기준 으로 하루 대화를 조회
        List<EmotionLog> dailyLogs = emotionLogRepository.findAllByUserAndCreatedDatetimeBetweenOrderByCreatedDatetimeAsc(user, startOfDay, endOfDay);

        List<ChatLogDto> chatLogs = new ArrayList<>();
        for (EmotionLog emotionLog : dailyLogs) {
            chatLogs.add(new ChatLogDto("USER", emotionLog.getText(), emotionLog.getCreatedDatetime(),null));

            String characterImageUrl = null;
            if (emotionLog.getEmotion() != null) {
                Optional<CharacterCollection> character = characterCollectionRepository.
                        findTopByUserAndEmotionCharacter_EmotionOrderByEmotionCharacter_LevelDesc(user, emotionLog.getEmotion());

                if (character.isPresent()) {
                    characterImageUrl = character.get().getEmotionCharacter().getImage();
                }
            }

            chatLogs.add(new ChatLogDto("CHATBOT", emotionLog.getAnswer(), emotionLog.getCreatedDatetime().plusNanos(1),characterImageUrl));
        }

        return chatLogs;
    }



}
