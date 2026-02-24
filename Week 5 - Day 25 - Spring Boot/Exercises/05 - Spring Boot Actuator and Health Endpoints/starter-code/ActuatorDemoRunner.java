package com.library;

// TODO: Import @Component, CommandLineRunner
// TODO: Import HealthEndpoint from org.springframework.boot.actuate.health

/**
 * Verifies Actuator is working by programmatically querying the HealthEndpoint bean.
 *
 * TODO:
 *   1. Add @Component and implement CommandLineRunner
 *   2. Inject HealthEndpoint via constructor injection
 *   3. In run():
 *        - Call healthEndpoint.health() to get the SystemHealth
 *        - Call .getStatus() on the result to get the Status object
 *        - Print "Application health: " + status
 */
public class ActuatorDemoRunner {

    // TODO: declare HealthEndpoint field

    // TODO: constructor

    // TODO: implement run(String... args)
}
