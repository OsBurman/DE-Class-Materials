package com.library;

import com.library.config.LibraryProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Runs immediately after the Spring context is fully started.
 *
 * CommandLineRunner is a Spring Boot callback — every @Component that implements it
 * will have run() called once, in application-startup order.
 * The String... args are the command-line arguments passed to main().
 */
@Component
public class ConfigDemoRunner implements CommandLineRunner {

    // Constructor injection — Spring detects the single constructor and injects LibraryProperties
    private final LibraryProperties props;

    public ConfigDemoRunner(LibraryProperties props) {
        this.props = props;
    }

    @Override
    public void run(String... args) {
        // These values were bound from application.properties (or application.yml)
        System.out.println("Max loan days: " + props.getMaxLoanDays());
        System.out.println("Welcome: " + props.getWelcomeMessage());
    }
}
