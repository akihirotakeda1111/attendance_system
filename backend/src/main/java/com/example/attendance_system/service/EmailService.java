package com.example.attendance_system.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.attendance_system.model.Breaktime;
import com.example.attendance_system.model.Users;
import com.example.attendance_system.repository.BreaktimeRepository;
import com.example.attendance_system.repository.UsersRepository;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final BreaktimeRepository breaktimeRepository;
    private final UsersRepository usersRepository;

    public EmailService(JavaMailSender mailSender,
            UsersRepository usersRepository,
            BreaktimeRepository breaktimeRepository) {
        this.mailSender = mailSender;
        this.usersRepository = usersRepository;
        this.breaktimeRepository = breaktimeRepository;
    }

    @Scheduled(fixedRate = 60000) // 1分単位で実行
    public void checkBreaktime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinuteBefore = now.minusMinutes(1);
        List<Breaktime> breaktimes = breaktimeRepository.findByExpectedEndTimeBetween(oneMinuteBefore, now);
        
        breaktimes.forEach((breaktime) -> {
            Users user = usersRepository.findById(breaktime.getUserId()).orElse(null);
            if (user != null) {
                sendNotification(user);
                breaktime.setExpectedEndTime(null);
                breaktimeRepository.save(breaktime);
            }
        });
    }

    public void sendNotification(Users user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@example.com");
        message.setTo(user.getEmail());
        message.setSubject("休憩時間終了通知");
        message.setText("休憩時間が終了しました。業務に復帰してください。");
        mailSender.send(message);
    }
}