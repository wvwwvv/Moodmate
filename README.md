# Moodmate

감정 일기 챗봇 서비스입니다.  
AI 챗봇 **마음이**와 대화하며 감정을 기록하고, 캘린더로 돌아볼 수 있습니다.

## 기술 스택

| 영역 | 기술 |
|------|------|
| Frontend | React 19, React Router, Axios |
| Backend | Spring Boot 3.4.3 (Java 17), Spring Security, OAuth2, JPA, MySQL, JWT |
| AI Service | Python (Flask, OpenAI API) |

## 주요 기능

- Google OAuth2 로그인
- AI 챗봇과 감정 대화
- 감정 캘린더 기록 및 조회
- 감정 컬렉션

## 실행 방법

**Backend**
```bash
cd backend
./gradlew bootRun
```

**Frontend**
```bash
cd frontend
npm install
npm start
```

**AI Service**
```bash
cd ai_service
pip install -r requirements.txt
python application.py
```
