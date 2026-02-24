package com.library;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public class CatalogService {

    private final NotificationService notificationService;

    public CatalogService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Called by Spring after the bean is constructed and all dependencies are injected
    @PostConstruct
    public void onStartup() {
        System.out.println("[CatalogService] Initialized — loading catalog cache");
    }

    // Called by Spring just before the bean is removed from the context
    @PreDestroy
    public void onShutdown() {
        System.out.println("[CatalogService] Destroyed — releasing catalog cache");
    }

    public String search(String keyword) {
        return "Results for: " + keyword;
    }
}
