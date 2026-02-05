import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import LoadingSpinner from "./LoadingSpinner";

const PrivateRoute = () => {
  const { user, loading } = useAuth();

  if (loading) {
    // 인증 상태 확인 중이면 로딩 메시지 표시
    return <LoadingSpinner />;
  }

  // 로그인 상태이면 요청된 페이지 보여주고, 아니면 로그인 페이지로 리디렉션
  return user ? <Outlet /> : <Navigate to="/" />;
};

export default PrivateRoute;
