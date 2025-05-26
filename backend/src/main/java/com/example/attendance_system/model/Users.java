package com.example.attendance_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class Users {
    @Id
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String role;

    public Users() {}

    public Users(String id, String password, String name, String email, String role) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
