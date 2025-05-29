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
@Table(name = "breaktime")
@IdClass(BreaktimeId.class)
@Data
public class Breaktime {
    @Id
    private LocalDate date;

    @Id
    private String userId;

    @Id
    private int number;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime expectedEndTime;

    public Breaktime() {}

    public Breaktime(LocalDate date, String userId, int number,
            LocalDateTime startTime, LocalDateTime endTime, LocalDateTime expectedEndTime) {
        this.date = date;
        this.userId = userId;
        this.number = number;
        this.startTime = startTime;
        this.endTime = endTime;
        this.expectedEndTime = expectedEndTime;
    }
}
