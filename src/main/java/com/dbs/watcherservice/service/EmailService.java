package com.dbs.watcherservice.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    public void sendMail(String subject, String body);

    public void draftMail(String filePath, String errorMessage);
}
