import React from "react";

const messages = {
  noData: "データが見つかりませんでした",
  isNotOverlappingPeriod: (item1, item2) => `${item1}は${item2}の範囲内で入力してください`,
  isStartToEnd: (item1, item2) => `${item2}は${item1}より後の日時を入力してください`,
};

const Message = ({ type, item1, item2 }) => (
  <div className="error-message">
    {typeof messages[type] === "function" ? messages[type](item1, item2) : messages[type]}
  </div>
);


export default Message;