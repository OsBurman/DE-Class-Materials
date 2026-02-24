package com.library;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LibraryConfig {

    // TODO 1: Declare a @Bean method that returns a new NotificationService instance
    //         Method name: notificationService


    // TODO 2: Declare a @Bean method that returns a new CatalogService instance
    //         Inject notificationService() via the constructor: new CatalogService(notificationService())
    //
    //         Add initMethod and destroyMethod to the @Bean annotation:
    //         @Bean(initMethod = "onStartup", destroyMethod = "onShutdown")
    //         This tells Spring to call these methods at the appropriate lifecycle points.

}
