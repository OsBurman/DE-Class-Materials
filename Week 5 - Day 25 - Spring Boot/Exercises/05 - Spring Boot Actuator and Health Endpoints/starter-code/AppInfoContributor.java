package com.library.actuator;

// TODO: Import @Component from org.springframework.stereotype
// TODO: Import InfoContributor and Info from org.springframework.boot.actuate.info
// TODO: Import LocalDateTime from java.time

/**
 * Contributes custom key-value pairs to the /actuator/info endpoint.
 *
 * Spring Boot calls contribute() on every InfoContributor bean it finds
 * and merges all results into the JSON response.
 *
 * TODO:
 *   1. Add @Component so Spring discovers this bean
 *   2. Implement InfoContributor
 *   3. Implement contribute(Info.Builder builder):
 *        builder.withDetail("startup-time", LocalDateTime.now().toString());
 *        builder.withDetail("java-version", System.getProperty("java.version"));
 */
public class AppInfoContributor {

    // TODO: implement contribute(Info.Builder builder)
}
