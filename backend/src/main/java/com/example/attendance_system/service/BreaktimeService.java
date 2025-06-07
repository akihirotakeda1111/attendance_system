package com.example.attendance_system.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.attendance_system.repository.BreaktimeRepository;

import com.example.attendance_system.dto.BreaktimeRequest;
import com.example.attendance_system.exception.ValidationException;
import com.example.attendance_system.model.Breaktime;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BreaktimeService {

    @Autowired
    private BreaktimeRepository breaktimeRepository;

    @Transactional(rollbackFor = Exception.class)
    public void saveBreaktimes(List<BreaktimeRequest> requests) {
        try {
            Map<Map.Entry<String, String>, List<BreaktimeRequest>> groupedIdAndDate = requests.stream()
                .collect(Collectors.groupingBy(request -> Map.entry(request.getUserId(), request.getDate())));
            groupedIdAndDate.forEach((key, value) -> {
                String userId = key.getKey();
                String date = key.getValue();
                deleteBreaktimes(userId, date);
            });
            for (int i = 0; i < requests.size(); i++) {
                if (requests.get(i).getStartTime() == null) {
                    continue;
                }
                requests.get(i).setNumber(i+1);
                saveBreaktime(requests.get(i));
            }
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void saveBreaktime(BreaktimeRequest request) {
        try {
            LocalDate date = LocalDate.parse(request.getDate());
            LocalDateTime startTime = LocalDateTime.parse(request.getStartTime());
            LocalDateTime endTime = LocalDateTime.parse(request.getEndTime());
            LocalDateTime expectedEndTime = request.getExpectedEndTime() == null ? null : LocalDateTime.parse(request.getExpectedEndTime());
            if (!endTime.isAfter(startTime)) {
                throw new ValidationException("End time(" + endTime.toString() + ") cannot be before start time(" + startTime.toString() + ") .");
            }

            Breaktime breaktime = new Breaktime(date, request.getUserId(), request.getNumber(), startTime, endTime, expectedEndTime);
            breaktimeRepository.save(breaktime);
        } catch (DateTimeParseException
            | ValidationException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void deleteBreaktimes(String userId, String date) {
        try {
            LocalDate ymd = LocalDate.parse(date);
            breaktimeRepository.deleteByUserIdAndDate(userId, ymd);
        } catch (DateTimeParseException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void saveStartBreaktime(BreaktimeRequest request) {
        try {
            LocalDate ymd = LocalDate.parse(request.getDate());
            LocalDateTime ymdhms = LocalDateTime.now();
            int number = breaktimeRepository
                    .findTopByUserIdAndDateOrderByNumberDesc(request.getUserId(), ymd)
                    .map(b -> b.getNumber() + 1)
                    .orElse(1);
            LocalDateTime expectedEndTime = ymdhms.plusMinutes(request.getMinute());
            Breaktime breaktime = new Breaktime(ymd, request.getUserId(), number, ymdhms, null, expectedEndTime);
            breaktimeRepository.save(breaktime);
        } catch (DateTimeException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void saveEndBreaktime(BreaktimeRequest request) {
        try {
            LocalDate ymd = LocalDate.parse(request.getDate());
            Breaktime latestBreaktime = breaktimeRepository
                    .findTopByUserIdAndDateOrderByNumberDesc(request.getUserId(), ymd)
                    .orElse(null);
            if (latestBreaktime != null) {
                LocalDateTime ymdhms = LocalDateTime.now();
                latestBreaktime.setEndTime(ymdhms);
                breaktimeRepository.save(latestBreaktime);
            }
        } catch (DateTimeParseException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Breaktime getLatestBreaktime(String userId, String date) {
        try {
            LocalDate ymd = LocalDate.parse(date);
            Breaktime latestBreaktime = breaktimeRepository
                    .findTopByUserIdAndDateAndEndTimeIsNullOrderByNumberDesc(userId, ymd)
                    .orElse(null);
            return latestBreaktime;
        } catch (DateTimeParseException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<Breaktime> getBreaktimeList(String year, String month, String userId) {
        try {
            LocalDate startDate = LocalDate.parse(
                    year + "-" + String.format("%02d", Integer.valueOf(month)) + "-01");
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
            return breaktimeRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        } catch (DateTimeParseException
            | java.util.IllegalFormatException
            | NumberFormatException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public List<Breaktime> getBreaktimeList(String date, String userId) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            return breaktimeRepository.findByUserIdAndDate(userId, parsedDate);
        } catch (DateTimeParseException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
