package com.academy.notification.service;

import com.academy.notification.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Sends notifications via SMS gateway.
 *
 * Demonstrates: SETTER INJECTION
 */
@Slf4j
// TODO Task 4: Add the @Service annotation
public class SmsService implements NotificationService {

    private String gatewayUrl;
    private String apiKey;

    // TODO Task 4: Implement Setter Injection
    //   Add a setter method for gatewayUrl and inject the value from application.properties:
    //
    //   @Autowired
    //   @Value("${sms.gateway.url}")
    //   public void setGatewayUrl(String gatewayUrl) {
    //       this.gatewayUrl = gatewayUrl;
    //   }
    //
    //   Add another setter for apiKey using @Value("${sms.api.key}")


    @Override
    public boolean send(Notification notification) {
        // Simulated SMS sending
        log.info("[SMS] Gateway: {} | To: {} | Message: {}",
                gatewayUrl, notification.getRecipient(), notification.getBody());

        notification.setStatus("SENT");
        return true;
    }

    @Override
    public String getChannel() {
        return "SMS";
    }
}
