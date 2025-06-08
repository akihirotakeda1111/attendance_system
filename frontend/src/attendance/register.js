import React, { useContext, useState, useEffect } from "react";
import { AuthContext } from "../AuthContext";
import { YearDropdown, MonthDropdown, DayDropdown, HourDropdown, MinuteDropdown } from "../components/Dropdown";
import { toRegistDateStr, isOverlappingPeriod, isPartOverlappingPeriod, isStartToEnd, getUserIdFromToken } from "../utils";
import { handleApiError } from "../errorHandler";
import Message from "../components/Message";

// 休憩情報の入力コンポーネント
const InputBreaktime = ({ breaktimes, setBreaktimes }) => {
  const setStartDate = (index, updatedDate) => {
    setBreaktimes(prev =>
      prev.map((b, i) =>
        i === index ? { ...b, startDate: updatedDate } : b
      )
    );
  };

  const setEndDate = (index, updatedDate) => {
    setBreaktimes(prev =>
      prev.map((b, i) =>
        i === index ? { ...b, endDate: updatedDate } : b
      )
    );
  };
  
  return (
    <>
      {Array.isArray(breaktimes) && breaktimes.length > 0 ? breaktimes.map((b, index) => (
        <tr key={index}>
          <th>
            開始時刻
          </th>
          <td>
            <span className="pr-20">
              <YearDropdown value={b.startDate.year}
                onChange={e => setStartDate(index, b.startDate.changeYear(e.target.value))} />
              年
              <MonthDropdown value={b.startDate.month}
                onChange={e => setStartDate(index, b.startDate.changeMonth(e.target.value))} />
              月
              <DayDropdown value={b.startDate.day}
                year={b.startDate.year}
                month={b.startDate.month}
                onChange={e => setStartDate(index, b.startDate.changeDay(e.target.value))} />
              日
            </span>
            <span>
              <HourDropdown value={b.startDate.hour}
                onChange={e => setStartDate(index, b.startDate.changeHour(e.target.value))} />
              ：
              <MinuteDropdown value={b.startDate.minute}
                onChange={e => setStartDate(index, b.startDate.changeMinute(e.target.value))} />
            </span>
          </td>
          <th>
            終了時刻
          </th>
          <td>
            <span className="pr-20">
              <YearDropdown value={b.endDate.year}
                onChange={e => setEndDate(index, b.endDate.changeYear(e.target.value))} />
              年
              <MonthDropdown value={b.endDate.month}
                onChange={e => setEndDate(index, b.endDate.changeMonth(e.target.value))} />
              月
              <DayDropdown value={b.endDate.day}
                year={b.endDate.year}
                month={b.endDate.month}
                onChange={e => setEndDate(index, b.endDate.changeDay(e.target.value))} />
              日
            </span>
            <span>
              <HourDropdown value={b.endDate.hour}
                onChange={e => setEndDate(index, b.endDate.changeHour(e.target.value))} />
              ：
              <MinuteDropdown value={b.endDate.minute}
                onChange={e => setEndDate(index, b.endDate.changeMinute(e.target.value))} />
            </span>
          </td>
          <td colSpan="4" className="right">
            <button className="danger-button"
              onClick={() => setBreaktimes(breaktimes.filter((_, i) => i !== index))}>
                  削除
            </button>
          </td>
        </tr>
      )) : (
        <tr>
          <td colSpan="4">
            <Message type="noData" />
          </td>
        </tr>
      )}
    </>
  );
};

