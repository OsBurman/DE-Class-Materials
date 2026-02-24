#!/bin/bash
# =============================================================================
# Day 39 — Kafka | Part 1
# File: 02-kafka-cli-fundamentals.sh
# Topics: Kafka CLI, Creating Topics, Producing Messages, Consuming Messages,
#         Consumer Groups, Load Balancing
# Domain: Bookstore Application
#
# Prerequisites:
#   - Kafka running locally OR via Docker Compose (see docker-compose.yml below)
#   - KAFKA_HOME set to your Kafka installation directory
#     OR use the docker exec approach shown in Section 1
# =============================================================================

# =============================================================================
# SECTION 0 — STARTING KAFKA FOR LOCAL DEVELOPMENT
# =============================================================================

# Option A: Docker Compose (recommended — no local installation needed)
# Save this as docker-compose.yml alongside this script, then run:
#   docker compose up -d

cat << 'DOCKER_COMPOSE'
# docker-compose.yml
version: '3.8'
services:

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "false"   # We'll create topics manually

  # Kafka UI — browse topics, messages, consumer groups in a browser
  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    ports:
      - "8090:8080"
    environment:
      KAFKA_CLUSTERS_0_NAME: bookstore-local
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:9092
    depends_on:
      - kafka
DOCKER_COMPOSE

echo ""
echo "# Start Kafka with Docker Compose:"
echo "docker compose up -d"
echo ""
echo "# Wait ~15 seconds for Kafka to be ready, then verify:"
echo "docker compose ps"
echo ""
echo "# To exec into the Kafka container (where CLI tools are available):"
echo "docker exec -it <kafka-container-name> bash"
echo ""
echo "# OR with the container name from docker compose:"
echo "docker exec -it kafka-kafka-1 bash"
echo ""

# Option B: Local installation
# export KAFKA_HOME=/usr/local/kafka
# export PATH=$PATH:$KAFKA_HOME/bin

# For this script, we'll use docker exec approach:
KAFKA_CONTAINER="kafka-kafka-1"   # adjust if your container name differs
KAFKA_CLI="docker exec -it ${KAFKA_CONTAINER}"

echo "# All CLI commands below use:"
echo "# ${KAFKA_CLI} kafka-topics.sh ..."
echo ""

# =============================================================================
# SECTION 1 — CREATING TOPICS
# =============================================================================

echo "=== SECTION 1: CREATING TOPICS ==="
echo ""

# -----------------------------------------------------------------------
# 1a. Create a basic topic
# -----------------------------------------------------------------------
echo "# Create the book-orders topic:"
echo "# --partitions 3  : split data across 3 partitions for parallelism"
echo "# --replication-factor 1 : one copy (dev/single-broker setup)"
echo ""

${KAFKA_CLI} kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create \
  --topic book-orders \
  --partitions 3 \
  --replication-factor 1

echo ""
echo "# Expected output: Created topic book-orders."
echo ""

# -----------------------------------------------------------------------
# 1b. Create additional bookstore topics
# -----------------------------------------------------------------------

${KAFKA_CLI} kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create \
  --topic inventory-events \
  --partitions 3 \
  --replication-factor 1

${KAFKA_CLI} kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create \
  --topic payment-results \
  --partitions 3 \
  --replication-factor 1

${KAFKA_CLI} kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create \
  --topic notification-events \
  --partitions 2 \
  --replication-factor 1

${KAFKA_CLI} kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create \
  --topic book-orders-dead-letter \
  --partitions 1 \
  --replication-factor 1

echo "# All bookstore topics created."
echo ""

# -----------------------------------------------------------------------
# 1c. List all topics
# -----------------------------------------------------------------------
echo "# List all topics in the cluster:"
${KAFKA_CLI} kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --list

echo ""
echo "# Expected output:"
echo "# book-orders"
echo "# book-orders-dead-letter"
echo "# inventory-events"
echo "# notification-events"
echo "# payment-results"
echo ""

