package com.example.attendance_system.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String category;     // エラー分類
    private int errorCode;       // エラーコード
    private String status;       // HTTPステータス名
    private String message;      // エラーメッセージ
    private LocalDateTime timestamp; // 発生時刻

    public ErrorResponse(ErrorType errorType, String message) {
        this.category = errorType.getCategory();
        this.errorCode = errorType.getErrorCode();
        this.status = errorType.getStatus();
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getCategory() { return category; }
    public int getErrorCode() { return errorCode; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}