package com.example.attendance_system.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.attendance_system.repository.BreaktimeRepository;
import com.example.attendance_system.dto.BreaktimeRequest;
import com.example.attendance_system.model.Breaktime;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class BreaktimeService {

    @Autowired
    private BreaktimeRepository breaktimeRepository;

    public void saveStartBreaktime(BreaktimeRequest request) {
        LocalDate ymd = request.getDate();
        LocalDateTime ymdhms = LocalDateTime.now();
        int number = breaktimeRepository
                .findTopByUserIdAndDateOrderByNumberDesc(request.getUserId(), ymd)
                .map(b -> b.getNumber() + 1)
                .orElse(1);
        LocalDateTime expectedEndTime = ymdhms.plusMinutes(request.getMinute());
        Breaktime breaktime = new Breaktime(ymd, request.getUserId(), number, ymdhms, expectedEndTime);
        breaktimeRepository.save(breaktime);
    }

    public void saveEndBreaktime(BreaktimeRequest request) {
        Breaktime latestBreaktime = breaktimeRepository
                .findTopByUserIdAndDateOrderByNumberDesc(request.getUserId(), request.getDate())
                .orElse(null);
        if (latestBreaktime != null) {
            LocalDateTime ymdhms = LocalDateTime.now();
            latestBreaktime.setEndTime(ymdhms);
            breaktimeRepository.save(latestBreaktime);
        }
    }

    public Breaktime latestStartDate(String userId, LocalDate date) {
        Breaktime latestBreaktime = breaktimeRepository
                .findTopByUserIdAndDateAndEndTimeIsNullOrderByNumberDesc(userId, date)
                .orElse(null);
        return latestBreaktime;
    }
}
