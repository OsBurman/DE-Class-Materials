package com.library.config;

// TODO: Import @Configuration from org.springframework.context.annotation
// TODO: Import @EnableConfigurationProperties from org.springframework.boot.context.properties

/**
 * Spring configuration class that activates LibraryProperties binding.
 *
 * TODO:
 *   1. Add @Configuration to mark this as a Spring configuration class
 *   2. Add @EnableConfigurationProperties(LibraryProperties.class)
 *      This tells Spring Boot to bind the "library.*" properties into a LibraryProperties bean
 */
public class LibraryConfig {
    // No @Bean methods needed — @EnableConfigurationProperties registers the bean for us
}
