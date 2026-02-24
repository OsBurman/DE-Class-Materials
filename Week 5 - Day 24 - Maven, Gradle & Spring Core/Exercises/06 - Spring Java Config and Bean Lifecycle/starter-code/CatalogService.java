package com.library;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Simulates a book catalog service with lifecycle management.
 */
public class CatalogService {

    private final NotificationService notificationService;

    // TODO: Add a constructor that accepts NotificationService and assigns it to the field
    public CatalogService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // TODO: Add a @PostConstruct method named onStartup()
    //       It should print: [CatalogService] Initialized — loading catalog cache


    // TODO: Add a @PreDestroy method named onShutdown()
    //       It should print: [CatalogService] Destroyed — releasing catalog cache


    /**
     * Simulates searching the catalog.
     *
     * @param keyword the search term
     * @return a result string
     */
    public String search(String keyword) {
        // TODO: Return "Results for: " + keyword
        return null;
    }
}
