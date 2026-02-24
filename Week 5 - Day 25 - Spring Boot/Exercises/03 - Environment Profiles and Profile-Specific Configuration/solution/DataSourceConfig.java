package com.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Registers a different dataSourceDescription bean depending on the active profile.
 *
 * Spring evaluates the @Profile condition at context startup:
 *   - If "dev" is active  → DevDataSourceConfig is loaded,  ProdDataSourceConfig is skipped
 *   - If "prod" is active → ProdDataSourceConfig is loaded, DevDataSourceConfig is skipped
 *
 * Both inner classes declare a @Bean named "dataSourceDescription" —
 * only one will ever exist in the context at runtime.
 */
public class DataSourceConfig {

    @Configuration
    @Profile("dev")
    static class DevDataSourceConfig {

        @Bean
        public String dataSourceDescription() {
            // H2 in-memory DB — fast, zero-setup, good for local dev and tests
            return "H2 in-memory — dev profile active";
        }
    }

    @Configuration
    @Profile("prod")
    static class ProdDataSourceConfig {

        @Bean
        public String dataSourceDescription() {
            // Real PostgreSQL — used only when spring.profiles.active=prod
            return "PostgreSQL — prod profile active";
        }
    }
}
