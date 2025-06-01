package com.example.attendance_system;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
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
class AttendanceTests {

	@Autowired
    private TestRestTemplate restTemplate;

	@Test
	void recordStartAttendance() {
        String url = "/api/attendance";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", "admin");

        ResponseEntity<String> response = restTemplate.postForEntity(url, params, String.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response.getBody());
	}

    @Test
	void getLatestAttendance() {
        String url = "/api/attendance/latest?userId={userId}";
        Map<String, Object> params = new HashMap<>();
        ResponseEntity<Attendance> response;

        params.put("userId", "admin");
        response = restTemplate.getForEntity(url, Attendance.class, params);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response);

        params.put("userId", "aaaa");
        response = restTemplate.getForEntity(url, Attendance.class, params);
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());
        System.out.println(response);
	}

    @Test
	void getTodayAttendance() {
        String url = "/api/attendance/today?userId={userId}";
        Map<String, Object> params = new HashMap<>();
        ResponseEntity<Attendance> response;

        params.put("userId", "admin");
        response = restTemplate.getForEntity(url, Attendance.class, params);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response);

        params.put("userId", "aaaa");
        response = restTemplate.getForEntity(url, Attendance.class, params);
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());
        System.out.println(response);
	}

    @Test
	void recordEndAttendance() {
        String url = "/api/attendance";
        Map<String, Object> params = new HashMap<>();
        HttpEntity<Object> requestEntity;
        ResponseEntity<String> response;

        params.put("userId", "admin");
        restTemplate.put(url, params);
        requestEntity = new HttpEntity<>(params);
        response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response.getBody());
	}
}
