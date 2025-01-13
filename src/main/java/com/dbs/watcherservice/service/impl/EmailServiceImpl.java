package com.dbs.watcherservice.service.impl;

import com.dbs.watcherservice.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${notification.email.to}")
    private String notificationEmail;

    @Override
    public void sendMail(String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notificationEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public void draftMail(String filePath, String errorMessage) {
        String subject = "Error Processing File:";

        String body = "An error occurred while processing the file:\n\n" +
                "File Path: " + filePath + "\n" +
                "Error Message: " + errorMessage;

        this.sendMail(subject, body);
    }
}
