package com.example.attendance_system.repository;

import org.springframework.stereotype.Repository;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import com.example.attendance_system.dto.AttendanceTotalizationResponse;
import com.example.attendance_system.dto.AttendanceTotalizationRequest;
import java.util.List;

@Repository
public class AttendanceTotalizationRepositoryImpl implements AttendanceTotalizationRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<AttendanceTotalizationResponse> findAttendanceTotalization(AttendanceTotalizationRequest request) {
        String date_group = "";
        String param_from = "";
        String param_to = "";
        if (request.isMonthly()) {
            date_group = "date_format(date, '%Y%m')";
            param_from = request.getYear() + "-01-01";
            param_to = request.getYear() + "-12-31";
        } else {
            date_group = "CAST(yearweek(date, 1) AS CHAR)";
            param_from = request.getYear() + "-" + request.getMonth() + "-01";
            param_to = request.getYear() + "-" + request.getMonth() + "-31";
        }

        String sql = 
            "select user_id\n"
            + "    , " + date_group + " date\n"
            + "    , sum(greatest(attendance_second - breaktime_second, 0)) / 3600 hours\n"
            + "from (\n"
            + "    select attendance.date\n"
            + "        , attendance.user_id\n"
            + "        , timestampdiff(second, attendance.start_time, ifnull(attendance.end_time, attendance.start_time)) attendance_second\n"
            + "        , breaktime_sum.breaktime_second\n"
            + "    from attendance\n"
            + "    left join (\n"
            + "        select date\n"
            + "            , user_id\n"
            + "            , sum(timestampdiff(second, start_time, end_time)) breaktime_second\n"
            + "        from breaktime\n"
            + "        where user_id = :userId\n"
            + "            and date between :from and :to\n"
            + "        and end_time is not null\n"
            + "        group by date, user_id\n"
            + "    ) breaktime_sum\n"
            + "    on attendance.date = breaktime_sum.date\n"
            + "        and attendance.user_id = breaktime_sum.user_id\n"
            + "    where attendance.user_id = :userId\n"
            + "        and attendance.date between :from and :to\n"
            + ") working_time\n"
            + "group by user_id, " + date_group + "\n"
            + "order by date";
        //System.out.println("SQL: " + sql);
        return em.createNativeQuery(sql, AttendanceTotalizationResponse.class)
                .setParameter("userId", request.getUserId())
                .setParameter("from", param_from)
                .setParameter("to", param_to)
                .getResultList();
    }
}