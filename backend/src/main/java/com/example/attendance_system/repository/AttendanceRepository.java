package com.example.attendance_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
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

    // 指定されたユーザーの最新の出勤情報を取得
    Optional<Attendance> findTopByUserIdOrderByDateDesc(String userId);

    // 指定ユーザーで退勤時刻がnullの最新レコードを取得
    Optional<Attendance> findTopByUserIdAndEndTimeIsNullOrderByDateDesc(String userId);

    // 指定された主キーの出勤情報を取得
    //List<Attendance> findByPrimaryKey(AttendanceId attendanceId);
}
