package com.example.attendance_system.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import com.example.attendance_system.repository.MasterRepository;
import com.example.attendance_system.repository.UsersRepository;
import com.example.attendance_system.config.SHA256Util;
import com.example.attendance_system.dto.UsersRequest;
import com.example.attendance_system.model.Master;
import com.example.attendance_system.model.Users;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final MasterRepository masterRepository;

    public UsersService(UsersRepository usersRepository, MasterRepository masterRepository) {
        this.usersRepository = usersRepository;
        this.masterRepository = masterRepository;
    }

    public void saveUser(UsersRequest request) {
        try {
            String passwordHash = SHA256Util.hash(request.getPassword());
            Users users = new Users(request.getId(), passwordHash, request.getName()
                , request.getEmail(), request.getRole());
            usersRepository.save(users);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(UsersRequest request) {
        try {
            String deleteId = request.getId();
            usersRepository.deleteById(deleteId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<Users> getUsers() {
        try {
            return usersRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<Users> getUsersWithConvertedCodes(String id, String name) {
        try {
            Users param = new Users();
            if (id != null && !id.isBlank()) param.setId(id);
            if (name != null && !name.isBlank()) param.setName(name);

            ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase();
            List<Users> users = usersRepository.findAll(Example.of(param, matcher));

            Map<String, String> roleMap = masterRepository.findRole()
                .stream().collect(Collectors.toMap(Master::getItemCode, Master::getName));

            return users.stream()
                .map(user -> new Users(user.getId(), user.getPassword(), user.getName(), user.getEmail(), roleMap.get(user.getRole())))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Users getUser(String id) {
        try {
            return usersRepository.findById(id)
                .orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
