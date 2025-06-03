package com.example.attendance_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import com.example.attendance_system.model.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {

    // すべてのユーザーを取得
    List<Users> findAll();

    // IDでユーザーを取得
    Optional<Users> findById(String id);
    
}
