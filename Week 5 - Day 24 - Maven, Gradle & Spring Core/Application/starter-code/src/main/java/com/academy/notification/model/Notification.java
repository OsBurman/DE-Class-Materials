package com.academy.notification.model;

import lombok.Builder;
import lombok.Data;

// TODO Task 1: Add the following Lombok annotations:
//   @Data      — generates getters, setters, toString, equals, hashCode
//   @Builder   — generates a builder pattern (used in Main.java)
//   @NoArgsConstructor — generates no-args constructor
//   @AllArgsConstructor — generates all-args constructor
// Note: @Builder and @NoArgsConstructor together require @AllArgsConstructor as well

/**
 * Represents a notification to be sent to a recipient.
 */
public class Notification {

    // TODO Task 1: Add Lombok annotations above this class

    private Long id;

    /**
     * The recipient — email address for EMAIL channel, phone number for SMS.
     */
    private String recipient;

    private String subject;

    private String body;

    /**
     * Delivery channel: "EMAIL" or "SMS"
     */
    private String channel;

    /**
     * Delivery status — set by the service after attempting to send.
     * Values: "PENDING", "SENT", "FAILED"
     */
    private String status = "PENDING";
}
