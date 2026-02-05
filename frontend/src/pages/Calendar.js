import React, { useState, useEffect } from "react";
import ReactCalendar from "react-calendar";
import { useNavigate, useParams } from "react-router-dom";
import Select from "react-select";
import "./Calendar.css";
import Logo from "../components/Logo";
import PageWrapper from "../components/PageWrapper";
import axios from "axios";
import { API_URL } from "../config";

function Calendar() {
  const navigate = useNavigate();
  const { year, month } = useParams(); //url 에서 year, month param 가져옴

  // 기본 감정 리스트
  const emotions = [
    "기쁨",
    "행복",
    "즐거움/신남",
    "고마움",
    "슬픔",
    "힘듦/지침",
    "불쌍함/연민",
    "두려움",
    "의심/불신",
    "분노",
    "화남/분노",
    "짜증",
    "불평/불만",
    "놀람",
    "중립/복합",
    "혐오",
    "안심/신뢰",
    "당황/난처",
  ];

  //url param 기반 초기 날짜 설정
  const getInitialDate = () => {
    if (year && month) {
      // URL 의 month 는 1부터 시작하지만, new Date()의 month 는 0부터 시작하므로 1을 빼줌
      return new Date(parseInt(year), parseInt(month) - 1, 1);
    }
    return new Date(); // param 없으면 현재 날짜로 설정
  };

  // value 초기화
  const [value, setValue] = useState(getInitialDate());
  const [monthData, setMonthData] = useState({});

  useEffect(() => {
    const currentYear = value.getFullYear();
    const currentMonth = value.getMonth() + 1;

    if (
      !year ||
      !month ||
      parseInt(year) !== currentYear ||
      parseInt(month) !== currentMonth
    ) {
      navigate(`/calendar/${currentYear}/${currentMonth}`, { replace: true });
    }
  }, [value, year, month, navigate]);

  useEffect(() => {
    const fetchMonthData = async () => {
      const year = value.getFullYear();
      const month = value.getMonth() + 1; //0부터 시작하므로 1 더함

      try {
        const response = await axios.get(
          `${API_URL}/api/calendar/${year}/${month}`
        );
        setMonthData(response.data);
      } catch (error) {
        console.error("Error Fetching calendar data: ", error);
        setMonthData({});
      }
    };
    fetchMonthData();
  }, [value]); //월/년이 바뀔 때마다 effect 실행

  const formatDate = (date) =>
    `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;

  const goToPreviousMonth = () => {
    const newDate = new Date(value.getFullYear(), value.getMonth() - 1, 1);
    setValue(newDate);
  };

  const goToNextMonth = () => {
    const newDate = new Date(value.getFullYear(), value.getMonth() + 1, 1);
    setValue(newDate);
  };

  const currentYear = new Date().getFullYear();
  const yearOptions = [];
  for (let i = currentYear - 20; i <= currentYear; i++) {
    yearOptions.push({ value: i, label: `${i}년` });
  }

  const monthOptions = [...Array(12)].map((_, i) => ({
    value: i,
    label: `${i + 1}월`,
  }));

  const customSelectStyles = {
    control: (provided) => ({
      ...provided,
      border: "none",
      background: "#fdfbf1",
      boxShadow: "none",
      minHeight: "auto",
      cursor: "pointer",
      "&:hover": {
        background: "#f5f0e6",
      },
    }),

    singleValue: (provided) => ({
      ...provided,
      color: "#8b7355",
      fontSize: "28px",
      fontWeight: "normal",
      fontFamily:
        '"Gaegu", "Hi Melody", -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif',
      textAlign: "center",
      width: "100%",
    }),
    menu: (provided) => ({
      ...provided,
      background: "#fdfbf1",
      border: "1px solid #d0c4b0",
      borderRadius: "8px",
      overflow: "hidden",
    }),
    menuList: (provided) => ({
      ...provided,
      padding: 0,
    }),
    option: (provided, state) => ({
      ...provided,
      background: state.isSelected
        ? "#f0e9d8"
        : state.isFocused
        ? "#f5f0e6"
        : "#fdfbf1",
      color: "#8b7355",
      fontSize: "16px",
      padding: "10px 15px",
      cursor: "pointer",
      fontFamily:
        '"Gaegu", "Hi Melody", -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif',
    }),
    indicatorSeparator: () => ({ display: "none" }),
    dropdownIndicator: () => ({ display: "none" }),
  };

  return (
    <PageWrapper>
      <Logo />
      <div className="calendar-container">
        <div className="custom-calendar-nav">
          <button className="nav-arrow" onClick={goToPreviousMonth}>
            ◀
          </button>

          <div className="nav-selectors">
            <Select
              value={yearOptions.find(
                (opt) => opt.value === value.getFullYear()
              )}
              onChange={(option) => {
                const newDate = new Date(option.value, value.getMonth(), 1);
                setValue(newDate);
              }}
              options={yearOptions}
              styles={customSelectStyles}
              isSearchable={false}
              className="year-dropdown"
            />

            <Select
              value={monthOptions.find((opt) => opt.value === value.getMonth())}
              onChange={(option) => {
                const newDate = new Date(value.getFullYear(), option.value, 1);
                setValue(newDate);
              }}
              options={monthOptions}
              styles={customSelectStyles}
              isSearchable={false}
              className="month-dropdown"
            />
          </div>

          <button className="nav-arrow" onClick={goToNextMonth}>
            ▶
          </button>
        </div>

        <ReactCalendar
          // 이미지 유무와 관계 없이 항상 상세 페이지 이동
          onClickDay={(date) => {
            navigate(`/calendar/detail/${formatDate(date)}`);
          }}
          value={value}
          onChange={setValue}
          locale="ko-KR"
          calendarType="gregory"
          showNeighboringMonth={true}
          formatDay={(_, date) => date.getDate()}
          formatShortWeekday={(locale, date) =>
            ["일", "월", "화", "수", "목", "금", "토"][date.getDay()]
          }
          // 다른 달의 날짜만 비활성화
          tileDisabled={({ date, view }) => {
            return view === "month" && date.getMonth() !== value.getMonth();
          }}
          tileClassName={({ date, view }) => {
            if (view === "month" && date.getMonth() !== value.getMonth()) {
              return "neighboring-month";
            }
            return null;
          }}
          tileContent={({ date }) => {
            if (date.getMonth() !== value.getMonth()) {
              return <div className="tile-inner"></div>;
            }

            const day = date.getDate();
            const imageUrl = monthData[day];

            if (imageUrl) {
              return (
                <div className="tile-inner">
                  <img
                    src={imageUrl}
                    alt={`감정 기록`}
                    className="calendar-emotion-icon"
                    onError={(e) => {
                      e.target.src = "/images/characters/joy_lv1.png";
                    }}
                  />
                </div>
              );
            }
            return <div className="tile-inner"></div>;
          }}
          onActiveStartDateChange={({ activeStartDate }) => {
            if (activeStartDate) {
              setValue(activeStartDate);
            }
          }}
        />
      </div>
    </PageWrapper>
  );
}

export default Calendar;
