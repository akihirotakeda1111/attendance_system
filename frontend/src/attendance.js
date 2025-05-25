import React, { useState, useEffect, useCallback } from "react";
import { toYMDHMS, isHalfWidthNumber } from "./utils";

const Attendance = () => {
  const [userId, setUserId] = useState("admin");
  const [minute, setMiute] = useState("");
  const [now, setNow] = useState(new Date());
  const [latestAttendance, setLatestAttendance] = useState(null);
  const [latestBreaktime, setLatestBreaktime] = useState(null);

  const fetchLatestAttendance = useCallback(async () => {
    const response = await fetch(
      `${process.env.REACT_APP_API_BASE_URL}/attendance/latest?userId=${userId}`
    );
    if (response.status === 404) {
      setLatestAttendance(null);
      return;
    }
    const data = await response.json();
    setLatestAttendance(data);
  }, [userId]);

  const fetchLatestBreaktime = useCallback(async () => {
    if (!latestAttendance) return;

    const response = await fetch(
      `${process.env.REACT_APP_API_BASE_URL}/breaktime/latest?userId=${userId}&date=${latestAttendance.date}`
    );
    if (response.status === 404) {
      setLatestBreaktime(null);
      return;
    }
    const data = await response.json();
    setLatestBreaktime(data);
  }, [userId, latestAttendance]);

  const attendanceStartSubmit = async () => {
    const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/attendance`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ userId }),
    });
    if (!response.ok) {
      const errorMessage = await response.text();
      alert(`Error: ${errorMessage}`);
      return;
    }
    const message = await response.text();
    alert(message);
    fetchLatestAttendance();
  };

  const attendanceEndSubmit = async () => {
    const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/attendance`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ userId }),
    });
    const message = await response.text();
    alert(message);
    fetchLatestAttendance();
  };

  const breaktimeStartSubmit = async () => {
    const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/breaktime`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        userId: userId,
        date: latestAttendance.date,
        minute: minute === "" ? 0 : parseInt(minute),
       }),
    });
    const message = await response.text();
    alert(message);
    fetchLatestBreaktime();
  };

  const breaktimeEndSubmit = async () => {
    const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/breaktime`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        userId: userId,
        date: latestAttendance.date,
       }),
    });
    const message = await response.text();
    alert(message);
    fetchLatestBreaktime();
  };

  useEffect(() => {
    const timer = setInterval(() => setNow(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  useEffect(() => {
    fetchLatestAttendance();
  }, [fetchLatestAttendance]);

  useEffect(() => {
    fetchLatestBreaktime();
  }, [fetchLatestBreaktime]);

  return (
    <div>
      <input
            type="text"
            placeholder="ユーザーID"
            value={userId}
            onChange={(e) => setUserId(e.target.value)}
          />
      <div className="center fs-50">{now.toLocaleString()}</div>
      <h2>出退勤登録</h2>
      <table className="table-layout none-border">
        <tbody>
          <tr>
            <td>
              <div>出勤時刻:{!latestAttendance ? "未出勤" : toYMDHMS(latestAttendance.startTime)}</div>
            </td>
          </tr>
          <tr>
            <td>
              {latestAttendance ? (
                <span className="register-button-disabled width-100">
                  出勤
                </span>
              ) : (
                <button
                  className="register-button width-100"
                  onClick={attendanceStartSubmit}>
                  出勤
                </button>
              )}
            </td>
            <td>
              {!latestAttendance ? (
                <span className="register-button-disabled width-100">
                  退勤
                </span>
              ) : (
                <button
                  className="register-button width-100"
                  onClick={attendanceEndSubmit}>
                  退勤
                </button>
              )}
            </td>
          </tr>
        </tbody>
      </table>
      <h2>離席・休憩登録</h2>
      <table className="table-layout none-border">
        <tbody>
          <tr>
            <td>
              <div>
                予定時間:
                <input
                  disabled={!latestAttendance || latestAttendance && latestBreaktime}
                  type="text"
                  placeholder="半角数字で入力"
                  value={minute}
                  maxLength={3}
                  onChange={(e) => {
                    const value = e.target.value;
                    if (isHalfWidthNumber(value) || value === "") {
                      setMiute(value);
                    }
                  }}
                />
                分
              </div>
            </td>
          </tr>
          {latestBreaktime && (
            <tr>
              <td>
                <div>開始時刻:{latestBreaktime == null ? "" : toYMDHMS(latestBreaktime.startTime)}</div>
              </td>
              <td>
                <div>終了予定時刻:{latestBreaktime == null ? "" : toYMDHMS(latestBreaktime.expectedEndTime)}</div>
              </td>
            </tr>
          )}
          <tr>
            <td>
              {!latestAttendance || latestAttendance && latestBreaktime ? (
                <span className="register-button-disabled width-100">
                  離席・休憩
                </span>
              ) : (
                <button
                  className="register-button width-100"
                  onClick={breaktimeStartSubmit}>
                  離席・休憩
                </button>
              )}
            </td>
            <td>
              {!latestAttendance || latestAttendance && !latestBreaktime ? (
                <span className="register-button-disabled width-100">
                  再開
                </span>
              ) : (
                <button
                  className="register-button width-100"
                  onClick={breaktimeEndSubmit}>
                  再開
                </button>
              )}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  );
};

export default Attendance;