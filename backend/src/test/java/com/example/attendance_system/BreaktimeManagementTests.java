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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BreaktimeManagementTests {

	@Autowired
    private TestRestTemplate restTemplate;

    @Test
    void recordBreaktimes() {
        String url = "/api/manage/breaktime";
        List<Map<String, Object>> requestData = new ArrayList<>();
		HttpEntity<List<Map<String, Object>>> requestEntity;
		Map<String, Object> entry1;
		Map<String, Object> entry2;
		ResponseEntity<String> response;

        entry1 = new HashMap<>();
        entry1.put("date", "2025-04-01");
        entry1.put("userId", "admin");
        entry1.put("number", 1);
        entry1.put("startTime", "2025-04-01T09:30:00");
        entry1.put("endTime", "2025-04-01T10:00:00");
        entry1.put("expectedEndTime", null);

        entry2 = new HashMap<>();
        entry2.put("date", "2025-04-01");
        entry2.put("userId", "admin");
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

		entry1 = new HashMap<>();
        entry1.put("date", "2025-04-01");
        entry1.put("userId", "admin");
        entry1.put("number", 1);
        entry1.put("startTime", "2025-04-01T12:00:00");
        entry1.put("endTime", "2025-04-01T13:00:00");
        entry1.put("expectedEndTime", null);

        entry2 = new HashMap<>();
        entry2.put("date", "2025-04-01");
        entry2.put("userId", "admin");
        entry1.put("number", 2);
        entry2.put("startTime", "2025-04-01T13:00:00");
        entry2.put("endTime", "2025-04-0114:00:00");
        entry2.put("expectedEndTime", null);

        requestData.add(entry1);
        requestData.add(entry2);

        requestEntity = new HttpEntity<>(requestData);
        response = restTemplate.postForEntity(url, requestEntity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        System.out.println(response.getBody());
	}

    @Test
	void getBreaktimesYearMonth() {
        String url = "/api/manage/breaktime?userId={userId}&year={year}&month={month}";
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
	void getBreaktimesDate() {
        String url = "/api/manage/breaktime/date?date={date}&userId={userId}";
        ResponseEntity<Object> response;
        Map<String, Object> params = new HashMap<>();

        params.put("userId", "admin");
        params.put("date", "2025-04-01");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response);

        params.put("date", "2030-04-01");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());
        System.out.println(response);
        
        params.put("date", "203004-01");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        System.out.println(response);
	}
}
