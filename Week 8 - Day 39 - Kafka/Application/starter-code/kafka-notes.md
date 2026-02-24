# Kafka Notes — Day 39

## Core Concepts

| Term | Definition | Analogy |
|------|-----------|---------|
| **Broker** | A single Kafka server that stores and serves messages | A post office |
| **Topic** | A named category/feed to which messages are published | A mailbox label |
| **Partition** | Ordered, immutable log within a topic; enables parallelism | A lane in a highway |
| **Offset** | Unique sequential ID for each message within a partition | Page number in a book |
| **Producer** | Application that publishes messages to a topic | Sender of a letter |
| **Consumer** | Application that subscribes to and reads messages | Recipient of a letter |
| **Consumer Group** | Group of consumers sharing the work of consuming a topic | A team sharing a mailbox |
| **DLQ (Dead Letter Queue)** | Topic where failed/unprocessable messages land | A "returned mail" bin |
| **Zookeeper** | Coordinates cluster metadata (older Kafka deployments) | A cluster manager |

---

## Message Flow

```
  Producer
     │
     │  kafkaTemplate.send(topic, key, value)
     ▼
┌────────────────────────────────────────┐
│            KAFKA BROKER                │
│                                        │
│  Topic: "orders"                       │
│  ┌──────────┐ ┌──────────┐ ┌────────┐ │
│  │Partition0│ │Partition1│ │Part. 2 │ │
│  │[0][1][2] │ │[0][1]    │ │[0]     │ │
│  └──────────┘ └──────────┘ └────────┘ │
└────────────────────────────────────────┘
     │                   │
     ▼                   ▼
Consumer Group A    Consumer Group B
(notification)      (inventory)
 @KafkaListener      @KafkaListener
```

**Key rule:** Each partition is consumed by exactly ONE consumer within a group at a time.

---

## Kafka vs REST — When to Use Each

| | Kafka (Async) | REST (Sync) |
|-|--------------|-------------|
| **Request/Response** | ❌ Fire-and-forget | ✅ Immediate response |
| **Decoupling** | ✅ Producer doesn't know consumers | ❌ Tight coupling |
| **Scaling** | ✅ Add consumers independently | ❌ Tied to server capacity |
| **Reliability** | ✅ Messages persisted on disk | ❌ Lost if server down |
| **Order guarantee** | ✅ Within a partition | ❌ No built-in ordering |
| **Fan-out** | ✅ Many consumers from one message | ❌ Must call each service |
| **Real-time streaming** | ✅ Designed for it | ❌ Polling required |

**Use Kafka when:** multiple services need the same event, you want loose coupling, or processing can be asynchronous (order notifications, inventory updates, audit logs).

**Use REST when:** you need an immediate response (login, checkout total, search results).

---

## Application Topics (Day 39 Project)

| Topic | Partitions | Consumers | Purpose |
|-------|-----------|-----------|---------|
| `orders` | 3 | notification-group, inventory-group | New/updated orders |
| `notifications` | 1 | — | Email/SMS triggers |
| `inventory` | 1 | — | Stock reservations |
| `orders.DLQ` | 1 | — | Failed order events |

---

## Running the Application

### 1. Start Kafka with Docker Compose

```bash
docker compose up -d
```

Wait ~15 seconds for Kafka to fully start.

### 2. Verify in Kafdrop (web UI)

Open: [http://localhost:9000](http://localhost:9000)

You can:
- Browse topics and their partitions
- Inspect individual messages
- View consumer group offsets

### 3. Start the Spring Boot App

```bash
mvn spring-boot:run
```

Topics are auto-created on first publish (or at startup via `KafkaTopicConfig`).

---

## Test Commands

### Place a new order (publishes to `orders` topic)

```bash
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "C001",
    "productId": "P001",
    "quantity": 2,
    "totalPrice": 29.99
  }' | jq .
```

### Update order status

```bash
curl -s -X PUT http://localhost:8080/api/orders/ORD-001/status \
  -H "Content-Type: application/json" \
  -d '{"status": "SHIPPED"}' | jq .
```

After each request, watch the application logs — you should see both `NotificationConsumer` and `InventoryConsumer` print messages.

---

## Understanding Consumer Groups

Run the test commands above, then open Kafdrop at [http://localhost:9000](http://localhost:9000):

1. Click on the `orders` topic
2. Click **Consumer Groups**
3. You should see `notification-group` and `inventory-group`
4. The **Lag** column shows how many messages haven't been consumed yet

**Question:** If you send 9 messages with 3 partitions and 3 consumers in one group, how many messages does each consumer get? ___

---

## Reflection Questions

1. What happens to messages if your consumer service goes down while Kafka is running? How does Kafka ensure no messages are lost?

   TODO

2. Why does the `orders` topic have 3 partitions while `notifications` has only 1?

   TODO

3. What is the purpose of the Dead Letter Queue (`orders.DLQ`)? Give a real-world example of when a message would land there.

   TODO

4. Your app processes 1,000 orders/second but your consumer can only handle 200/second. How would you solve this with Kafka?

   TODO

5. Explain the difference between `notification-group` and `inventory-group` consuming the same `orders` topic. Do both groups receive every message?

   TODO
