package com.example.attendance_system.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.attendance_system.service.AttendanceService;
import com.example.attendance_system.dto.AttendanceRequest;
import com.example.attendance_system.exception.ValidationException;
import com.example.attendance_system.model.Attendance;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<String> recordStartAttendance(@RequestBody AttendanceRequest request) {
        if (request.getUserId() == null) {
            throw new ValidationException("userId is null");
        }

        attendanceService.saveStartAttendance(request);
        return ResponseEntity.ok("出勤登録が完了しました");
    }

    @PutMapping
    public ResponseEntity<String> recordEndAttendance(@RequestBody AttendanceRequest request) {
        if (request.getUserId() == null) {
            throw new ValidationException("userId is null");
        }
        
        attendanceService.saveEndAttendance(request);
        return ResponseEntity.ok("退勤登録が完了しました");
    }

    @GetMapping("/latest")
    public ResponseEntity<Attendance> getLatestAttendance(@RequestParam("userId") String userId) {
        Attendance latestAttendance = attendanceService.getLatestAttendance(userId);
        if (latestAttendance == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(latestAttendance);
    }

    @GetMapping("/today")
    public ResponseEntity<Attendance> getTodayAttendance(@RequestParam("userId") String userId) {
        LocalDate nowDate = LocalDate.now();
        Attendance attendance = attendanceService.getAttendance(nowDate.toString(), userId);
        if (attendance == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(attendance);
    }
}
