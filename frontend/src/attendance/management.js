import React, { useContext, useState, useEffect } from "react";
import { AuthContext } from "../AuthContext";
import { YearDropdown, MonthDropdown } from "../components/Dropdown";
import CommonDialog from "../components/CommonDialog";
import Message from "../components/Message";
import { getDaysInMonth, toYMDHMS, getDiffHours, getUserIdFromToken, exportToCSV } from "../utils";
import { handleApiError } from "../errorHandler";
import AttendanceRegister from "./register";

// 休憩情報のセルコンポーネント
const DataCell = ({ breaktimeData, breaktimeHours }) => {
  const [dialogOpen, setDialogOpen] = useState(false);

  return (
    <>
      <a 
        href="javascript:void(0)" 
        onClick={(e) => {
          e.preventDefault();
          if (breaktimeData.length > 0) setDialogOpen(true);
        }}
        style={{
          cursor: breaktimeData.length > 0 ? "pointer" : "default",
          textDecoration: breaktimeData.length > 0 ? "underline" : "none",
          color: breaktimeData.length > 0 ? "blue" : "gray"
        }}
      >
        {breaktimeData.length > 0 ? `${breaktimeHours}h(${breaktimeData.length}件)` : "-"}
      </a>
      
      <CommonDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        title="休憩時間"
        content=
          {
            <ul>
              {breaktimeData.map((b, index) => (
                <li key={index}>{toYMDHMS(b.startTime)} - {b.endTime ? toYMDHMS(b.endTime) : "未終了"}</li>
              ))}
            </ul>
          }
      />
    </>
  );
};

// 修正・登録ボタンコンポーネント
const RegistButton = ({ data, selectedUserId, fecthData, handleDelete, stateHandlers }) => {
  const [dialogOpen, setDialogOpen] = useState(false);
  const handleClose = () => {
    setDialogOpen(false)
  }

  useEffect(() => {
    if (!dialogOpen) {
      fecthData();
    }
  }, [dialogOpen]);

  return (
    <>
      <button className="non-register-button"
        onClick={() => setDialogOpen(true)}>
        {data.startTime === "-" ? "登録" : "修正"}
      </button>

      <CommonDialog
        open={dialogOpen}
        onClose={() => handleClose()}
        onDelete={data.startTime === "-" ? null : () => {
          handleDelete();
          setDialogOpen(false);
        }}
        title={`勤務登録(${selectedUserId} ${data.date})`}
        content={<AttendanceRegister date={data.date} selectedUserId={selectedUserId}
          handleClose={handleClose} stateHandlers={stateHandlers} />} />
    </>
  );
};

