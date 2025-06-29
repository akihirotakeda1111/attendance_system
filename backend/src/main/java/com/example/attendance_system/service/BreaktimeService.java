package com.example.attendance_system.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.attendance_system.repository.AttendanceRepository;
import com.example.attendance_system.repository.BreaktimeRepository;
import com.example.attendance_system.repository.UsersRepository;
import com.example.attendance_system.dto.BreaktimeRequest;
import com.example.attendance_system.exception.NotFoundException;
import com.example.attendance_system.exception.ValidationException;
import com.example.attendance_system.model.Attendance;
import com.example.attendance_system.model.AttendanceId;
import com.example.attendance_system.model.Breaktime;
import com.example.attendance_system.model.Users;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BreaktimeService {

    private final BreaktimeRepository breaktimeRepository;
    private final AttendanceRepository attendanceRepository;
    private final UsersRepository usersRepository;

    public BreaktimeService(BreaktimeRepository breaktimeRepository,
            AttendanceRepository attendanceRepository,
            UsersRepository usersRepository) {
        this.breaktimeRepository = breaktimeRepository;
        this.attendanceRepository = attendanceRepository;
        this.usersRepository = usersRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBreaktimes(List<BreaktimeRequest> requests) {
        try {
            // userId,dateでグループ化して休憩情報をdelete
            Map<Map.Entry<String, String>, List<BreaktimeRequest>> groupedIdAndDate = requests.stream()
                .collect(Collectors.groupingBy(request -> Map.entry(request.getUserId(), request.getDate())));
            groupedIdAndDate.forEach((key, value) -> {
                String userId = key.getKey();
                String date = key.getValue();

                Users user = usersRepository.findById(userId).orElse(null);
                if (user == null) {
                    throw new NotFoundException("not found user: " + userId);
                }
                
                deleteBreaktimes(userId, date);
            });

            // 休憩情報をinsert
            for (int i = 0; i < requests.size(); i++) {
                if (requests.get(i).getStartTime() == null) {
                    continue;
                }
                requests.get(i).setNumber(i+1);
                saveBreaktime(requests.get(i));
            }
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void saveBreaktime(BreaktimeRequest request) {
        try {
            LocalDate date = LocalDate.parse(request.getDate());
            AttendanceId attendanceId = new AttendanceId(date, request.getUserId());
            Attendance attendance = attendanceRepository.findById(attendanceId).orElse(null);
            if (attendance == null) {
                throw new NotFoundException("not found attendance: " + request.getUserId() + "," + date);
            }

            LocalDateTime startTime = LocalDateTime.parse(request.getStartTime());
            LocalDateTime endTime = LocalDateTime.parse(request.getEndTime());
            LocalDateTime expectedEndTime = request.getExpectedEndTime() == null ? null : LocalDateTime.parse(request.getExpectedEndTime());
            if (!endTime.isAfter(startTime)) {
                throw new ValidationException("End time(" + endTime.toString() + ") cannot be before start time(" + startTime.toString() + ") .");
            }

            Breaktime breaktime = new Breaktime(date, request.getUserId(), request.getNumber(), startTime, endTime, expectedEndTime);
            breaktimeRepository.save(breaktime);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
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
            Users user = usersRepository.findById(request.getUserId()).orElse(null);
            if (user == null) {
                throw new NotFoundException("not found user: " + request.getUserId());
            }

            LocalDate ymd = LocalDate.parse(request.getDate());
            LocalDateTime ymdhms = LocalDateTime.now();
            int number = breaktimeRepository
                    .findTopByUserIdAndDateOrderByNumberDesc(request.getUserId(), ymd)
                    .map(b -> b.getNumber() + 1)
                    .orElse(1);
            LocalDateTime expectedEndTime = request.getMinute() == 0 ? null : ymdhms.plusMinutes(request.getMinute());
            Breaktime breaktime = new Breaktime(ymd, request.getUserId(), number, ymdhms, null, expectedEndTime);
            breaktimeRepository.save(breaktime);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (DateTimeException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void saveEndBreaktime(BreaktimeRequest request) {
        try {
            Users user = usersRepository.findById(request.getUserId()).orElse(null);
            if (user == null) {
                throw new NotFoundException("not found user: " + request.getUserId());
            }

            LocalDate ymd = LocalDate.parse(request.getDate());
            Breaktime latestBreaktime = breaktimeRepository
                    .findTopByUserIdAndDateOrderByNumberDesc(request.getUserId(), ymd)
                    .orElse(null);
            if (latestBreaktime == null) {
                throw new NotFoundException("not found breaktime: " + request.getUserId());
            }

            LocalDateTime ymdhms = LocalDateTime.now();
            latestBreaktime.setEndTime(ymdhms);
            latestBreaktime.setExpectedEndTime(null);
            breaktimeRepository.save(latestBreaktime);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
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
            Users user = usersRepository.findById(userId).orElse(null);
            if (user == null) {
                throw new NotFoundException("not found user: " + userId);
            }

            LocalDate startDate = LocalDate.parse(
                    year + "-" + String.format("%02d", Integer.valueOf(month)) + "-01");
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
            return breaktimeRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
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
            Users user = usersRepository.findById(userId).orElse(null);
            if (user == null) {
                throw new NotFoundException("not found user: " + userId);
            }

            LocalDate parsedDate = LocalDate.parse(date);
            return breaktimeRepository.findByUserIdAndDate(userId, parsedDate);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (DateTimeParseException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
