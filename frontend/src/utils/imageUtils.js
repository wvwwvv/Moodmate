// src/utils/imageUtils.js
// 감정별 이미지 경로를 생성하는 유틸리티 함수

// 감정 순서
const emotionOrder = [
  "기쁨",
  "행복",
  "즐거움/신남",
  "고마움",
  "슬픔",
  "힘듦/지침",
  "불쌍함/연민",
  "두려움",
  "의심/불신",
  "분노",
  "화남/분노",
  "짜증",
  "불평/불만",
  "놀람",
  "중립/복합",
  "혐오",
  "안심/신뢰",
  "당황/난처",
];

// 한글 감정명 → 영어 파일명 매핑
const emotionToEnglish = {
  기쁨: "joy",
  행복: "happiness",
  "즐거움/신남": "fun",
  고마움: "gratitude",
  슬픔: "sadness",
  "힘듦/지침": "exhaustion",
  "불쌍함/연민": "pity",
  두려움: "fear",
  "의심/불신": "doubt",
  분노: "anger",
  "화남/분노": "rage",
  짜증: "annoyance",
  "불평/불만": "complaint",
  놀람: "surprise",
  "중립/복합": "neutral",
  혐오: "disgust",
  "안심/신뢰": "trust",
  "당황/난처": "embarrassment",
};

/**
 * 감정과 레벨에 따른 이미지 인덱스를 계산
 * @param {string} emotion - 감정명
 * @param {number} level - 레벨 (1-5)
 * @returns {number} - 이미지 인덱스 (1부터 시작)
 */
export const getImageIndex = (emotion, level = 1) => {
  const emotionIndex = emotionOrder.indexOf(emotion);
  if (emotionIndex === -1) {
    console.warn(`Unknown emotion: ${emotion}`);
    return 1; // 기본값
  }

  // 각 감정마다 5개 이미지, 레벨은 1-5
  const clampedLevel = Math.max(1, Math.min(5, level));
  return emotionIndex * 5 + clampedLevel;
};

/**
 * 감정과 레벨에 따른 이미지 경로를 반환
 * @param {string} emotion - 감정명
 * @param {number} level - 레벨 (1-5)
 * @returns {string} - 이미지 경로
 */
export const getCharacterImagePath = (emotion, level = 1) => {
  const englishEmotion = emotionToEnglish[emotion];

  if (!englishEmotion) {
    console.warn(`Unknown emotion: ${emotion}`);
    return "/images/characters/joy_lv1.png"; // 기본값
  }

  const clampedLevel = Math.max(1, Math.min(5, level));

  // 새로운 파일명 형식: emotion_lvN.png
  return `/images/characters/${englishEmotion}_lv${clampedLevel}.png`;
};

/**
 * 캐릭터 이름에서 감정을 추출하고 이미지 경로 반환
 * @param {string} characterName - "세부감정 기본감정" 형태의 캐릭터명
 * @param {number} level - 레벨 (1-5)
 * @returns {string} - 이미지 경로
 */
export const getCharacterImageFromName = (characterName, level = 1) => {
  if (!characterName) return "/images/characters/joy_lv1.png"; // 기본 이미지

  // "세부감정 기본감정" 형태에서 기본감정 추출
  const parts = characterName.split(" ");
  const baseEmotion = parts[parts.length - 1]; // 마지막 부분이 기본감정

  return getCharacterImagePath(baseEmotion, level);
};

/**
 * 기본 감정에 대한 대표 이미지 경로 반환 (레벨 3 사용)
 * @param {string} emotion - 감정명
 * @returns {string} - 이미지 경로
 */
export const getDefaultEmotionImage = (emotion) => {
  return getCharacterImagePath(emotion, 3); // 중간 레벨인 3을 대표 이미지로 사용
};
