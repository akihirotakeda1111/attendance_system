package com.example.attendance_system.dto;

import lombok.Data;

@Data
public class AttendanceTotalizationRequest {
    private boolean monthly;

    private boolean weekly;

    private String year;

    private String month;

    private String userId;
}
