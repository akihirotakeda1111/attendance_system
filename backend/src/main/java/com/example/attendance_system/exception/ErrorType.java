package com.example.attendance_system.exception;

public enum ErrorType {
    OK("正常", 200, "OK"),
    NO_CONTENT("データなし", 204, "No Content"),
    BAD_REQUEST("不正なリクエスト", 400, "Bad Request"),
    UNAUTHORIZED("認証エラー", 401, "Unauthorized"),
    FORBIDDEN("アクセス権なし", 403, "Forbidden"),
    NOT_FOUND("リソースなし", 404, "Not Found"),
    INTERNAL_SERVER_ERROR("システム異常", 500, "Internal Server Error");

    private final String category;
    private final int errorCode;
    private final String status;

    ErrorType(String category, int errorCode, String status) {
        this.category = category;
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getCategory() { return category; }
    public int getErrorCode() { return errorCode; }
    public String getStatus() { return status; }
}