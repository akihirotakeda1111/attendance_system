import React from "react";

const messages = {
  noData: "データが見つかりませんでした"
};

const Message = ({ type = "noData" }) => (
  <div className="message">{messages[type]}</div>
);

export default Message;