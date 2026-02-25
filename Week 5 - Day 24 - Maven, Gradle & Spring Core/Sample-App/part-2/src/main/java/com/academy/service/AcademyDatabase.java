package com.academy.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Demonstrates @PostConstruct and @PreDestroy lifecycle callbacks.
 * This simulates a connection pool that opens on startup and closes on shutdown.
 */
@Component
@Slf4j
public class AcademyDatabase {

    private boolean connected = false;

    /**
     * Called AFTER Spring injects all dependencies.
     * Use for: initialization, validation, opening resources.
     */
    @PostConstruct
    public void init() {
        connected = true;
        log.info("@PostConstruct — AcademyDatabase connection pool opened (connected={})", connected);
    }

    public boolean isConnected() { return connected; }

    /**
     * Called BEFORE Spring destroys the bean (on ApplicationContext.close()).
     * Use for: releasing resources, closing connections, flushing caches.
     */
    @PreDestroy
    public void cleanup() {
        connected = false;
        log.info("@PreDestroy  — AcademyDatabase connection pool closed (connected={})", connected);
    }
}
