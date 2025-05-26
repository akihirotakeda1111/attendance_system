import { getWeek } from "date-fns";

// ISO形式を"yyyy-mm-dd hh:mm:ss"に変換する
export function toYMDHMS(dateTimeStr) {
    let retVal = ""
    if (dateTimeStr && /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/.test(dateTimeStr)) {
        const date = new Date(dateTimeStr);
        const yyyy = date.getFullYear();
        const mm = String(date.getMonth() + 1).padStart(2, "0");
        const dd = String(date.getDate()).padStart(2, "0");
        const hh = String(date.getHours()).padStart(2, "0");
        const mi = String(date.getMinutes()).padStart(2, "0");
        const ss = String(date.getSeconds()).padStart(2, "0");
        retVal = `${yyyy}-${mm}-${dd} ${hh}:${mi}:${ss}`;
    }
    return retVal;
}

// 半角数字か判定する
export function isHalfWidthNumber(str) {
    return /^[0-9]+$/.test(str);
}

// 月の週番号を取得する
export function getWeeksInMonth(year, month) {
  const weeks = new Set();
  const firstDay = new Date(year, month - 1, 1);
  const lastDay = new Date(year, month, 0);

  for (let d = new Date(firstDay); d <= lastDay; d.setDate(d.getDate() + 1)) {
    const week = getWeek(d, { weekStartsOn: 1 }); // 月曜始まり
    weeks.add(`${d.getFullYear()}${String(week).padStart(2, "0")}`);
  }
  return Array.from(weeks);
}