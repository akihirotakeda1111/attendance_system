package com.example.attendance_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.example.attendance_system.model.Master;

@Repository
public interface MasterRepository extends JpaRepository<Master, String> {

    // 権限情報を取得
    @Query("SELECT DISTINCT m FROM Master m WHERE m.category_code = '00'")
    List<Master> findRole();
    
}
