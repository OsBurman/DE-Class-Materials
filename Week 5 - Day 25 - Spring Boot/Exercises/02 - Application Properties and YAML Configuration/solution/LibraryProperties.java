package com.library.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Typed configuration holder for all "library.*" properties.
 *
 * @ConfigurationProperties(prefix = "library") tells Spring Boot to look for properties
 * that start with "library." and bind them to the fields of this class using relaxed binding:
 *   library.max-loan-days   → maxLoanDays  (kebab-case → camelCase)
 *   library.welcome-message → welcomeMessage
 *
 * Relaxed binding means you can use any of:
 *   library.max-loan-days   (kebab-case — recommended in .properties / .yml)
 *   library.maxLoanDays     (camelCase)
 *   library.MAX_LOAN_DAYS   (SCREAMING_SNAKE_CASE — useful for environment variables)
 */
@ConfigurationProperties(prefix = "library")
public class LibraryProperties {

    private int maxLoanDays;
    private String welcomeMessage;

    public int getMaxLoanDays() {
        return maxLoanDays;
    }

    public void setMaxLoanDays(int maxLoanDays) {
        this.maxLoanDays = maxLoanDays;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }
}
