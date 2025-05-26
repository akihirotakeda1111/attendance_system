import React, { useState, useEffect, useCallback } from "react";
import { YearDropdown, MonthDropdown } from "../components/Dropdown";
import Message from "../components/Message";
import { getWeeksInMonth } from "../utils";
import { Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const AttendanceTotalization = () => {
  const now = new Date();
  const currentYear = String(now.getFullYear());
  const currentMonth = String(now.getMonth() + 1);

  const [userId, setUserId] = useState("admin");
  const [period, setPeriod] = useState("monthly");
  const [year, setYear] = useState(currentYear);
  const [month, setMonth] = useState(currentMonth);
  const [users, setUsers] = useState();
  const [workingData, setWorkingData] = useState();

  const searchSubmit = async () => {
    const params = new URLSearchParams({
      monthly: period === "monthly",
      weekly: period === "weekly",
      year: year,
      month: month,
      userId: userId,
    });
    const response = await fetch(
      `${process.env.REACT_APP_API_BASE_URL}/manage/totalization?${params.toString()}`
    );
    if (response.status === 404) {
      setWorkingData(null);
      return;
    }
    const data = await response.json();
    let tmpData = [];
    if (period === "monthly") {
      Array.from({ length: 12 }, (_, i) => i + 1).forEach(month => {
        const strYear = String(year) + "年";
        const strMonth = String(month) + "月";
        const monthData = data.find(item => item.date === year + String(month).padStart(2, "0"));
        if (monthData) {
          monthData.date = strYear + strMonth;
          monthData.hours = monthData.hours.toFixed(2);
          tmpData.push(monthData);
        } else {
          tmpData.push({ userId: userId, date: strYear + strMonth, hours: 0 });
        }
      });
    } else {
      const weekLabels = getWeeksInMonth(year, month);
      let weekNum = 1;
      weekLabels.forEach(yearWeek => {
        const strYear = String(year) + "年";
        const strMonth = String(month) + "月";
        const strWeekNum = String(weekNum) + "週";
        const weekData = data.find(item => item.date === yearWeek);
        if (weekData) {
          weekData.date = strYear + strMonth + strWeekNum;
          weekData.hours = weekData.hours.toFixed(2);
          tmpData.push(weekData);
        } else {
          tmpData.push({ userId: userId, date: strYear + strMonth + strWeekNum, hours: 0 });
        }
        weekNum++;
      });
    }
    setWorkingData(tmpData);
  };

  const barData = {
    labels: Array.isArray(workingData)
      ? workingData.map((item) => item.date)
      : [],
    datasets: [
      {
        label: "勤務時間",
        data: Array.isArray(workingData)
          ? workingData.map((item) => item.hours)
          : [],
        backgroundColor: "rgba(54, 162, 235, 0.6)",
      },
    ],
  };

  useEffect(() => {
    fetch(`${process.env.REACT_APP_API_BASE_URL}/users`)
      .then(res => res.json())
      .then(data => setUsers(data));
  }, []);

  useEffect(() => {
    searchSubmit();
  }, []);

  return (
    <div>
      <h2>勤務集計</h2>
      <table className="table-layout none-border">
        <tbody>
          <tr>
            <td colspan="2">
              <span className="pr-20">
                <label>
                  <input
                    type="radio"
                    name="period"
                    value="monthly"
                    checked={period === "monthly"}
                    onChange={() => setPeriod("monthly")}
                  />
                  月次
                </label>
                <label>
                  <input
                    type="radio"
                    name="period"
                    value="weekly"
                    checked={period === "weekly"}
                    onChange={() => setPeriod("weekly")}
                  />
                  週次
                </label>
              </span>
              <span>
                <YearDropdown value={year}
                  onChange={e => setYear(e.target.value)} />
                年
                <MonthDropdown value={month}
                  onChange={e => setMonth(e.target.value)} />
                月
              </span>
            </td>
          </tr>
          <tr>
            <td>
              ID(氏名):
              <select value={userId} onChange={e => setUserId(e.target.value)}>
                {users && users.map(user => (
                  <option key={user.id} value={user.id}>
                    {user.id} ({user.name})
                  </option>
                ))}
              </select>
            </td>
            <td>
              <button className="search-button"
                onClick={searchSubmit}>検索</button>
            </td>
          </tr>
        </tbody>
      </table>
      {Array.isArray(workingData) && workingData.length > 0 ? (
        <div style={{ maxWidth: 600 }}>
          <Bar data={barData} />
        </div>
      ) : <Message type="noData" />}
    </div>
  );
};

export default AttendanceTotalization;