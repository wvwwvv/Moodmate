import React, { useState, useEffect } from "react";
import Logo from "../components/Logo";
import "./EmotionDetail.css";
import { getCharacterImagePath } from "../utils/imageUtils";
import { API_URL } from "../config";
import LoadingSpinner from "../components/LoadingSpinner";

function EmotionDetail({ selectedEmotion, onBack }) {
  const [characterDetail, setCharacterDetail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedLevel, setSelectedLevel] = useState(null);

  useEffect(() => {
    fetchCharacterDetail();
  }, [selectedEmotion]);

  const fetchCharacterDetail = async () => {
    try {
      setLoading(true);
      console.log(`API 호출: ${API_URL}/api/collection/${selectedEmotion}`);

      const response = await fetch(
        `${API_URL}/api/collection/${selectedEmotion}`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
        }
      );

      console.log("API 응답 상태:", response.status);

      if (!response.ok) {
        throw new Error(
          `상세 정보를 불러오는데 실패했습니다. (상태: ${response.status})`
        );
      }

      const data = await response.json();
      console.log("받은 상세 데이터:", data);
      setCharacterDetail(data);

      const initialLevel =
        data.initialDisplayLevel > 0 ? data.initialDisplayLevel : 1;

      const initialCharacter = data.levelDetails.find(
        (d) => d.level === initialLevel
      );

      setSelectedLevel(initialCharacter);
      setError(null);
    } catch (err) {
      console.error("Error fetching character detail:", err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // NEW 상태 해제 함수
  const handleNewCheck = async (collectionId) => {
    try {
      console.log("NEW 마커 제거 API 호출:", collectionId);

      const response = await fetch(`${API_URL}/api/collection/checked`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ collectionId }),
      });

      if (response.ok) {
        console.log("NEW 마커 제거 성공");
        fetchCharacterDetail();
      }
    } catch (err) {
      console.error("Error checking character:", err);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return "알 수 없음";
    const date = new Date(new Date(dateString).getTime() + 9 * 60 * 60 * 1000);
    const year = String(date.getFullYear()).slice(-2);
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}년 ${month}월 ${day}일`;
  };

  const handleLevelClick = (levelDetail) => {
    if (!levelDetail.acquired) return;

    setSelectedLevel(levelDetail);

    if (levelDetail["new"]) {
      handleNewCheck(levelDetail.collectionId);
    }
  };

  if (loading) {
    return (
      <div className="emotion-detail-page">
        <Logo />
        <div className="emotion-detail-header">
          <button className="back-button" onClick={onBack}>
            ← 뒤로가기
          </button>
          <h2>{selectedEmotion}</h2>
        </div>
        <LoadingSpinner message="상세 정보를 불러오는 중..." />
      </div>
    );
  }

  if (error) {
    return (
      <div className="emotion-detail-page">
        <Logo />
        <div className="emotion-detail-header">
          <button className="back-button" onClick={onBack}>
            ← 뒤로가기
          </button>
          <h2>{selectedEmotion}</h2>
        </div>
        <div className="error-message">{error}</div>
      </div>
    );
  }

  if (!characterDetail) return null;

  return (
    <div className="emotion-detail-page">
      <Logo />

      <div className="emotion-detail-header">
        <button className="back-button" onClick={onBack}>
          ← 뒤로가기
        </button>
        <h2>{characterDetail?.emotion || selectedEmotion}</h2>
      </div>

      {/* 대표 캐릭터 */}
      <div className="main-character-section">
        <div className="main-character-card">
          <div className="main-character-image">
            <img
              src={
                selectedLevel?.imageUrl ||
                getCharacterImagePath(selectedEmotion, 1)
              }
              alt="대표 캐릭터"
              onError={(e) => {
                e.target.src = "/images/characters/1.png";
              }}
            />
          </div>

          <div className="main-character-text">
            {selectedLevel && selectedLevel.acquired ? (
              <>
                <div className="character-name">
                  {selectedLevel.name || `${selectedEmotion} 캐릭터`}
                </div>

                <div className="character-info">
                  <div className="character-info-item">
                    처음 만난 날짜: {formatDate(selectedLevel.createdDatetime)}
                  </div>

                  <div className="character-info-item">
                    만난 횟수: {selectedLevel.count || 0}회
                  </div>

                  <div className="character-info-item">
                    레벨: {selectedLevel.level}
                  </div>
                </div>

                {selectedLevel.description && (
                  <div className="character-description">
                    {selectedLevel.description}
                  </div>
                )}
              </>
            ) : (
              <div className="character-placeholder">
                {characterDetail.emotion} 감정의 캐릭터를 만나보세요!
              </div>
            )}
          </div>
        </div>
      </div>

      {/* 하단 레벨 그리드 */}
      <div className="level-grid">
        {characterDetail.levelDetails.map((levelDetail) => (
          <div
            key={levelDetail.level}
            className={`level-card ${levelDetail.acquired ? "unlocked" : ""} ${
              selectedLevel?.level === levelDetail.level ? "selected" : ""
            }`}
            onClick={() => handleLevelClick(levelDetail)}
          >
            <div className="level-character">
              <div className="level-image">
                <img
                  src={levelDetail.imageUrl}
                  alt={`레벨 ${levelDetail.level}`}
                  className={!levelDetail.acquired ? "locked-image" : ""}
                  onError={(e) => {
                    e.target.src = getCharacterImagePath(
                      selectedEmotion,
                      levelDetail.level
                    );
                  }}
                />

                {levelDetail.acquired && levelDetail["new"] && (
                  <div className="level-new-icon">
                    <img src="/images/new.png" alt="NEW" />
                  </div>
                )}
              </div>

              {!levelDetail.acquired && (
                <div className="level-lock-overlay">
                  <img
                    src="/images/lock.png"
                    alt="잠금"
                    className="level-lock-icon"
                  />
                </div>
              )}
            </div>

            <div className="level-text">LV.{levelDetail.level}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default EmotionDetail;
