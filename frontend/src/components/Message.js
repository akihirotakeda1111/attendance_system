import React from "react";

const messages = {
  noData: "データが見つかりませんでした",
  isEmailaddress: (item1) => `${item1}はemail形式で入力してください`,
  isRiquired: (item1) => `${item1}を入力してください`,
  isOverLength: (item1, item2) => `${item1}は${item2}文字以内で入力してください`,
  isHalfWidthNumberAndAlpha: (item1) => `${item1}は半角英数字で入力してください`,
  isHalfWidthNumber: (item1) => `${item1}は半角数字で入力してください`,
  isPassword: (item1) => `${item1}は半角英数字と記号(\@$!%*?&)で入力してください`,
  isNotOverlappingPeriod: (item1, item2) => `${item1}は${item2}の範囲内で入力してください`,
  isOverlappingPeriod: (item1, item2) => `${item1}は${item2}と期間が重複しないように入力してください`,
  isStartToEnd: (item1, item2) => `${item2}は${item1}より後の日時を入力してください`,
};

const Message = ({ type, item1, item2 }) => (
  <div className="error-message">
    {typeof messages[type] === "function" ? messages[type](item1, item2) : messages[type]}
  </div>
);


export default Message;