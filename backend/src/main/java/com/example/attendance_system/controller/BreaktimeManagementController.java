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

import com.example.attendance_system.service.BreaktimeService;
import com.example.attendance_system.dto.BreaktimeRequest;
import com.example.attendance_system.model.Breaktime;

@RestController
@RequestMapping("/api/manage/breaktime")
public class BreaktimeManagementController {

    @Autowired
    private BreaktimeService breaktimeService;

    @PostMapping
    public ResponseEntity<String> recordBreaktimes(@RequestBody List<BreaktimeRequest> requests) {
        breaktimeService.saveBreaktimes(requests);
        return ResponseEntity.ok("休憩登録がされました");
    }

    @GetMapping
    public ResponseEntity<List<Breaktime>> getBreaktimes(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("userId") String userId) {
        List<Breaktime> breaktime = breaktimeService.getBreaktimeList(year, month, userId);
        if (breaktime == null || breaktime.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(breaktime);
    }

    @GetMapping("/date")
    public ResponseEntity<List<Breaktime>> getBreaktimes(
            @RequestParam("date") String date,
            @RequestParam("userId") String userId) {
        LocalDate parsedDate = LocalDate.parse(date);
        List<Breaktime> breaktime = breaktimeService.getBreaktimeList(parsedDate, userId);
        if (breaktime == null || breaktime.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(breaktime);
    }
}
