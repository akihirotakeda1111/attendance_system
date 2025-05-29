import React, { useState, useEffect, useCallback } from "react";
import { YearDropdown, MonthDropdown, DayDropdown, HourDropdown, MinuteDropdown } from "../components/Dropdown";
import { toRegistDateStr } from "../utils";
import Message from "../components/Message";

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
            <button className="search-button"
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

const AttendanceRegister = ({ date }) => {
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

  const now = date ? new Date(date) : new Date();

  const [userId, setUserId] = useState("admin");
  const [attendanceStartDate, setAttendanceStartDate] = useState(new DateValue(now));
  const [attendanceEndDate, setAttendanceEndDate] = useState(new DateValue(now));
  const [breaktimes, setBreaktimes] = useState([]);

  const getInputData = async () => {
    const params = new URLSearchParams({
      date: date,
      userId: userId
    });
    const responseAttendance = await fetch(
      `${process.env.REACT_APP_API_BASE_URL}/manage/attendance/date?${params.toString()}`
    );
    if (responseAttendance.status === 404) {
      return;
    }
    const attendanceData = await responseAttendance.json();
    setAttendanceStartDate(new DateValue(new Date(attendanceData.startTime)))
    setAttendanceEndDate(new DateValue(new Date(attendanceData.endTime)))

    const responseBreaktime = await fetch(
      `${process.env.REACT_APP_API_BASE_URL}/manage/breaktime/date?${params.toString()}`
    );
    if (responseBreaktime.status === 404) {
      setBreaktimes([]);
      return;
    }
    
    const breaktimeData = await responseBreaktime.json();
    let tmpBreaktimeData = breaktimeData.map(b => ({
      startDate: new DateValue(new Date(b.startTime)),
      endDate: new DateValue(new Date(b.endTime))
    }));
    setBreaktimes(tmpBreaktimeData);
  };

  const registAttendanceSubmit = async () => {
    const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/manage/attendance`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        userId: userId,
        date: date,
        startTime: attendanceStartDate.toString(),
        endTime: attendanceEndDate.toString(),
       }),
    });
    if (!response.ok) {
      const errorMessage = await response.text();
      alert(`Error: ${errorMessage}`);
      return;
    }
    const message = await response.text();
    alert(message);
  };

  const registBreaktimeSubmit = async () => {
    const request = breaktimes.map((b, i) => ({
      userId: userId,
      date: date,
      number: i+1,
      startTime: b.startDate.toString(),
      endTime: b.endDate.toString(),
      expectedEndTime: null
    }));
    const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/manage/breaktime`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(request),
    });
    if (!response.ok) {
      const errorMessage = await response.text();
      alert(`Error: ${errorMessage}`);
      return;
    }
    const message = await response.text();
    alert(message);
  };

  useEffect(() => {
      getInputData();
    }, []);

  return (
    <div>
      <h2>出退勤情報</h2>
      <table className="table-layout none-border">
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
      <button className="register-button width-100"
        onClick={registAttendanceSubmit}>
        勤務登録
      </button>
      <h2>離席・休憩情報</h2>
      <table className="table-layout none-border">
        <tbody>
          <InputBreaktime breaktimes={breaktimes} setBreaktimes={setBreaktimes} />
          <tr>
            <td colSpan="4" className="right">
              <button className="search-button"
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
      <button className="register-button width-100"
        onClick={registBreaktimeSubmit}>
        離席・休憩登録
      </button>
    </div>
  );
};

export default AttendanceRegister;