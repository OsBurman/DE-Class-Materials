#!/usr/bin/env bash
# Exercise 03: Kafka CLI — Topics, Producers, and Consumers
# Replace each "# TODO" with the correct command.
# Run commands one at a time in separate terminal windows as noted.
# Assumes Kafka is running on localhost:9092.

# ─────────────────────────────────────────────
# PART A — Topic Management
# ─────────────────────────────────────────────

# Step 1: Create topic "order-events" with 3 partitions, replication-factor 1
# TODO: kafka-topics.sh ...

# Step 2: List all topics to confirm "order-events" was created
# TODO: kafka-topics.sh ...

# Step 3: Describe the "order-events" topic (partitions, leaders, replicas)
# TODO: kafka-topics.sh ...

# Step 4: Create topic "user-events" with 1 partition, replication-factor 1
# TODO: kafka-topics.sh ...

# Step 5: Delete the "user-events" topic
# TODO: kafka-topics.sh ...


# ─────────────────────────────────────────────
# PART B — Producing Messages
# ─────────────────────────────────────────────

# Step 6: Open a console producer for "order-events" (no key, plain value)
# Run this in Terminal 1. Then type the three JSON messages and press Enter after each.
# TODO: kafka-console-producer.sh ...

# Step 7: Open a console producer with key/value support (separator = ":")
# Run this in Terminal 1. Type:  ORD-001:{"orderId":"ORD-001","status":"DELIVERED"}
#                                ORD-002:{"orderId":"ORD-002","status":"CANCELLED"}
# TODO: kafka-console-producer.sh ... --property "parse.key=true" --property "key.separator=:"


# ─────────────────────────────────────────────
# PART C — Consuming Messages
# ─────────────────────────────────────────────

# Step 8: Read ALL messages from "order-events" from the beginning (no consumer group)
# TODO: kafka-console-consumer.sh ... --from-beginning

# Step 9: Read from the beginning in consumer group "order-processor"
# TODO: kafka-console-consumer.sh ... --from-beginning --group order-processor

# Step 10: Open a SECOND terminal and run another consumer in the SAME group "order-processor"
# Observe the rebalance — partitions are split between the two instances.
# TODO: (same command as Step 9 — run in a new terminal window)

# Step 11: Check consumer group offsets for "order-processor"
# TODO: kafka-consumer-groups.sh ...


# ─────────────────────────────────────────────
# PART D — Reflection Questions (answer in comments below)
# ─────────────────────────────────────────────

# Q1: Topic has 3 partitions; 2 consumers are in group "order-processor".
#     How are the partitions distributed between the two consumers?
# A1: TODO

# Q2: If you run a consumer WITHOUT --from-beginning, which messages does it receive?
# A2: TODO

# Q3: What --property flag enables key/value parsing in the console producer?
#     What is the default separator character if you don't specify one?
# A3: TODO
