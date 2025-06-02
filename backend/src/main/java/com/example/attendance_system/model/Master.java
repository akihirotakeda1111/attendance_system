package com.example.attendance_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "master")
@IdClass(MasterId.class)
@Data
public class Master {
    @Id
    private String category_code;

    @Id
    private String item_code;

    @Column(nullable = false)
    private String name;

    public Master() {}

    public Master(String category_code, String item_code, String name) {
        this.category_code = category_code;
        this.item_code = item_code;
        this.name = name;
    }

    public String getCategoryCode() { return category_code; }
    public String getItemCode() { return item_code; }
    public String getName() { return name; }
}
