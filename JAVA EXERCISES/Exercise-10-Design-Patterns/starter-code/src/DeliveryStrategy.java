/**
 * STRATEGY PATTERN — DeliveryStrategy, concrete strategies, NotificationSender
 *
 * The strategy pattern allows swapping delivery algorithms at runtime.
 *
 * TODO 1: Define interface DeliveryStrategy with:
 * void deliver(Notification notification);
 *
 * TODO 2: Implement EmailStrategy:
 * Print "Email: Sending to <recipient> — Subject: <title>"
 *
 * TODO 3: Implement SmsStrategy:
 * Print "SMS: Sending SMS to <recipient> — <body>"
 *
 * TODO 4: Implement PushStrategy:
 * Print "Push: Push notification → device:<recipient> — <title>"
 *
 * TODO 5: Implement NotificationSender:
 * - field: DeliveryStrategy strategy
 * - setStrategy(DeliveryStrategy) — swap strategy at runtime
 * - send(Notification) — delegates to strategy.deliver(n)
 */

// TODO 1
// interface DeliveryStrategy { ... }

// TODO 2
// class EmailStrategy implements DeliveryStrategy { ... }

// TODO 3
// class SmsStrategy implements DeliveryStrategy { ... }

// TODO 4
// class PushStrategy implements DeliveryStrategy { ... }

// TODO 5
class NotificationSender {
    // TODO: add strategy field and methods
    void send(Notification n) {
        System.out.println("(strategy not yet set)");
    }
}
