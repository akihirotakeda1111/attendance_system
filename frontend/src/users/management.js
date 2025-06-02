import React, { useState, useEffect, useCallback } from "react";
import CommonDialog from "../components/CommonDialog";
import Message from "../components/Message";
import { isHalfWidthNumberAndAlpha } from "../utils";
import { handleApiError } from "../errorHandler";
//import UsersRegister from "./register";

// 登録・修正ボタンコンポーネント
const RegistButton = ({ data, fecthData }) => {
  const [dialogOpen, setDialogOpen] = useState(false);
  const handleClose = () => {
    setDialogOpen(false)
    fecthData();
  }

  return (
    <>
      <button className="register-button"
        onClick={() => setDialogOpen(true)}>
        {!data ? "新規登録" : "修正"}
      </button>

      <CommonDialog
        open={dialogOpen}
        onClose={() => handleClose()}
        title={!data ? "従業員登録(新規登録)" : `従業員登録(${data.id})`}
        content={<usersRegister id={!data ? null : data.id}/>} />
    </>
  );
};

// 従業員管理コンポーネント
const UsersManagement = () => {
  const [userId, setUserId] = useState("admin");
  const [id, setId] = useState("");
  const [userName, setUserName] = useState("");
  const [usersData, setUsersData] = useState([]);

  // 検索イベント
  const searchSubmit = async () => {
    const params = new URLSearchParams({
      id: id ? id : "",
      name: userName ? userName : "",
    });
    const response = await fetch(
      `${process.env.REACT_APP_API_BASE_URL}/manage/users?${params.toString()}`
    );
    if (response.status === 204) {
      setUsersData([]);
      return;
    }
    if (!response.ok) {
      const errorResponse = await response.json();
      handleApiError(errorResponse);
      setUsersData([]);
      return;
    }
    const data = await response.json();
    setUsersData(data);
  };

  // 初期検索の実行
  useEffect(() => {
    searchSubmit();
  }, []);

  return (
    <div>
      <h2>勤務管理</h2>
      <table className="table-layout none-border">
        <tbody>
          <tr>
            <td>
              <span className="pr-20">
                ID:
                <input
                  type="text"
                  placeholder="半角英数字で入力"
                  value={id}
                  maxLength={12}
                  onChange={(e) => {
                    const value = e.target.value;
                    if (isHalfWidthNumberAndAlpha(value) || value === "") {
                      setId(value);
                    }
                  }}
                />
              </span>
              <span>
                氏名:
                <input
                  type="text"
                  value={userName}
                  maxLength={16}
                  onChange={(e) => setUserName(e.target.value)}
                />
              </span>
            </td>
            <td>
              <button className="search-button"
                onClick={searchSubmit}>検索</button>
            </td>
          </tr>
          <tr>
            <td colSpan="2">
              <RegistButton data={null} fecthData={searchSubmit} />
            </td>
          </tr>
        </tbody>
      </table>
      <table className="table-layout">
        <tbody>
          <tr>
            <th className="center">ID</th>
            <th className="center">氏名</th>
            <th className="center">メールアドレス</th>
            <th className="center">権限</th>
            <th className="center">修正</th>
          </tr>
          {Array.isArray(usersData) && usersData.length > 0 ? (
            usersData.map((data, index) => (
              <tr key={index}>
                <td className="center">{data.id}</td>
                <td className="center">{data.name}</td>
                <td className="center">{data.email}</td>
                <td className="center">{data.role}</td>
                <td className="center"><RegistButton data={data} fecthData={searchSubmit} /></td>
              </tr>
            ))
          ) : (
            <tr>
              <td colspan="5">
                <Message type="noData" />
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default UsersManagement;