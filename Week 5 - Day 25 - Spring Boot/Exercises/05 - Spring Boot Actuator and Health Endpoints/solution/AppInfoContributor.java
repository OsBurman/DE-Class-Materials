package com.library.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Adds custom key-value pairs to the /actuator/info response.
 *
 * Spring Boot discovers every @Component that implements InfoContributor
 * and calls contribute() on each one, merging the results into the JSON body.
 *
 * This is the correct extension point — prefer it over:
 *   - Overriding the entire endpoint (too invasive)
 *   - Writing to info.* properties (static only; can't compute runtime values)
 */
@Component
public class AppInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        // Dynamic runtime values — computed at application startup
        builder.withDetail("startup-time", LocalDateTime.now().toString());
        builder.withDetail("java-version", System.getProperty("java.version"));
    }
}
