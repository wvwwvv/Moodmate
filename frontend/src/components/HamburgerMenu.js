import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./HamburgerMenu.css";

const menuItems = [
  { icon: "/images/home.png", label: "홈", path: "/chat" },
  { icon: "/images/dex.png", label: "도감", path: "/collection" },
  { icon: "/images/calendar.png", label: "캘린더", path: "/calendar" },
  { icon: "/images/lock.png", label: "마이페이지", path: "/settings" },
];

function HamburgerMenu() {
  const [isOpen, setIsOpen] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();

  const toggleMenu = () => {
    setIsOpen(!isOpen);
  };

  const handleMenuClick = (path) => {
    navigate(path);
    setIsOpen(false);
  };

  const getCurrentPage = () => {
    const currentItem = menuItems.find(
      (item) => item.path === location.pathname
    );
    return currentItem ? currentItem.label : "홈";
  };

  return (
    <>
      {/* 햄버거 버튼 */}
      <div className="hamburger-container">
        <button className="hamburger-button" onClick={toggleMenu}>
          <span className={`hamburger-line ${isOpen ? "open" : ""}`}></span>
          <span className={`hamburger-line ${isOpen ? "open" : ""}`}></span>
          <span className={`hamburger-line ${isOpen ? "open" : ""}`}></span>
        </button>
      </div>

      {/* 오버레이 */}
      {isOpen && <div className="menu-overlay" onClick={toggleMenu}></div>}

      {/* 슬라이드 메뉴 */}
      <div className={`slide-menu ${isOpen ? "open" : ""}`}>
        <div className="hamburger-menu-header">
          <h3>메뉴</h3>
        </div>

        <div className="hamburger-menu-items">
          {menuItems.map((item) => (
            <div
              key={item.path}
              className={`hamburger-menu-item ${
                location.pathname === item.path ? "active" : ""
              }`}
              onClick={() => handleMenuClick(item.path)}
            >
              <img
                src={item.icon}
                alt={item.label}
                className="hamburger-menu-icon"
              />
              <span className="hamburger-menu-label">{item.label}</span>
            </div>
          ))}
        </div>
      </div>
    </>
  );
}

export default HamburgerMenu;
