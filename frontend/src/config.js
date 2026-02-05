// API URL 설정
// 환경 변수가 있으면 사용하고, 없으면 로컬 개발 서버 사용
export const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8080";

// 필요한 경우 다른 설정도 추가 가능
export const config = {
  apiUrl: API_URL,
};

export default config;
