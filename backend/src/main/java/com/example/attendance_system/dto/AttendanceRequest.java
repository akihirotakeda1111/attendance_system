package com.example.attendance_system.dto;

import lombok.Data;

@Data
public class AttendanceRequest {
    private String userId;

    private String date;

    private String startTime;
    
    private String endTime;
}
