/**
 * FACTORY PATTERN — NotificationFactory
 *
 * Creates pre-configured Notification objects by type string.
 *
 * TODO: Implement create(String type) using a switch expression:
 *   "alert"    → new Notification.Builder()
 *                  .type("ALERT").title("System Alert")
 *                  .recipient("ops-team").priority("HIGH")
 *                  .body("A system alert has been triggered.")
 *                  .build()
 *   "reminder" → type=REMINDER, title="Reminder", recipient="all-users",
 *                priority="MEDIUM", body="Don't forget your scheduled task."
 *   "promo"    → type=PROMO, title="Special Offer", recipient="customers",
 *                priority="LOW", body="Check out today's deals!"
 *   default    → throw new IllegalArgumentException("Unknown type: " + type)
 */
public class NotificationFactory {

    public static Notification create(String type) {
        // TODO: implement with switch expression / switch statement
        throw new IllegalArgumentException("Unknown type: " + type);
    }
}
