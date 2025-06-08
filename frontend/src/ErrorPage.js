import React, { useContext, useEffect  } from 'react';
import { useNavigate } from "react-router-dom";
import { AuthContext } from "./AuthContext";

const HomeButton = () => {
  const navigate = useNavigate();

  const handleHome = () => {
    navigate("/");
  };

  return (
    <button className="non-register-button" onClick={handleHome}>ログイン画面へ戻る</button>
  );
};

const ErrorPage = ({setContentOnly}) => {
  const { setAuthToken } = useContext(AuthContext);

  useEffect(() => {
    setContentOnly(true);
  }, [setContentOnly]);

  // エラー発生時、JWTtokenを削除する
  useEffect(() => {
    setAuthToken(null);
    localStorage.removeItem("authToken");
  }, []);

  return (
    <div className="error-container">
      <h1>予期しないエラーが発生しました</h1>
      <HomeButton />
    </div>
  );
};

export default ErrorPage;