package com.example.attendance_system.dto;

import lombok.Data;

@Data
public class BreaktimeRequest {
    private String date;

    private String userId;

    private int number;

    private String startTime;

    private String endTime;

    private int minute;

    private String expectedEndTime;
}
