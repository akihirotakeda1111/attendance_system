package com.example.attendance_system.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.attendance_system.service.UsersService;
import com.example.attendance_system.model.Users;
import java.util.List;

@RestController
@RequestMapping("/api/manage/users")
public class UsersManagementController {

    @Autowired
    private UsersService usersService;
    
    @GetMapping
    public ResponseEntity<List<Users>> getUsers(
            @RequestParam(name = "id", required = false, defaultValue = "") String id,
            @RequestParam(name = "name", required = false, defaultValue = "") String name) {
        List<Users> users = usersService.getUsersWithConvertedCodes(id, name);
        if (users == null || users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }
}
