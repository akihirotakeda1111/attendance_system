package com.example.attendance_system;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersManagementTests {

	@Autowired
    private TestRestTemplate restTemplate;

    @Test
	void getUsers() {
        String url = "/api/manage/users?id={id}&name={name}";
        ResponseEntity<Object> response;
        Map<String, Object> params = new HashMap<>();

        params.put("id", "ad");
        params.put("name", "");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertNotNull(response.getBody());
        System.out.println(response.getBody());

        params.put("id", "dm");
        params.put("name", "");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertNotNull(response.getBody());
        System.out.println(response.getBody());

        params.put("id", "in");
        params.put("name", "");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertNotNull(response.getBody());
        System.out.println(response.getBody());

        params.put("id", "");
        params.put("name", "ad");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertNotNull(response.getBody());
        System.out.println(response.getBody());

        params.put("id", "");
        params.put("name", "dm");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertNotNull(response.getBody());
        System.out.println(response.getBody());

        params.put("id", "");
        params.put("name", "in");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertNotNull(response.getBody());
        System.out.println(response.getBody());

        params.put("id", "in");
        params.put("name", "ad");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertNotNull(response.getBody());
        System.out.println(response.getBody());

        params.put("id", "ad");
        params.put("name", "dm");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertNotNull(response.getBody());
        System.out.println(response.getBody());

        params.put("id", "mi");
        params.put("name", "in");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertNotNull(response.getBody());
        System.out.println(response.getBody());

        params.put("id", "zzz");
        params.put("name", "ad");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertNull(response.getBody());
        System.out.println(response.getBody());

        params.put("id", "ad");
        params.put("name", "zzz");
        response = restTemplate.getForEntity(url, Object.class, params);
        assertNull(response.getBody());
        System.out.println(response.getBody());
	}
}
