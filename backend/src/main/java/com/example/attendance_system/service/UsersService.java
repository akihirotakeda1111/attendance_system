package com.example.attendance_system.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.attendance_system.repository.UsersRepository;
import com.example.attendance_system.model.Users;

import java.util.List;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    public List<Users> getUsers() {
        return usersRepository.findAll();
    }
}
