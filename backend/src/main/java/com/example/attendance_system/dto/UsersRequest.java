package com.example.attendance_system.dto;

import lombok.Data;

@Data
public class UsersRequest {
    private String id;

    private String password;

    private String name;
    
    private String email;

    private String role;
}