// 勤務登録コンポーネント
const AttendanceRegister = ({ date, selectedUserId, handleClose, stateHandlers }) => {
  class DateValue {
    constructor(date) {
      this.year = date.getFullYear();
      this.month = date.getMonth() + 1;
      this.day = date.getDate();
      this.hour = date.getHours();
      this.minute = date.getMinutes();
    }

    changeYear(year) { return new DateValue(new Date(year, this.month - 1, this.day, this.hour, this.minute)); }
    changeMonth(month) { return new DateValue(new Date(this.year, month - 1, this.day, this.hour, this.minute)); }
    changeDay(day) { return new DateValue(new Date(this.year, this.month - 1, day, this.hour, this.minute)); }
    changeHour(hour) { return new DateValue(new Date(this.year, this.month - 1, this.day, hour, this.minute)); }
    changeMinute(minute) { return new DateValue(new Date(this.year, this.month - 1, this.day, this.hour, minute)); }

    toString() { return toRegistDateStr(this.year, this.month, this.day, this.hour, this.minute); }
  }

  const { setError, setContentOnly, setIsLoading } = stateHandlers;
  const { authToken } = useContext(AuthContext);
  
  const now = date ? new Date(date) : new Date();

  const [userId, setUserId] = useState(getUserIdFromToken());
  const [attendanceStartDate, setAttendanceStartDate] = useState(new DateValue(now));
  const [attendanceEndDate, setAttendanceEndDate] = useState(new DateValue(now));
  const [breaktimes, setBreaktimes] = useState([]);
  const [errorMessage, setErrorMessage] = useState("");

  // 入力項目の既存データ取得イベント
  const getInputData = async () => {
    try {
      setIsLoading(true);
      const params = new URLSearchParams({
        date: date,
        userId: selectedUserId
      });
      const responseAttendance = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/manage/attendance/date?${params.toString()}`, {
          method: "GET",
          headers: {"Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}`}
        }
      );
      if (responseAttendance.status === 204) {
        return;
      }
      if (!responseAttendance.ok) {
        const errorResponse = await responseAttendance.json();
        handleApiError(errorResponse);
        return;
      }
      const attendanceData = await responseAttendance.json();
      setAttendanceStartDate(new DateValue(new Date(attendanceData.startTime)))
      setAttendanceEndDate(new DateValue(
        attendanceData.endTime ? new Date(attendanceData.endTime) : new Date(now)))

      const responseBreaktime = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/manage/breaktime/date?${params.toString()}`, {
          method: "GET",
          headers: {"Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}`}
        }
      );
      if (responseBreaktime.status === 204) {
        setBreaktimes([]);
        return;
      }
      if (!responseBreaktime.ok) {
        const errorResponse = await responseBreaktime.json();
        handleApiError(errorResponse);
        setBreaktimes([]);
        return;
      }
      
      const breaktimeData = await responseBreaktime.json();
      let tmpBreaktimeData = breaktimeData.map(b => ({
        startDate: new DateValue(new Date(b.startTime)),
        endDate: new DateValue(new Date(
          b.endTime ? new Date(b.endTime) : new Date(now)))
      }));
      setBreaktimes(tmpBreaktimeData);
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  };

  // 登録イベント
  const registSubmit = async () => {
    try {
      setIsLoading(true);
      const attendansResponse = await fetch(`${process.env.REACT_APP_API_BASE_URL}/manage/attendance`, {
        method: "POST",
        headers: { "Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}` },
        body: JSON.stringify({
          userId: selectedUserId,
          date: date,
          startTime: attendanceStartDate.toString(),
          endTime: attendanceEndDate.toString(),
        }),
      });
      if (!attendansResponse.ok) {
        const errorMessage = await attendansResponse.text();
        alert(`Error: ${errorMessage}`);
        return;
      }
      const attendanceMessage = await attendansResponse.text();
      alert(attendanceMessage);

      const breaktimeRequest = Array.isArray(breaktimes) && breaktimes.length > 0 ?
        breaktimes.map((b, i) => ({
          userId: selectedUserId,
          date: date,
          number: i+1,
          startTime: b.startDate.toString(),
          endTime: b.endDate.toString(),
          expectedEndTime: null
        })) : [
          {
            userId: selectedUserId,
            date: date,
            number: 0,
            startTime: null,
            endTime: null,
            expectedEndTime: null
          }
        ];
      const breaktimeResponse = await fetch(`${process.env.REACT_APP_API_BASE_URL}/manage/breaktime`, {
        method: "POST",
        headers: { "Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}` },
        body: JSON.stringify(breaktimeRequest),
      });
      if (!breaktimeResponse.ok) {
        const errorMessage = await breaktimeResponse.text();
        alert(`Error: ${errorMessage}`);
        return;
      }
      const breaktimeMessage = await breaktimeResponse.text();
      alert(breaktimeMessage);
      handleClose();
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  };

  // 初期データ取得
  useEffect(() => {
    getInputData();
  }, []);

  // 入力チェック
  useEffect(() => {
    // 前の入力エラーが解消されるまで、次の入力チェックは行わない
    let errorMessages = [];

    let isAttendanceValid = false;
    if (!isStartToEnd(attendanceStartDate.toString(), attendanceEndDate.toString())) {
      errorMessages.push(<Message type="isStartToEnd" item1="出勤時刻" item2="退勤時刻" />);
      isAttendanceValid = true;
    }

    if (!isAttendanceValid && Array.isArray(breaktimes) && breaktimes.length > 0) {
      let isBreaktimeValid = false;
      breaktimes.forEach((b, index) => {
        if (!isStartToEnd(b.startDate.toString(), b.endDate.toString())) {
          errorMessages.push(<Message type="isStartToEnd" item1={`開始時刻(${index+1}行目)`} item2={`終了時刻(${index+1}行目)`} />);
          isBreaktimeValid = true;
        }
        
        if (!isBreaktimeValid) {
          if (!isOverlappingPeriod(attendanceStartDate.toString(), attendanceEndDate.toString(), b.startDate.toString(), b.endDate.toString())) {
            errorMessages.push(<Message type="isNotOverlappingPeriod" item1={`離席・休憩時間(${index+1}行目)`} item2="出退勤時間" />);
            isBreaktimeValid = true;
          }
          for (const [index2, b2] of breaktimes.entries()) {
            if ((index !== index2) && isPartOverlappingPeriod(b.startDate.toString(), b.endDate.toString(), b2.startDate.toString(), b2.endDate.toString())) {
              errorMessages.push(<Message type="isOverlappingPeriod" item1={`離席・休憩時間(${index+1}行目)`} item2={`離席・休憩時間(${index2+1}行目)`} />);
              break;
            }
          }
        }
      });
    }
    setErrorMessage(errorMessages);
  }, [breaktimes, attendanceStartDate, attendanceEndDate]);

  return (
    <div>
      <h2>出退勤情報</h2>
      <table className="table-layout table-regist none-border">
        <tbody>
          <tr>
            <th>
              出勤時刻
            </th>
            <td>
              <span className="pr-20">
                <YearDropdown value={attendanceStartDate.year}
                  onChange={e => setAttendanceStartDate(attendanceStartDate.changeYear(e.target.value))} />
                年
                <MonthDropdown value={attendanceStartDate.month}
                  onChange={e => setAttendanceStartDate(attendanceStartDate.changeMonth(e.target.value))} />
                月
                <DayDropdown value={attendanceStartDate.day}
                  year={attendanceStartDate.year}
                  month={attendanceStartDate.month}
                  onChange={e => setAttendanceStartDate(attendanceStartDate.changeDay(e.target.value))} />
                日
              </span>
              <span>
                <HourDropdown value={attendanceStartDate.hour}
                  onChange={e => setAttendanceStartDate(attendanceStartDate.changeHour(e.target.value))} />
                ：
                <MinuteDropdown value={attendanceStartDate.minute}
                  onChange={e => setAttendanceStartDate(attendanceStartDate.changeMinute(e.target.value))} />
              </span>
            </td>
            <th>
              退勤時刻
            </th>
            <td>
              <span className="pr-20">
                <YearDropdown value={attendanceEndDate.year}
                  onChange={e => setAttendanceEndDate(attendanceEndDate.changeYear(e.target.value))} />
                年
                <MonthDropdown value={attendanceEndDate.month}
                  onChange={e => setAttendanceEndDate(attendanceEndDate.changeMonth(e.target.value))} />
                月
                <DayDropdown value={attendanceEndDate.day}
                  year={attendanceEndDate.year}
                  month={attendanceEndDate.month}
                  onChange={e => setAttendanceEndDate(attendanceEndDate.changeDay(e.target.value))} />
                日
              </span>
              <span>
                <HourDropdown value={attendanceEndDate.hour}
                  onChange={e => setAttendanceEndDate(attendanceEndDate.changeHour(e.target.value))} />
                ：
                <MinuteDropdown value={attendanceEndDate.minute}
                  onChange={e => setAttendanceEndDate(attendanceEndDate.changeMinute(e.target.value))} />
              </span>
            </td>
          </tr>
        </tbody>
      </table>
      <h2>離席・休憩情報</h2>
      <table className="table-layout table-regist none-border">
        <tbody>
          <InputBreaktime breaktimes={breaktimes} setBreaktimes={setBreaktimes} />
          <tr>
            <td colSpan="4" className="right">
              <button className="non-register-button"
                onClick={() => setBreaktimes(
                  [...breaktimes, {
                    startDate: new DateValue(new Date(date)),
                    endDate: new DateValue(new Date(date)) }
                  ])}>
                    追加
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      {(Array.isArray(errorMessage) && errorMessage.length > 0) && (
        <div className="error-box">
          <ul>
            {errorMessage.map((message, index) => (
              message ? (
                <li key={index}>
                  {message}
                </li>
              ) : null
            ))}
          </ul>
        </div>
      )}
      {(Array.isArray(errorMessage) && errorMessage.length > 0) ? (
        <span className="register-button-disabled width-100">
          登録
        </span>
      ) : (
        <button
          className="register-button width-100"
          onClick={registSubmit}>
          登録
        </button>
      )}
    </div>
  );
};

export default AttendanceRegister;