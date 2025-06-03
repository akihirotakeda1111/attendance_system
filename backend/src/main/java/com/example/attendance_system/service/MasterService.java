package com.example.attendance_system.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.attendance_system.repository.MasterRepository;
import com.example.attendance_system.model.Master;

import java.util.List;

@Service
public class MasterService {

    @Autowired
    private MasterRepository masterRepository;

    public List<Master> getRoles() {
        try {
            return masterRepository.findRole();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
