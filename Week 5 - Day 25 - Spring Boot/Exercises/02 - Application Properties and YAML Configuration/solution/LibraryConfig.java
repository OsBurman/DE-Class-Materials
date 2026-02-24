package com.library.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Activates the LibraryProperties @ConfigurationProperties binding.
 *
 * @EnableConfigurationProperties registers LibraryProperties as a Spring bean
 * and triggers property binding. Without this (or @Component on LibraryProperties),
 * the class would never be instantiated and the binding would not occur.
 *
 * Prefer @EnableConfigurationProperties over putting @Component on the properties
 * class — it makes the activation explicit and keeps the properties class a plain POJO.
 */
@Configuration
@EnableConfigurationProperties(LibraryProperties.class)
public class LibraryConfig {
    // No @Bean methods required — @EnableConfigurationProperties handles registration
}
