import React from "react";

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