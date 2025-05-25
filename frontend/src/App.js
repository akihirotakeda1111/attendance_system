import React from 'react';
import './App.css';
import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import Attendance from "./attendance";

function App() {
  return (
    <BrowserRouter>
      <div className="app-container">
        <nav className="sidebar">
          <Link to="/attendance">出退勤記録</Link>
          {/* 他のメニューもここに追加できます */}
        </nav>
        <main className="main-content">
          <Routes>
            <Route path="/attendance" element={<Attendance />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;