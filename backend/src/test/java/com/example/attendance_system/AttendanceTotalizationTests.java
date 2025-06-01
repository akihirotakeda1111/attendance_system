package com.example.attendance_system;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AttendanceTotalizationTests {

	@Autowired
    private TestRestTemplate restTemplate;

    @Test
	void getAttendanceTotalization() {
        String url = "/api/manage/totalization?monthly={monthly}&weekly={weekly}&userId={userId}&year={year}&month={month}";
        ResponseEntity<Object> response;
        Map<String, Object> params = new HashMap<>();

        params.put("monthly", true);
        params.put("weekly", false);
        params.put("year", "2025");
        params.put("month", "5");
        params.put("userId", "admin");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("monthly", false);
        params.put("weekly", true);
        params.put("year", "2025");
        params.put("month", "5");
        params.put("userId", "admin");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("monthly", false);
        params.put("weekly", false);
        params.put("year", "2025");
        params.put("month", "5");
        params.put("userId", "admin");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("monthly", true);
        params.put("weekly", false);
        params.put("year", "2030");
        params.put("month", "5");
        params.put("userId", "admin");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("monthly", false);
        params.put("weekly", true);
        params.put("year", "2030");
        params.put("month", "5");
        params.put("userId", "admin");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("monthly", false);
        params.put("weekly", false);
        params.put("year", "2030");
        params.put("month", "5");
        params.put("userId", "admin");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("monthly", false);
        params.put("weekly", true);
        params.put("year", "aaaa");
        params.put("month", "5");
        params.put("userId", "admin");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("monthly", false);
        params.put("weekly", true);
        params.put("year", "2025");
        params.put("month", "aaaa");
        params.put("userId", "admin");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("monthly", false);
        params.put("weekly", false);
        params.put("year", "aaaa");
        params.put("month", "5");
        params.put("userId", "admin");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("monthly", false);
        params.put("weekly", false);
        params.put("year", "2025");
        params.put("month", "aaaa");
        params.put("userId", "admin");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        System.out.println(response.getBody());
	}
}
