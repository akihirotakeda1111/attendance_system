package com.example.attendance_system.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.attendance_system.dto.LoginRequest;
import com.example.attendance_system.exception.ValidationException;
import com.example.attendance_system.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.getId() == null) {
            throw new ValidationException("userId is null");
        }

        if (request.getPassword() == null) {
            throw new ValidationException("password is null");
        }

        String token = authService.authenticate(request.getId(), request.getPassword());
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }
}
