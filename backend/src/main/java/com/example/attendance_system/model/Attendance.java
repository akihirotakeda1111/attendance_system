package com.example.attendance_system.model;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "attendance")
@IdClass(AttendanceId.class)
@Data
public class Attendance {
    @Id
    private LocalDate date;

    @Id
    private String userId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public Attendance() {}

    public Attendance(LocalDate date, String userId, LocalDateTime startTime, LocalDateTime endTime) {
        this.date = date;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
