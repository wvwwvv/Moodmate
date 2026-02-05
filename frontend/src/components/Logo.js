import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import HamburgerMenu from "./HamburgerMenu";
import "./Logo.css";

function Logo() {
  const location = useLocation();
  const navigate = useNavigate();
  const isHome = location.pathname === "/";

  const handleLogoClick = () => {
    navigate("/chat");
  };

  return (
    <div className="header">
      <div className="logo-container">
        <img
          src="/images/logo.png"
          alt="MoodMate Logo"
          className="logo-image"
          onClick={handleLogoClick}
          style={{ cursor: "pointer" }}
        />
      </div>

      {/* 홈 페이지가 아닐 때만 햄버거 메뉴 표시 */}
      {!isHome && <HamburgerMenu />}
    </div>
  );
}

export default Logo;
