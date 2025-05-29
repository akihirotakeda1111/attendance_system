import React from "react";
import { getDaysInMonth } from "../utils";

// 年のドロップダウン
export const YearDropdown = ({ value, onChange, start = 1950, end, ...props }) => {
    const endYear =　new Date().getFullYear();
    return (
        <select value={value} onChange={onChange} {...props}>
        {Array.from({ length: endYear - start + 1 }, (_, i) => {
            const year = String(start + i);
            return (
            <option key={year} value={year}>
                {year}
            </option>
            );
        })}
        </select>
    )
};

// 月のドロップダウン
export const MonthDropdown = ({ value, onChange, ...props }) => (
  <select value={value} onChange={onChange} {...props}>
    {Array.from({ length: 12 }, (_, i) => {
      const month = String(i + 1);
      return (
        <option key={month} value={month}>
          {month}
        </option>
      );
    })}
  </select>
);

// 日のドロップダウン
export const DayDropdown = ({ value, year, month, onChange, ...props }) => (
  <select value={value} onChange={onChange} {...props}>
    {Array.from({ length: getDaysInMonth(year, month).length }, (_, i) => {
      const day = String(i + 1);
      return (
        <option key={day} value={day}>
          {day}
        </option>
      );
    })}
  </select>
);

// 時のドロップダウン
export const HourDropdown = ({ value, onChange, ...props }) => (
  <select value={value} onChange={onChange} {...props}>
    {Array.from({ length: 24 }, (_, i) => {
      const hour = String(i);
      return (
        <option key={hour} value={hour}>
          {hour}
        </option>
      );
    })}
  </select>
);

// 分のドロップダウン
export const MinuteDropdown = ({ value, onChange, ...props }) => (
  <select value={value} onChange={onChange} {...props}>
    {Array.from({ length: 60 }, (_, i) => {
      const minute = String(i);
      return (
        <option key={minute} value={minute}>
          {minute}
        </option>
      );
    })}
  </select>
);

const Dropdown = ({ value, onChange, options, label, ...props }) => (
  <label>
    {label}
    <select value={value} onChange={onChange} {...props}>
      {options.map(opt => (
        <option key={opt.value} value={opt.value}>
          {opt.label}
        </option>
      ))}
    </select>
  </label>
);

export default Dropdown;