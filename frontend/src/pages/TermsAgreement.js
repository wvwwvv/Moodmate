import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./TermsAgreement.css";
import { API_URL } from "../config";
import TermsModal from "../components/TermsModal";

function TermsAgreement() {
  const navigate = useNavigate();
  const [allAgreed, setAllAgreed] = useState(false);
  const [termsAgreed, setTermsAgreed] = useState(false);
  const [privacyAgreed, setPrivacyAgreed] = useState(false);

  // 모달 상태
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalType, setModalType] = useState(""); // "terms" or "privacy"

  // 스크롤 활성화
  useEffect(() => {
    const originalBodyOverflow = document.body.style.overflow;
    const originalHtmlOverflow = document.documentElement.style.overflow;
    const originalRootOverflow =
      document.getElementById("root")?.style.overflow;

    document.body.style.overflow = "hidden";
    document.documentElement.style.overflow = "hidden";
    if (document.getElementById("root")) {
      document.getElementById("root").style.overflow = "hidden";
    }

    return () => {
      document.body.style.overflow = originalBodyOverflow;
      document.documentElement.style.overflow = originalHtmlOverflow;
      if (document.getElementById("root")) {
        document.getElementById("root").style.overflow = originalRootOverflow;
      }
    };
  }, []);

  // 전체 동의 체크박스 처리
  const handleAllAgree = (checked) => {
    setAllAgreed(checked);
    setTermsAgreed(checked);
    setPrivacyAgreed(checked);
  };

  // 개별 동의 시 전체 동의 상태 업데이트
  useEffect(() => {
    if (termsAgreed && privacyAgreed) {
      setAllAgreed(true);
    } else {
      setAllAgreed(false);
    }
  }, [termsAgreed, privacyAgreed]);

  const handleTermsView = () => {
    setModalType("terms");
    setIsModalOpen(true);
  };

  const handlePrivacyView = () => {
    setModalType("privacy");
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setModalType("");
  };

  const handleSubmit = async () => {
    if (!termsAgreed || !privacyAgreed) {
      alert("필수 약관에 모두 동의해주세요.");
      return;
    }

    try {
      // 백엔드에 동의 정보 전송
      const response = await fetch(`${API_URL}/api/user/agree-terms`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include", // 쿠키 포함
        body: JSON.stringify({
          termsAgreed: termsAgreed,
          privacyAgreed: privacyAgreed,
          termsVersion: "1.0",
          privacyVersion: "1.0",
        }),
      });

      if (response.ok) {
        // 성공 시 홈으로 이동
        navigate("/chat");
      } else {
        alert("약관 동의 처리 중 오류가 발생했습니다.");
      }
    } catch (error) {
      console.error("약관 동의 오류:", error);
      alert("약관 동의 처리 중 오류가 발생했습니다.");
    }
  };

  return (
    <>
      <div className="terms-agreement-container">
        <div className="terms-agreement-header">
          <img
            src="/images/logo.png"
            alt="MoodMate Logo"
            className="agreement-logo"
          />
          <h1 className="agreement-title">서비스 이용 동의</h1>
          <p className="agreement-subtitle">
            MoodMate 서비스 이용을 위해
            <br />
            약관 동의가 필요합니다.
          </p>
        </div>

        <div className="terms-agreement-content">
          {/* 전체 동의 */}
          <div className="agreement-all">
            <label className="agreement-label">
              <input
                type="checkbox"
                checked={allAgreed}
                onChange={(e) => handleAllAgree(e.target.checked)}
                className="agreement-checkbox"
              />
              <span className="checkbox-custom"></span>
              <span className="agreement-text-all">전체 동의</span>
            </label>
          </div>

          <div className="agreement-divider"></div>

          {/* 개별 약관 동의 */}
          <div className="agreement-list">
            {/* 서비스 이용약관 */}
            <div className="agreement-item">
              <label className="agreement-label">
                <input
                  type="checkbox"
                  checked={termsAgreed}
                  onChange={(e) => setTermsAgreed(e.target.checked)}
                  className="agreement-checkbox"
                />
                <span className="checkbox-custom"></span>
                <span className="agreement-text">
                  <span className="required">[필수]</span> 서비스 이용약관
                </span>
              </label>
              <button className="view-button" onClick={handleTermsView}>
                보기
              </button>
            </div>

            {/* 개인정보 처리방침 */}
            <div className="agreement-item">
              <label className="agreement-label">
                <input
                  type="checkbox"
                  checked={privacyAgreed}
                  onChange={(e) => setPrivacyAgreed(e.target.checked)}
                  className="agreement-checkbox"
                />
                <span className="checkbox-custom"></span>
                <span className="agreement-text">
                  <span className="required">[필수]</span> 개인정보 처리방침
                </span>
              </label>
              <button className="view-button" onClick={handlePrivacyView}>
                보기
              </button>
            </div>
          </div>

          {/* 안내 메시지 */}
          <div className="agreement-notice">
            <p>
              MoodMate는 사용자의 감정 데이터를 안전하게 보호하며,
              <br />
              AI 분석을 위해 OpenAI에 대화 내용을 전송합니다.
            </p>
          </div>

          {/* 시작하기 버튼 */}
          <div className="agreement-button-section">
            <button
              className={`start-button ${
                !termsAgreed || !privacyAgreed ? "disabled" : ""
              }`}
              onClick={handleSubmit}
              disabled={!termsAgreed || !privacyAgreed}
            >
              시작하기
            </button>
          </div>
        </div>
      </div>

      {/* 약관 모달 */}
      <TermsModal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        type={modalType}
      />
    </>
  );
}

export default TermsAgreement;
