package com.example.backend.Service;

//추후 삭제

/*@Service
@RequiredArgsConstructor
public class EmotionLogService {
    private final EmotionLogRepository emotionLogRepository;

    public List<EmotionLogDto> getTodayLogs(User user) {
        //아래 두 변수 사이가 "오늘"을 의미함
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<EmotionLog> logs = emotionLogRepository.findByUserAndCreatedDatetimeBetweenOrderByCreatedDatetimeAsc(user, startOfDay, endOfDay);

        return logs.stream()
                .map(EmotionLogDto::fromEntity)
                .collect(Collectors.toList());
    }
}*/
