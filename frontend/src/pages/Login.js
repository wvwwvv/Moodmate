import React from "react";
import "./Login.css";
import { API_URL } from "../config";

function Login() {
  const handleKakaoLogin = () => {
    console.log("카카오 로그인 시작");

    // 백엔드의 Spring Security OAuth2 로그인 URL로 리디렉션합니다.
    window.location.replace(`${API_URL}/oauth2/authorization/kakao`);
  };

  return (
    <div className="login-container">
      <div className="login-content">
        {/* 로고 섹션 */}
        <div className="logo-section">
          <div className="logo-image-container">
            <img
              src="/images/logo.png"
              alt="MoodMate Logo"
              className="logo-image"
            />
          </div>
        </div>

        {/* 설명 문구 */}
        <div className="description-section">
          <p className="description-text">
            "하루를 간격으로 나눠보세요.
            <br />
            무드메이트가 당신의 마음을 읽어드립니다."
          </p>
        </div>

        {/* 로그인 버튼 */}
        <div className="login-button-section">
          <button className="kakao-login-button" onClick={handleKakaoLogin}>
            <div className="kakao-icon">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 3c5.799 0 10.5 3.664 10.5 8.185 0 4.52-4.701 8.184-10.5 8.184a13.5 13.5 0 0 1-1.727-.11l-4.408 2.883c-.501.265-.678.236-.472-.413l.892-3.678c-2.88-1.46-4.785-3.99-4.785-6.866C1.5 6.665 6.201 3 12 3z" />
              </svg>
            </div>
            카카오로 시작하기
          </button>
        </div>
      </div>
    </div>
  );
}

export default Login;
