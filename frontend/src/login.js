import { useContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { AppSettingsContext } from "./AppSettingsContext";
import { AuthContext } from "./AuthContext";
import { handleApiError } from "./errorHandler";
import Message from "./components/Message";
import { isHalfWidthNumberAndAlpha, isPassword } from "./utils";

const Login = () => {
  const { inputMaxLength } = useContext(AppSettingsContext);
  const { setAuthToken } = useContext(AuthContext);

  const [id, setId] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        id: id,
        password: password,
      }),
    });

    if (!response.ok) {
      const errorResponse = await response.json();
      handleApiError(errorResponse);
      return;
    }

    const data = await response.json();
    setAuthToken(data.token);
    navigate("/attendance");
  };

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

    setErrorMessage(errorMessages);
  }, [id, password]);

  return (
    <div>
      <form onSubmit={handleSubmit}>
        <table className="table-layout table-regist none-border">
          <tbody>
            <tr>
              <th>ID</th>
              <td>
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
            ログイン
          </span>
        ) : (
          <button
            type="submit"
            className="register-button width-100">
            ログイン
          </button>
        )}
      </form>
    </div>
  );
};

export default Login;