# -----------------------------------------------------------------------
# 1d. Describe a topic — shows partitions, leaders, replicas
# -----------------------------------------------------------------------
echo "# Describe the book-orders topic (shows partition details):"
${KAFKA_CLI} kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --describe \
  --topic book-orders

echo ""
echo "# Expected output:"
echo "# Topic: book-orders    TopicId: xxxxx  PartitionCount: 3  ReplicationFactor: 1"
echo "# Topic: book-orders    Partition: 0    Leader: 1  Replicas: 1  Isr: 1"
echo "# Topic: book-orders    Partition: 1    Leader: 1  Replicas: 1  Isr: 1"
echo "# Topic: book-orders    Partition: 2    Leader: 1  Replicas: 1  Isr: 1"
echo ""
echo "# ISR = In-Sync Replicas: replicas that are caught up with the leader"
echo ""

# -----------------------------------------------------------------------
# 1e. Delete a topic (cleanup)
# -----------------------------------------------------------------------
echo "# Delete a topic (use carefully — permanent data loss):"
echo "# ${KAFKA_CLI} kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic book-orders"
echo "# (Commented out — we need this topic for the next sections)"
echo ""

# -----------------------------------------------------------------------
# 1f. Alter a topic — add partitions
# -----------------------------------------------------------------------
echo "# Add partitions to an existing topic (can only increase, never decrease):"
echo "# ${KAFKA_CLI} kafka-topics.sh --bootstrap-server localhost:9092 --alter --topic book-orders --partitions 6"
echo "# (Commented out — adding partitions changes key-based routing and can break ordering)"
echo ""

# =============================================================================
# SECTION 2 — PRODUCING MESSAGES
# =============================================================================

echo "=== SECTION 2: PRODUCING MESSAGES ==="
echo ""

# -----------------------------------------------------------------------
# 2a. Basic producer — interactive console
# -----------------------------------------------------------------------
echo "# Start an interactive console producer:"
echo "# Type a message and press Enter to send. Ctrl+C to exit."
echo ""
echo "${KAFKA_CLI} kafka-console-producer.sh \\"
echo "  --bootstrap-server localhost:9092 \\"
echo "  --topic book-orders"
echo ""
echo "# When started, you'll see '>'"
echo "# Type: Hello from Bookstore"
echo "# Type: Another order message"
echo "# Press Ctrl+C to stop"
echo ""

# -----------------------------------------------------------------------
# 2b. Produce with a key (non-interactive, pipe messages)
# -----------------------------------------------------------------------
echo "# Produce messages WITH a key:"
echo "# Key and value are separated by ':'  (key.separator=:)"
echo ""

echo "# Method 1 — Pipe messages directly to the producer:"
echo 'ORD-001:{"orderId":"ORD-001","userId":"USR-10","isbn":"978-0-13-468599-1","qty":2,"status":"PLACED"}
ORD-002:{"orderId":"ORD-002","userId":"USR-11","isbn":"978-0-7432-7356-5","qty":1,"status":"PLACED"}
ORD-003:{"orderId":"ORD-003","userId":"USR-10","isbn":"978-0-14-028329-7","qty":3,"status":"PLACED"}
ORD-004:{"orderId":"ORD-004","userId":"USR-12","isbn":"978-0-06-112008-4","qty":1,"status":"PLACED"}' | \
${KAFKA_CLI} kafka-console-producer.sh \
  --bootstrap-server localhost:9092 \
  --topic book-orders \
  --property "parse.key=true" \
  --property "key.separator=:"

echo ""
echo "# KEY INSIGHT: ORD-001 and ORD-003 both belong to USR-10."
echo "# Because they have DIFFERENT keys (ORD-001 vs ORD-003), they may go to different partitions."
echo "# If we used userId as the key, all orders for USR-10 would go to the same partition."
echo ""

