package com.library;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LibraryConfig {

    @Bean
    public NotificationService notificationService() {
        return new NotificationService();
    }

    // initMethod / destroyMethod are called by Spring at the appropriate lifecycle points
    @Bean(initMethod = "onStartup", destroyMethod = "onShutdown")
    public CatalogService catalogService() {
        // Constructor injection: pass the dependency explicitly
        return new CatalogService(notificationService());
    }
}
