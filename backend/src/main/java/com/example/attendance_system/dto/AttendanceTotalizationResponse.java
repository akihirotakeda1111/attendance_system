package com.example.attendance_system.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AttendanceTotalizationResponse {
    private String userId;
    
    private String date;

    private BigDecimal hours;

    public AttendanceTotalizationResponse(String userId, String date, BigDecimal hours) {
        this.userId = userId;
        this.date = date;
        this.hours = hours;
    }
}
