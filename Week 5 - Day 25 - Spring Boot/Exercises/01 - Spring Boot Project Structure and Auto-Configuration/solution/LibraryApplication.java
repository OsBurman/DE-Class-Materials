package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Library Service.
 *
 * @SpringBootApplication combines three annotations:
 *   @Configuration         — this class can declare @Bean methods
 *   @EnableAutoConfiguration — tell Spring Boot to auto-configure based on the classpath
 *   @ComponentScan          — scan com.library and sub-packages for Spring components
 *
 * Because spring-boot-starter-web is on the classpath, auto-configuration fires:
 *   - TomcatServletWebServerFactoryAutoConfiguration → starts embedded Tomcat on port 8080
 *   - DispatcherServletAutoConfiguration             → registers the Spring MVC DispatcherServlet
 *   - JacksonAutoConfiguration                       → registers an ObjectMapper for JSON
 */
@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        // SpringApplication.run() bootstraps the entire Spring context and starts the server
        SpringApplication.run(LibraryApplication.class, args);
    }
}
