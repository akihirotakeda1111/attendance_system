package com.example.attendance_system.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.attendance_system.repository.UsersRepository;
import com.example.attendance_system.config.JwtUtil;
import com.example.attendance_system.exception.AuthenticationException;
import com.example.attendance_system.model.Users;

@Service
public class AuthService {

    @Autowired
    private UsersRepository usersRepository;

    public String authenticate(String id, String password) {
        try {
            Users user = usersRepository.findById(id).orElse(null);
            if (user == null) {
                throw new AuthenticationException("not found user: " + id);
            }

            if (!user.getPassword().equals(password)) {
                throw new AuthenticationException("password is incorrect for user: " + id);
            }

            return JwtUtil.generateToken(user);
        } catch (AuthenticationException e) {
            throw new AuthenticationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