# -----------------------------------------------------------------------
# 2c. Produce with explicit partition targeting
# -----------------------------------------------------------------------
echo "# Force a message to a specific partition:"
echo ""
echo "echo 'urgent-order-001' | ${KAFKA_CLI} kafka-console-producer.sh \\"
echo "  --bootstrap-server localhost:9092 \\"
echo "  --topic book-orders \\"
echo "  --property 'partitioner.class=org.apache.kafka.clients.producer.internals.RoundRobinPartitioner'"
echo ""

# -----------------------------------------------------------------------
# 2d. Produce messages with acks configuration
# -----------------------------------------------------------------------
echo "# Produce with all-acks (most durable — waits for all in-sync replicas):"
echo ""
echo "echo 'high-value-order' | ${KAFKA_CLI} kafka-console-producer.sh \\"
echo "  --bootstrap-server localhost:9092 \\"
echo "  --topic book-orders \\"
echo "  --producer-property acks=all \\"
echo "  --producer-property enable.idempotence=true"
echo ""
echo "# acks=all + enable.idempotence=true = exactly-once delivery semantics"
echo ""

# =============================================================================
# SECTION 3 — CONSUMING MESSAGES
# =============================================================================

echo "=== SECTION 3: CONSUMING MESSAGES ==="
echo ""

# -----------------------------------------------------------------------
# 3a. Basic consumer — read from the end (default: latest)
# -----------------------------------------------------------------------
echo "# Start a console consumer (reads NEW messages only — from now onwards):"
echo "# Open a NEW terminal and run this while the producer is running in the other."
echo ""
echo "${KAFKA_CLI} kafka-console-consumer.sh \\"
echo "  --bootstrap-server localhost:9092 \\"
echo "  --topic book-orders"
echo ""
echo "# This reads from the END of the topic — only new messages."
echo ""

# -----------------------------------------------------------------------
# 3b. Read from the beginning (--from-beginning)
# -----------------------------------------------------------------------
echo "# Read ALL messages from the beginning of the topic:"
${KAFKA_CLI} kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic book-orders \
  --from-beginning \
  --max-messages 10 &    # --max-messages exits after reading 10 messages

CONSUMER_PID=$!
sleep 3
kill $CONSUMER_PID 2>/dev/null

echo ""
echo "# Expected: The 4 order messages we produced in Section 2"
echo ""
echo "# ⚠️ WATCH OUT: Without --from-beginning, you'll see nothing if you start the consumer"
echo "#    after the messages were produced. Beginners often think Kafka is broken."
echo ""

# -----------------------------------------------------------------------
# 3c. Read and show keys
# -----------------------------------------------------------------------
echo "# Read messages AND show their keys:"
echo ""
echo "${KAFKA_CLI} kafka-console-consumer.sh \\"
echo "  --bootstrap-server localhost:9092 \\"
echo "  --topic book-orders \\"
echo "  --from-beginning \\"
echo "  --property print.key=true \\"
echo "  --property key.separator=' -> '"
echo ""
echo "# Expected output:"
echo "# ORD-001 -> {\"orderId\":\"ORD-001\",\"userId\":\"USR-10\",...}"
echo "# ORD-002 -> {\"orderId\":\"ORD-002\",\"userId\":\"USR-11\",...}"
echo ""

# -----------------------------------------------------------------------
# 3d. Read with partition and offset info
# -----------------------------------------------------------------------
echo "# Show partition and offset for each message:"
echo ""
echo "${KAFKA_CLI} kafka-console-consumer.sh \\"
echo "  --bootstrap-server localhost:9092 \\"
echo "  --topic book-orders \\"
echo "  --from-beginning \\"
echo "  --property print.key=true \\"
echo "  --property print.partition=true \\"
echo "  --property print.offset=true \\"
echo "  --property print.timestamp=true"
echo ""
echo "# Expected output format:"
echo "# CreateTime:1705312245123 Partition:0 Offset:0 ORD-001 -> {...}"
echo "# CreateTime:1705312245456 Partition:1 Offset:0 ORD-002 -> {...}"
echo ""