// 勤務管理コンポーネント
const AttendanceManagement = ({stateHandlers}) => {
  const { setError, setContentOnly, setIsLoading } = stateHandlers;
  useEffect(() => {
    setContentOnly(false);
  }, [setContentOnly]);

  const { authToken } = useContext(AuthContext);

  const now = new Date();
  const currentYear = String(now.getFullYear());
  const currentMonth = String(now.getMonth() + 1);

  const [userId, setUserId] = useState(getUserIdFromToken());
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [year, setYear] = useState(currentYear);
  const [month, setMonth] = useState(currentMonth);
  const [users, setUsers] = useState();
  const [workingData, setWorkingData] = useState(getWorkingData(null, null, null));

  // 勤務データの取得関数
  function getWorkingData(attendanceData, breaktimeData, worktimeData) {
    const days = getDaysInMonth(year, month);
    const data = days.map(item => {
      const dateStr = `${year}-${String(month).padStart(2, "0")}-${String(item).padStart(2, "0")}`;
      let attendance = null;
      let breaktimes = null;
      let worktime = null;
      if (attendanceData) { attendance = attendanceData.filter(b => b.date === dateStr); }
      if (breaktimeData) { breaktimes = breaktimeData.filter(b => b.date === dateStr); }
      if (worktimeData) { worktime = worktimeData.filter(b => b.date === dateStr); }
      
      return {
        date: dateStr,
        userId: attendance && attendance.length > 0 ? attendance[0].userId : null,
        startTime: attendance && attendance.length > 0 ? attendance[0].startTime : "-",
        endTime: attendance && attendance.length > 0 ? attendance[0].endTime : "-",
        breaktimeData: breaktimes && breaktimes.length > 0 ? breaktimes : [],
        breaktimeHours: breaktimes && breaktimes.length > 0 ?
          breaktimes.reduce((sum, item) => sum + getDiffHours(item.startTime, item.endTime), 0).toFixed(2) : 0,
        workHours: worktime && worktime.length > 0 ? worktime[0].hours.toFixed(2) : "-",
      };
    });
    return data;
  }

  // 検索イベント
  const searchSubmit = async () => {
    setSelectedUserId(userId);

    try {
      setIsLoading(true);
      const params = new URLSearchParams({
        year: year,
        month: month,
        userId: userId,
      });
      const responseAttendance = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/manage/attendance?${params.toString()}`, {
          method: "GET",
          headers: {"Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}`}
        }
      );
      if (responseAttendance.status === 204) {
        setWorkingData(getWorkingData(null, null, null));
        return;
      }
      if (!responseAttendance.ok) {
        const errorResponse = await responseAttendance.json();
        handleApiError(errorResponse);
        setWorkingData(getWorkingData(null, null, null));
        return;
      }
      const attendanceData = await responseAttendance.json();

      const responseBreaktime = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/manage/breaktime?${params.toString()}`, {
          method: "GET",
          headers: {"Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}`}
        }
      );
      if (responseBreaktime.status === 204) {
        setWorkingData(getWorkingData(attendanceData, null, null));
        return;
      }
      if (!responseBreaktime.ok) {
        const errorResponse = await responseBreaktime.json();
        handleApiError(errorResponse);
        setWorkingData(getWorkingData(attendanceData, null, null));
        return;
      }
      const breaktimeData = await responseBreaktime.json();

      const worktimeParams = new URLSearchParams({
        monthly: false,
        weekly: false,
        year: year,
        month: month,
        userId: userId,
      });
      const worktimeResponse = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/manage/totalization?${worktimeParams.toString()}`, {
          method: "GET",
          headers: {"Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}`}
        }
      );
      if (worktimeResponse.status === 204) {
        setWorkingData(getWorkingData(attendanceData, breaktimeData, null));
        return;
      }
      if (!worktimeResponse.ok) {
        const errorResponse = await worktimeResponse.json();
        handleApiError(errorResponse);
        setWorkingData(getWorkingData(attendanceData, breaktimeData, null));
        return;
      }
      const workTimeData = await worktimeResponse.json();
      setWorkingData(getWorkingData(attendanceData, breaktimeData, workTimeData));
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  };

  // 削除処理
  const handleDelete = async (deleteId, deleteDate) => {
    try {
      setIsLoading(true);
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/manage/attendance`, {
          method: "DELETE",
          headers: {"Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}`},
          body: JSON.stringify({
            userId: deleteId,
            date: deleteDate
          }),
        }
      );
      if (response.status === 204) {
        return;
      }
      if (!response.ok) {
        const errorResponse = await response.json();
        handleApiError(errorResponse);
        return;
      }
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  };

  // ユーザードロップダウンの作成
  useEffect(() => {
    try {
      setIsLoading(true);
      fetch(`${process.env.REACT_APP_API_BASE_URL}/manage/users`, {
          method: "GET",
          headers: {"Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}`}
        })
        .then(res => res.json())
        .then(data => setUsers(data));
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  // 初期検索の実行
  useEffect(() => {
    searchSubmit();
  }, []);

  return (
    <div>
      <h2>勤務管理</h2>
      <table className="table-layout none-border">
        <tbody>
          <tr>
            <td>
              <span className="pr-20">
                <YearDropdown value={year}
                  onChange={e => setYear(e.target.value)} />
                年
                <MonthDropdown value={month}
                  onChange={e => setMonth(e.target.value)} />
                月
              </span>
              <span>
                ID(氏名):
                <select value={userId} onChange={e => setUserId(e.target.value)}>
                  {users && users.map(user => (
                    <option key={user.id} value={user.id}>
                      {user.id} ({user.name})
                    </option>
                  ))}
                </select>
              </span>
            </td>
            <td>
              <button className="non-register-button"
                onClick={searchSubmit}>検索</button>
            </td>
          </tr>
        </tbody>
      </table>
      {Array.isArray(workingData) && workingData.length > 0 ? (
        <div className="table-header">
          <button onClick={() => exportToCSV(
              ['date', 'startTime', 'endTime', 'breaktimeHours', 'workHours'],
              workingData, `${selectedUserId}_${year}-${month}_WorkingData`)}>
            CSV出力
          </button>
        </div>
      ) : null}
      <table className="table-layout">
        <tbody>
          <tr>
            <th className="center">日付</th>
            <th className="center">出勤</th>
            <th className="center">退勤</th>
            <th className="center">離席・休憩</th>
            <th className="center">稼働</th>
            <th className="center">登録・修正</th>
          </tr>
          {Array.isArray(workingData) && workingData.length > 0 ? (
            workingData.map((data, index) => (
              <tr key={index}>
                <td className="center">{data.date}</td>
                <td className="center">{toYMDHMS(data.startTime)}</td>
                <td className="center">{toYMDHMS(data.endTime)}</td>
                <td className="center">
                  <DataCell breaktimeData={data.breaktimeData}
                    breaktimeHours={data.breaktimeHours} />
                </td>
                <td className="center">{data.workHours}</td>
                <td className="center">
                  <RegistButton data={data}
                    selectedUserId={selectedUserId}
                    fecthData={searchSubmit}
                    handleDelete={() => handleDelete(data.userId, data.date)}
                    stateHandlers={stateHandlers} />
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="6">
                <Message type="noData" />
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default AttendanceManagement;