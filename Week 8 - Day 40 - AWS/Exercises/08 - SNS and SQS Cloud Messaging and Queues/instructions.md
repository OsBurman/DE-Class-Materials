# Exercise 08: SNS and SQS — Cloud Messaging and Queues

## Objective

Understand the difference between Amazon SNS (pub/sub) and Amazon SQS (queues), implement a fan-out messaging pattern using CLI, and compare these services to Apache Kafka.

## Background

**Amazon SNS (Simple Notification Service)** is a managed pub/sub messaging service. Publishers send messages to a **topic**, and all subscribers receive a copy immediately (push-based). **Amazon SQS (Simple Queue Service)** is a managed message queue. Producers enqueue messages, and consumers poll to receive and process them (pull-based). Together, SNS + SQS implement the **fan-out** pattern — one event triggers multiple independent processing pipelines without coupling the services together.

## Requirements

### Part 1 — SNS vs SQS Concepts

1. Complete the comparison table:

   | Feature | SNS | SQS |
   |---|---|---|
   | Delivery model | | |
   | Consumer model | | |
   | Message persistence | | |
   | Message ordering | | |
   | Delivery guarantee | | |
   | Max message retention | | |
   | Use case | | |

2. **SQS Queue Types** — explain the difference:

   | Property | Standard Queue | FIFO Queue |
   |---|---|---|
   | Ordering | | |
   | Throughput | | |
   | Duplicate delivery | | |
   | Naming requirement | | |
   | Use case | | |

3. What is a **Dead Letter Queue (DLQ)**? When does a message get moved to a DLQ, and why is this important for production systems?

### Part 2 — Fan-Out Architecture (CLI)

You are building an **Order Processing System**. When an order is placed:
- The **Inventory Service** must reserve stock
- The **Email Service** must send a confirmation email
- The **Analytics Service** must log the order event

This requires a **fan-out**: one order event → three independent consumers.

Write the AWS CLI commands to:

a. Create an SNS topic: `order-events`

b. Create three SQS queues: `inventory-queue`, `email-queue`, `analytics-queue`

c. Get the ARN and URL of each queue

d. Add an SQS Access Policy to each queue allowing SNS to send messages to it (this is required before subscribing — include the policy JSON)

e. Subscribe each SQS queue to the SNS topic

f. Publish a test order event to the SNS topic:
   ```json
   {"orderId": "ORD-001", "userId": "user-42", "total": 149.99, "items": 3}
   ```

g. Receive the message from `inventory-queue` and then delete it (simulate processing)

### Part 3 — Reflection Questions

1. In the fan-out pattern, SNS delivers the message to all three queues **simultaneously**. If the `email-queue` consumer is down for 30 minutes, what happens to messages? Why is this better than calling the Email Service directly from the Order Service?

2. **SQS visibility timeout** — what is it? If your consumer crashes while processing a message (before deleting it), what happens?

3. You need to process financial transactions **in order** (debit before credit). Which SQS queue type should you use and why? What is the **message group ID** used for in this context?

4. **SNS vs Kafka** — fill in the table:

   | Dimension | SNS + SQS | Apache Kafka |
   |---|---|---|
   | Message replay | | |
   | Retention | | |
   | Consumer groups | | |
   | Ordering | | |
   | Throughput ceiling | | |
   | Setup complexity | | |
   | Best for | | |

5. When would you choose **Kafka over SNS/SQS** in a production system?

## Hints

- SNS → SQS fan-out requires an **SQS access policy** (resource policy) that grants `sqs:SendMessage` to the SNS topic ARN. Without it, the subscription will be created but message delivery will be silently denied.
- After `aws sqs receive-message`, you must call `aws sqs delete-message` with the `--receipt-handle` returned by the receive call. Messages reappear after the visibility timeout if not deleted.
- SQS FIFO queues require the queue name to end in `.fifo`.
- SNS subscriptions to SQS use protocol `sqs` and endpoint set to the SQS queue ARN (not URL).

## Expected Output

```
Part 1: Two comparison tables + DLQ explanation
Part 2: CLI commands for SNS topic, 3 SQS queues, subscriptions, publish, consume
Part 3: 5 reflection answers including Kafka vs SNS/SQS table
```
