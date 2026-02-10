import React, { useState, useEffect, useRef } from "react";
import axios from "axios";
import PageWrapper from "../components/PageWrapper";
import "./Chat.css";
import Logo from "../components/Logo";
import { API_URL } from "../config";

const formatTime = (timeString) => {
  if (!timeString) return "";
  const date = new Date(timeString);
  return date.toLocaleString("ko-KR", {
    hour: "numeric",
    minute: "numeric",
    hour12: true,
  });
};

// 이미지 URL 절대경로 변환
const toAbsoluteUrl = (url) => {
  if (!url) return "";
  try {
    // window.location.origin : 포트를 포함한 origin 주소
    // 로컬 환경에서 http://localhost:3000/images/characters 반환함
    // public은 url에서 빠짐
    return url.startsWith("http") ? url : `${window.location.origin}${url}`;
  } catch {
    return url;
  }
};

function Chat() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [isInitialLoad, setIsInitialLoad] = useState(true);
  const [isDesktop, setIsDesktop] = useState(window.innerWidth >= 769);
  const [isSending, setIsSending] = useState(false);
  const chatAreaRef = useRef(null);
  const textareaRef = useRef(null);

  useEffect(() => {
    const checkIsDesktop = () => setIsDesktop(window.innerWidth >= 769);
    window.addEventListener("resize", checkIsDesktop);
    return () => window.removeEventListener("resize", checkIsDesktop);
  }, []);

  const scrollToBottom = (smooth = true) => {
    const area = chatAreaRef.current;
    if (area) {
      area.scrollTo({
        top: area.scrollHeight,
        behavior: smooth ? "smooth" : "instant",
      });
    }
  };

  const adjustTextareaHeight = () => {
    const textarea = textareaRef.current;
    if (textarea) {
      textarea.style.height = "auto";
      textarea.style.height = textarea.scrollHeight > 50 ? "76px" : "50px";
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter" && !e.shiftKey && isDesktop) {
      e.preventDefault();
      handleSend();
    }
  };

  // ★ 기존 채팅 기록 불러오기
  useEffect(() => {
    const fetchTodayChats = async () => {
      try {
        const response = await axios.get(`${API_URL}/api/chat`);
        const chatLogs = response.data;

        if (chatLogs.length === 0) {
          setMessages([
            {
              type: "bot",
              text: "당신의 기분은 어떤가요?",
              imageUrl: toAbsoluteUrl("/images/characters/nothing.png"),
            },
          ]);
        } else {
          const formattedMessages = chatLogs.map((log) => ({
            type: log.sender.toLowerCase() === "chatbot" ? "bot" : "user",
            text: log.message,
            timestamp: log.timestamp
              ? new Date(new Date(log.timestamp).getTime()/* + 9 * 60 * 60 * 1000*/) // todo 9시간 time zone 문제
              : null,
            imageUrl: toAbsoluteUrl(
              log.characterImageUrl || "/images/characters/nothing.png"
            ),
          }));
          setMessages(formattedMessages);
        }
      } catch (error) {
        console.error("채팅 기록 로딩 실패:", error);
        setMessages([
          {
            type: "bot",
            text: "채팅 기록을 불러오는 중 오류가 발생했어요.",
            imageUrl: toAbsoluteUrl("/images/characters/nothing.png"),
          },
        ]);
      }
    };
    fetchTodayChats();
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => {
      scrollToBottom(!isInitialLoad);
      if (isInitialLoad) setIsInitialLoad(false);
    }, 10);
    return () => clearTimeout(timer);
  }, [messages]);

  useEffect(() => {
    adjustTextareaHeight();
  }, [input]);

  // ★ 메시지 전송
  const handleSend = async () => {
    const userText = input.trim();
    if (!userText || isSending) return;

    setIsSending(true);
    const tempUserMessage = { type: "user", text: userText, timestamp: null };
    setMessages((prev) => [...prev, tempUserMessage]);
    setInput("");

    try {
      const response = await axios.post(`${API_URL}/api/chat`, {
        text: userText,
      });
      const botResponse = response.data;


      // todo db에 저장된 time 올바른 값이지만, db에서 꺼내서 백으로부터 json으로 받은 time은 9시간 빠른 값이 들어옴
      // todo 주석 처리한 코드는 9시간 더해주는 하드 코딩
      const adjustedTime = botResponse.time
        ? new Date(new Date(botResponse.time).getTime())
        : null;

      /*const adjustedTime = botResponse.time
          ? new Date(new Date(botResponse.time).getTime()  + 9 * 60 * 60 * 1000)
          : null;*/

      const finalUserMessage = {
        type: "user",
        text: userText,
        timestamp: adjustedTime,
      };

      const botMessage = {
        type: "bot",
        text: botResponse.answer,
        imageUrl: toAbsoluteUrl(botResponse.characterImageUrl),
        timestamp: adjustedTime,
      };

      setMessages((prev) => [
        ...prev.slice(0, -1),
        finalUserMessage,
        botMessage,
      ]);
    } catch (error) {
      console.error("메시지 전송 실패:", error);
      setMessages((prev) => [
        ...prev.slice(0, -1),
        {
          type: "bot",
          text: "메시지를 보내는 중 오류가 발생했습니다.",
          imageUrl: toAbsoluteUrl("/images/characters/nothing.png"),
        },
      ]);
    } finally {
      setIsSending(false);
      requestAnimationFrame(() => {
        textareaRef.current?.focus();
      });
    }
  };

  return (
    <PageWrapper>
      <Logo />
      <div className="chat-container">
        <div className="chat-area" ref={chatAreaRef}>
          <div className="chat-box">
            {messages.map((msg, idx) => (
              <div
                key={idx}
                className={`chat-message-wrapper ${msg.type}-wrapper`}
              >
                {msg.type === "bot" ? (
                  <div className="chat-message-container">
                    <div className="bot-character">
                      <img src={msg.imageUrl} alt="캐릭터" />
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
            ))}
          </div>
        </div>

        <div className="chat-input-bar">
          <textarea
            ref={textareaRef}
            placeholder={
              isDesktop
                ? "채팅을 입력하세요 (Shift+Enter: 줄바꿈, Enter: 전송)"
                : "채팅을 입력하세요"
            }
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={handleKeyDown}
            rows={1}
            disabled={isSending}
          />
          <button onClick={handleSend} disabled={isSending}>
            {isSending ? "..." : "➤"}
          </button>
        </div>
      </div>
    </PageWrapper>
  );
}

export default Chat;
