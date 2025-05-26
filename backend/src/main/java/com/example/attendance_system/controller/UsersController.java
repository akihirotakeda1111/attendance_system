package com.example.attendance_system.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.attendance_system.service.UsersService;
import com.example.attendance_system.model.Users;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UsersService usersService;
    
    @GetMapping
    public ResponseEntity<List<Users>> getUsers() {
        List<Users> users = usersService.getUsers();
        if (users == null || users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users);
    }
}
