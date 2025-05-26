package com.example.attendance_system.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.attendance_system.repository.AttendanceTotalizationRepository;
import com.example.attendance_system.dto.AttendanceTotalizationResponse;
import com.example.attendance_system.dto.AttendanceTotalizationRequest;

import java.util.List;

@Service
public class AttendanceTotalizationService {

    @Autowired
    private AttendanceTotalizationRepository attendanceTotalizationRepository;

    public List<AttendanceTotalizationResponse> getAttendanceTotalization(AttendanceTotalizationRequest request) {
        List<AttendanceTotalizationResponse> attendanceTotalizationDto =
                attendanceTotalizationRepository.findAttendanceTotalization(request);
        return attendanceTotalizationDto;
    }
}
