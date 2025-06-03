package com.example.attendance_system.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.attendance_system.service.MasterService;
import com.example.attendance_system.model.Master;
import java.util.List;

@RestController
@RequestMapping("/api/master")
public class MasterController {

    @Autowired
    private MasterService masterService;
    
    @GetMapping("/roles")
    public ResponseEntity<List<Master>> getRoles() {
        List<Master> master = masterService.getRoles();
        if (master == null || master.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(master);
    }
}