# =============================================================================
# SECTION 4 — CONSUMER GROUPS
# =============================================================================

echo "=== SECTION 4: CONSUMER GROUPS ==="
echo ""

# -----------------------------------------------------------------------
# 4a. Start consumer with an explicit group ID
# -----------------------------------------------------------------------
echo "# Start consumer as part of a named consumer group:"
echo "# Open THREE separate terminals and run these simultaneously:"
echo ""
echo "# Terminal 1 (Consumer 1 in inventory-service-group):"
echo "${KAFKA_CLI} kafka-console-consumer.sh \\"
echo "  --bootstrap-server localhost:9092 \\"
echo "  --topic book-orders \\"
echo "  --group inventory-service-group \\"
echo "  --property print.partition=true"
echo ""
echo "# Terminal 2 (Consumer 2 in inventory-service-group):"
echo "${KAFKA_CLI} kafka-console-consumer.sh \\"
echo "  --bootstrap-server localhost:9092 \\"
echo "  --topic book-orders \\"
echo "  --group inventory-service-group \\"
echo "  --property print.partition=true"
echo ""
echo "# Terminal 3 (Consumer 3 in inventory-service-group):"
echo "${KAFKA_CLI} kafka-console-consumer.sh \\"
echo "  --bootstrap-server localhost:9092 \\"
echo "  --topic book-orders \\"
echo "  --group inventory-service-group \\"
echo "  --property print.partition=true"
echo ""
echo "# Now produce messages in a 4th terminal:"
for i in {1..9}; do
  echo "ORDER-00${i}:Message for order ${i}"
done
echo ""
echo "# OBSERVE: Each consumer will receive a different subset of messages."
echo "#          Messages are NOT duplicated — each is processed by exactly ONE consumer."
echo ""

# -----------------------------------------------------------------------
# 4b. Start a SEPARATE group — demonstrates pub/sub (all get all messages)
# -----------------------------------------------------------------------
echo "# Start a consumer in a DIFFERENT group (payment-service-group):"
echo ""
echo "${KAFKA_CLI} kafka-console-consumer.sh \\"
echo "  --bootstrap-server localhost:9092 \\"
echo "  --topic book-orders \\"
echo "  --group payment-service-group \\"
echo "  --from-beginning"
echo ""
echo "# OBSERVE: payment-service-group gets ALL the messages — it has its OWN offset pointer."
echo "#          It's independent from inventory-service-group."
echo ""

# -----------------------------------------------------------------------
# 4c. List all consumer groups
# -----------------------------------------------------------------------
echo "# List all consumer groups:"
${KAFKA_CLI} kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --list

echo ""
echo "# Expected:"
echo "# inventory-service-group"
echo "# payment-service-group"
echo ""

# -----------------------------------------------------------------------
# 4d. Describe a consumer group — see offsets and lag
# -----------------------------------------------------------------------
echo "# Describe a consumer group (most important monitoring command):"
${KAFKA_CLI} kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --describe \
  --group inventory-service-group

echo ""
echo "# Expected output:"
echo "# GROUP                   TOPIC        PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG"
echo "# inventory-service-group book-orders  0          3               3               0"
echo "# inventory-service-group book-orders  1          3               3               0"
echo "# inventory-service-group book-orders  2          3               3               0"
echo ""
echo "# LAG = LOG-END-OFFSET - CURRENT-OFFSET"
echo "# LAG = 0 means the consumer is fully caught up"
echo "# LAG > 0 means the consumer is behind — messages are building up unprocessed"
echo "# This is the KEY metric for monitoring Kafka consumer health."
echo ""

