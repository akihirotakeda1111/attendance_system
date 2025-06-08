import React, { useContext, useState, useEffect } from "react";
import { AuthContext } from "../AuthContext";
import { AppSettingsContext } from "../AppSettingsContext";
import { handleApiError } from "../errorHandler";
import Message from "../components/Message";
import { isHalfWidthNumberAndAlpha, isPassword, isEmailaddress, getUserIdFromToken } from "../utils";

// 登録コンポーネント
const UsersRegister = ({ selectedId, handleClose, stateHandlers }) => {
  const { setError, setContentOnly, setIsLoading } = stateHandlers;
  const { inputMaxLength } = useContext(AppSettingsContext);
  const { authToken } = useContext(AuthContext);

  const [userId, setUserId] = useState(getUserIdFromToken());
  const [id, setId] = useState(selectedId ? selectedId : "");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [role, setRole] = useState("00");
  const [roles, setRoles] = useState([]);
  const [errorMessage, setErrorMessage] = useState("");

  // 入力項目の既存データ取得イベント
  const getInputData = async () => {
    try {
      setIsLoading(true);
      const params = new URLSearchParams({
        id: id,
      });
      const reaponse = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/manage/users/id?${params.toString()}`, {
          method: "GET",
          headers: {"Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}`}
        }
      );
      if (reaponse.status === 204) {
        return;
      }
      if (!reaponse.ok) {
        const errorResponse = await reaponse.json();
        handleApiError(errorResponse);
        return;
      }
      const data = await reaponse.json();
      setId(data.id);
      setPassword(data.password);
      setName(data.name);
      setEmail(data.email);
      setRole(data.role);
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  };

  // 登録イベント
  const registSubmit = async () => {
    try {
      setIsLoading(true);
      // 新規登録時はIDの重複チェック
      if (!selectedId) {
        const params = new URLSearchParams({
          id: id,
        });
        const reaponse = await fetch(
          `${process.env.REACT_APP_API_BASE_URL}/manage/users/id?${params.toString()}`, {
            method: "GET",
            headers: {"Content-Type": "application/json",
              "Authorization": `Bearer ${authToken}`}
          }
        );
        if (reaponse.status !== 204) {
          alert("入力されたIDはすでに登録されています");
          return;
        }
      }

      // 登録処理
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/manage/users`, {
        method: "POST",
        headers: { "Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}` },
        body: JSON.stringify({
          id: id,
          password: password,
          name: name,
          email: email,
          role: role,
        }),
      });
      if (!response.ok) {
        const errorMessage = await response.text();
        alert(`Error: ${errorMessage}`);
        return;
      }
      const message = await response.text();
      alert(message);
      handleClose();
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  };

  // 初期データ取得
  useEffect(() => {
    getInputData();
  }, []);

  // 権限ドロップダウンの作成
  useEffect(() => {
    try {
      setIsLoading(true);
      fetch(`${process.env.REACT_APP_API_BASE_URL}/master/roles`, {
          method: "GET",
          headers: {"Content-Type": "application/json",
            "Authorization": `Bearer ${authToken}`}
        })
        .then(res => res.json())
        .then(data => setRoles(data));
    } catch(error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  // 入力チェック
  useEffect(() => {
    let errorMessages = [];
    let itemName = "";

    itemName = "ID";
    if (!id) {
      errorMessages.push(<Message type="isRiquired" item1={itemName} />);
    } else if (id.length > inputMaxLength.id) {
      errorMessages.push(<Message type="isOverLength" item1={itemName} item2={inputMaxLength.id}/>);
    } else if (!isHalfWidthNumberAndAlpha(id)) {
      errorMessages.push(<Message type="isHalfWidthNumberAndAlpha" item1={itemName} />);
    }

    itemName = "パスワード";
    if (!password) {
      errorMessages.push(<Message type="isRiquired" item1={itemName} />);
    } else if (password.length > inputMaxLength.password) {
      errorMessages.push(<Message type="isOverLength" item1={itemName} item2={inputMaxLength.password} />);
    } else if (!isPassword(password)) {
      errorMessages.push(<Message type="isPassword" item1={itemName} />);
    }

    itemName = "氏名";
    if (!name) {
      errorMessages.push(<Message type="isRiquired" item1={itemName} />);
    } else if (name.length > inputMaxLength.name) {
      errorMessages.push(<Message type="isOverLength" item1={itemName} item2={inputMaxLength.name} />);
    }

    itemName = "メールアドレス";
    if (!email) {
      errorMessages.push(<Message type="isRiquired" item1={itemName} />);
    } else if (email.length > inputMaxLength.email) {
      errorMessages.push(<Message type="isOverLength" item1={itemName} item2={inputMaxLength.email} />);
    } else if (!isEmailaddress(email)) {
      errorMessages.push(<Message type="isEmailaddress" item1={itemName} />);
    }

    itemName = "権限";
    if (!role) {
      errorMessages.push(<Message type="isRiquired" item1={itemName} />);
    }

    setErrorMessage(errorMessages);
  }, [id, password, name, email, role]);

  return (
    <div>
      <h2>従業員情報</h2>
      <table className="table-layout table-regist none-border">
        <tbody>
          <tr>
            <th>ID</th>
            <td>
              {selectedId ? selectedId : (
                <input
                  type="text"
                  placeholder="半角英数字で入力"
                  value={id}
                  maxLength={inputMaxLength.id}
                  onChange={(e) => {
                    const value = e.target.value;
                    if (isHalfWidthNumberAndAlpha(value) || value === "") {
                      setId(value);
                    }
                  }}
                />
              )}
            </td>
            <th>パスワード</th>
            <td>
              <input
                type="password"
                placeholder="半角英数字と記号で入力"
                value={password}
                maxLength={inputMaxLength.password}
                onChange={(e) => {
                  const value = e.target.value;
                  if (isPassword(value) || value === "") {
                    setPassword(value);
                  }
                }}
              />
            </td>
          </tr>
          <tr>
            <th>氏名</th>
            <td>
              <input
                type="text"
                value={name}
                maxLength={inputMaxLength.name}
                onChange={(e) => setName(e.target.value)}
              />
            </td>
            <th>メールアドレス</th>
            <td>
              <input
                type="text"
                placeholder="emailの形式で入力"
                value={email}
                maxLength={inputMaxLength.email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </td>
          </tr>
          <tr>
            <th>権限</th>
            <td>
              <select
                value={role}
                onChange={(e) => setRole(e.target.value)}>
                {roles.map((role) => (
                  <option key={role.itemCode} value={role.itemCode}>
                    {role.name}
                  </option>
                ))}
              </select>
            </td>
          </tr>
        </tbody>
      </table>
      {(Array.isArray(errorMessage) && errorMessage.length > 0) && (
        <div className="error-box">
          <ul>
            {errorMessage.map((message, index) => (
              message ? (
                <li key={index}>
                  {message}
                </li>
              ) : null
            ))}
          </ul>
        </div>
      )}
      {(Array.isArray(errorMessage) && errorMessage.length > 0) ? (
        <span className="register-button-disabled width-100">
          登録
        </span>
      ) : (
        <button
          className="register-button width-100"
          onClick={registSubmit}>
          登録
        </button>
      )}
    </div>
  );
};

export default UsersRegister;