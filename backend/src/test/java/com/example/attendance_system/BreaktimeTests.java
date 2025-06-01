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
class BreaktimeTests {

	@Autowired
	private TestRestTemplate restTemplate;


	@Test
	void recordStartBreaktime() {
		String url = "/api/breaktime";
		ResponseEntity<String> response;
		Map<String, Object> params = new HashMap<>();

		params.put("userId", "admin");
		params.put("date", "2025-04-01");
		params.put("minute", "100");
		response = restTemplate.postForEntity(url, params, String.class);
		assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
		System.out.println(response.getBody());

		params.put("date", "202504-01");
		params.put("minute", "100");
		response = restTemplate.postForEntity(url, params, String.class);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
		System.out.println(response.getBody());

		params.put("date", "2025-04-01");
		params.put("minute", "a");
		response = restTemplate.postForEntity(url, params, String.class);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
		System.out.println(response.getBody());
	}

    @Test
	void getLatestBreaktime() {
		String url = "/api/breaktime/latest?userId={userId}&date={date}";
		Map<String, Object> params = new HashMap<>();
		ResponseEntity<Attendance> response;

		params.put("userId", "admin");
		params.put("date", "2025-04-01");
		response = restTemplate.getForEntity(url, Attendance.class, params);
		assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
		System.out.println(response);

		params.put("date", "2030-04-01");
		response = restTemplate.getForEntity(url, Attendance.class, params);
		assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());
		System.out.println(response);

		params.put("date", "202504-01");
		response = restTemplate.getForEntity(url, Attendance.class, params);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
		System.out.println(response);
	}

    @Test
	void recordEndBreaktime() {
		String url = "/api/breaktime";
		Map<String, Object> params = new HashMap<>();
		HttpEntity<Object> requestEntity;
		ResponseEntity<String> response;

		params.put("userId", "admin");
		params.put("date", "2025-04-01");
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

		params.put("date", "202504-01");
		restTemplate.put(url, params);
		requestEntity = new HttpEntity<>(params);
		response = restTemplate.exchange(
				url,
				HttpMethod.PUT,
				requestEntity,
				String.class
		);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
		System.out.println(response.getBody());
	}

}
