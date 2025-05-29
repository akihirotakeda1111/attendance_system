package com.example.attendance_system.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.attendance_system.repository.AttendanceRepository;
import com.example.attendance_system.dto.AttendanceRequest;
import com.example.attendance_system.model.Attendance;
import com.example.attendance_system.model.AttendanceId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public void saveAttendance(AttendanceRequest request) {
        LocalDate date = LocalDate.parse(request.getDate());
        LocalDateTime startTime = LocalDateTime.parse(request.getStartTime());
        LocalDateTime endTime = LocalDateTime.parse(request.getEndTime());
        Attendance attendance = new Attendance(date, request.getUserId(), startTime, endTime);
        attendanceRepository.save(attendance);
    }

    public void saveStartAttendance(AttendanceRequest request) {
        LocalDate ymd = LocalDate.now();
        LocalDateTime ymdhms = LocalDateTime.now();
        Attendance attendance = new Attendance(ymd, request.getUserId(), ymdhms, null);
        attendanceRepository.save(attendance);
    }

    public void saveEndAttendance(AttendanceRequest request) {
        Attendance latestAttendance = attendanceRepository
                .findTopByUserIdOrderByDateDesc(request.getUserId())
                .orElse(null);
        if (latestAttendance != null) {
            LocalDateTime ymdhms = LocalDateTime.now();
            latestAttendance.setEndTime(ymdhms);
            attendanceRepository.save(latestAttendance);
        }
    }

    public Attendance getAttendance(AttendanceId key) {
        return attendanceRepository.findById(key).orElse(null);
    }

    public Attendance getLatestAttendance(String userId) {
        Attendance latestAttendance = attendanceRepository
                .findTopByUserIdAndEndTimeIsNullOrderByDateDesc(userId)
                .orElse(null);
        return latestAttendance;
    }

    public List<Attendance> getAttendanceList(String year, String month, String userId) {
        LocalDate startDate = LocalDate.parse(
                year + "-" + String.format("%02d", Integer.valueOf(month)) + "-01");
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return attendanceRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }
}
