# Exercise 03: Kafka CLI — Topics, Producers, and Consumers

## Objective

Use the Kafka command-line tools to create topics, produce messages, and consume messages — building hands-on familiarity with the operations you'll automate with Spring Kafka.

## Background

Every Spring Kafka application ultimately calls the same underlying Kafka APIs that the CLI tools expose. Understanding the CLI helps you:
- Debug production issues without an application running
- Verify topic configuration and partition assignment
- Test message flow end-to-end in seconds
- Understand exactly what `kafkaTemplate.send()` is doing under the hood

The three primary CLI tools are:
| Tool | Purpose |
|---|---|
| `kafka-topics.sh` | Create, list, describe, alter, and delete topics |
| `kafka-console-producer.sh` | Write messages to a topic from stdin |
| `kafka-console-consumer.sh` | Read messages from a topic to stdout |

> **Running Kafka locally:** The commands below assume Kafka is running on `localhost:9092`. The easiest way to start a local instance is with Docker:
> ```bash
> docker run -d --name kafka -p 9092:9092 \
>   apache/kafka:3.7.0
> ```
> If you are using a Kafka installation (not Docker), the scripts are in `$KAFKA_HOME/bin/`.

## Requirements

Work through the tasks in order. Each builds on the previous one. Write or uncomment the correct command for each `# TODO` in the starter file.

### Part A — Topic Management

1. **Create** a topic named `order-events` with **3 partitions** and a **replication factor of 1**.
2. **List** all topics in the cluster to confirm `order-events` was created.
3. **Describe** the `order-events` topic to see its partition leaders and replication assignments.
4. **Create** a second topic named `user-events` with **1 partition** and **replication factor of 1**.
5. **Delete** the `user-events` topic.

### Part B — Producing Messages

6. **Open a producer** connected to the `order-events` topic. Type and send these three messages one per line:
   ```
   {"orderId": "ORD-001", "status": "PLACED"}
   {"orderId": "ORD-002", "status": "PLACED"}
   {"orderId": "ORD-003", "status": "SHIPPED"}
   ```
7. **Open a producer with a key separator** (`:`) so messages can be sent as `key:value` pairs. Produce two messages:
   ```
   ORD-001:{"orderId": "ORD-001", "status": "DELIVERED"}
   ORD-002:{"orderId": "ORD-002", "status": "CANCELLED"}
   ```

### Part C — Consuming Messages

8. **Open a consumer** that reads `order-events` **from the beginning** (all historical messages).
9. **Open a consumer** in a named consumer group `order-processor` that reads from the beginning.
10. **Open a second terminal** and run another consumer in the **same group** `order-processor`. Observe: both consumers are now active in the same group. What happens to partition assignment?
11. **Check consumer group offsets** for the `order-processor` group using `kafka-consumer-groups.sh`. What does the `LAG` column tell you?

### Part D — Reflection Questions

Answer these in the comment block at the bottom of `commands.sh`:

- Q1: In step 10, if the topic has 3 partitions and 2 consumers are in the group, how are the partitions distributed?
- Q2: If you run a consumer **without** `--from-beginning`, which messages does it receive?
- Q3: What `--property` flag tells the console producer to use a key separator? What is the default separator character?

## Hints

- All commands use `--bootstrap-server localhost:9092` (not `--zookeeper` — that flag is deprecated).
- `kafka-console-producer.sh` reads from stdin; press `Ctrl+C` to exit.
- `kafka-console-consumer.sh` streams to stdout; press `Ctrl+C` to exit.
- To send key/value pairs with the console producer, add:
  `--property "parse.key=true" --property "key.separator=:"`
- `kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group <group-id>` shows CURRENT-OFFSET, LOG-END-OFFSET, and LAG per partition.

## Expected Output

```
Part A — Topic created and confirmed:
  $ kafka-topics.sh --list ...
  order-events
  user-events

  $ kafka-topics.sh --describe --topic order-events ...
  Topic: order-events  PartitionCount: 3  ReplicationFactor: 1
  Partition: 0  Leader: 1  Replicas: 1  Isr: 1
  Partition: 1  Leader: 1  Replicas: 1  Isr: 1
  Partition: 2  Leader: 1  Replicas: 1  Isr: 1

Part B — Messages sent (no output; producer accepts input silently).

Part C — Consumer reads messages from beginning (all 5 messages visible).

Part D — Reflection answers written in comments.
```
