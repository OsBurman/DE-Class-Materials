package com.academy.notification.service;

import com.academy.notification.model.Notification;
import com.academy.notification.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Sends notifications via Email (SMTP).
 *
 * Demonstrates: CONSTRUCTOR INJECTION
 */
@Slf4j
// TODO Task 2: Add the @Service annotation
public class EmailService implements NotificationService {

    private final String fromAddress;
    private final String smtpHost;
    private final NotificationRepository repository;

    // TODO Task 2: Implement Constructor Injection
    // - Inject NotificationRepository via constructor (use @Autowired on the
    // constructor)
    // - Inject email.from and email.smtp.host from application.properties
    // using @Value("${email.from}") and @Value("${email.smtp.host}")
    //
    // public EmailService(NotificationRepository repository,
    // @Value("${email.from}") String fromAddress,
    // @Value("${email.smtp.host}") String smtpHost) {
    // this.repository = repository;
    // this.fromAddress = fromAddress;
    // this.smtpHost = smtpHost;
    // }

    public EmailService(NotificationRepository repository, String fromAddress, String smtpHost) {
        // TODO: replace this constructor with one that uses @Autowired and @Value
        this.repository = repository;
        this.fromAddress = fromAddress;
        this.smtpHost = smtpHost;
    }

    @Override
    public boolean send(Notification notification) {
        // Simulated email sending — in a real app, use JavaMailSender
        log.info("[EMAIL] From: {} | SMTP: {} | To: {} | Subject: {}",
                fromAddress, smtpHost, notification.getRecipient(), notification.getSubject());
        log.info("[EMAIL] Body: {}", notification.getBody());

        notification.setStatus("SENT");

        // TODO Task 3: Save the notification using the injected repository
        // repository.save(notification);

        return true;
    }

    @Override
    public String getChannel() {
        return "EMAIL";
    }
}
