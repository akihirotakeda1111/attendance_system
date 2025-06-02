package com.example.attendance_system.model;

import java.io.Serializable;
import java.util.Objects;

public class MasterId implements Serializable {
    private String category_code;
    private String item_code;

    public MasterId() {}

    public MasterId(String category_code, String item_code) {
        this.category_code = category_code;
        this.item_code = item_code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MasterId that = (MasterId) o;
        return Objects.equals(category_code, that.category_code) && Objects.equals(item_code, that.item_code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category_code, item_code);
    }
}