package com.example.attendance_system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.attendance_system.model.Users;
import com.example.attendance_system.service.EmailService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmailTests {

    @Autowired
    private EmailService emailService;

    @Test
	void sendMail() {
        Users user = new Users();
        user.setEmail("test@example.com");
        emailService.sendNotification(user);
	}
}
