import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { API_URL } from "../config";
import PageWrapper from "../components/PageWrapper";
import Logo from "../components/Logo";
import "./AccountSettings.css";

function AccountSettings() {
  const navigate = useNavigate();
  const [nickname, setNickname] = useState("");
  const [modalState, setModalState] = useState({
    isOpen: false,
    type: "", // "success", "error", "info"
    title: "",
    message: "",
    onConfirm: null,
  });
  const [termsInfo, setTermsInfo] = useState({
    termsAgreedAt: null,
    privacyAgreedAt: null,
    termsVersion: null,
    privacyVersion: null,
  });

  useEffect(() => {
    fetchUserInfo();
  }, []);

  const fetchUserInfo = async () => {
    try {
      const response = await axios.get(`${API_URL}/api/my-page`);
      setNickname(response.data.nickname);

      // 약관 동의 정보가 있으면 설정
      if (response.data.termsAgreedAt) {
        setTermsInfo({
          termsAgreedAt: response.data.termsAgreedAt,
          privacyAgreedAt: response.data.privacyAgreedAt,
          termsVersion: response.data.termsVersion || "1.0",
          privacyVersion: response.data.privacyVersion || "1.0",
        });
      }
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
    });
  };

  const handleNicknameUpdate = async () => {
    if (!nickname.trim()) {
      showModal("error", "입력 오류", "닉네임을 입력해주세요.");
      return;
    }

    try {
      await axios.put(`${API_URL}/api/my-page/settings/nickname`, {
        nickname,
      });
      showModal(
        "success",
        "변경 완료!",
        `닉네임이 '${nickname}'으로 변경되었습니다.`
      );
    } catch (error) {
      console.error("닉네임 변경 실패:", error);
      showModal(
        "error",
        "변경 실패",
        "닉네임은 최대 20자까지 설정 가능합니다."
      );
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return "-";
    const date = new Date(new Date(dateString).getTime() + 9 * 60 * 60 * 1000);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(
      2,
      "0"
    )}.${String(date.getDate()).padStart(2, "0")}`;
  };

  const handleViewTerms = () => {
    navigate("/terms");
  };

  const handleViewPrivacy = () => {
    navigate("/privacy");
  };

  const handleWithdraw = () => {
    showModal(
      "confirm",
      "회원 탈퇴",
      "정말로 회원 탈퇴를 하시겠습니까?\n모든 데이터가 삭제되며 복구할 수 없습니다.",
      async () => {
        try {
          await axios.delete(`${API_URL}/api/my-page/settings/withdraw`);
          localStorage.removeItem("token");
          navigate("/");
        } catch (error) {
          console.error("회원 탈퇴 실패:", error);
          showModal("error", "탈퇴 실패", "회원 탈퇴에 실패했습니다.");
        }
      }
    );
  };

  return (
    <PageWrapper>
      <div className="account-settings-container">
        <Logo />
        <div className="account-settings-header">
          <button className="back-btn" onClick={() => navigate(-1)}>
            ← 뒤로가기
          </button>
          <h2 className="settings-title">계정 설정</h2>
        </div>
        <div className="account-settings-area">
          <div className="settings-content">
            {/* 닉네임 수정 */}
            <div className="setting-item">
              <h3>닉네임</h3>
              <div className="nickname-edit">
                <input
                  type="text"
                  value={nickname}
                  onChange={(e) => setNickname(e.target.value)}
                  placeholder="닉네임을 입력하세요"
                />
                <button className="btn-primary" onClick={handleNicknameUpdate}>
                  저장
                </button>
              </div>
            </div>

            {/* 약관 및 정책 */}
            <div className="setting-item">
              <h3>약관 및 정책</h3>
              <div className="terms-info-section">
                <div className="terms-info-row">
                  <button className="terms-link-btn" onClick={handleViewTerms}>
                    서비스 이용약관 보기 →
                  </button>
                </div>
                <div className="terms-info-row">
                  <button
                    className="terms-link-btn"
                    onClick={handleViewPrivacy}
                  >
                    개인정보 처리방침 보기 →
                  </button>
                </div>
                <div className="terms-agreed-date">
                  <span className="date-label">동의일:</span>
                  <span className="date-value">
                    {formatDate(termsInfo.termsAgreedAt)}
                  </span>
                </div>
              </div>
            </div>

            {/* 회원 탈퇴 */}
            <div className="setting-item danger-zone">
              <h3>회원 탈퇴</h3>
              <div className="warning-texts">
                탈퇴 시 모든 데이터가 영구적으로 삭제됩니다.
              </div>
              <button className="btn-danger" onClick={handleWithdraw}>
                회원 탈퇴
              </button>
            </div>
          </div>
        </div>

        {/* 통합 모달 */}
        {modalState.isOpen && (
          <div
            className="account-modal-overlay"
            onClick={modalState.type === "confirm" ? null : closeModal}
          >
            <div
              className={`account-modal-content account-modal-${modalState.type}`}
              onClick={(e) => e.stopPropagation()}
            >
              <div
                className={`account-modal-icon account-modal-icon-${modalState.type}`}
              >
                {modalState.type === "success" && "✓"}
                {modalState.type === "error" && "!"}
                {modalState.type === "info" && "i"}
                {modalState.type === "confirm" && "?"}
              </div>
              <h3 className="account-modal-title">{modalState.title}</h3>
              <p className="account-modal-message">{modalState.message}</p>

              {modalState.type === "confirm" ? (
                <div className="account-modal-buttons">
                  <button
                    className="account-modal-btn account-modal-btn-cancel"
                    onClick={closeModal}
                  >
                    취소
                  </button>
                  <button
                    className="account-modal-btn account-modal-btn-confirm"
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
                <button className="account-modal-btn" onClick={closeModal}>
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

export default AccountSettings;
