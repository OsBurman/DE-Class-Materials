package com.library;

import jakarta.annotation.PostConstruct;

public class NotificationService {

    @PostConstruct
    public void init() {
        System.out.println("[NotificationService] Ready");
    }

    public void notify(String message) {
        System.out.println("[NOTIFY] " + message);
    }
}
