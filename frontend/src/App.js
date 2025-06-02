import React from 'react';
import './App.css';
import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import Attendance from "./attendance";
import AttendanceTotalization from "./attendance/totalization";
import AttendanceManagement from "./attendance/management";
import UsersManagement from "./users/management";

function App() {
  return (
    <BrowserRouter>
      <div className="app-container">
        <nav className="sidebar">
          <Link to="/attendance">出退勤記録</Link>
          <Link to="/attendance/totalization">勤務集計</Link>
          <Link to="/attendance/management">勤務管理</Link>
          <Link to="/users/management">従業員管理</Link>
        </nav>
        <main className="main-content">
          <Routes>
            <Route path="/attendance" element={<Attendance />} />
            <Route path="/attendance/totalization" element={<AttendanceTotalization />} />
            <Route path="/attendance/management" element={<AttendanceManagement />} />
            <Route path="/users/management" element={<UsersManagement />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;