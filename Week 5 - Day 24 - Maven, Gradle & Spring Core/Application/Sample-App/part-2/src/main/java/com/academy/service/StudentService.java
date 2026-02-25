package com.academy.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Demonstrates CONSTRUCTOR INJECTION — the recommended style.
 *
 * Benefits:
 *   • greeter field can be final (immutable)
 *   • Spring fails at startup if the dependency is missing
 *   • Easy to unit-test: new StudentService(mockGreeter)
 */
@Service
@Slf4j   // Lombok generates: private static final Logger log = LoggerFactory.getLogger(StudentService.class);
public class StudentService {

    // Injected via constructor — note the 'final' keyword (immutability)
    private final GreetingService greeter;
    private int enrollmentCount = 0;

    /**
     * Constructor injection.
     * @Qualifier selects the Spanish greeter specifically.
     * Remove @Qualifier to use the @Primary one (English).
     */
    public StudentService(@Qualifier("englishGreetingService") GreetingService greeter) {
        this.greeter = greeter;
        log.info("StudentService created with greeter: {}", greeter.getClass().getSimpleName());
    }

    public String enrollStudent(String name, String major) {
        enrollmentCount++;
        String welcomeMsg = greeter.greet(name);
        log.info("Enrolled student #{}: {} in {}", enrollmentCount, name, major);
        return welcomeMsg + " You are enrolled in " + major + " (student #" + enrollmentCount + ").";
    }

    public int getEnrollmentCount() { return enrollmentCount; }
}
