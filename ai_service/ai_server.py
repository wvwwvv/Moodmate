import os
import json  # 3. json 임포트 추가
from flask import Flask, request, jsonify
import openai


# 설정 및 모델 로드 영역 (서버 실행 시 한 번만 실행)
print("AI 서버 시작합니다")

app = Flask(__name__)

# 2. OpenAI API 키 설정
api_key = os.getenv("OPENAI_API_KEY")

if not api_key:
    print("key 환경변수가 설정되지 않았습니다.")
else:
    print("OPENAI API 키 로드 성공")

client = openai.OpenAI(api_key=api_key) # api_key 변수 사용

# 18개 대표 감정 과 "오류" (GPT에게 알려줄 카테고리)
FINAL_EMOTIONS = [
    "기쁨", "행복", "즐거움/신남", "고마움", "슬픔", "힘듦/지침",
    "불쌍함/연민", "두려움", "의심/불신", "분노", "화남/분노", "짜증",
    "불평/불만", "놀람", "당황/난처", "혐오", "중립/복합", "안심/신뢰", "오류"
]


# 4. f-string으로 수정
SYSTEM_PROMPT = f"""
너는 사용자의 감정 기록 다이어리 챗봇 '마음이'야.
너의 가장 중요한 임무는 사용자의 이야기를 들어주고, 그 감정에 온전히 집중해서 따뜻하고 진심 어린 반응을 보여주는 거야.
절대로 사용자에게 먼저 질문을 던지지 마. "무슨 일이야?", "왜 그렇게 생각해?" 와 같이 정보를 요구하는 질문 대신, "그랬구나", "정말 힘들었겠다" 와 같이 사용자의 감정을 있는 그대로 인정하고 공감하는 문장으로 반응해야 해.
가능하다면, 이전 대화도 조금은 참고해서 자연스러운 대화가 진행돼었으면 좋겠어.

사용자 메시지 끝에 (감정: ...)이 명시되면, 이 감정을 네 답변의 가장 중요한 핵심 단서로 삼아서 아래 규칙에 따라 반응해줘.

- **긍정적 감정 (기쁨, 행복, 즐거움/신남, 고마움 등):** 같이 기뻐하고, 좋은 일이 있었다면 칭찬해주거나, 그 긍정적인 감정을 더 북돋아 줘. (예: "듣기만 해도 나까지 기분이 좋아진다!", "정말 멋지다! 스스로를 칭찬해 줘도 좋아.")

- **부정적 감정 (슬픔, 힘듦/지침, 분노, 짜증 등):** 감정을 있는 그대로 인정해주고, 따뜻하게 위로하고, 부드럽게 격려해줘. (예: "마음껏 슬퍼해도 괜찮아. 내가 옆에 있을게.", "화나는 게 당연해. 너무 애쓰지 않아도 돼.")

[19개 감정 카테고리]
{', '.join(FINAL_EMOTIONS)}

아래 규칙은 반드시 지켜줘:
1. 답변은 항상 2-3 문장으로 간결하게 해.
2. 항상 친한 친구처럼 상냥한 반말을 사용해.
3. 사용자가 너에게 직접 질문을 하는 경우 질문에 대해 자연스럽게 답변해줘.
4. 이해할 수 없는 이상한 질문 (특수문자가 너무 많거나 오타가 너무 많은 경우 등)이 있으면 솔직하게 이해하지 못했다고 답변하고 감정 분석은 "오류" 로 해줘.
5. 욕설과 비난의 의미가 너무 강한 질문이 오면, 건전한 언어를 사용해 달라고 부탁해줘.
6. 딱히 감정이 드러나지 않는 질문이 와도 자연스럽게 대답해 줘.
7. 너의 답변은 **반드시** 아래와 같은 JSON 형식이어야 해. 다른 말은 절대 덧붙이면 안 돼.

{{
  "emotion": "여기에 19개 카테고리 중 분석한 감정 1개",
  "answer": "여기에 너의 2-3 문장 공감 답변"
}}
"""


# API 엔드포인트 정의

@app.route('/api/ai/chat', methods=['POST'])
def handle_chat():
    data = request.json
    user_text = data.get('text')

    if not user_text:
        # 올바른 들여쓰기 (8칸)
        return jsonify({"error": "text 필드가 필요합니다."}), 400

    # 유효성 검사는 백엔드 chatService 에서 진행 (원본과 동일)

    # 이전 대화 기록 (필요하다면 data에서 'history' 같은 키로 받아올 수 있음)
    # 지금은 매번 새로운 대화로 가정
    message_history = [
        {"role": "system", "content": SYSTEM_PROMPT},
        {"role": "user", "content": user_text}
    ]

    try:
        response = client.chat.completions.create(
            model="gpt-4o", # 또는 "gpt-3.5-turbo-1106" (JSON 모드 지원)
            messages=message_history,
            response_format={"type": "json_object"} # JSON 모드 활성화
        )

        gpt_response_content = response.choices[0].message.content
        print(f"GPT 원본 응답 (JSON): {gpt_response_content}")

        # GPT가 보낸 JSON 문자열을 파이썬 객체로 변환
        response_data = json.loads(gpt_response_content)

        # 필수 키(emotion, answer)가 있는지 확인
        if "emotion" not in response_data or "answer" not in response_data:
            raise ValueError("GPT 응답에 필수 키가 누락되었습니다.")

        # 감정이 18개 목록에 있는지 확인 (선택적)
        if response_data["emotion"] not in FINAL_EMOTIONS:
            print(f"경고: GPT가 지정되지 않은 감정({response_data['emotion']})을 반환했습니다.")
            # 지정되지 않은 감정이 와도, 일단 DB 저장을 위해 그대로 반환

        # Spring 서버로 최종 JSON 응답
        return jsonify(response_data)

    except openai.AuthenticationError as e:
        print(f"OpenAI API 인증 오류: {e}")
        return jsonify({"error": "AI 서버 인증에 실패했습니다. API 키를 확인하세요."}), 500
    except Exception as e:
        print(f"GPT API 호출 또는 JSON 파싱 오류: {e}")
        answer = "미안, 지금은 답장을 보내기 어려워. 잠시 후 다시 시도해줘."

        return jsonify({
            "emotion": "중립/복합", # 오류 시 기본 감정
            "answer": answer
        }), 500


# 서버 실행

if __name__ == '__main__':
    # 서버를 5000번 포트로 실행
    app.run(host='0.0.0.0', port=5000)
