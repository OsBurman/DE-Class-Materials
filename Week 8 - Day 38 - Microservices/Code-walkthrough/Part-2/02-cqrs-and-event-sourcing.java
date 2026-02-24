// =============================================================================
// Day 38 — Microservices | Part 2
// File: 02-cqrs-and-event-sourcing.java
// Topic: CQRS (Command Query Responsibility Segregation),
//        Event Sourcing, Database Per Service Pattern
// Domain: Bookstore Application — Order Service
// =============================================================================

package com.bookstore.order.cqrs;

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1: DATABASE PER SERVICE PATTERN
// ─────────────────────────────────────────────────────────────────────────────

/**
 * DATABASE PER SERVICE — Why it matters
 *
 * ANTI-PATTERN (shared database):
 * ┌─────────────────────────────────────────────────────┐
 * │ Book Service  ──┐                                   │
 * │ Order Service ──┼──► Shared PostgreSQL DB  ← BAD!   │
 * │ User Service  ──┘                                   │
 * └─────────────────────────────────────────────────────┘
 * Problems:
 *   - Schema changes in one service break other services
 *   - Services become tightly coupled through the database
 *   - Cannot independently scale storage per service
 *   - Cannot use different database technologies per service
 *
 * CORRECT PATTERN (database per service):
 * ┌──────────────────────────────────────────────────────────────────────────┐
 * │ Book Service      → PostgreSQL (books_db)       — relational catalog     │
 * │ Order Service     → PostgreSQL (orders_db)      — relational transactions│
 * │ User Service      → PostgreSQL (users_db)       — relational profiles    │
 * │ Inventory Service → Redis       (inventory_db)  — fast key-value stock   │
 * │ Session Service   → Redis       (sessions_db)   — fast session storage   │
 * └──────────────────────────────────────────────────────────────────────────┘
 *
 * Consequences:
 *   + Order Service can change its schema without affecting Book Service
 *   + Inventory Service can switch from Redis to MongoDB — nobody cares
 *   + Each database scales independently
 *   - No cross-service ACID transactions → need Saga/eventual consistency
 *   - Joins across services require API calls, not SQL
 */

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2: CQRS — WHAT AND WHY
// ─────────────────────────────────────────────────────────────────────────────

/**
 * CQRS = Command Query Responsibility Segregation
 *
 * Core idea: Use DIFFERENT models for reads and writes.
 *
 * Traditional approach (one model for both):
 *   Write: POST /orders → OrderEntity → save to orders table
 *   Read:  GET /orders  → join orders + users + books → complex query
 *
 * Problem: The write model (normalized, ACID) is often terrible for reads.
 *   - The read model needs JOINs across 5 tables
 *   - Every GET /orders runs expensive queries
 *   - Cannot add an index for reads without impacting write performance
 *
 * CQRS solution — separate the models:
 *
 *                    ┌────── COMMAND SIDE ──────────────────────────┐
 * POST /orders ──────► OrderCommandHandler                          │
 *                    │   ↓ validates command                        │
 *                    │   ↓ writes to normalized Order DB            │
 *                    │   ↓ emits OrderCreatedEvent                 │
 *                    └─────────────────────────────────────────────┘
 *                             │ event
 *                             ▼
 *                    ┌────── EVENT PROPAGATION ─────────────────────┐
 *                    │   OrderProjectionHandler listens             │
 *                    │   Builds denormalized read view              │
 *                    │   Saves to read model (fast query table)     │
 *                    └─────────────────────────────────────────────┘
 *                             │ projection ready
 *                             ▼
 *                    ┌────── QUERY SIDE ────────────────────────────┐
 * GET /orders ───────► OrderQueryHandler                            │
 *                    │   Reads from denormalized read model         │
 *                    │   No JOINs needed — data pre-assembled       │
 *                    │   Returns in milliseconds                    │
 *                    └─────────────────────────────────────────────┘
 */

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3: COMMAND SIDE — COMMANDS AND HANDLERS
// ─────────────────────────────────────────────────────────────────────────────

