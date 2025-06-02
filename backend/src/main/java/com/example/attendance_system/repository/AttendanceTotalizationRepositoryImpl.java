package com.example.attendance_system.repository;

import org.springframework.stereotype.Repository;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import com.example.attendance_system.dto.AttendanceTotalizationResponse;
import com.example.attendance_system.exception.ValidationException;
import com.example.attendance_system.dto.AttendanceTotalizationRequest;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Repository
public class AttendanceTotalizationRepositoryImpl implements AttendanceTotalizationRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<AttendanceTotalizationResponse> findAttendanceTotalization(AttendanceTotalizationRequest request) {
        try {
            String date_group = "";
            String param_from = "";
            String param_to = "";
            if (request.isMonthly()) {
                // 勤務集計->月次
                date_group = "date_format(date, '%Y%m')";
                param_from = request.getYear() + "-01-01";
                param_to = request.getYear() + "-12-31";
            } else if (request.isWeekly()) {
                // 勤務集計->週次
                LocalDate fromDate = LocalDate.parse(
                    request.getYear() + "-" + String.format("%02d", Integer.valueOf(request.getMonth())) + "-01");
                LocalDate toDate = fromDate.plusMonths(1).minusDays(1);
                
                date_group = "cast(yearweek(date, 1) as char)";
                param_from = fromDate.toString();
                param_to = toDate.toString();
            } else {
                // 勤務管理->稼働
                LocalDate fromDate = LocalDate.parse(
                    request.getYear() + "-" + String.format("%02d", Integer.valueOf(request.getMonth())) + "-01");
                LocalDate toDate = fromDate.plusMonths(1).minusDays(1);
                
                date_group = "cast(date as char)";
                param_from = fromDate.toString();
                param_to = toDate.toString();
            }

            String sql = 
                "select user_id "
                    + ", " + date_group + " date "
                    + ", sum(greatest(attendance_second - breaktime_second, 0)) / 3600 hours "
                + "from ( "
                    + "select attendance.date "
                        + ", attendance.user_id "
                        + ", timestampdiff(second, attendance.start_time, ifnull(attendance.end_time, attendance.start_time)) attendance_second "
                        + ", ifnull(breaktime_sum.breaktime_second, 0) breaktime_second "
                    + "from attendance "
                    + "left join ( "
                        + "select date "
                            + ", user_id "
                            + ", sum(timestampdiff(second, start_time, end_time)) breaktime_second "
                        + "from breaktime "
                        + "where user_id = :userId "
                        + "and date between :from and :to "
                        + "and end_time is not null "
                        + "group by date, user_id "
                    + ") breaktime_sum "
                    + "on attendance.date = breaktime_sum.date "
                    + "and attendance.user_id = breaktime_sum.user_id "
                    + "where attendance.user_id = :userId "
                    + "and attendance.date between :from and :to "
                + ") working_time "
                + "group by user_id, " + date_group + " "
                + "order by date ";
            
            return em.createNativeQuery(sql, AttendanceTotalizationResponse.class)
                    .setParameter("userId", request.getUserId())
                    .setParameter("from", param_from)
                    .setParameter("to", param_to)
                    .getResultList();
        } catch (DateTimeParseException
            | java.util.IllegalFormatException
            | NumberFormatException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}