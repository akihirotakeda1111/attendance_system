package com.example.attendance_system.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.attendance_system.repository.BreaktimeRepository;

import com.example.attendance_system.dto.BreaktimeRequest;
import com.example.attendance_system.model.Breaktime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BreaktimeService {

    @Autowired
    private BreaktimeRepository breaktimeRepository;

    public void saveBreaktimes(List<BreaktimeRequest> requests) {
        Map<Map.Entry<String, String>, List<BreaktimeRequest>> groupedIdAndDate = requests.stream()
            .collect(Collectors.groupingBy(request -> Map.entry(request.getUserId(), request.getDate())));
        groupedIdAndDate.forEach((key, value) -> {
            String userId = key.getKey();
            String date = key.getValue();
            deleteBreaktimes(userId, date);
        });
        requests.forEach( request -> {
            saveBreaktime(request);
        });
    }

    public void saveBreaktime(BreaktimeRequest request) {
        LocalDate date = LocalDate.parse(request.getDate());
        LocalDateTime startTime = LocalDateTime.parse(request.getStartTime());
        LocalDateTime endTime = LocalDateTime.parse(request.getEndTime());
        LocalDateTime expectedEndTime = request.getExpectedEndTime() == null ? null : LocalDateTime.parse(request.getExpectedEndTime());
        Breaktime breaktime = new Breaktime(date, request.getUserId(), request.getNumber(), startTime, endTime, expectedEndTime);
        breaktimeRepository.save(breaktime);
    }

    public void deleteBreaktimes(String userId, String date) {
        LocalDate ymd = LocalDate.parse(date);
        breaktimeRepository.deleteByUserIdAndDate(userId, ymd);
    }

    public void saveStartBreaktime(BreaktimeRequest request) {
        LocalDate ymd = LocalDate.parse(request.getDate());
        LocalDateTime ymdhms = LocalDateTime.now();
        int number = breaktimeRepository
                .findTopByUserIdAndDateOrderByNumberDesc(request.getUserId(), ymd)
                .map(b -> b.getNumber() + 1)
                .orElse(1);
        LocalDateTime expectedEndTime = ymdhms.plusMinutes(request.getMinute());
        Breaktime breaktime = new Breaktime(ymd, request.getUserId(), number, ymdhms, null, expectedEndTime);
        breaktimeRepository.save(breaktime);
    }

    public void saveEndBreaktime(BreaktimeRequest request) {
        LocalDate ymd = LocalDate.parse(request.getDate());
        Breaktime latestBreaktime = breaktimeRepository
                .findTopByUserIdAndDateOrderByNumberDesc(request.getUserId(), ymd)
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

    public List<Breaktime> getBreaktimeList(String year, String month, String userId) {
        LocalDate startDate = LocalDate.parse(
                year + "-" + String.format("%02d", Integer.valueOf(month)) + "-01");
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
     
        return breaktimeRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }
    
    public List<Breaktime> getBreaktimeList(LocalDate date, String userId) {
        return breaktimeRepository.findByUserIdAndDate(userId, date);
    }
}