import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

// Commands — represent intentions to change state
// Commands are IMPERATIVE: "Place this order", "Cancel this order"

record PlaceOrderCommand(
    String orderId,
    String userId,
    List<OrderLineItem> items,
    String shippingAddress
) {}

record CancelOrderCommand(
    String orderId,
    String cancelledBy,
    String reason
) {}

record OrderLineItem(
    String isbn,
    String bookTitle,
    int quantity,
    double unitPrice
) {}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4: DOMAIN EVENTS
// Events record what HAS happened (past tense)
// ─────────────────────────────────────────────────────────────────────────────

// Base class for all domain events
abstract class DomainEvent {
    private final String eventId;
    private final String eventType;
    private final String aggregateId;
    private final Instant occurredOn;
    private final int version;

    protected DomainEvent(String eventType, String aggregateId, int version) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.occurredOn = Instant.now();
        this.version = version;
    }

    public String getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public String getAggregateId() { return aggregateId; }
    public Instant getOccurredOn() { return occurredOn; }
    public int getVersion() { return version; }
}

// Events — PAST TENSE: "OrderWasPlaced", "OrderWasCancelled"
class OrderPlacedEvent extends DomainEvent {
    private final String userId;
    private final List<OrderLineItem> items;
    private final double totalAmount;
    private final String shippingAddress;

    public OrderPlacedEvent(String orderId, String userId,
                            List<OrderLineItem> items, double totalAmount,
                            String shippingAddress, int version) {
        super("ORDER_PLACED", orderId, version);
        this.userId = userId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
    }

    public String getUserId() { return userId; }
    public List<OrderLineItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public String getShippingAddress() { return shippingAddress; }
}

class OrderCancelledEvent extends DomainEvent {
    private final String cancelledBy;
    private final String reason;

    public OrderCancelledEvent(String orderId, String cancelledBy, String reason, int version) {
        super("ORDER_CANCELLED", orderId, version);
        this.cancelledBy = cancelledBy;
        this.reason = reason;
    }

    public String getCancelledBy() { return cancelledBy; }
    public String getReason() { return reason; }
}

class OrderShippedEvent extends DomainEvent {
    private final String trackingNumber;
    private final String carrier;

    public OrderShippedEvent(String orderId, String trackingNumber, String carrier, int version) {
        super("ORDER_SHIPPED", orderId, version);
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
    }

    public String getTrackingNumber() { return trackingNumber; }
    public String getCarrier() { return carrier; }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5: ORDER AGGREGATE (Write Model)
// The source of truth — holds business rules and raises events
// ─────────────────────────────────────────────────────────────────────────────

class Order {

    // State — derived by replaying events
    private String orderId;
    private String userId;
    private String status;
    private List<OrderLineItem> items;
    private double totalAmount;
    private String shippingAddress;
    private int version;

    // All events raised since last save (uncommitted events)
    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();

