package com.example.attendance_system.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException ex) {
        logger.warn("Input format error: {}", ex.getMessage(), ex);
        return new ErrorResponse(ErrorType.BAD_REQUEST, ex.getMessage(), "入力形式に誤りがあります");
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, 
                       MethodArgumentTypeMismatchException.class, 
                       MethodArgumentNotValidException.class,
                       ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(Exception ex) {
        logger.error("Fatal input error: {}", ex.getMessage(), ex);
        return new ErrorResponse(ErrorType.BAD_REQUEST, ex.getMessage(), "不正なリクエストです");
    }

    @ExceptionHandler({NotFoundException.class,
                        NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(NotFoundException ex) {
        logger.error("No resources: {}", ex.getMessage(), ex);
        return new ErrorResponse(ErrorType.NOT_FOUND, ex.getMessage(), "リソースが存在しません");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        logger.error("System error: {}", ex.getMessage(), ex);
        return new ErrorResponse(ErrorType.INTERNAL_SERVER_ERROR, ex.getMessage(), "システムエラーが発生しました");
    }

}