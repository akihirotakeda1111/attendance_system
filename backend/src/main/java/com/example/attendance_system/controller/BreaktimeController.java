package com.example.attendance_system.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.attendance_system.service.BreaktimeService;
import com.example.attendance_system.dto.BreaktimeRequest;
import com.example.attendance_system.model.Breaktime;

@RestController
@RequestMapping("/api/breaktime")
public class BreaktimeController {

    @Autowired
    private BreaktimeService breaktimeService;

    @PostMapping
    public ResponseEntity<String> recordStartBreaktime(@RequestBody BreaktimeRequest request) {
        breaktimeService.saveStartBreaktime(request);
        return ResponseEntity.ok("休憩開始登録がされました");
    }

    @PutMapping
    public ResponseEntity<String> recordEndBreaktime(@RequestBody BreaktimeRequest request) {
        breaktimeService.saveEndBreaktime(request);
        return ResponseEntity.ok("休憩終了登録がされました");
    }

    @GetMapping("/latest")
    public ResponseEntity<Breaktime> getLatestBreaktime(
            @RequestParam("userId") String userId
            , @RequestParam("date") LocalDate date) {
        Breaktime latestBreaktime = breaktimeService.getLatestBreaktime(userId, date);
        if (latestBreaktime == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(latestBreaktime);
    }
}