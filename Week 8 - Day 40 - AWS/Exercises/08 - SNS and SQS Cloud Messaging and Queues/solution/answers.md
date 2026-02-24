# Exercise 08 — SNS and SQS: SOLUTION

## Part 1 — SNS vs SQS Concepts

### 1. SNS vs SQS Comparison

| Feature | SNS | SQS |
|---|---|---|
| Delivery model | Push — SNS pushes to all subscribers simultaneously | Pull — consumers poll the queue for messages |
| Consumer model | Fan-out: every subscriber gets every message | Competing consumers: each message is processed by one consumer |
| Message persistence | Not persisted — SNS delivers and discards. Delivery to SQS persists it there. | Persisted in the queue until deleted or retention window expires |
| Message ordering | No ordering guarantee (use FIFO SNS topic + FIFO SQS for ordering) | Standard: unordered. FIFO: strict order within a message group |
| Delivery guarantee | At-least-once push delivery | Standard: at-least-once. FIFO: exactly-once within deduplication window |
| Max message retention | N/A — SNS is transient | Up to 14 days |
| Use case | Event fan-out, push notifications, triggering multiple independent services | Work queues, decoupled microservices, retry/backoff processing |

### 2. SQS Queue Types

| Property | Standard Queue | FIFO Queue |
|---|---|---|
| Ordering | Best-effort (not guaranteed) | Strictly ordered within each Message Group ID |
| Throughput | Nearly unlimited TPS | High throughput mode up to 70,000 messages/s |
| Duplicate delivery | Possible (at-least-once delivery) | Exactly-once processing within 5-min deduplication window |
| Naming requirement | Any valid SQS name | Must end with `.fifo` |
| Use case | High-volume tasks where order doesn't matter | Financial transactions requiring strict ordering |

### 3. Dead Letter Queue (DLQ)

A **Dead Letter Queue** is a separate SQS queue that receives messages which failed processing `maxReceiveCount` times. When a consumer receives a message but doesn't delete it (crashes or throws), SQS makes it visible again after the visibility timeout. Once received `maxReceiveCount` times without deletion, SQS routes it to the DLQ.

**Why critical for production:**
- Prevents a poison message from blocking the entire queue indefinitely
- Preserves failed messages for debugging, inspection, and reprocessing after a fix
- Enables CloudWatch alarms on DLQ depth to detect processing failures early

---

## Part 2 — Fan-Out Architecture (CLI)

