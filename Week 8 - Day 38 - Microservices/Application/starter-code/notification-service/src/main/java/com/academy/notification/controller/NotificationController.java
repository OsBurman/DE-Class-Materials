package com.academy.notification.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Notification Controller — receives notification requests from other services.
 *
 * TODO Task 7: Add a POST endpoint that logs and stores notifications.
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final List<Map<String, Object>> sentNotifications = new ArrayList<>();

    // TODO Task 7a: POST /api/notifications
    // Body: { "type": "ORDER_CREATED", "recipient": "user@example.com", "message":
    // "..." }
    @PostMapping
    public ResponseEntity<Map<String, Object>> sendNotification(@RequestBody Map<String, Object> body) {
        log.info("Sending notification: type={}, recipient={}", body.get("type"), body.get("recipient"));
        // TODO: simulate sending (just log and store)
        var notification = new java.util.HashMap<>(body);
        notification.put("sentAt", Instant.now().toString());
        notification.put("status", "SENT");
        sentNotifications.add(notification);
        return ResponseEntity.ok(notification);
    }

    // TODO Task 7b: GET /api/notifications — list all sent notifications
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getNotifications() {
        return ResponseEntity.ok(sentNotifications);
    }
}
