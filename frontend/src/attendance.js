import React, { useContext, useState, useEffect, useCallback } from "react";
import { AuthContext } from "./AuthContext";
import { toYMDHMS, isHalfWidthNumber, getUserIdFromToken } from "./utils";
import { handleApiError } from "./errorHandler";

// 出退勤登録コンポーネント
const Attendance = ({stateHandlers}) => {
  const { setError, setContentOnly, setIsLoading } = stateHandlers;
  useEffect(() => {
    setContentOnly(false);
  }, [setContentOnly]);

  const { authToken } = useContext(AuthContext);
  
  const [userId, setUserId] = useState(getUserIdFromToken());
  const [minute, setMiute] = useState("");
  const [now, setNow] = useState(new Date());
  const [todayAttendance, setTodayAttendance] = useState(null);
  const [latestAttendance, setLatestAttendance] = useState(null);
  const [latestBreaktime, setLatestBreaktime] = useState(null);

  // 当日の出勤情報取得イベント
  const fetchTodayAttendance = useCallback(async () => {
    try {
      setIsLoading(true);
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/attendance/today?userId=${userId}`, {
          method: "GET",
          headers: {"Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}`}
        }
      );
      if (response.status === 204) {
        setTodayAttendance(null);
        return;
      }
      if (!response.ok) {
        const errorResponse = await response.json();
        handleApiError(errorResponse);
        setTodayAttendance(null);
        return;
      }
      const data = await response.json();
      setTodayAttendance(data);
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  }, [userId]);

  // 最新の出勤情報取得イベント
  const fetchLatestAttendance = useCallback(async () => {
    try {
      setIsLoading(true);
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/attendance/latest?userId=${userId}`, {
          method: "GET",
          headers: { "Content-Type": "application/json",
            Authorization: `Bearer ${authToken}`}
        }
      );
      if (response.status === 204) {
        setLatestAttendance(null);
        return;
      }
      if (!response.ok) {
        const errorResponse = await response.json();
        handleApiError(errorResponse);
        setLatestAttendance(null);
        return;
      }
      const data = await response.json();
      setLatestAttendance(data);
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  }, [userId]);

  // 最新の休憩情報取得イベント
  const fetchLatestBreaktime = useCallback(async () => {
    if (!latestAttendance) return;

    try {
      setIsLoading(true);
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/breaktime/latest?userId=${userId}&date=${latestAttendance.date}`, {
          method: "GET",
          headers: { "Content-Type": "application/json",
            Authorization: `Bearer ${authToken}`}
        }
      );
      if (response.status === 204) {
        setLatestBreaktime(null);
        return;
      }
      if (!response.ok) {
        const errorResponse = await response.json();
        handleApiError(errorResponse);
        setLatestBreaktime(null);
        return;
      }
      const data = await response.json();
      setLatestBreaktime(data);
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  }, [userId, latestAttendance]);

  // 出勤時刻登録イベント
  const attendanceStartSubmit = async () => {
    try {
      setIsLoading(true);
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/attendance`, {
        method: "POST",
        headers: { "Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}` },
        body: JSON.stringify({ userId }),
      });
      if (!response.ok) {
        const errorResponse = await response.json();
        handleApiError(errorResponse);
        return;
      }
      const message = await response.text();
      alert(message);
      fetchTodayAttendance();
      fetchLatestAttendance();
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  };

  // 退勤時刻登録イベント
  const attendanceEndSubmit = async () => {
    try {
      setIsLoading(true);
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/attendance`, {
        method: "PUT",
        headers: { "Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}` },
        body: JSON.stringify({ userId }),
      });
      if (!response.ok) {
        const errorResponse = await response.json();
        handleApiError(errorResponse);
        return;
      }
      const message = await response.text();
      alert(message);
      fetchTodayAttendance();
      fetchLatestAttendance();
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  };

  // 休憩開始時刻登録イベント
  const breaktimeStartSubmit = async () => {
    try {
      setIsLoading(true);
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/breaktime`, {
        method: "POST",
        headers: { "Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}` },
        body: JSON.stringify({
          userId: userId,
          date: latestAttendance.date,
          minute: minute === "" ? 0 : parseInt(minute),
        }),
      });
      if (!response.ok) {
        const errorResponse = await response.json();
        handleApiError(errorResponse);
        return;
      }
      const message = await response.text();
      alert(message);
      fetchLatestBreaktime();
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  };

  // 休憩終了時刻登録イベント
  const breaktimeEndSubmit = async () => {
    try {
      setIsLoading(true);
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/breaktime`, {
        method: "PUT",
        headers: { "Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}` },
        body: JSON.stringify({
          userId: userId,
          date: latestAttendance.date,
        }),
      });
      if (!response.ok) {
        const errorResponse = await response.json();
        handleApiError(errorResponse);
        return;
      }
      const message = await response.text();
      alert(message);
      fetchLatestBreaktime();
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  };

  // 現在日時の更新
  useEffect(() => {
    const timer = setInterval(() => setNow(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);
  
  // 当日の出勤情報を取得
  useEffect(() => {
    fetchTodayAttendance();
  }, [fetchTodayAttendance]);

  // 最新の出勤情報を取得
  useEffect(() => {
    fetchLatestAttendance();
  }, [fetchLatestAttendance]);

  // 最新の休憩情報を取得
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
              <div>出勤時刻:{!latestAttendance ?
                    !todayAttendance ? "未出勤" : "退勤済み"
                  : toYMDHMS(latestAttendance.startTime)}</div>
            </td>
          </tr>
          <tr>
            <td>
              {latestAttendance || todayAttendance ? (
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
              {!latestAttendance || latestBreaktime ? (
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
                  disabled={!latestAttendance || (latestAttendance && latestBreaktime)}
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
              {!latestAttendance || (latestAttendance && latestBreaktime) ? (
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
              {!latestAttendance || (latestAttendance && !latestBreaktime) ? (
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