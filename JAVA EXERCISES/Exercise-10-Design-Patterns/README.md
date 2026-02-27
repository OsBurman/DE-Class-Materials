# Exercise 10 — Design Patterns

## Overview
Build a **Smart Notification System** that applies five classic Gang-of-Four design patterns.

## Learning Objectives
- Implement the **Singleton** pattern (thread-safe)
- Implement the **Factory** pattern
- Implement the **Observer** pattern
- Implement the **Builder** pattern
- Implement the **Strategy** pattern

## Setup
```bash
cd Exercise-10-Design-Patterns/starter-code/src
javac *.java
java Main
```

## Files

| File | Pattern | Role |
|------|---------|------|
| `Logger.java` | Singleton | Single app-wide logger |
| `Notification.java` | Builder | Fluent notification construction |
| `NotificationFactory.java` | Factory | Create notifications by type |
| `DeliveryStrategy.java` | Strategy | Swappable delivery algorithms |
| `EventBus.java` | Observer | Publish/subscribe event system |
| `Main.java` | — | Driver |

## Your TODOs

### Logger (Singleton)
- Private static `instance` field
- Private constructor
- `getInstance()` — return the single instance (use double-checked locking)
- `log(String)` — prints `[LOG hh:mm:ss] message`

### Notification (Builder)
- Fields: `title`, `body`, `recipient`, `priority`, `type`
- Private constructor taking a `Builder` inner class
- `Builder` with fluent setters returning `this`
- `Builder.build()` validates that `title` and `recipient` are set

### NotificationFactory (Factory)
- Static `create(String type)` returns a pre-configured `Notification`
- Types: `"alert"`, `"reminder"`, `"promo"` → each returns a builder-configured instance

### DeliveryStrategy (Strategy)
- Interface `DeliveryStrategy` with `void deliver(Notification n)`
- Implementations: `EmailStrategy`, `SmsStrategy`, `PushStrategy`
- `NotificationSender` has `setStrategy(DeliveryStrategy)` and `send(Notification)`

### EventBus (Observer)
- Interface `EventListener` with `void onEvent(String eventType, Object data)`
- `EventBus` has `subscribe(String eventType, EventListener)` and `publish(String eventType, Object data)`
- All listeners for the event type are called when published

## Expected Output
```
[LOG ...] App started
Built notification: [ALERT] 'Server Down' → admin@example.com (HIGH)
Factory alert: [ALERT] 'System Alert' → ops-team
Email: Sending to admin@example.com — Subject: Server Down
SMS:   Sending SMS to +1-555-0100 — Server Down
Push:  Push notification → device:admin — Server Down
[EventBus] UserLogin received by SecurityListener: alice
[EventBus] UserLogin received by AuditListener: alice
[EventBus] PaymentProcessed received by AuditListener: $99.99
```
