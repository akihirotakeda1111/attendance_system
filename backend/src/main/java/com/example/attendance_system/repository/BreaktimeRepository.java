package com.example.attendance_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import com.example.attendance_system.model.Breaktime;
import com.example.attendance_system.model.BreaktimeId;

@Repository
public interface BreaktimeRepository extends JpaRepository<Breaktime, BreaktimeId> {

    // 指定されたユーザーの休憩記録を取得
    List<Breaktime> findByUserId(String userId);

    // 指定された日付の休憩情報を取得
    List<Breaktime> findByDate(Date date);

    // 指定されたユーザーの最新の休憩情報を取得
    Optional<Breaktime> findTopByUserIdOrderByDateDesc(String userId);

    // 指定ユーザー・日付の最新（最大）のnumberを持つBreaktimeを1件取得
    Optional<Breaktime> findTopByUserIdAndDateOrderByNumberDesc(String userId, LocalDate date);

    // 指定ユーザーで終了時刻がnullの最新レコードを取得
    Optional<Breaktime> findTopByUserIdAndDateAndEndTimeIsNullOrderByNumberDesc(String userId, LocalDate date);

    // 指定された主キーの出勤情報を取得
    //List<Attendance> findByPrimaryKey(AttendanceId attendanceId);
}