```bash
# a. Create SNS topic
SNS_TOPIC_ARN=$(aws sns create-topic --name order-events --query "TopicArn" --output text)
echo "SNS Topic ARN: $SNS_TOPIC_ARN"

# b. Create three SQS queues
aws sqs create-queue --queue-name inventory-queue
aws sqs create-queue --queue-name email-queue
aws sqs create-queue --queue-name analytics-queue

# c. Get ARN and URL of each queue
INVENTORY_QUEUE_URL=$(aws sqs get-queue-url --queue-name inventory-queue --query "QueueUrl" --output text)
EMAIL_QUEUE_URL=$(aws sqs get-queue-url --queue-name email-queue --query "QueueUrl" --output text)
ANALYTICS_QUEUE_URL=$(aws sqs get-queue-url --queue-name analytics-queue --query "QueueUrl" --output text)

INVENTORY_QUEUE_ARN=$(aws sqs get-queue-attributes --queue-url $INVENTORY_QUEUE_URL --attribute-names QueueArn --query "Attributes.QueueArn" --output text)
EMAIL_QUEUE_ARN=$(aws sqs get-queue-attributes --queue-url $EMAIL_QUEUE_URL --attribute-names QueueArn --query "Attributes.QueueArn" --output text)
ANALYTICS_QUEUE_ARN=$(aws sqs get-queue-attributes --queue-url $ANALYTICS_QUEUE_URL --attribute-names QueueArn --query "Attributes.QueueArn" --output text)

# d. Set SQS access policy on each queue to allow SNS to send messages
for QUEUE_URL in "$INVENTORY_QUEUE_URL" "$EMAIL_QUEUE_URL" "$ANALYTICS_QUEUE_URL"; do
  QUEUE_ARN=$(aws sqs get-queue-attributes \
    --queue-url "$QUEUE_URL" \
    --attribute-names QueueArn \
    --query "Attributes.QueueArn" --output text)

  aws sqs set-queue-attributes \
    --queue-url "$QUEUE_URL" \
    --attributes "{\"Policy\":\"{\\\"Version\\\":\\\"2012-10-17\\\",\\\"Statement\\\":[{\\\"Effect\\\":\\\"Allow\\\",\\\"Principal\\\":\\\"*\\\",\\\"Action\\\":\\\"sqs:SendMessage\\\",\\\"Resource\\\":\\\"$QUEUE_ARN\\\",\\\"Condition\\\":{\\\"ArnEquals\\\":{\\\"aws:SourceArn\\\":\\\"$SNS_TOPIC_ARN\\\"}}}]}\"}"
done

# e. Subscribe each SQS queue to the SNS topic
aws sns subscribe --topic-arn $SNS_TOPIC_ARN --protocol sqs --notification-endpoint $INVENTORY_QUEUE_ARN
aws sns subscribe --topic-arn $SNS_TOPIC_ARN --protocol sqs --notification-endpoint $EMAIL_QUEUE_ARN
aws sns subscribe --topic-arn $SNS_TOPIC_ARN --protocol sqs --notification-endpoint $ANALYTICS_QUEUE_ARN

# f. Publish test order event to the SNS topic
aws sns publish \
  --topic-arn $SNS_TOPIC_ARN \
  --message '{"orderId":"ORD-001","userId":"user-42","total":149.99,"items":3}'

# g. Receive message from inventory-queue and delete it (simulate processing)
RECEIVE_OUTPUT=$(aws sqs receive-message \
  --queue-url $INVENTORY_QUEUE_URL \
  --max-number-of-messages 1 \
  --wait-time-seconds 5)

echo "Received: $(echo $RECEIVE_OUTPUT | python3 -c 'import sys,json; d=json.load(sys.stdin); print(d["Messages"][0]["Body"])')"

RECEIPT_HANDLE=$(echo $RECEIVE_OUTPUT | python3 -c 'import sys,json; d=json.load(sys.stdin); print(d["Messages"][0]["ReceiptHandle"])')

aws sqs delete-message \
  --queue-url $INVENTORY_QUEUE_URL \
  --receipt-handle "$RECEIPT_HANDLE"

echo "Message deleted — order processed by Inventory Service"
```

---

## Part 3 — Reflection Questions

**1.** If `email-queue` consumer is down for 30 minutes, what happens?

SQS persists messages until the retention period expires (default 4 days). The consumer can resume and process the backlog. No messages are lost. This is fundamentally better than direct synchronous calls — with a direct call, if the Email Service is down the Order Service either fails or needs complex retry logic. SQS decouples availability and isolates failures between services.

**2.** Visibility timeout and consumer crash behavior?

The **visibility timeout** is the window after receiving a message during which it's hidden from other consumers. If the consumer crashes and never deletes the message, the timeout expires and the message reappears for another consumer. This provides at-least-once delivery — processing must be **idempotent** (receiving the same message twice must not cause duplicate side effects, e.g., sending two confirmation emails).

**3.** FIFO queue for financial transactions — message group ID?

Use a **FIFO queue** (name ends in `.fifo`). The **Message Group ID** groups related messages that must be processed in order — use `accountId` so all transactions for the same account are ordered, while different accounts process in parallel.

**4.** SNS + SQS vs Apache Kafka

| Dimension | SNS + SQS | Apache Kafka |
|---|---|---|
| Message replay | Limited — SQS messages deleted after processing | Full replay — consumers reset offsets and re-read any historical message |
| Retention | SQS up to 14 days | Configurable per-topic: hours to indefinitely |
| Consumer groups | Competing consumers per queue | Consumer groups with committed offsets; multiple groups read independently |
| Ordering | FIFO queue: ordered per group; Standard: unordered | Ordered within a partition |
| Throughput ceiling | High, AWS-managed limits | Very high — scales with partition count and broker sizing |
| Setup complexity | Fully managed — zero infrastructure | Brokers, partitions, replication, KRaft — operationally complex |
| Best for | Simple fan-out, decoupled microservices, reliable work queues | Event streaming pipelines, high-throughput ingestion, long-term retention |

**5.** When to choose Kafka over SNS/SQS?

Choose Kafka when you need durable event streams with long-term retention, high-throughput streaming at millions of events/second, event replay/sourcing, Kafka Streams for real-time processing, and multiple independent teams consuming the same topic at their own pace with offset control. SNS/SQS is the right default for typical microservice decoupling — use Kafka when you're building a data platform, not just a message queue.
