package com.example.attendance_system.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.attendance_system.service.AttendanceTotalizationService;
import com.example.attendance_system.dto.AttendanceTotalizationResponse;
import com.example.attendance_system.dto.AttendanceTotalizationRequest;
import java.util.List;

@RestController
@RequestMapping("/api/manage/totalization")
public class AttendanceTotalizationController {

    @Autowired
    private AttendanceTotalizationService attendanceTotalizationService;

    @GetMapping
    public ResponseEntity<List<AttendanceTotalizationResponse>> getAttendanceTotalization(
            @RequestParam(name = "monthly") boolean monthly,
            @RequestParam(name = "weekly") boolean weekly,
            @RequestParam(name = "year") String year,
            @RequestParam(name = "month") String month,
            @RequestParam(name = "userId") String userId) {

        AttendanceTotalizationRequest request = new AttendanceTotalizationRequest();
        request.setMonthly(monthly);
        request.setWeekly(weekly);
        request.setYear(year);
        request.setMonth(month);
        request.setUserId(userId);

        List<AttendanceTotalizationResponse> attendanceTotalization =
                attendanceTotalizationService.getAttendanceTotalization(request);
        if (attendanceTotalization == null || attendanceTotalization.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(attendanceTotalization);
    }
}
