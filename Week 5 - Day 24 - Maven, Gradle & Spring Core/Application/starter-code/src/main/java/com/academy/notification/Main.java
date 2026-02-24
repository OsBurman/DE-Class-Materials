package com.academy.notification;

import com.academy.notification.config.AppConfig;
import com.academy.notification.model.Notification;
import com.academy.notification.service.NotificationRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Entry point for the Notification Service demo.
 *
 * This class is COMPLETE — do not modify.
 * Your job is to implement the classes that this class uses.
 *
 * Run with: mvn compile exec:java -Dexec.mainClass="com.academy.notification.Main"
 *       or: mvn package && java -jar target/notification-service-0.0.1-SNAPSHOT.jar
 */
@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Starting Notification Service...");

        // Bootstrap the Spring IoC container using your AppConfig
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // Get the NotificationRouter bean from the container
        NotificationRouter router = context.getBean(NotificationRouter.class);

        // Test 1: Send an EMAIL notification
        Notification emailNotification = Notification.builder()
                .recipient("student@academy.com")
                .subject("Welcome to the Academy!")
                .body("Your account has been created successfully.")
                .channel("EMAIL")
                .build();

        log.info("--- Sending EMAIL notification ---");
        router.route(emailNotification);

        // Test 2: Send an SMS notification
        Notification smsNotification = Notification.builder()
                .recipient("+1-555-0100")
                .subject("OTP Code")
                .body("Your one-time password is: 847291")
                .channel("SMS")
                .build();

        log.info("--- Sending SMS notification ---");
        router.route(smsNotification);

        // Test 3: Demonstrate prototype scope
        // Two calls to getBean should return DIFFERENT instances for prototype-scoped beans
        log.info("--- Demonstrating Bean Scopes ---");
        Object repo1 = context.getBean("notificationRepository");
        Object repo2 = context.getBean("notificationRepository");
        log.info("Same instance? {}", repo1 == repo2);  // Should print: false (prototype)

        Object emailService1 = context.getBean("emailService");
        Object emailService2 = context.getBean("emailService");
        log.info("EmailService singleton? {}", emailService1 == emailService2);  // Should print: true (singleton)

        log.info("Notification Service completed.");

        // Close the context to trigger @PreDestroy methods
        ((AnnotationConfigApplicationContext) context).close();
    }
}
