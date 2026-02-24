# Exercise 08 — SNS and SQS: Your Answers

## Part 1 — SNS vs SQS Concepts

### 1. SNS vs SQS Comparison

| Feature | SNS | SQS |
|---|---|---|
| Delivery model | TODO | TODO |
| Consumer model | TODO | TODO |
| Message persistence | TODO | TODO |
| Message ordering | TODO | TODO |
| Delivery guarantee | TODO | TODO |
| Max message retention | TODO | TODO |
| Use case | TODO | TODO |

### 2. SQS Queue Types

| Property | Standard Queue | FIFO Queue |
|---|---|---|
| Ordering | TODO | TODO |
| Throughput | TODO | TODO |
| Duplicate delivery | TODO | TODO |
| Naming requirement | TODO | TODO |
| Use case | TODO | TODO |

### 3. Dead Letter Queue (DLQ)

What is a DLQ? TODO

When does a message move to a DLQ? TODO

Why is this important for production systems? TODO

---

## Part 2 — Fan-Out Architecture (CLI)

```bash
# a. Create SNS topic
# TODO

SNS_TOPIC_ARN="TODO"

# b. Create three SQS queues
# TODO

# c. Get ARN and URL of each queue
INVENTORY_QUEUE_URL=$(TODO)
INVENTORY_QUEUE_ARN=$(TODO)

EMAIL_QUEUE_URL=$(TODO)
EMAIL_QUEUE_ARN=$(TODO)

ANALYTICS_QUEUE_URL=$(TODO)
ANALYTICS_QUEUE_ARN=$(TODO)

# d. Add SQS access policy to allow SNS to send messages
# (Repeat for each queue — fill in the JSON policy and set-queue-attributes call)
# TODO

# e. Subscribe each SQS queue to the SNS topic
# TODO

# f. Publish test order event to SNS topic
# TODO

# g. Receive from inventory-queue and delete the message
# TODO
RECEIPT_HANDLE=$(TODO)
# TODO: delete-message
```

---

## Part 3 — Reflection Questions

**1.** If the email-queue consumer is down for 30 minutes, what happens to messages?
TODO

Why is this better than calling the Email Service directly?
TODO

**2.** What is SQS visibility timeout? What happens if a consumer crashes?
TODO

**3.** Which queue type for ordered financial transactions, and why?
TODO

What is the message group ID used for?
TODO

**4.** SNS + SQS vs Apache Kafka

| Dimension | SNS + SQS | Apache Kafka |
|---|---|---|
| Message replay | TODO | TODO |
| Retention | TODO | TODO |
| Consumer groups | TODO | TODO |
| Ordering | TODO | TODO |
| Throughput ceiling | TODO | TODO |
| Setup complexity | TODO | TODO |
| Best for | TODO | TODO |

**5.** When would you choose Kafka over SNS/SQS?
TODO
