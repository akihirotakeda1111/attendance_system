package com.example.attendance_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import com.example.attendance_system.model.Attendance;
import com.example.attendance_system.model.AttendanceId;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceId> {

    // 指定されたユーザーの出退勤記録を取得
    List<Attendance> findByUserId(String userId);

    // 指定された日付の出勤情報を取得
    List<Attendance> findByDate(Date date);

    // keyを指定して出勤情報を取得
    Optional<Attendance> findById(AttendanceId id);

    // 指定されたユーザー、日付の出勤情報を取得
    List<Attendance> findByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate);

    // 指定されたユーザーの最新の出勤情報を取得
    Optional<Attendance> findTopByUserIdOrderByDateDesc(String userId);

    // 指定ユーザーで退勤時刻がnullの最新レコードを取得
    Optional<Attendance> findTopByUserIdAndEndTimeIsNullOrderByDateDesc(String userId);

    // ユーザーと日付を指定して出退勤情報を削除
    void deleteByUserIdAndDate(String userId, LocalDate date);

}
