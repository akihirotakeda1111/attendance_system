package com.example.attendance_system;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.attendance_system.model.Attendance;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AttendanceManagementTests {

	@Autowired
    private TestRestTemplate restTemplate;

	@Test
	void recordAttendance() {
        String url = "/api/manage/attendance";
        ResponseEntity<String> response;
        Map<String, Object> params = new HashMap<>();

        params.put("userId", "admin");
        params.put("date", "2025-04-01");
        params.put("startTime", "2025-04-01T09:00:00");
        params.put("endTime", "2025-04-01T17:00:00");
        response = restTemplate.postForEntity(url, params, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("endTime", "2025-04-01T09:01:00");
        response = restTemplate.postForEntity(url, params, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response.getBody());
        
        params.put("endTime", "2025-04-01T09:00:00");
        response = restTemplate.postForEntity(url, params, String.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        System.out.println(response.getBody());
        
        params.put("endTime", "2025-04-01T08:59:00");
        response = restTemplate.postForEntity(url, params, String.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("endTime", "2025-04-0110:00:00");
        response = restTemplate.postForEntity(url, params, String.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        System.out.println(response.getBody());
	}

    @Test
	void getAttendanceList() {
        String url = "/api/manage/attendance?userId={userId}&year={year}&month={month}";
        ResponseEntity<Object> response;
        Map<String, Object> params = new HashMap<>();

        params.put("year", "2025");
        params.put("month", "5");
        params.put("userId", "admin");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("year", "2030");
        params.put("month", "5");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("year", "a");
        params.put("month", "5");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("year", "2025");
        params.put("month", "13");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        System.out.println(response.getBody());
	}

    @Test
	void getAttendance() {
        String url = "/api/manage/attendance/date?date={date}&userId={userId}";
        ResponseEntity<Attendance> response;
        Map<String, Object> params = new HashMap<>();

        params.put("userId", "admin");
        params.put("date", "2025-04-01");
        response = restTemplate.getForEntity(url, Attendance.class, params);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response);

        params.put("date", "2030-04-01");
        response = restTemplate.getForEntity(url, Attendance.class, params);
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());
        System.out.println(response);
        
        params.put("date", "203004-01");
        response = restTemplate.getForEntity(url, Attendance.class, params);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        System.out.println(response);
	}

    @Test
    void deleteAttendance() {
        String url = "/api/manage/users";
        ResponseEntity<String> response;
        Map<String, Object> params = new HashMap<>();
        HttpEntity<Object> requestEntity = new HttpEntity<>(params);
        params.put("id", "testUser");
        response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                requestEntity,
                String.class
        );
        
        params = new HashMap<>();
        params.put("id", "testUser");
        params.put("password", "testUserPass");
        params.put("name", "テストユーザー");
        params.put("email", "a@a.com");
        params.put("role", "00");
        response = restTemplate.postForEntity(url, params, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        url = "/api/manage/attendance";
        params = new HashMap<>();
        params.put("userId", "testUser");
        params.put("date", "2025-04-01");
        params.put("startTime", "2025-04-01T09:00:00");
        params.put("endTime", "2025-04-01T17:00:00");
        response = restTemplate.postForEntity(url, params, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        url = "/api/manage/breaktime";
        List<Map<String, Object>> requestData = new ArrayList<>();
		Map<String, Object> entry1;
		Map<String, Object> entry2;
        entry1 = new HashMap<>();
        entry1.put("date", "2025-04-01");
        entry1.put("userId", "testUser");
        entry1.put("number", 1);
        entry1.put("startTime", "2025-04-01T09:30:00");
        entry1.put("endTime", "2025-04-01T10:00:00");
        entry1.put("expectedEndTime", null);
        entry2 = new HashMap<>();
        entry2.put("date", "2025-04-01");
        entry2.put("userId", "testUser");
        entry1.put("number", 2);
        entry2.put("startTime", "2025-04-01T10:00:00");
        entry2.put("endTime", "2025-04-01T11:00:00");
        entry2.put("expectedEndTime", null);
        requestData.add(entry1);
        requestData.add(entry2);
        requestEntity = new HttpEntity<>(requestData);
        response = restTemplate.postForEntity(url, requestEntity, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        url = "/api/manage/attendance";
        params = new HashMap<>();
        requestEntity = new HttpEntity<>(params);
        params.put("userId", "testUser");
        params.put("date", "2025-0401");
        response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                requestEntity,
                String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params = new HashMap<>();
        requestEntity = new HttpEntity<>(params);
        params.put("userId", "testUser");
        params.put("date", "2025-04-01");
        response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                requestEntity,
                String.class
        );
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response.getBody());
    }
}
