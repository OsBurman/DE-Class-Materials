#!/usr/bin/env bash
# Exercise 03: Kafka CLI — Topics, Producers, and Consumers — SOLUTION
# Assumes Kafka is running on localhost:9092.

# ─────────────────────────────────────────────
# PART A — Topic Management
# ─────────────────────────────────────────────

# Step 1: Create topic "order-events" with 3 partitions, replication-factor 1
kafka-topics.sh \
  --create \
  --topic order-events \
  --partitions 3 \
  --replication-factor 1 \
  --bootstrap-server localhost:9092

# Step 2: List all topics
kafka-topics.sh \
  --list \
  --bootstrap-server localhost:9092

# Step 3: Describe "order-events"
kafka-topics.sh \
  --describe \
  --topic order-events \
  --bootstrap-server localhost:9092

# Step 4: Create "user-events" with 1 partition, replication-factor 1
kafka-topics.sh \
  --create \
  --topic user-events \
  --partitions 1 \
  --replication-factor 1 \
  --bootstrap-server localhost:9092

# Step 5: Delete "user-events"
kafka-topics.sh \
  --delete \
  --topic user-events \
  --bootstrap-server localhost:9092


# ─────────────────────────────────────────────
# PART B — Producing Messages
# ─────────────────────────────────────────────

# Step 6: Plain-value producer for "order-events"
# Run in Terminal 1; type messages and press Enter after each.
kafka-console-producer.sh \
  --topic order-events \
  --bootstrap-server localhost:9092

# (Type these messages one per line, then Ctrl+C)
# {"orderId": "ORD-001", "status": "PLACED"}
# {"orderId": "ORD-002", "status": "PLACED"}
# {"orderId": "ORD-003", "status": "SHIPPED"}

# Step 7: Key/value producer with ":" separator
kafka-console-producer.sh \
  --topic order-events \
  --bootstrap-server localhost:9092 \
  --property "parse.key=true" \
  --property "key.separator=:"

# (Type these messages, then Ctrl+C)
# ORD-001:{"orderId": "ORD-001", "status": "DELIVERED"}
# ORD-002:{"orderId": "ORD-002", "status": "CANCELLED"}


# ─────────────────────────────────────────────
# PART C — Consuming Messages
# ─────────────────────────────────────────────

# Step 8: Read all messages from the beginning (no group — each run re-reads everything)
kafka-console-consumer.sh \
  --topic order-events \
  --from-beginning \
  --bootstrap-server localhost:9092

# Step 9: Read from the beginning in consumer group "order-processor"
kafka-console-consumer.sh \
  --topic order-events \
  --from-beginning \
  --group order-processor \
  --bootstrap-server localhost:9092

# Step 10: Run a SECOND consumer in the SAME group (open a new terminal)
kafka-console-consumer.sh \
  --topic order-events \
  --group order-processor \
  --bootstrap-server localhost:9092
# Kafka will rebalance: the 3 partitions will be split between the two consumers.
# Consumer 1 might get P0+P1; Consumer 2 gets P2 (or similar).

# Step 11: Check consumer group offsets
kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --describe \
  --group order-processor


# ─────────────────────────────────────────────
# PART D — Reflection Answers
# ─────────────────────────────────────────────

# Q1: 3 partitions, 2 consumers in "order-processor":
# A1: One consumer gets 2 partitions (e.g., P0 and P1); the other gets 1 partition (P2).
#     Kafka's group coordinator distributes partitions as evenly as possible.
#     With 3 partitions and 2 consumers, a perfect split is impossible, so one consumer
#     handles the extra partition.

# Q2: Consumer started WITHOUT --from-beginning:
# A2: It only receives messages produced AFTER it started (offset = latest).
#     The consumer is assigned the current end-of-log offset for each partition,
#     so historical messages are not replayed.

# Q3: Key/value parsing in the console producer:
# A3: The required properties are:
#       --property "parse.key=true"      (enables key parsing)
#       --property "key.separator=:"     (defines the separator character)
#     If "key.separator" is not specified, the default separator is TAB (\t).
