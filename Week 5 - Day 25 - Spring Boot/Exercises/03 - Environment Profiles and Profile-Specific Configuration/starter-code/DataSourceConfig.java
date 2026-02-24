package com.library.config;

// TODO: Import @Bean, @Configuration from org.springframework.context.annotation
// TODO: Import @Profile from org.springframework.context.annotation

/**
 * Registers profile-specific beans.
 *
 * TODO: Create TWO static nested (or separate) @Configuration classes:
 *
 *   DevDataSourceConfig  — annotated @Configuration @Profile("dev")
 *     Contains a @Bean method:
 *       public String dataSourceDescription() {
 *           return "H2 in-memory — dev profile active";
 *       }
 *
 *   ProdDataSourceConfig — annotated @Configuration @Profile("prod")
 *     Contains a @Bean method with the SAME name:
 *       public String dataSourceDescription() {
 *           return "PostgreSQL — prod profile active";
 *       }
 *
 * Spring will register exactly ONE of these beans depending on the active profile.
 */
public class DataSourceConfig {

    // TODO: implement DevDataSourceConfig inner class here

    // TODO: implement ProdDataSourceConfig inner class here
}
