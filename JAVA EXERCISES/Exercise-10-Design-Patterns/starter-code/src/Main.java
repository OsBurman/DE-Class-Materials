/**
 * Exercise 10 — Design Patterns
 * Main driver — do not modify
 */
public class Main {
    public static void main(String[] args) {
        // ---- Singleton ----
        Logger log = Logger.getInstance();
        log.log("App started");
        // Verify same instance
        Logger log2 = Logger.getInstance();
        System.out.println("Same logger instance: " + (log == log2));

        // ---- Builder ----
        Notification n = new Notification.Builder()
                .type("ALERT")
                .title("Server Down")
                .body("Production server is not responding.")
                .recipient("admin@example.com")
                .priority("HIGH")
                .build();
        System.out.println("Built notification: " + n);

        // ---- Factory ----
        Notification alert = NotificationFactory.create("alert");
        Notification reminder = NotificationFactory.create("reminder");
        Notification promo = NotificationFactory.create("promo");
        System.out.println("Factory alert:    " + alert);
        System.out.println("Factory reminder: " + reminder);
        System.out.println("Factory promo:    " + promo);

        // ---- Strategy ----
        NotificationSender sender = new NotificationSender();
        sender.setStrategy(new EmailStrategy());
        sender.send(n);
        sender.setStrategy(new SmsStrategy());
        sender.send(new Notification.Builder()
                .type("ALERT").title("Server Down")
                .body("Production server is not responding.")
                .recipient("+1-555-0100").priority("HIGH").build());
        sender.setStrategy(new PushStrategy());
        sender.send(new Notification.Builder()
                .type("ALERT").title("Server Down")
                .body("Production server is not responding.")
                .recipient("admin").priority("HIGH").build());

        // ---- Observer ----
        EventBus bus = new EventBus();
        EventListener security = (type, data) -> System.out
                .println("[EventBus] " + type + " received by SecurityListener: " + data);
        EventListener audit = (type, data) -> System.out
                .println("[EventBus] " + type + " received by AuditListener: " + data);

        bus.subscribe("UserLogin", security);
        bus.subscribe("UserLogin", audit);
        bus.subscribe("PaymentProcessed", audit);

        bus.publish("UserLogin", "alice");
        bus.publish("PaymentProcessed", "$99.99");
        bus.publish("UnknownEvent", "ignored"); // no listeners — silent
    }
}
