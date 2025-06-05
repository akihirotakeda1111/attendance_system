package com.example.attendance_system.exception;

public class ErrorResponse {
    private String category;
    private int errorCode;
    private String status;
    private String message;
    //private LocalDateTime timestamp;
    private String displayMessage;

    public ErrorResponse(ErrorType errorType, String message, String displayMessage) {
        this.category = errorType.getCategory();
        this.errorCode = errorType.getErrorCode();
        this.status = errorType.getStatus();
        this.message = message;
        //this.timestamp = LocalDateTime.now();
        this.displayMessage = displayMessage;
    }

    public String getCategory() { return category; }
    public int getErrorCode() { return errorCode; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    //public LocalDateTime getTimestamp() { return timestamp; }
    public String getDisplayMessage() { return displayMessage; }
}