package com.library;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Demonstrates which profile is active and which DataSource bean was selected.
 */
@Component
public class ProfileDemoRunner implements CommandLineRunner {

    private final Environment env;
    private final String dataSourceDescription;

    // Spring injects Environment automatically; @Qualifier selects the bean by name
    public ProfileDemoRunner(Environment env,
                             @Qualifier("dataSourceDescription") String dataSourceDescription) {
        this.env = env;
        this.dataSourceDescription = dataSourceDescription;
    }

    @Override
    public void run(String... args) {
        // getActiveProfiles() returns all active profile names as a String[]
        String activeProfile = env.getActiveProfiles().length > 0
                ? env.getActiveProfiles()[0]
                : "default";

        System.out.println("Active profile: " + activeProfile);
        System.out.println("DataSource: " + dataSourceDescription);
    }
}
