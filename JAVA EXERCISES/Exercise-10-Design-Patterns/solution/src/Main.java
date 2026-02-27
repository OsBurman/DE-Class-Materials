/** Exercise 10 — Design Patterns: Main driver (Solution) */
public class Main {
    public static void main(String[] args) {
        Logger log = Logger.getInstance();
        log.log("App started");
        System.out.println("Same logger instance: " + (log == Logger.getInstance()));

        Notification n = new Notification.Builder()
                .type("ALERT").title("Server Down")
                .body("Production server is not responding.")
                .recipient("admin@example.com").priority("HIGH").build();
        System.out.println("Built notification: " + n);

        System.out.println("Factory alert:    " + NotificationFactory.create("alert"));
        System.out.println("Factory reminder: " + NotificationFactory.create("reminder"));
        System.out.println("Factory promo:    " + NotificationFactory.create("promo"));

        NotificationSender sender = new NotificationSender();
        sender.setStrategy(new EmailStrategy());
        sender.send(n);
        sender.setStrategy(new SmsStrategy());
        sender.send(new Notification.Builder().type("ALERT").title("Server Down")
                .body("Production server is not responding.").recipient("+1-555-0100").priority("HIGH").build());
        sender.setStrategy(new PushStrategy());
        sender.send(new Notification.Builder().type("ALERT").title("Server Down")
                .body("Production server is not responding.").recipient("admin").priority("HIGH").build());

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
        bus.publish("UnknownEvent", "ignored");
    }
}
