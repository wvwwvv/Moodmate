import React, { useState, useEffect } from "react";
import PageWrapper from "../components/PageWrapper";
import EmotionDetail from "./EmotionDetail";
import "./Collection.css";
import Logo from "../components/Logo";
import { getCharacterImagePath } from "../utils/imageUtils";
import { API_URL } from "../config";
import LoadingSpinner from "../components/LoadingSpinner";

function Collection() {
  const [selectedEmotion, setSelectedEmotion] = useState(null);
  const [collectionData, setCollectionData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 컬렉션 데이터 로드
  useEffect(() => {
    fetchCollectionData();
  }, []);

  const fetchCollectionData = async () => {
    try {
      setLoading(true);
      const response = await fetch(`${API_URL}/api/collection`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
      });

      if (!response.ok) {
        throw new Error(
          `컬렉션 데이터를 불러오는데 실패했습니다. (상태: ${response.status})`
        );
      }

      const data = await response.json();
      setCollectionData(data);
      setError(null);
    } catch (err) {
      console.error("Error fetching collection:", err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  /* // NEW 상태 해제 요청 함수 (collectionId 기반)
  const markAsChecked = async (collectionId) => {
    try {
      const response = await fetch(
        "http://localhost:8080/api/collection/checked",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
          body: JSON.stringify({
            collectionId: collectionId, // 서버 DTO와 일치
          }),
        }
      );

      if (!response.ok) {
        console.warn(`NEW 상태 해제 실패: ${response.status}`);
      } else {
        console.log(`NEW 상태 해제 성공: ${collectionId}`);
      }
    } catch (err) {
      console.error("NEW 상태 해제 중 오류 발생:", err);
    }
  };*/

  const hasCharacterForEmotion = (emotion) => {
    const item = collectionData.find((item) => item.emotion === emotion);
    return item && item.level > 0;
  };

  const getEmotionLevel = (emotion) => {
    const item = collectionData.find((item) => item.emotion === emotion);
    return item?.level || 1;
  };

  const getEmotionImageUrl = (emotion) => {
    const item = collectionData.find((item) => item.emotion === emotion);
    return item?.imageUrl || getCharacterImagePath(emotion, 1);
  };

  const isNewEmotion = (emotion) => {
    const item = collectionData.find((item) => item.emotion === emotion);
    return item ? item["new"] : false;
  };

  /*// 감정 선택 시 NEW 상태라면 해제 API 호출
  const handleEmotionSelect = async (emotion) => {
    const hasCharacter = hasCharacterForEmotion(emotion);
    if (!hasCharacter) return;

    const item = collectionData.find((item) => item.emotion === emotion);
    const emotionEng = item?.emotionEng || emotion;

    // NEW 상태라면 서버에 해제 요청
    if (item["new"]) {
      await markAsChecked(item.collectionId); // collectionId 전달
      // 로컬 상태도 즉시 반영
      setCollectionData((prev) =>
        prev.map((c) => (c.emotion === emotion ? { ...c, new: false } : c))
      );
    }

    setSelectedEmotion(emotionEng);
  };*/

  // 감정 선택 시 상세 페이지로 이동만 담당
  const handleEmotionSelect = (emotion) => {
    const item = collectionData.find((item) => item.emotion === emotion);
    if (!item || !item.level || item.level <= 0) {
      // 획득하지 않은 캐릭터는 클릭해도 아무 동작 안 함
      return;
    }

    // item.emotionEng 값을 사용해 상세 페이지로 이동
    setSelectedEmotion(item.emotionEng);
  };

  const handleBackToCategories = () => {
    setSelectedEmotion(null);
    fetchCollectionData(); // 뒤로가기 시 최신 상태 갱신
  };

  if (loading) {
    return (
      <PageWrapper>
        <Logo />
        <div className="collection-container">
          <LoadingSpinner message="컬렉션을 불러오는 중..." />
        </div>
      </PageWrapper>
    );
  }

  if (error) {
    return (
      <PageWrapper>
        <Logo />
        <div className="collection-container">
          <div className="error-message">{error}</div>
        </div>
      </PageWrapper>
    );
  }

  if (selectedEmotion) {
    return (
      <PageWrapper>
        <EmotionDetail
          selectedEmotion={selectedEmotion}
          onBack={handleBackToCategories}
        />
      </PageWrapper>
    );
  }

  return (
    <PageWrapper>
      <Logo />
      <div className="collection-container categories-view">
        <div className="emotion-grid">
          {collectionData.map((item) => {
            const hasCharacter = item.level > 0;
            const isNew = item["new"];

            return (
              <div
                key={item.emotion}
                className={`emotion-card ${!hasCharacter ? "locked-card" : ""}`}
                onClick={() => handleEmotionSelect(item.emotion)}
              >
                <div className="card-icon">
                  <img
                    src={item.imageUrl}
                    alt={item.emotion}
                    className={!hasCharacter ? "locked-image" : ""}
                    onError={(e) => {
                      e.target.src = getCharacterImagePath(item.emotion, 1);
                    }}
                  />
                  {hasCharacter && isNew && (
                    <div className="new-icon">
                      <img src="/images/new.png" alt="NEW" />
                    </div>
                  )}
                  {!hasCharacter && (
                    <div className="lock-overlay">
                      <img
                        src="/images/lock.png"
                        alt="잠금"
                        className="lock-icon"
                      />
                    </div>
                  )}
                </div>
                <div className="card-title">{item.emotion}</div>
              </div>
            );
          })}
        </div>
      </div>
    </PageWrapper>
  );
}

export default Collection;
