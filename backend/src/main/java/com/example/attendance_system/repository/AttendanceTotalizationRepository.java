package com.example.attendance_system.repository;

import org.springframework.stereotype.Repository;
import java.util.List;
import com.example.attendance_system.dto.AttendanceTotalizationResponse;
import com.example.attendance_system.dto.AttendanceTotalizationRequest;

@Repository
public interface AttendanceTotalizationRepository {
    List<AttendanceTotalizationResponse> findAttendanceTotalization(AttendanceTotalizationRequest request);
}