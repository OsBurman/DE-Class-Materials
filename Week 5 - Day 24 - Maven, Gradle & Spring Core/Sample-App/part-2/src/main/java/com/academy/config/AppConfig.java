package com.academy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import com.academy.model.Course;

/**
 * Java-based Spring configuration.
 * @Configuration marks this as a source of @Bean definitions.
 * @ComponentScan tells Spring to scan the package for @Component, @Service, etc.
 */
@Configuration
@ComponentScan(basePackages = "com.academy")
public class AppConfig {

    /**
     * @Bean registers a method's return value as a Spring-managed bean.
     * Bean name defaults to the method name: "course"
     * Scope PROTOTYPE = new instance every time getBean() is called.
     */
    @Bean
    @Scope("prototype")
    public Course course() {
        return new Course();
    }

    // Note: StudentService, GreetingService implementations, and AcademyDatabase
    // are discovered automatically by @ComponentScan because they have @Service / @Component.
    // Only beans that CAN'T use component scanning need explicit @Bean methods here.
}
