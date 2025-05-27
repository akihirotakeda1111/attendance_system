package com.example.attendance_system.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.example.attendance_system.service.BreaktimeService;
import com.example.attendance_system.model.Breaktime;

@RestController
@RequestMapping("/api/manage/breaktime")
public class BreaktimeManagementController {

    @Autowired
    private BreaktimeService breaktimeService;

    @GetMapping
    public ResponseEntity<List<Breaktime>> getBreaktime(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("userId") String userId) {
        List<Breaktime> breaktime = breaktimeService.getBreaktimeList(year, month, userId);
        if (breaktime == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(breaktime);
    }
}
