import React, { useContext, useState, useEffect, useMemo } from "react";
import { AuthContext } from "../AuthContext";
import CommonDialog from "../components/CommonDialog";
import TableWithPaging from "../components/TableWithPaging";
import { isHalfWidthNumberAndAlpha, getUserIdFromToken } from "../utils";
import { handleApiError } from "../errorHandler";
import UsersRegister from "./register";

// 登録・修正ボタンコンポーネント
const RegistButton = ({ data, fecthData, handleDelete, stateHandlers }) => {
  const [dialogOpen, setDialogOpen] = useState(false);
  const handleClose = () => {
    setDialogOpen(false)
  }

  useEffect(() => {
    if (!dialogOpen) {
      fecthData();
    }
  }, [dialogOpen]);

  return (
    <>
      <button className="non-register-button"
        onClick={() => setDialogOpen(true)}>
        {!data ? "新規登録" : "修正"}
      </button>

      <CommonDialog
        open={dialogOpen}
        onClose={() => handleClose()}
        onDelete={(!data || data.id === getUserIdFromToken()) ? null : () => {
          handleDelete();
          setDialogOpen(false);
        }}
        title={!data ? "従業員登録(新規登録)" : `従業員登録(${data.id})`}
        content={<UsersRegister selectedId={!data ? null : data.id} handleClose={handleClose} stateHandlers={stateHandlers} />} />
    </>
  );
};

// 従業員管理コンポーネント
const UsersManagement = ({stateHandlers}) => {
  const { setError, setContentOnly, setIsLoading } = stateHandlers;
  useEffect(() => {
    setContentOnly(false);
  }, [setContentOnly]);

  const { authToken } = useContext(AuthContext);

  const [userId, setUserId] = useState(getUserIdFromToken());
  const [id, setId] = useState("");
  const [userName, setUserName] = useState("");
  const [usersData, setUsersData] = useState([]);

  // 検索イベント
  const searchSubmit = async () => {
    try {
      setIsLoading(true);
      const params = new URLSearchParams({
        id: id ? id : "",
        name: userName ? userName : "",
      });
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/manage/users?${params.toString()}`, {
          method: "GET",
          headers: {"Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}`}
        }
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
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  };

  // 削除処理
  const handleDelete = async (deleteId) => {
    try {
      setIsLoading(true);
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/manage/users`, {
          method: "DELETE",
          headers: {"Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}`},
          body: JSON.stringify({
            id: deleteId
          }),
        }
      );
      if (response.status === 204) {
        return;
      }
      if (!response.ok) {
        const errorResponse = await response.json();
        handleApiError(errorResponse);
        return;
      }
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  };

  // 初期検索の実行
  useEffect(() => {
    searchSubmit();
  }, []);

  const thCols = ["ID", "氏名", "メールアドレス", "権限", "修正"];
  const tdRows = useMemo(() => {
    return usersData.map((data, index) => (
      <tr key={index}>
        <td className="center">{data.id}</td>
        <td className="center">{data.name}</td>
        <td className="center email-cell">{data.email}</td>
        <td className="center">{data.role}</td>
        <td className="center">
          <RegistButton
            data={data}
            fecthData={searchSubmit}
            handleDelete={() => handleDelete(data.id)}
            stateHandlers={stateHandlers}
          />
        </td>
      </tr>
    ));
  }, [usersData]);
  
  return (
    <div>
      <h2>従業員管理</h2>
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
              <button className="non-register-button"
                onClick={searchSubmit}>検索</button>
            </td>
          </tr>
          <tr>
            <td colSpan="2">
              <RegistButton data={null} fecthData={searchSubmit} stateHandlers={stateHandlers} />
            </td>
          </tr>
        </tbody>
      </table>
      <TableWithPaging thCols={thCols} tdRows={tdRows} />
    </div>
  );
};

export default UsersManagement;