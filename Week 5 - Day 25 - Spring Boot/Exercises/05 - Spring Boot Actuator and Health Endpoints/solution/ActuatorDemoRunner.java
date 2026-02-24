package com.library;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.stereotype.Component;

/**
 * Verifies Actuator is wired correctly by querying the HealthEndpoint bean directly.
 *
 * HealthEndpoint is auto-registered by spring-boot-starter-actuator.
 * Calling health() returns a SystemHealth aggregate built from all registered HealthIndicators
 * (DiskSpaceHealthIndicator, PingHealthIndicator, etc.).
 */
@Component
public class ActuatorDemoRunner implements CommandLineRunner {

    private final HealthEndpoint healthEndpoint;

    public ActuatorDemoRunner(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @Override
    public void run(String... args) {
        // Status.UP, Status.DOWN, Status.OUT_OF_SERVICE, etc.
        System.out.println("Application health: " + healthEndpoint.health().getStatus());
    }
}