    // ── Factory method — creates new order from command ────────────────────
    public static Order place(PlaceOrderCommand cmd) {
        Order order = new Order();

        // Business rule validation
        if (cmd.items() == null || cmd.items().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        double total = cmd.items().stream()
            .mapToDouble(item -> item.quantity() * item.unitPrice())
            .sum();

        // Raise the event — do NOT set state directly
        OrderPlacedEvent event = new OrderPlacedEvent(
            cmd.orderId(), cmd.userId(), cmd.items(), total,
            cmd.shippingAddress(), 1
        );
        order.apply(event);  // Apply changes the state AND records the event

        return order;
    }

    // ── Cancel order ───────────────────────────────────────────────────────
    public void cancel(CancelOrderCommand cmd) {
        // Business rule: can only cancel pending or confirmed orders
        if ("SHIPPED".equals(this.status) || "DELIVERED".equals(this.status)) {
            throw new IllegalStateException(
                "Cannot cancel order in status: " + this.status
            );
        }
        apply(new OrderCancelledEvent(orderId, cmd.cancelledBy(), cmd.reason(), version + 1));
    }

    // ── Apply event — updates state based on event ────────────────────────
    // This is the ONLY place state is mutated
    private void apply(DomainEvent event) {
        switch (event) {
            case OrderPlacedEvent e -> {
                this.orderId = e.getAggregateId();
                this.userId = e.getUserId();
                this.items = e.getItems();
                this.totalAmount = e.getTotalAmount();
                this.shippingAddress = e.getShippingAddress();
                this.status = "PENDING";
                this.version = e.getVersion();
            }
            case OrderCancelledEvent e -> {
                this.status = "CANCELLED";
                this.version = e.getVersion();
            }
            case OrderShippedEvent e -> {
                this.status = "SHIPPED";
                this.version = e.getVersion();
            }
            default -> throw new IllegalArgumentException("Unknown event: " + event.getClass());
        }
        uncommittedEvents.add(event);  // Track for persistence
    }

    // ── Reconstitute from event history (Event Sourcing) ─────────────────
    public static Order reconstitute(List<DomainEvent> eventHistory) {
        Order order = new Order();
        for (DomainEvent event : eventHistory) {
            order.apply(event);           // Replay each event in sequence
            order.uncommittedEvents.clear(); // Clear — these are already committed
        }
        return order;
    }

    public List<DomainEvent> getUncommittedEvents() {
        return Collections.unmodifiableList(uncommittedEvents);
    }

    public void markEventsAsCommitted() {
        uncommittedEvents.clear();
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public double getTotalAmount() { return totalAmount; }
    public int getVersion() { return version; }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 6: EVENT STORE (Event Sourcing)
// Persists ALL events — the event log is the source of truth
// ─────────────────────────────────────────────────────────────────────────────

import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.*;

/**
 * EVENT SOURCING
 *
 * Traditional persistence: save the CURRENT state.
 *   INSERT INTO orders (id, status, total) VALUES ('ord-1', 'PENDING', 49.99)
 *   UPDATE orders SET status='CANCELLED' WHERE id='ord-1'
 *   → Previous state is GONE. You cannot ask "when was this cancelled?"
 *
 * Event sourcing: save ALL EVENTS that led to the current state.
 *   INSERT INTO event_store (aggregate_id, event_type, payload, version)
 *     VALUES ('ord-1', 'ORDER_PLACED',    '{...}', 1)
 *     VALUES ('ord-1', 'ORDER_SHIPPED',   '{...}', 2)
 *     VALUES ('ord-1', 'ORDER_DELIVERED', '{...}', 3)
 *
 *   Current state = replay all events in order.
 *   Complete audit trail — you can see EVERY state change with timestamp.
 *   You can "time-travel" — reconstruct state at any point in history.
 */

@Entity
@Table(name = "event_store")
class StoredEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String aggregateId;            // Which order this event belongs to

    @Column(nullable = false)
    private String aggregateType;          // "ORDER" — for multi-aggregate event stores

    @Column(nullable = false)
    private String eventType;             // "ORDER_PLACED", "ORDER_CANCELLED", etc.

    @Column(nullable = false)
    private int version;                  // Sequence number — detect concurrency conflicts

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;               // JSON-serialized event data

    @Column(nullable = false)
    private Instant occurredOn;

    // Standard constructors, getters, setters
    public StoredEvent() {}
    public StoredEvent(String aggregateId, String aggregateType,
                       String eventType, int version, String payload, Instant occurredOn) {
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.version = version;
        this.payload = payload;
        this.occurredOn = occurredOn;
    }

    public String getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public int getVersion() { return version; }
    public String getPayload() { return payload; }
    public Instant getOccurredOn() { return occurredOn; }
}

interface EventStoreRepository extends JpaRepository<StoredEvent, Long> {
    // Find all events for a given order, in version order
    List<StoredEvent> findByAggregateIdOrderByVersionAsc(String aggregateId);

    // Find events after a version (for optimistic concurrency check)
    boolean existsByAggregateIdAndVersion(String aggregateId, int version);
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 7: ORDER REPOSITORY — SAVES AND LOADS VIA EVENTS
// ─────────────────────────────────────────────────────────────────────────────

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEventPublisher;

@org.springframework.stereotype.Repository
class OrderRepository {

    private final EventStoreRepository eventStoreRepository;
    private final ApplicationEventPublisher eventPublisher;  // Publish events to Spring
    private final ObjectMapper objectMapper;

    public OrderRepository(EventStoreRepository eventStoreRepository,
                           ApplicationEventPublisher eventPublisher,
                           ObjectMapper objectMapper) {
        this.eventStoreRepository = eventStoreRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    /**
     * Save an order by persisting its uncommitted events.
     * Then publish them for projections + other services to react.
     */
    public void save(Order order) {
        for (DomainEvent event : order.getUncommittedEvents()) {
            // Optimistic concurrency check — prevent version conflicts
            if (eventStoreRepository.existsByAggregateIdAndVersion(
                    event.getAggregateId(), event.getVersion())) {
                throw new IllegalStateException(
                    "Concurrency conflict on order: " + event.getAggregateId()
                );
            }

            // Serialize event to JSON for storage
            String payload;
            try {
                payload = objectMapper.writeValueAsString(event);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize event", e);
            }

            // Persist the event
            StoredEvent stored = new StoredEvent(
                event.getAggregateId(),
                "ORDER",
                event.getEventType(),
                event.getVersion(),
                payload,
                event.getOccurredOn()
            );
            eventStoreRepository.save(stored);

            // Publish to Spring event system — projections will listen
            eventPublisher.publishEvent(event);
        }
        order.markEventsAsCommitted();
    }

    /**
     * Load an order by replaying ALL its events from the event store.
     * The order is fully reconstituted from its event history.
     */
    public Optional<Order> findById(String orderId) {
        List<StoredEvent> storedEvents = eventStoreRepository
            .findByAggregateIdOrderByVersionAsc(orderId);

        if (storedEvents.isEmpty()) {
            return Optional.empty();
        }

        // Deserialize each stored event back to domain event
        List<DomainEvent> domainEvents = storedEvents.stream()
            .map(this::deserializeEvent)
            .toList();

        // Reconstitute the order by replaying events
        return Optional.of(Order.reconstitute(domainEvents));
    }

    private DomainEvent deserializeEvent(StoredEvent stored) {
        try {
            return switch (stored.getEventType()) {
                case "ORDER_PLACED"    -> objectMapper.readValue(stored.getPayload(), OrderPlacedEvent.class);
                case "ORDER_CANCELLED" -> objectMapper.readValue(stored.getPayload(), OrderCancelledEvent.class);
                case "ORDER_SHIPPED"   -> objectMapper.readValue(stored.getPayload(), OrderShippedEvent.class);
                default -> throw new IllegalArgumentException("Unknown event type: " + stored.getEventType());
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize event: " + stored.getEventType(), e);
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 8: CQRS — QUERY SIDE (Read Models / Projections)
// Denormalized read model updated by listening to domain events
// ─────────────────────────────────────────────────────────────────────────────

import org.springframework.context.event.EventListener;

/**
 * OrderSummaryView — the READ model for GET /orders.
 *
 * Denormalized: all the data a client needs is pre-assembled.
 * No JOINs needed at query time — it's already flat and ready.
 */
@Entity
@Table(name = "order_summary_view")
class OrderSummaryView {

    @Id
    private String orderId;
    private String userId;
    private String userEmail;               // Denormalized from User Service
    private String status;
    private double totalAmount;
    private int itemCount;
    private String shippingAddress;
    private Instant placedAt;
    private Instant lastUpdatedAt;

    // Constructor, getters, setters
    public OrderSummaryView() {}
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public Instant getPlacedAt() { return placedAt; }
    public void setPlacedAt(Instant placedAt) { this.placedAt = placedAt; }
    public void setLastUpdatedAt(Instant t) { this.lastUpdatedAt = t; }
}

interface OrderSummaryViewRepository extends JpaRepository<OrderSummaryView, String> {
    List<OrderSummaryView> findByUserId(String userId);
    List<OrderSummaryView> findByStatus(String status);
    List<OrderSummaryView> findByUserIdOrderByPlacedAtDesc(String userId);
}

/**
 * OrderProjectionHandler — listens to domain events and updates the read model.
 *
 * This is eventual consistency in action:
 *   1. Command handler saves event → read model is NOT yet updated
 *   2. Event is published → projection handler receives it
 *   3. Projection handler updates OrderSummaryView
 *   4. Next GET /orders reads the updated view
 *
 * The gap between step 1 and 4 is the "eventual" in eventual consistency.
 * Usually milliseconds. Acceptable for most use cases.
 */
@Component
class OrderProjectionHandler {

    private final OrderSummaryViewRepository viewRepository;

    public OrderProjectionHandler(OrderSummaryViewRepository viewRepository) {
        this.viewRepository = viewRepository;
    }

    @EventListener
    public void on(OrderPlacedEvent event) {
        OrderSummaryView view = new OrderSummaryView();
        view.setOrderId(event.getAggregateId());
        view.setUserId(event.getUserId());
        view.setStatus("PENDING");
        view.setTotalAmount(event.getTotalAmount());
        view.setItemCount(event.getItems().size());
        view.setShippingAddress(event.getShippingAddress());
        view.setPlacedAt(event.getOccurredOn());
        view.setLastUpdatedAt(event.getOccurredOn());
        viewRepository.save(view);
        System.out.println("[Projection] OrderSummaryView created for: " + event.getAggregateId());
    }

    @EventListener
    public void on(OrderCancelledEvent event) {
        viewRepository.findById(event.getAggregateId()).ifPresent(view -> {
            view.setStatus("CANCELLED");
            view.setLastUpdatedAt(event.getOccurredOn());
            viewRepository.save(view);
        });
    }

    @EventListener
    public void on(OrderShippedEvent event) {
        viewRepository.findById(event.getAggregateId()).ifPresent(view -> {
            view.setStatus("SHIPPED");
            view.setLastUpdatedAt(event.getOccurredOn());
            viewRepository.save(view);
        });
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 9: CQRS — COMMAND AND QUERY HANDLERS + CONTROLLER
// ─────────────────────────────────────────────────────────────────────────────

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

// Command Handler — handles write operations
@Service
class OrderCommandHandler {

    private final OrderRepository orderRepository;

    public OrderCommandHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public String handle(PlaceOrderCommand command) {
        Order order = Order.place(command);
        orderRepository.save(order);
        return order.getOrderId();  // Return the new order ID
    }

    public void handle(CancelOrderCommand command) {
        Order order = orderRepository.findById(command.orderId())
            .orElseThrow(() -> new RuntimeException("Order not found: " + command.orderId()));
        order.cancel(command);
        orderRepository.save(order);
    }
}

// Query Handler — handles read operations
@Service
class OrderQueryHandler {

    private final OrderSummaryViewRepository viewRepository;

    public OrderQueryHandler(OrderSummaryViewRepository viewRepository) {
        this.viewRepository = viewRepository;
    }

    // FAST — single table lookup, no JOINs
    public Optional<OrderSummaryView> findById(String orderId) {
        return viewRepository.findById(orderId);
    }

    // FAST — single table query by index
    public List<OrderSummaryView> findByUser(String userId) {
        return viewRepository.findByUserIdOrderByPlacedAtDesc(userId);
    }
}

// CQRS Controller — commands and queries go to SEPARATE handlers
@RestController
@RequestMapping("/orders")
class OrderController {

    private final OrderCommandHandler commandHandler;
    private final OrderQueryHandler queryHandler;

    public OrderController(OrderCommandHandler commandHandler,
                           OrderQueryHandler queryHandler) {
        this.commandHandler = commandHandler;
        this.queryHandler = queryHandler;
    }

    // ── COMMAND: Place an order (WRITE side) ──────────────────────────────
    @PostMapping
    public ResponseEntity<Map<String, String>> placeOrder(
        @RequestHeader("X-User-Id") String userId,
        @RequestBody PlaceOrderCommand command) {

        String orderId = commandHandler.handle(
            new PlaceOrderCommand(
                UUID.randomUUID().toString(),
                userId,
                command.items(),
                command.shippingAddress()
            )
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of(
                "orderId", orderId,
                "status", "PENDING",
                "message", "Order placed successfully"
            ));
    }

    // ── COMMAND: Cancel an order (WRITE side) ─────────────────────────────
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
        @PathVariable String orderId,
        @RequestHeader("X-User-Id") String userId,
        @RequestParam(defaultValue = "Customer request") String reason) {

        commandHandler.handle(new CancelOrderCommand(orderId, userId, reason));
        return ResponseEntity.noContent().build();
    }

    // ── QUERY: Get order by ID (READ side) ────────────────────────────────
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderSummaryView> getOrder(@PathVariable String orderId) {
        return queryHandler.findById(orderId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ── QUERY: Get all orders for a user (READ side) ──────────────────────
    @GetMapping
    public List<OrderSummaryView> getUserOrders(
        @RequestHeader("X-User-Id") String userId) {
        return queryHandler.findByUser(userId);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 10: SAGA PATTERN — COORDINATING DISTRIBUTED TRANSACTIONS
// Choreography-based Saga using events (no central coordinator)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * SAGA FLOW for "Place Order":
 *
 * 1. Order Service         → places order → emits OrderPlacedEvent
 * 2. Inventory Service     → listens → reserves stock → emits StockReservedEvent
 *                                    (or emits StockUnavailableEvent on failure)
 * 3. Payment Service       → listens → charges card → emits PaymentSucceededEvent
 *                                    (or emits PaymentFailedEvent on failure)
 * 4. Order Service         → listens → confirms order → emits OrderConfirmedEvent
 *
 * COMPENSATING TRANSACTIONS (rollback on failure):
 * If PaymentFailed:
 *   Payment Service   → emits PaymentFailedEvent
 *   Inventory Service → listens → releases reserved stock (compensating)
 *   Order Service     → listens → sets order to FAILED
 */

// Saga coordinator in Order Service — listens to events from other services
@Component
class OrderSaga {

    private final OrderCommandHandler commandHandler;

    public OrderSaga(OrderCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    /**
     * Step 4: Stock was reserved by Inventory Service.
     * Continue to payment phase.
     */
    @EventListener
    public void onStockReserved(StockReservedEvent event) {
        System.out.println("[Saga] Stock reserved for order: " + event.orderId() +
            " — proceeding to payment");
        // In real system: publish PaymentRequestEvent for Payment Service
    }

    /**
     * Compensation: Stock unavailable.
     * Cancel the order.
     */
    @EventListener
    public void onStockUnavailable(StockUnavailableEvent event) {
        System.out.println("[Saga] Insufficient stock for order: " + event.orderId() +
            " — cancelling order");
        commandHandler.handle(
            new CancelOrderCommand(event.orderId(), "system", "Insufficient stock")
        );
    }

    /**
     * Compensation: Payment failed.
     * Cancel the order (Inventory Service will compensate stock separately).
     */
    @EventListener
    public void onPaymentFailed(PaymentFailedEvent event) {
        System.out.println("[Saga] Payment failed for order: " + event.orderId() +
            " — initiating rollback");
        commandHandler.handle(
            new CancelOrderCommand(event.orderId(), "system", "Payment failed: " + event.reason())
        );
    }
}

// Events from other services (received via messaging in production)
record StockReservedEvent(String orderId, String isbn, int quantity) {}
record StockUnavailableEvent(String orderId, String isbn, int requested, int available) {}
record PaymentFailedEvent(String orderId, String userId, double amount, String reason) {}
