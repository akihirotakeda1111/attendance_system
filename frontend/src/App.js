import React, { useContext, useState  } from 'react';
import { FaClock, FaUsers } from "react-icons/fa";
import { MdBarChart } from "react-icons/md";
import { AiOutlineSchedule } from "react-icons/ai";
import './App.css';
import { BrowserRouter, Routes, Route, Link, useNavigate  } from "react-router-dom";
import ErrorBoundary from "./ErrorBoundary";
import ErrorPage from "./ErrorPage";
import { AuthContext } from "./AuthContext";
import { AuthProvider } from "./AuthContext";
import { getRoleFromToken } from "./utils";
import Login from "./login";
import Attendance from "./attendance";
import AttendanceTotalization from "./attendance/totalization";
import AttendanceManagement from "./attendance/management";
import UsersManagement from "./users/management";

// ログアウトボタンコンポーネント
const LogoutButton = () => {
  const { setAuthToken } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    setAuthToken(null);
    localStorage.removeItem("authToken");
    navigate("/");
  };

  return (
    <button className="danger-button logout-button" onClick={handleLogout}>ログアウト</button>
  );
};

// タイトルバー
const Titlebar = () => {
  return (
    <header className="titlebar">
      <h1 className="title">出退勤管理システム</h1>
    </header>
  );
};


// サイドメニューコンポーネント
const Sidebar = () => {
  const role = getRoleFromToken();
  
  return (
    <nav className="sidebar">
      <Link to="/attendance"><FaClock />出退勤記録</Link>
      {role === "00" ? null : <Link to="/attendance/totalization"><MdBarChart />勤務集計</Link>}
      {role === "00" ? null : <Link to="/attendance/management"><AiOutlineSchedule />勤務管理</Link>}
      {role === "00" ? null : <Link to="/users/management"><FaUsers />従業員管理</Link>}
    </nav>
  );
};

function App() {
  const [error, setError] = useState(null);
  const [isContentOnly, setContentOnly] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const stateHandlers = { setError, setContentOnly, setIsLoading };

  return (
    <AuthProvider>
      <BrowserRouter>
        <ErrorBoundary error={error}>
          <div className="app-container">
            {isLoading && (
              <div className="loading-overlay">
                <div className="spinner"></div>
              </div>
            )}
            {isContentOnly ? null : <Sidebar />}
            <main className="main-content">
              <Titlebar />
              {isContentOnly ? null : <div className="right"><LogoutButton /></div>}
              <Routes>
                <Route path="/" element={<Login stateHandlers={stateHandlers} />} />
                <Route path="/attendance" element={<Attendance stateHandlers={stateHandlers} />} />
                <Route path="/attendance/totalization" element={<AttendanceTotalization stateHandlers={stateHandlers} />} />
                <Route path="/attendance/management" element={<AttendanceManagement stateHandlers={stateHandlers} />} />
                <Route path="/users/management" element={<UsersManagement stateHandlers={stateHandlers} />} />
                <Route path="/error" element={<ErrorPage setContentOnly={setContentOnly} />} />
                <Route path="*" element={<ErrorPage setContentOnly={setContentOnly} />} />
              </Routes>
            </main>
          </div>
        </ErrorBoundary>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;