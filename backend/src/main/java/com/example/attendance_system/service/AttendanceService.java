package com.example.attendance_system.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.attendance_system.repository.AttendanceRepository;
import com.example.attendance_system.repository.BreaktimeRepository;
import com.example.attendance_system.repository.UsersRepository;
import com.example.attendance_system.dto.AttendanceRequest;
import com.example.attendance_system.exception.NotFoundException;
import com.example.attendance_system.exception.ValidationException;
import com.example.attendance_system.model.Attendance;
import com.example.attendance_system.model.AttendanceId;
import com.example.attendance_system.model.Users;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class AttendanceService {

    private AttendanceRepository attendanceRepository;
    private BreaktimeRepository breaktimeRepository;
    private UsersRepository usersRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
            BreaktimeRepository breaktimeRepository,
            UsersRepository usersRepository) {
        this.attendanceRepository = attendanceRepository;
        this.breaktimeRepository = breaktimeRepository;
        this.usersRepository = usersRepository;
    }

    public void saveAttendance(AttendanceRequest request) {
        try {
            Users user = usersRepository.findById(request.getUserId()).orElse(null);
            if (user == null) {
                throw new NotFoundException("not found user: " + request.getUserId());
            }

            LocalDate date = LocalDate.parse(request.getDate());
            LocalDateTime startTime = LocalDateTime.parse(request.getStartTime());
            LocalDateTime endTime = LocalDateTime.parse(request.getEndTime());
            if (!endTime.isAfter(startTime)) {
                throw new ValidationException("End time(" + endTime.toString() + ") cannot be before start time(" + startTime.toString() + ") .");
            }

            Attendance attendance = new Attendance(date, request.getUserId(), startTime, endTime);
            attendanceRepository.save(attendance);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (DateTimeParseException
            | ValidationException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        
    }

    public void updateAttendance(AttendanceRequest request) {
        try {
            Users user = usersRepository.findById(request.getUserId()).orElse(null);
            if (user == null) {
                throw new NotFoundException("not found user: " + request.getUserId());
            }

            LocalDate date = LocalDate.parse(request.getDate());
            AttendanceId attendanceId = new AttendanceId(date, request.getUserId());
            Attendance attendance = attendanceRepository.findById(attendanceId).orElse(null);
            if (attendance == null) {
                throw new NotFoundException("not found attendance: " + request.getUserId() + "," + request.getDate());
            }
            LocalDateTime startTime = LocalDateTime.parse(request.getStartTime());
            LocalDateTime endTime = LocalDateTime.parse(request.getEndTime());
            if (!endTime.isAfter(startTime)) {
                throw new ValidationException("End time(" + endTime.toString() + ") cannot be before start time(" + startTime.toString() + ") .");
            }

            attendance.setStartTime(startTime);
            attendance.setEndTime(endTime);
            attendanceRepository.save(attendance);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (DateTimeParseException
            | ValidationException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void saveStartAttendance(AttendanceRequest request) {
        try {
            Users user = usersRepository.findById(request.getUserId()).orElse(null);
            if (user == null) {
                throw new NotFoundException("not found user: " + request.getUserId());
            }

            LocalDate ymd = LocalDate.now();
            LocalDateTime ymdhms = LocalDateTime.now();
            Attendance attendance = new Attendance(ymd, request.getUserId(), ymdhms, null);
            attendanceRepository.save(attendance);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void saveEndAttendance(AttendanceRequest request) {
        try {
            Users user = usersRepository.findById(request.getUserId()).orElse(null);
            if (user == null) {
                throw new NotFoundException("not found user: " + request.getUserId());
            }

            Attendance latestAttendance = attendanceRepository
                .findTopByUserIdOrderByDateDesc(request.getUserId())
                .orElse(null);
            if (latestAttendance == null) {
                throw new NotFoundException("not found attendance: " + request.getUserId());
            }

            LocalDateTime ymdhms = LocalDateTime.now();
            latestAttendance.setEndTime(ymdhms);
            attendanceRepository.save(latestAttendance);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAttendance(AttendanceRequest request) {
        try {
            Users user = usersRepository.findById(request.getUserId()).orElse(null);
            if (user == null) {
                throw new NotFoundException("not found user: " + request.getUserId());
            }

            String deleteId = request.getUserId();
            LocalDate deleteDate = LocalDate.parse(request.getDate());
            AttendanceId attendanceId = new AttendanceId(deleteDate, deleteId);
            Attendance attendance = attendanceRepository.findById(attendanceId).orElse(null);
            if (attendance == null) {
                throw new NotFoundException("not found attendance: " + deleteId + "," + deleteDate);
            }

            attendanceRepository.deleteByUserIdAndDate(deleteId, deleteDate);
            breaktimeRepository.deleteByUserIdAndDate(deleteId, deleteDate);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (DateTimeParseException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Attendance getAttendance(String date, String userId) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            AttendanceId attendanceId = new AttendanceId(parsedDate, userId);
            return attendanceRepository.findById(attendanceId).orElse(null);
        } catch (DateTimeParseException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        
    }

    public Attendance getLatestAttendance(String userId) {
        try {
            Attendance latestAttendance = attendanceRepository
                .findTopByUserIdAndEndTimeIsNullOrderByDateDesc(userId)
                .orElse(null);
            return latestAttendance;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<Attendance> getAttendanceList(String year, String month, String userId) {
        try {
            Users user = usersRepository.findById(userId).orElse(null);
            if (user == null) {
                throw new NotFoundException("not found user: " + userId);
            }

            LocalDate startDate = LocalDate.parse(
                    year + "-" + String.format("%02d", Integer.valueOf(month)) + "-01");
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);

            return attendanceRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
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
}
