package com.academy.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

// TODO Task 8: Add the following annotations to this class:
//   @Configuration       — marks this as a Spring configuration class
//   @ComponentScan(basePackages = "com.academy.notification")  — enables component scanning
//   @PropertySource("classpath:application.properties")        — loads the properties file

/**
 * Spring Java configuration class.
 *
 * This replaces a beans.xml file entirely.
 * All beans not picked up by @ComponentScan should be declared here as @Bean
 * methods.
 */
public class AppConfig {

    // TODO Task 8: Add @Bean methods for any beans that can't use
    // @Component/@Service
    // Hint: EmailService needs @Value-injected constructor args — declare it as a
    // @Bean here
    // so you can pass the @Value properties explicitly.
    //
    // Example:
    // @Bean
    // public EmailService emailService(NotificationRepository repository,
    // @Value("${email.from}") String from,
    // @Value("${email.smtp.host}") String smtp) {
    // return new EmailService(repository, from, smtp);
    // }

    // TODO Task 9: Declare a @Bean for a PropertySourcesPlaceholderConfigurer
    // This is required in Spring Core (non-Boot) to resolve @Value placeholders:
    //
    // @Bean
    // public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
    // return new PropertySourcesPlaceholderConfigurer();
    // }
}
