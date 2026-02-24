package com.academy.notification.service;

import com.academy.notification.model.Notification;

/**
 * Contract for all notification senders.
 * Implementations: EmailService, SmsService
 */
public interface NotificationService {

    /**
     * Send the given notification.
     * @param notification the notification to send
     * @return true if sent successfully, false otherwise
     */
    boolean send(Notification notification);

    /**
     * The channel this service handles (e.g., "EMAIL", "SMS").
     */
    String getChannel();
}
