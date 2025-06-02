package com.example.attendance_system.service;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import com.example.attendance_system.repository.MasterRepository;
import com.example.attendance_system.repository.UsersRepository;
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
}
