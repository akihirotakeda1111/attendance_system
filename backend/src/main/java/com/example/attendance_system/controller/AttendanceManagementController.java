package com.example.attendance_system.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import com.example.attendance_system.service.AttendanceService;
import com.example.attendance_system.dto.AttendanceRequest;
import com.example.attendance_system.exception.ValidationException;
import com.example.attendance_system.model.Attendance;

@RestController
@RequestMapping("/api/manage/attendance")
public class AttendanceManagementController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<String> recordAttendance(@RequestBody AttendanceRequest request) {
        if (request.getUserId() == null) { throw new ValidationException("userId is null"); }
        if (request.getDate() == null) { throw new ValidationException("date is null"); }
        if (request.getStartTime() == null) { throw new ValidationException("startTime is null"); }
        if (request.getEndTime() == null) { throw new ValidationException("endTime is null"); }

        attendanceService.saveAttendance(request);
        return ResponseEntity.ok("出勤登録が完了しました");
    }

    @PutMapping
    public ResponseEntity<String> updateAttendance(@RequestBody AttendanceRequest request) {
        if (request.getUserId() == null) { throw new ValidationException("userId is null"); }
        if (request.getDate() == null) { throw new ValidationException("date is null"); }
        if (request.getStartTime() == null) { throw new ValidationException("startTime is null"); }
        if (request.getEndTime() == null) { throw new ValidationException("endTime is null"); }

        attendanceService.updateAttendance(request);
        return ResponseEntity.ok("出勤登録が完了しました");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAttendance(@RequestBody AttendanceRequest request) {
        if (request.getUserId() == null) { throw new ValidationException("userId is null"); }
        if (request.getDate() == null) { throw new ValidationException("date is null"); }

        attendanceService.deleteAttendance(request);
        return ResponseEntity.ok("出退勤削除が完了しました");
    }

    @GetMapping
    public ResponseEntity<List<Attendance>> getAttendanceList(
            @RequestParam(name = "year", defaultValue = "") String year,
            @RequestParam(name = "month", defaultValue = "") String month,
            @RequestParam(name = "userId", defaultValue = "") String userId) {
        if (userId.isEmpty()) { throw new ValidationException("userId is null"); }
        if (year.isEmpty()) { throw new ValidationException("year is null"); }
        if (month.isEmpty()) { throw new ValidationException("month is null");}

        List<Attendance> attendance = attendanceService.getAttendanceList(year, month, userId);
        if (attendance == null || attendance.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/date")
    public ResponseEntity<Attendance> getAttendance(
            @RequestParam("date") String date,
            @RequestParam("userId") String userId) {
        Attendance attendance = attendanceService.getAttendance(date, userId);
        if (attendance == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(attendance);
    }
}
