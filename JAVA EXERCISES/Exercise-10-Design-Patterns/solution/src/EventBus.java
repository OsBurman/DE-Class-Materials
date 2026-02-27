import java.util.*;

/** OBSERVER — EventListener + EventBus (Solution) */
interface EventListener {
    void onEvent(String eventType, Object data);
}

class EventBus {
    private final Map<String, List<EventListener>> listeners = new HashMap<>();

    public void subscribe(String eventType, EventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public void publish(String eventType, Object data) {
        List<EventListener> subs = listeners.getOrDefault(eventType, Collections.emptyList());
        for (EventListener l : subs) l.onEvent(eventType, data);
    }
}
