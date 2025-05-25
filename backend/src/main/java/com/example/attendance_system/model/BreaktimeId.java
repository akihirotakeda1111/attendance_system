package com.example.attendance_system.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class BreaktimeId implements Serializable {
    private LocalDate date;
    private String userId;
    private int number;

    public BreaktimeId() {}

    public BreaktimeId(LocalDate date, String userId, int number) {
        this.date = date;
        this.userId = userId;
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BreaktimeId that = (BreaktimeId) o;
        return Objects.equals(date, that.date) && Objects.equals(userId, that.userId) && number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, userId, number);
    }
}