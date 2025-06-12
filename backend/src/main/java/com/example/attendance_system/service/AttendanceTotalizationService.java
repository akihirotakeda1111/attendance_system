package com.example.attendance_system.service;

import org.springframework.stereotype.Service;

import com.example.attendance_system.repository.AttendanceTotalizationRepository;
import com.example.attendance_system.repository.UsersRepository;
import com.example.attendance_system.dto.AttendanceTotalizationResponse;
import com.example.attendance_system.exception.NotFoundException;
import com.example.attendance_system.exception.ValidationException;
import com.example.attendance_system.model.Users;
import com.example.attendance_system.dto.AttendanceTotalizationRequest;

import java.util.List;

@Service
public class AttendanceTotalizationService {

    private AttendanceTotalizationRepository attendanceTotalizationRepository;
    private UsersRepository usersRepository;

    public AttendanceTotalizationService(AttendanceTotalizationRepository attendanceTotalizationRepository,
            UsersRepository usersRepository) {
        this.attendanceTotalizationRepository = attendanceTotalizationRepository;
        this.usersRepository = usersRepository;
    }

    public List<AttendanceTotalizationResponse> getAttendanceTotalization(AttendanceTotalizationRequest request) {
        try {
            Users user = usersRepository.findById(request.getUserId()).orElse(null);
            if (user == null) {
                throw new NotFoundException("not found user: " + request.getUserId());
            }

            List<AttendanceTotalizationResponse> attendanceTotalizationDto =
                    attendanceTotalizationRepository.findAttendanceTotalization(request);
            return attendanceTotalizationDto;
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
