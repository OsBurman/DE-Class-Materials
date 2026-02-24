# Exercise 06 — RDS and DynamoDB: Your Answers

## Part 1 — RDS Core Concepts

### 1. RDS vs Self-managed on EC2

| Feature | Amazon RDS | Self-managed DB on EC2 |
|---|---|---|
| OS patching | TODO | TODO |
| Automated backups | TODO | TODO |
| Multi-AZ failover | TODO | TODO |
| Read replicas | TODO | TODO |
| Custom DB engine version | TODO | TODO |
| Cost | TODO | TODO |

### 2. Multi-AZ vs Read Replicas

**Multi-AZ:**
- What it does: TODO
- Is the standby readable? TODO

**Read Replicas:**
- What they do: TODO
- Synchronous or asynchronous? TODO

**80% reads / 20% writes scenario:**
- Feature to improve throughput: TODO
- Feature to improve availability: TODO

### 3. RDS CLI Commands

```bash
# a. Create PostgreSQL RDS instance
# TODO

# b. Describe instance to get endpoint
# TODO

# c. Connect with psql
# TODO
```

---

## Part 2 — DynamoDB Core Concepts

### 4. DynamoDB Concepts

| Concept | Definition |
|---|---|
| Table | TODO |
| Item | TODO |
| Attribute | TODO |
| Partition Key (PK) | TODO |
| Sort Key (SK) | TODO |
| Global Secondary Index (GSI) | TODO |
| Provisioned vs On-Demand capacity | TODO |

### 5. Data Model Design

Access patterns:
1. Get all items for a specific order (orderId)
2. Get all orders for a specific user (userId)
3. Get a specific item within an order (orderId + itemId)

- **Primary Key (PK):** TODO
- **Sort Key (SK):** TODO
- **GSI needed for access pattern 2:** TODO (GSI PK: ______, GSI SK: ______)

### 6. DynamoDB CLI Commands

```bash
# a. Create the orders table with your schema
# TODO

# b. Put an item into the table
# TODO

# c. Get a single item by primary key
# TODO

# d. Query all items for orderId "ORD-001"
# TODO
```

---

## Part 3 — RDS vs DynamoDB Decision

| Scenario | Choice | Reason |
|---|---|---|
| Banking app with ACID transactions | TODO | TODO |
| Gaming leaderboard <5ms at 1M RPS | TODO | TODO |
| Spring Boot app using JPA/Hibernate | TODO | TODO |
| Session store for 50M users' carts | TODO | TODO |
| Complex multi-table joins & reporting | TODO | TODO |
| Startup with unknown data model | TODO | TODO |
