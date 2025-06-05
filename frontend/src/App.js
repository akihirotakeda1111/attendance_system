import React, { useContext  } from 'react';
import './App.css';
import { BrowserRouter, Routes, Route, Link, useLocation, useNavigate  } from "react-router-dom";
import { AuthContext } from "./AuthContext";
import { AuthProvider } from "./AuthContext";
import { getRoleFromToken } from "./utils";
import Login from "./login";
import Attendance from "./attendance";
import AttendanceTotalization from "./attendance/totalization";
import AttendanceManagement from "./attendance/management";
import UsersManagement from "./users/management";

const LogoutButton = () => {
  const { setAuthToken } = useContext(AuthContext);
  const navigate = useNavigate();

  const location = useLocation();
  const isLoginPage = location.pathname === "/";

  const handleLogout = () => {
    setAuthToken(null);
    localStorage.removeItem("authToken");
    navigate("/");
  };

  return isLoginPage ? null : (
    <button onClick={handleLogout}>ログアウト</button>
  );
};

const Sidebar = () => {
  const location = useLocation();
  const isLoginPage = location.pathname === "/";
  const role = getRoleFromToken();
  
  return isLoginPage ? null : (
    <nav className="sidebar">
      <Link to="/attendance">出退勤記録</Link>
      {role === "00" ? null : <Link to="/attendance/totalization">勤務集計</Link>}
      {role === "00" ? null : <Link to="/attendance/management">勤務管理</Link>}
      {role === "00" ? null : <Link to="/users/management">従業員管理</Link>}
    </nav>
  );
};

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <div className="app-container">
          <Sidebar />
          <main className="main-content">
            <div className="right">
              <LogoutButton />
            </div>
            <Routes>
              <Route path="/" element={<Login />} />
              <Route path="/attendance" element={<Attendance />} />
              <Route path="/attendance/totalization" element={<AttendanceTotalization />} />
              <Route path="/attendance/management" element={<AttendanceManagement />} />
              <Route path="/users/management" element={<UsersManagement />} />
            </Routes>
          </main>
        </div>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;