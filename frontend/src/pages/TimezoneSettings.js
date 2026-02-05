import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import PageWrapper from "../components/PageWrapper";
import Logo from "../components/Logo";
import "./TimezoneSettings.css";
import { API_URL } from "../config";

function TimezoneSettings() {
  const navigate = useNavigate();
  const [resetTime, setResetTime] = useState("00:00");
  const [modalState, setModalState] = useState({
    isOpen: false,
    type: "",
    title: "",
    message: "",
  });

  useEffect(() => {
    fetchUserInfo();
  }, []);

  const fetchUserInfo = async () => {
    try {
      const response = await axios.get(`${API_URL}/api/my-page`);
      // LocalTime 형식을 HH:mm으로 변환
      const time = response.data.dailyResetTime;
      setResetTime(time.substring(0, 5)); // "00:00:00" -> "00:00"
    } catch (error) {
      console.error("사용자 정보 로드 실패:", error);
    }
  };

  const showModal = (type, title, message) => {
    setModalState({
      isOpen: true,
      type,
      title,
      message,
    });
  };

  const closeModal = () => {
    setModalState({
      isOpen: false,
      type: "",
      title: "",
      message: "",
    });
  };

  const handleTimeChange = async () => {
    try {
      await axios.put(`${API_URL}/api/my-page/settings/reset-time`, {
        dailyResetTime: resetTime + ":00",
      });
      showModal(
        "success",
        "변경 완료!",
        `하루 시작 시간이 ${resetTime}로 변경되었습니다.`
      );
      setTimeout(() => {
        navigate(-1);
      }, 1500);
    } catch (error) {
      console.error("시간대 변경 실패:", error);
      showModal("error", "변경 실패", "시간대 변경에 실패했습니다.");
    }
  };

  return (
    <PageWrapper>
      <div className="timezone-settings-container">
        <Logo />

        <div className="timezone-settings-header">
          <button className="back-btn" onClick={() => navigate(-1)}>
            ← 뒤로가기
          </button>
          <h2 className="settings-title">시간대 설정</h2>
        </div>

        <div className="timezone-settings-area">
          <div className="settings-content">
            <div className="time-selector">
              <label>하루 시작 시간</label>
              <input
                type="time"
                value={resetTime}
                onChange={(e) => setResetTime(e.target.value)}
              />
            </div>

            <div className="example-box">
              <p className="example-title">예시</p>
              <ul>
                <li>00:00 설정 시 → 자정부터 다음날 자정 전까지</li>
                <li>05:00 설정 시 → 새벽 5시부터 다음날 새벽 5시 전까지</li>
              </ul>
            </div>

            <button className="btn-save" onClick={handleTimeChange}>
              저장
            </button>
          </div>
        </div>
        {/* 통합 모달 */}
        {modalState.isOpen && (
          <div className="modal-overlay" onClick={closeModal}>
            <div
              className={`modal-content modal-${modalState.type}`}
              onClick={(e) => e.stopPropagation()}
            >
              <div className={`modal-icon modal-icon-${modalState.type}`}>
                {modalState.type === "success" && "✓"}
                {modalState.type === "error" && "!"}
              </div>
              <h3 className="modal-title">{modalState.title}</h3>
              <p className="modal-message">{modalState.message}</p>
              <button className="modal-btn" onClick={closeModal}>
                확인
              </button>
            </div>
          </div>
        )}
      </div>
    </PageWrapper>
  );
}

export default TimezoneSettings;
