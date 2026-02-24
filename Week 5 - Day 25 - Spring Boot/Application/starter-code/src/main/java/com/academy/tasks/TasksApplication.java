package com.academy.tasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Task Management API.
 *
 * This class is COMPLETE — do not modify.
 *
 * Run with:
 *   mvn spring-boot:run                           (default profile)
 *   mvn spring-boot:run -Dspring-boot.run.profiles=dev
 *   mvn spring-boot:run -Dspring-boot.run.profiles=prod
 */
@SpringBootApplication
public class TasksApplication {

    public static void main(String[] args) {
        SpringApplication.run(TasksApplication.class, args);
    }
}
