package com.example.attendance_system.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import com.example.attendance_system.service.AttendanceService;
import com.example.attendance_system.dto.AttendanceRequest;
import com.example.attendance_system.model.Attendance;
import com.example.attendance_system.model.AttendanceId;

@RestController
@RequestMapping("/api/manage/attendance")
public class AttendanceManagementController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<String> recordAttendance(@RequestBody AttendanceRequest request) {
        attendanceService.saveAttendance(request);
        return ResponseEntity.ok("出勤登録がされました");
    }

    @GetMapping
    public ResponseEntity<List<Attendance>> getAttendance(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("userId") String userId) {
        List<Attendance> attendance = attendanceService.getAttendanceList(year, month, userId);
        if (attendance == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/date")
    public ResponseEntity<Attendance> getAttendance(
            @RequestParam("date") String date,
            @RequestParam("userId") String userId) {
        LocalDate parsedDate = LocalDate.parse(date);
        Attendance attendance = attendanceService.getAttendance(
            new AttendanceId(parsedDate, userId));
        if (attendance == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(attendance);
    }
}
