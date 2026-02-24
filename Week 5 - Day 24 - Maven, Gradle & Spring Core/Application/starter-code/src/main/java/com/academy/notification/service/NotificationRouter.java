package com.academy.notification.service;

import com.academy.notification.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Routes notifications to the appropriate service based on channel.
 *
 * Demonstrates: FIELD INJECTION (and also injecting a List of beans)
 */
@Slf4j
// TODO Task 5: Add the @Component annotation
public class NotificationRouter {

    // TODO Task 5: Implement Field Injection
    //   Use @Autowired to inject a List<NotificationService>
    //   Spring will automatically collect ALL beans implementing NotificationService
    //   (both EmailService and SmsService) into this list.
    //
    //   @Autowired
    //   private List<NotificationService> services;

    private List<NotificationService> services;

    // TODO Task 6: Build a Map<String, NotificationService> from the injected list
    //   Key = service.getChannel(), Value = service
    //   Hint: use Collectors.toMap() in a @PostConstruct method or constructor
    //
    //   @PostConstruct
    //   private void init() {
    //       serviceMap = services.stream()
    //           .collect(Collectors.toMap(NotificationService::getChannel, Function.identity()));
    //       log.info("Loaded notification channels: {}", serviceMap.keySet());
    //   }

    private Map<String, NotificationService> serviceMap;

    /**
     * Route and send the notification using the correct service.
     * @param notification the notification to deliver
     */
    public void route(Notification notification) {
        // TODO Task 7: Look up the service by notification.getChannel()
        //   If found, call service.send(notification)
        //   If not found, log a warning: "Unknown notification channel: {channel}"

        log.warn("TODO: implement routing logic");
    }
}
