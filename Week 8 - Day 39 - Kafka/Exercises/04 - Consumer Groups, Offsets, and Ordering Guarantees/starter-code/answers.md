# Exercise 04: Consumer Groups, Offsets, and Ordering Guarantees

## Requirement 1 — Consumer Group Mechanics

### 1a. Partition assignment (6 partitions, 4 consumers in group "payments")

TODO: Describe or draw the partition assignment.

Maximum consumers before some become idle: TODO

### 1b. Two independent applications reading every message

TODO: Describe the consumer group setup.

### 1c. Consumer group rebalance

What is a rebalance?

TODO:

Three events that trigger a rebalance:
1. TODO
2. TODO
3. TODO

What happens to message processing during a rebalance?

TODO:

### 1d. Static group membership (`group.instance.id`)

TODO:

---

## Requirement 2 — Offset Management

### 2a. What is an offset? What does "committing an offset" mean?

TODO:

### 2b. Commit strategies comparison

| Strategy | Config / Method | Delivery Guarantee | Risk |
|---|---|---|---|
| Auto-commit | `enable.auto.commit=true` | TODO | TODO |
| Synchronous manual commit | `commitSync()` | TODO | TODO |
| Asynchronous manual commit | `commitAsync()` | TODO | TODO |
| Manual batch commit | Commit only after batch processing | TODO | TODO |

### 2c. `auto.offset.reset` — earliest vs latest

`earliest`: TODO

`latest`: TODO

Which is safer for a new consumer group that must not miss messages? TODO

### 2d. Consumer crashes after processing offsets 0–99 but before committing

What offset does it resume from on restart? TODO

What delivery guarantee does this represent? TODO

---

## Requirement 3 — Ordering Guarantees

### 3a. Why does Kafka only guarantee order within a partition?

TODO:

### 3b. Order events produced without a key vs with key = "ORD-999"

Without a key:

TODO:

With key = "ORD-999":

TODO:

### 3c. Bank transaction partition key design

Partition key choice: TODO

Number of partitions recommendation: TODO

Explanation: TODO

### 3d. Two reasons why too many partitions can be harmful

1. TODO
2. TODO

---

## Requirement 4 — Scenario Analysis

| Scenario | What goes wrong | Fix |
|---|---|---|
| `enable.auto.commit=true`, consumer crashes before 5-second auto-commit fires | TODO | TODO |
| New consumer group, `auto.offset.reset=latest`, 1 million historical messages | TODO | TODO |
| Topic has 3 partitions; 10 consumer instances in the same group | TODO | TODO |
| All order events produced with key `"orders"` | TODO | TODO |
