import java.util.*;

/**
 * OBSERVER PATTERN — EventListener, EventBus
 *
 * The observer pattern lets objects subscribe to events and be notified.
 *
 * TODO 1: Define interface EventListener with:
 *           void onEvent(String eventType, Object data);
 *
 * TODO 2: Implement EventBus:
 *           - private Map<String, List<EventListener>> listeners
 *           - subscribe(String eventType, EventListener listener)
 *               → add listener to the list for that event type
 *                 (create list if absent: listeners.computeIfAbsent(eventType, k -> new ArrayList<>()))
 *           - publish(String eventType, Object data)
 *               → call onEvent on all listeners registered for eventType
 *                 (if none registered, do nothing)
 *
 * TODO 3: In Main.java, create two concrete EventListeners:
 *           SecurityListener — prints "[EventBus] <eventType> received by SecurityListener: <data>"
 *           AuditListener    — prints "[EventBus] <eventType> received by AuditListener: <data>"
 *         Subscribe SecurityListener to "UserLogin"
 *         Subscribe AuditListener to both "UserLogin" and "PaymentProcessed"
 *         Publish: eventType="UserLogin",          data="alice"
 *         Publish: eventType="PaymentProcessed",   data="$99.99"
 */

// TODO 1
// interface EventListener { ... }

// TODO 2
class EventBus {
    private Map<String, List<Object>> listeners = new HashMap<>(); // placeholder
    // TODO: replace with correct Map type and implement subscribe/publish
    void subscribe(String eventType, Object listener) { /* TODO */ }
    void publish(String eventType, Object data) { /* TODO */ }
}
