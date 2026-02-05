import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import PageWrapper from "../components/PageWrapper";
import Logo from "../components/Logo";
import "./Settings.css";
import { API_URL } from "../config";

function Settings() {
  const navigate = useNavigate();
  const [userInfo, setUserInfo] = useState({
    nickname: "",
    level: 1,
    email: "",
    dailyResetTime: "00:00",
    termsVersion: null,
    privacyVersion: null,
  });

  const [modalState, setModalState] = useState({
    isOpen: false,
    type: "",
    title: "",
    message: "",
    onConfirm: null,
  });

  useEffect(() => {
    fetchUserInfo();
  }, []);

  const fetchUserInfo = async () => {
    try {
      const response = await axios.get(`${API_URL}/api/my-page`);
      setUserInfo(response.data);
    } catch (error) {
      console.error("사용자 정보 로드 실패:", error);
    }
  };

  const showModal = (type, title, message, onConfirm = null) => {
    setModalState({
      isOpen: true,
      type,
      title,
      message,
      onConfirm,
    });
  };

  const closeModal = () => {
    setModalState({
      isOpen: false,
      type: "",
      title: "",
      message: "",
      onConfirm: null,
    });
  };

  const handleLogout = () => {
    showModal("confirm", "로그아웃", "로그아웃 하시겠습니까?", () => {
      localStorage.removeItem("token");
      navigate("/");
    });
  };

  return (
    <PageWrapper>
      <div className="settings-container">
        <Logo />

        {/* 사용자 정보 */}
        <div className="profile-section">
          <div className="user-info">
            <div className="user-name-container">
              <div className="user-level">Lv.{userInfo.level}</div>
              <span className="user-name">{userInfo.nickname} 님</span>
            </div>
          </div>
        </div>

        {/* 메뉴 */}
        <div className="menu-section">
          <div
            className="menu-item"
            onClick={() => navigate("/settings/account")}
          >
            <span>계정 설정</span>
            <span className="arrow">›</span>
          </div>

          <div
            className="menu-item"
            onClick={() => navigate("/settings/timezone")}
          >
            <span>시간대 설정</span>
            <span className="arrow">›</span>
          </div>

          <div className="menu-item logout" onClick={handleLogout}>
            <span>로그아웃</span>
          </div>
        </div>

        {/* 버전 정보 */}
        <div className="version-info">
          <p>버전 정보</p>
          <p className="version-number">v{userInfo.termsVersion || "1.0.0"}</p>
        </div>

        {/* 통합 모달 */}
        {modalState.isOpen && (
          <div
            className="settings-modal-overlay"
            onClick={modalState.type === "confirm" ? null : closeModal}
          >
            <div
              className={`settings-modal-content settings-modal-${modalState.type}`}
              onClick={(e) => e.stopPropagation()}
            >
              <div
                className={`settings-modal-icon settings-modal-icon-${modalState.type}`}
              >
                {modalState.type === "success" && "✓"}
                {modalState.type === "error" && "!"}
                {modalState.type === "info" && "i"}
                {modalState.type === "confirm" && "?"}
              </div>
              <h3 className="settings-modal-title">{modalState.title}</h3>
              <p className="settings-modal-message">{modalState.message}</p>

              {modalState.type === "confirm" ? (
                <div className="settings-modal-buttons">
                  <button
                    className="settings-modal-btn settings-modal-btn-cancel"
                    onClick={closeModal}
                  >
                    취소
                  </button>
                  <button
                    className="settings-modal-btn settings-modal-btn-confirm"
                    onClick={() => {
                      closeModal();
                      if (modalState.onConfirm) {
                        modalState.onConfirm();
                      }
                    }}
                  >
                    확인
                  </button>
                </div>
              ) : (
                <button className="settings-modal-btn" onClick={closeModal}>
                  확인
                </button>
              )}
            </div>
          </div>
        )}
      </div>
    </PageWrapper>
  );
}

export default Settings;
