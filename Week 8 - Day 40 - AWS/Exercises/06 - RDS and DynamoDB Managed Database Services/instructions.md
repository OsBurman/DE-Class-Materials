# Exercise 06: RDS and DynamoDB — Managed Database Services on AWS

## Objective

Understand when to use Amazon RDS (relational) vs DynamoDB (NoSQL), configure an RDS instance, model a DynamoDB table, and analyze the trade-offs between managed database options.

## Background

AWS offers two primary managed database paradigms: **Amazon RDS** manages relational databases (MySQL, PostgreSQL, Oracle, SQL Server) without you having to handle OS patching, backups, or failover. **Amazon DynamoDB** is a fully serverless key-value and document NoSQL database that scales to any throughput with single-digit millisecond latency. Choosing between them — and between AWS managed vs self-managed — is one of the most important architectural decisions in a cloud application.

## Requirements

### Part 1 — RDS Core Concepts

1. Complete the table:

   | Feature | Amazon RDS | Self-managed DB on EC2 |
   |---|---|---|
   | OS patching | | |
   | Automated backups | | |
   | Multi-AZ failover | | |
   | Read replicas | | |
   | Custom DB engine version | | |
   | Cost | | |

2. **RDS Multi-AZ** vs **Read Replicas** — they sound similar but serve different purposes. Explain:
   - What does **Multi-AZ** do? Is the standby instance readable?
   - What do **Read Replicas** do? Are they synchronous or asynchronous?
   - If your app has 80% reads and 20% writes, which feature helps throughput? Which helps availability?

3. **RDS CLI exercise.** Write the commands to:
   a. Create a PostgreSQL RDS instance (`db.t3.micro`, storage 20 GB, publicly accessible for this exercise, master user `admin`)
   b. Describe the instance to get its endpoint
   c. Connect to it using `psql`

### Part 2 — DynamoDB Core Concepts

4. Define each DynamoDB concept:

   | Concept | Definition |
   |---|---|
   | Table | |
   | Item | |
   | Attribute | |
   | Partition Key (PK) | |
   | Sort Key (SK) | |
   | Global Secondary Index (GSI) | |
   | Provisioned vs On-Demand capacity | |

5. **Data modeling exercise.** You are designing DynamoDB for an e-commerce order system. Orders have many items; users place many orders.

   Design the DynamoDB table:
   - Table name: `orders`
   - Access patterns you must support:
     1. Get all items for a specific order (orderId)
     2. Get all orders for a specific user (userId)
     3. Get a specific item within an order (orderId + itemId)
   
   Fill in:
   - Primary Key (PK): ______
   - Sort Key (SK): ______
   - What GSI is needed to support access pattern 2?

6. **DynamoDB CLI exercise.** Write commands to:
   a. Create the `orders` table with your designed schema
   b. Put an item into the table
   c. Get a single item by primary key
   d. Query all items for orderId `"ORD-001"`

### Part 3 — RDS vs DynamoDB Decision

7. For each scenario, choose **RDS** or **DynamoDB** and justify:

   | Scenario | Choice | Reason |
   |---|---|---|
   | A banking app storing transactions requiring ACID compliance | | |
   | A gaming leaderboard needing <5ms reads at 1 million RPS | | |
   | A Spring Boot app using JPA/Hibernate already | | |
   | A session store for 50 million users' shopping carts | | |
   | An app with complex multi-table joins and reporting queries | | |
   | A startup that doesn't know its final data model yet | | |

## Hints

- RDS Multi-AZ creates a **synchronous standby** in another AZ for failover (HA). The standby is NOT used for reads — it's purely for failover.
- Read replicas use **asynchronous replication** — you trade some consistency for read scalability. Your app must route read queries to replica endpoints.
- DynamoDB's PK determines which partition your data lives on. Choosing a high-cardinality PK (like `orderId`) avoids "hot partitions."
- For DynamoDB, use a composite key (PK + SK) when you have one-to-many relationships within the same table (single-table design).
- `aws dynamodb put-item` requires attribute values in DynamoDB JSON format: `{"orderId": {"S": "ORD-001"}, "amount": {"N": "99.99"}}`.

## Expected Output

```
Part 1: RDS comparison table + Multi-AZ vs Read Replica explanation + CLI commands
Part 2: DynamoDB definitions table + data model design + CLI commands
Part 3: Decision table with 6 rows filled
```
