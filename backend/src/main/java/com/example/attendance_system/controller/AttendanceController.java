package com.example.attendance_system.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.attendance_system.service.AttendanceService;
import com.example.attendance_system.dto.AttendanceRequest;
import com.example.attendance_system.model.Attendance;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<String> recordStartAttendance(@RequestBody AttendanceRequest request) {
        attendanceService.saveStartAttendance(request);
        return ResponseEntity.ok("出勤登録がされました");
    }

    @PutMapping
    public ResponseEntity<String> recordEndAttendance(@RequestBody AttendanceRequest request) {
        attendanceService.saveEndAttendance(request);
        return ResponseEntity.ok("退勤登録がされました");
    }

    @GetMapping("/latest")
    public ResponseEntity<Attendance> getAttendanceStartDate(@RequestParam("userId") String userId) {
        Attendance latestAttendance = attendanceService.latestStartDate(userId);
        if (latestAttendance == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(latestAttendance);
    }

    /* @GetMapping("/{userId}")
    public ResponseEntity<AttendanceRecord> getAttendance(@PathVariable Long userId) {
        return ResponseEntity.ok(attendanceService.getAttendance(userId));
    } */
}
