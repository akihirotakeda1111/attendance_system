import { getWeek } from "date-fns";
import { jwtDecode } from "jwt-decode";

/* 取得関数 */
// tokenから権限を取得
export const getRoleFromToken = () => {
  const token = localStorage.getItem("authToken");
  if (!token || token === "null") {
    return "00";
  } else {
    const decoded = jwtDecode(token);
    return decoded.roles[0] || "00";
  }
};

// tokenからIDを取得
export const getUserIdFromToken = () => {
  const token = localStorage.getItem("authToken");
  if (!token || token === "null") {
    return null;
  } else {
    const decoded = jwtDecode(token);
    return decoded.sub || null;
  }
};

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

// 月の日付を取得する
export function getDaysInMonth(year, month) {
  const days = new Set();
  const firstDay = new Date(year, month - 1, 1);
  const lastDay = new Date(year, month, 0);

  for (let d = new Date(firstDay); d <= lastDay; d.setDate(d.getDate() + 1)) {
    days.add(String(d.getDate()).padStart(2, "0"));
  }
  return Array.from(days);
}

// 経過時間を計算する
export function getDiffHours(dateStr1, dateStr2) {
  const date1 = isISOTimeStr(dateStr1) ? new Date(dateStr1) : null;
  const date2 = isISOTimeStr(dateStr2) ? new Date(dateStr2) : null;
  let retVal = 0;
  if (date1 instanceof Date && date2 instanceof Date) {
    const diffMs = date2.getTime() - date1.getTime();
    retVal = diffMs / (1000 * 60 * 60);
  }
  return retVal;
}

/* 変換関数 */
// 日付を登録形式の文字列に変換する
export function toRegistDateStr(year, month, day, hour, minute) {
    return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}T${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}:00`;
}

// ISO形式を"yyyy-mm-dd hh:mm:ss"に変換する
export function toYMDHMS(dateTimeStr) {
    let retVal = dateTimeStr;
    if (dateTimeStr && isISOTimeStr(dateTimeStr)) {
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

/* 入力チェック関数 */
// ISO形式の日付文字か判定する
export function isISOTimeStr(dateTimeStr) {
    return /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/.test(dateTimeStr);
}

// 半角数字か判定する
export function isHalfWidthNumber(str) {
    return /^[0-9]+$/.test(str);
}

// 半角英数字か判定する
export function isHalfWidthNumberAndAlpha(str) {
    return /^[a-zA-Z0-9]+$/.test(str);
}

// パスワードの形式が判定する
export function isPassword(str) {
    return /^[a-zA-Z\d@$!%*?&]+$/.test(str);
}

// メールアドレスの形式か判定する
export function isEmailaddress(str) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(str);
}

// 2つの期間が完全に重なっているか判定する
export function isOverlappingPeriod(start1, end1, start2, end2) {
  let isOverlappingPeriod = false;

  const startDate1 = isISOTimeStr(start1) ? new Date(start1) : null;
  const endDate1 = isISOTimeStr(end1) ? new Date(end1) : null;
  const startDate2 = isISOTimeStr(start2) ? new Date(start2) : null;
  const endDate2 = isISOTimeStr(end2) ? new Date(end2) : null;

  if (startDate1 && endDate1 && startDate2 && endDate2) {
    if (startDate1 <= startDate2 && endDate1 >= startDate2
        && startDate1 <= endDate2 && endDate1 >= endDate2
    ) {
      isOverlappingPeriod = true;
    }
  }
  return isOverlappingPeriod;
}

// 2つの期間の一部が重なっているか判定する
export function isPartOverlappingPeriod(start1, end1, start2, end2) {
  let isPartOverlappingPeriod = false;

  const startDate1 = isISOTimeStr(start1) ? new Date(start1) : null;
  const endDate1 = isISOTimeStr(end1) ? new Date(end1) : null;
  const startDate2 = isISOTimeStr(start2) ? new Date(start2) : null;
  const endDate2 = isISOTimeStr(end2) ? new Date(end2) : null;

  if (startDate1 && endDate1 && startDate2 && endDate2) {
    if ((startDate1 > startDate2 && startDate1 < endDate2)
        || (endDate1 > startDate2 && endDate1 < endDate2)
    ) {
      isPartOverlappingPeriod = true;
    }
  }
  return isPartOverlappingPeriod;
}

// 2つの日時の前後関係を判定する
export function isStartToEnd(dateStr1, dateStr2) {
  let isStartToEnd = false;

  const date1 = isISOTimeStr(dateStr1) ? new Date(dateStr1) : null;
  const date2 = isISOTimeStr(dateStr2) ? new Date(dateStr2) : null;
  if (date1 instanceof Date && date2 instanceof Date) {
    isStartToEnd = date1 < date2;
  }
  return isStartToEnd;
}

/* 出力関数 */
export function exportToCSV(headers, data, filename) {
  const csvContent = [
    headers,
    ...data.map(row => headers.map(header => row[header])),
  ]
  .map(row => row.join(","))
  .join("\n");

  const blob = new Blob([csvContent], { type: "text/csv" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}