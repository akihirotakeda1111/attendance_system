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
class AuthTests {

	@Autowired
    private TestRestTemplate restTemplate;

    @Test
	void login() {
        String url = "/api/auth/login";
        ResponseEntity<Object> response;
        Map<String, Object> params = new HashMap<>();

        params.put("id", "admin");
        params.put("password", "password");
        response = restTemplate.postForEntity(url, params, Object.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("id", "admin");
        params.put("password", "abc");
        response = restTemplate.postForEntity(url, params, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("id", "abc");
        params.put("password", "password");
        response = restTemplate.postForEntity(url, params, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
        System.out.println(response.getBody());

        params.put("id", "abc");
        params.put("password", "abc");
        response = restTemplate.postForEntity(url, params, Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
        System.out.println(response.getBody());
	}
}
