package com.example.attendance_system.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.example.attendance_system.service.AttendanceService;
import com.example.attendance_system.model.Attendance;

@RestController
@RequestMapping("/api/manage/attendance")
public class AttendanceManagementController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<List<Attendance>> getAttendance(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("userId") String userId) {
        List<Attendance> sttendance = attendanceService.getAttendanceList(year, month, userId);
        if (sttendance == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sttendance);
    }
}
