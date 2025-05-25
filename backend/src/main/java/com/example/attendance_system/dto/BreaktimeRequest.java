package com.example.attendance_system.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BreaktimeRequest {
    private LocalDate date;

    private String userId;

    private int minute;
}
