/** FACTORY — NotificationFactory (Solution) */
public class NotificationFactory {
    public static Notification create(String type) {
        return switch (type.toLowerCase()) {
            case "alert" -> new Notification.Builder()
                    .type("ALERT").title("System Alert").recipient("ops-team")
                    .priority("HIGH").body("A system alert has been triggered.").build();
            case "reminder" -> new Notification.Builder()
                    .type("REMINDER").title("Reminder").recipient("all-users")
                    .priority("MEDIUM").body("Don't forget your scheduled task.").build();
            case "promo" -> new Notification.Builder()
                    .type("PROMO").title("Special Offer").recipient("customers")
                    .priority("LOW").body("Check out today's deals!").build();
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }
}
