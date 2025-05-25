package com.example.attendance_system.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class AttendanceId implements Serializable {
    private LocalDate date;
    private String userId;

    public AttendanceId() {}

    public AttendanceId(LocalDate date, String userId) {
        this.date = date;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceId that = (AttendanceId) o;
        return Objects.equals(date, that.date) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, userId);
    }
}