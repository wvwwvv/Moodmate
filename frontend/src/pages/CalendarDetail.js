import React, { useEffect, useState, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import PageWrapper from "../components/PageWrapper";
import "./Chat.css";
import "./CalendarDetail.css";
import Logo from "../components/Logo";
import axios from "axios";
import { API_URL } from "../config";
import LoadingSpinner from "../components/LoadingSpinner";

const formatTime = (timeString) => {
  if (!timeString) return "";
  const date = new Date(timeString);
  return date.toLocaleString("ko-KR", {
    hour: "numeric",
    minute: "numeric",
    hour12: true,
  });
};

function CalendarDetail() {
  const { date } = useParams(); //url param 에서 'YYYY-MM-DD' 형식으로 가져옴
  const navigate = useNavigate();
  const [messages, setMessages] = useState([]);
  const [isLoading, setIsLoading] = useState(true); //로그 로딩 상태 state
  const chatAreaRef = useRef(null);

  // 날짜 포맷: 2025-9-27 → 2025년 9월 27일
  const formatKoreanDate = (dateStr) => {
    if (!dateStr) return "";
    const [year, month, day] = dateStr.split("-");
    return `${year}년 ${Number(month)}월 ${Number(day)}일`;
  };

  useEffect(() => {
    const fetchChatLogs = async () => {
      if (!date) return;

      setIsLoading(true); // api 호출 시작 시 로딩 state true

      const [year, month, day] = date.split("-");

      try {
        const response = await axios.get(
          `${API_URL}/api/calendar/logs/${year}/${month}/${day}`
        );
        const chatLogs = response.data;

        const formattedMessages = chatLogs.map((log) => ({
          type: log.sender.toLowerCase() === "chatbot" ? "bot" : "user",
          text: log.message,
          timestamp: log.timestamp
            ? new Date(new Date(log.timestamp).getTime() + 9 * 60 * 60 * 1000)
            : null,
          imageUrl: log.characterImageUrl || "/images/characters/nothing.png",
        }));

        setMessages(formattedMessages);
      } catch (error) {
        console.error("Error fetching daily chat logs :", error);
        setMessages([]);
      } finally {
        setIsLoading(false); // api 호출 완료
      }
    };

    fetchChatLogs();
  }, [date]);

  return (
    <PageWrapper>
      <div className="calendarDetail-container">
        <Logo />

        <div className="calendar-detail-header">
          <button className="back-btn" onClick={() => navigate(-1)}>
            ← 뒤로가기
          </button>
          <h2 className="detail-date">{formatKoreanDate(date)}</h2>
        </div>

        <div className="calendarDetail-area" ref={chatAreaRef}>
          <div className="calendarDetail-box">
            {isLoading ? (
              <div className="loading-message">
                <LoadingSpinner message="대화 기록을 불러오는 중..." />
              </div>
            ) : messages.length > 0 ? (
              messages.map((msg, idx) => (
                <div
                  key={idx}
                  className={`chat-message-wrapper ${msg.type}-wrapper`}
                >
                  {msg.type === "bot" ? (
                    <div className="chat-message-container">
                      <div className="bot-character">
                        <img
                          src={msg.imageUrl}
                          alt="캐릭터"
                          onError={(e) => {
                            e.target.src = "/images/characters/nothing.png";
                          }}
                        />
                      </div>
                      <div className="message-bubble">{msg.text}</div>
                    </div>
                  ) : (
                    <div className="chat-message-user">
                      <span>{msg.text}</span>
                      {msg.timestamp && (
                        <span className="message-timestamp">
                          {formatTime(msg.timestamp)}
                        </span>
                      )}
                    </div>
                  )}
                </div>
              ))
            ) : (
              <div className="no-chat-log">
                <p>이 날의 대화 기록이 없어요.</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </PageWrapper>
  );
}

export default CalendarDetail;