# -----------------------------------------------------------------------
# 4e. Reset consumer group offsets
# -----------------------------------------------------------------------
echo "# Reset a consumer group to re-process all messages from the beginning:"
echo "# (Only works when the consumer group is NOT running)"
echo ""
echo "${KAFKA_CLI} kafka-consumer-groups.sh \\"
echo "  --bootstrap-server localhost:9092 \\"
echo "  --group inventory-service-group \\"
echo "  --topic book-orders \\"
echo "  --reset-offsets \\"
echo "  --to-earliest \\"
echo "  --execute"
echo ""
echo "# Other reset options:"
echo "# --to-latest          → skip all existing messages, start fresh"
echo "# --to-offset 5        → start from offset 5 in each partition"
echo "# --shift-by -3        → go back 3 messages from current position"
echo "# --to-datetime 2024-01-15T10:00:00.000   → reset to a specific timestamp"
echo ""

# =============================================================================
# SECTION 5 — USEFUL MONITORING COMMANDS
# =============================================================================

echo "=== SECTION 5: MONITORING AND DIAGNOSTICS ==="
echo ""

# -----------------------------------------------------------------------
# 5a. Get the latest offset (total message count) for a topic
# -----------------------------------------------------------------------
echo "# Show the latest offset (total messages) per partition:"
${KAFKA_CLI} kafka-run-class.sh kafka.tools.GetOffsetShell \
  --bootstrap-server localhost:9092 \
  --topic book-orders \
  --time -1   # -1 = latest, -2 = earliest

echo ""
echo "# Output example:"
echo "# book-orders:0:12   (partition 0 has messages up to offset 12)"
echo "# book-orders:1:11"
echo "# book-orders:2:13"
echo ""

# -----------------------------------------------------------------------
# 5b. Check which broker is the leader for each partition
# -----------------------------------------------------------------------
echo "# Full topic description with leader information:"
${KAFKA_CLI} kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --describe \
  --topic book-orders

echo ""

# -----------------------------------------------------------------------
# 5c. View broker configuration
# -----------------------------------------------------------------------
echo "# View broker configuration:"
echo "${KAFKA_CLI} kafka-configs.sh \\"
echo "  --bootstrap-server localhost:9092 \\"
echo "  --describe \\"
echo "  --broker 1 \\"
echo "  --all | grep log.retention"
echo ""

# =============================================================================
# SECTION 6 — QUICK REFERENCE COMMAND CARD
# =============================================================================

echo "=== KAFKA CLI QUICK REFERENCE ==="
echo ""
cat << 'REFERENCE'
# ─── TOPICS ──────────────────────────────────────────────────────────────────
# Create:   kafka-topics.sh --create --topic <name> --partitions N --replication-factor N
# List:     kafka-topics.sh --list
# Describe: kafka-topics.sh --describe --topic <name>
# Delete:   kafka-topics.sh --delete --topic <name>
# Alter:    kafka-topics.sh --alter --topic <name> --partitions N   (increase only)

# ─── PRODUCE ─────────────────────────────────────────────────────────────────
# Basic:    kafka-console-producer.sh --topic <name>
# With key: kafka-console-producer.sh --topic <name> --property parse.key=true --property key.separator=:

# ─── CONSUME ─────────────────────────────────────────────────────────────────
# New msgs: kafka-console-consumer.sh --topic <name>
# All msgs: kafka-console-consumer.sh --topic <name> --from-beginning
# Group:    kafka-console-consumer.sh --topic <name> --group <group-id>
# With key: kafka-console-consumer.sh --topic <name> --property print.key=true

# ─── CONSUMER GROUPS ─────────────────────────────────────────────────────────
# List:     kafka-consumer-groups.sh --list
# Describe: kafka-consumer-groups.sh --describe --group <group-id>   ← shows LAG
# Reset:    kafka-consumer-groups.sh --reset-offsets --group <group-id> --topic <name> --to-earliest --execute

# All commands need: --bootstrap-server localhost:9092
REFERENCE
