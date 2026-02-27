/**
 * BUILDER PATTERN — Notification & Notification.Builder
 *
 * Fluent builder for constructing Notification objects.
 *
 * TODO 1: Add private fields: title, body, recipient, priority, type (all String)
 * TODO 2: Make the Notification constructor private, taking a Builder parameter.
 * TODO 3: Complete the Builder inner class:
 *           - fields mirroring Notification
 *           - fluent setters returning Builder (this)
 *           - build() that validates title & recipient are non-null/non-empty,
 *             then returns new Notification(this)
 * TODO 4: Implement toString() showing type, title, recipient, priority
 */
public class Notification {

    // TODO 1: private String title, body, recipient, priority, type;

    // Getters (needed by strategy classes)
    public String getTitle()     { return ""; /* TODO: return title */ }
    public String getBody()      { return ""; /* TODO: return body  */ }
    public String getRecipient() { return ""; /* TODO: return recipient */ }
    public String getPriority()  { return ""; /* TODO: return priority */ }
    public String getType()      { return ""; /* TODO: return type */ }

    // TODO 2: private Notification(Builder b) { ... }

    @Override public String toString() {
        // TODO 4: return formatted string
        return "Notification";
    }

    // TODO 3: public static class Builder { ... }
    public static class Builder {
        // TODO: fields, setters, build()
        public Notification build() {
            // TODO: validate and return new Notification(this)
            return new Notification();
        }
        // Temporary no-arg so starter compiles
        private Notification() { }
        // ↑ remove this inner constructor — it's just to allow compilation
    }

    // Temporary public constructor so file compiles before Builder is done
    Notification() { }
}
