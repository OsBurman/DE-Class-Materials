# Exercise 06 — RDS and DynamoDB: Solution

## Part 1 — RDS Core Concepts

### 1. RDS vs Self-managed on EC2

| Feature | Amazon RDS | Self-managed DB on EC2 |
|---|---|---|
| OS patching | AWS handles automatically | You patch manually (yum/apt) |
| Automated backups | Built-in, configurable retention (1–35 days) | You script it (cron + pg_dump) |
| Multi-AZ failover | One-click, automatic failover ~60–120s | You configure replication manually |
| Read replicas | Managed replicas with endpoint provisioning | You configure streaming replication |
| Custom DB engine version | Limited to RDS-supported versions | Full control, any version/fork |
| Cost | Higher (managed premium) | Lower compute cost, higher ops overhead |

### 2. Multi-AZ vs Read Replicas

**Multi-AZ:**
- Creates a **synchronous standby** replica in a different AZ. Writes are committed to the primary AND standby before acknowledging.
- The standby is **NOT readable** — it exists only for failover. If the primary fails, AWS automatically updates the DNS CNAME to the standby (no application change needed).

**Read Replicas:**
- Create **asynchronous copies** of the primary database that can serve read traffic.
- Replication is **asynchronous** — there may be slight replication lag. The app must route reads to replica endpoints.

**80% reads / 20% writes scenario:**
- Feature to improve **throughput**: **Read Replicas** — offload the 80% reads to 1–5 replicas
- Feature to improve **availability**: **Multi-AZ** — ensures failover if the primary AZ goes down

### 3. RDS CLI Commands

```bash
# a. Create PostgreSQL RDS instance
aws rds create-db-instance \
  --db-instance-identifier my-postgres-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --master-username admin \
  --master-user-password MySecurePass123! \
  --allocated-storage 20 \
  --publicly-accessible \
  --backup-retention-period 7 \
  --no-multi-az

# b. Describe instance to get endpoint (wait for "available" status)
aws rds describe-db-instances \
  --db-instance-identifier my-postgres-db \
  --query "DBInstances[0].Endpoint.Address" \
  --output text

# c. Connect with psql (replace <endpoint> with the value from step b)
psql -h <endpoint> -U admin -d postgres
```

---

## Part 2 — DynamoDB Core Concepts

### 4. DynamoDB Concepts

| Concept | Definition |
|---|---|
| Table | Top-level DynamoDB resource — a collection of items (similar to an SQL table, but schema-free) |
| Item | A single record in a table — a collection of attributes (similar to a row in SQL) |
| Attribute | A key-value pair within an item — can be string, number, binary, boolean, list, map, null |
| Partition Key (PK) | Hash key that determines which partition stores the item. Must be unique if no sort key. High cardinality reduces hot partitions. |
| Sort Key (SK) | Range key that, combined with PK, forms a composite primary key. Allows multiple items per PK, queryable in order. |
| Global Secondary Index (GSI) | An alternate primary key (different PK + optional SK) on the same table. Enables querying by non-primary attributes. Has its own read/write capacity. |
| Provisioned vs On-Demand capacity | Provisioned: you set RCU/WCU upfront (cheaper, requires capacity planning). On-Demand: AWS scales automatically per request (more expensive per request, zero planning). |

### 5. Data Model Design

Access patterns:
1. Get all items for a specific order → query by `orderId`
2. Get all orders for a specific user → query by `userId` (requires GSI)
3. Get a specific item within an order → get by `orderId` (PK) + `itemId` (SK)

- **Primary Key (PK):** `orderId` (String)
- **Sort Key (SK):** `itemId` (String) — e.g., `"ITEM-001"` for line items; for the order header itself use `"ORDER#META"`
- **GSI for access pattern 2:**
  - GSI name: `userId-index`
  - GSI PK: `userId` (String)
  - GSI SK: `createdAt` (String, ISO timestamp — sorts orders by date)

**Single-table design example items:**

| orderId (PK) | itemId (SK) | userId | productName | quantity | createdAt |
|---|---|---|---|---|---|
| ORD-001 | ORDER#META | user-42 | — | — | 2024-01-15T10:00:00Z |
| ORD-001 | ITEM-001 | user-42 | Keyboard | 1 | 2024-01-15T10:00:00Z |
| ORD-001 | ITEM-002 | user-42 | Mouse | 2 | 2024-01-15T10:00:00Z |

### 6. DynamoDB CLI Commands

```bash
# a. Create the orders table
aws dynamodb create-table \
  --table-name orders \
  --attribute-definitions \
    AttributeName=orderId,AttributeType=S \
    AttributeName=itemId,AttributeType=S \
    AttributeName=userId,AttributeType=S \
    AttributeName=createdAt,AttributeType=S \
  --key-schema \
    AttributeName=orderId,KeyType=HASH \
    AttributeName=itemId,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST \
  --global-secondary-indexes '[
    {
      "IndexName": "userId-index",
      "KeySchema": [
        {"AttributeName": "userId", "KeyType": "HASH"},
        {"AttributeName": "createdAt", "KeyType": "RANGE"}
      ],
      "Projection": {"ProjectionType": "ALL"}
    }
  ]'

# b. Put an item (order header)
aws dynamodb put-item \
  --table-name orders \
  --item '{
    "orderId":   {"S": "ORD-001"},
    "itemId":    {"S": "ORDER#META"},
    "userId":    {"S": "user-42"},
    "total":     {"N": "149.99"},
    "status":    {"S": "PENDING"},
    "createdAt": {"S": "2024-01-15T10:00:00Z"}
  }'

# Put a line item
aws dynamodb put-item \
  --table-name orders \
  --item '{
    "orderId":     {"S": "ORD-001"},
    "itemId":      {"S": "ITEM-001"},
    "userId":      {"S": "user-42"},
    "productName": {"S": "Mechanical Keyboard"},
    "quantity":    {"N": "1"},
    "price":       {"N": "99.99"},
    "createdAt":   {"S": "2024-01-15T10:00:00Z"}
  }'

# c. Get a single item by primary key (order header)
aws dynamodb get-item \
  --table-name orders \
  --key '{
    "orderId": {"S": "ORD-001"},
    "itemId":  {"S": "ORDER#META"}
  }'

# d. Query all items for orderId "ORD-001"
aws dynamodb query \
  --table-name orders \
  --key-condition-expression "orderId = :oid" \
  --expression-attribute-values '{":oid": {"S": "ORD-001"}}'
```

---

## Part 3 — RDS vs DynamoDB Decision

| Scenario | Choice | Reason |
|---|---|---|
| Banking app with ACID transactions | **RDS** | Requires ACID compliance, complex transactions, foreign key constraints — relational model is essential. Use Aurora PostgreSQL for performance. |
| Gaming leaderboard <5ms at 1M RPS | **DynamoDB** | Single-digit ms latency at any scale. A leaderboard is a simple PK lookup or sorted query — no joins needed. DynamoDB On-Demand handles traffic spikes automatically. |
| Spring Boot app using JPA/Hibernate | **RDS** | JPA/Hibernate is designed for relational databases. Using RDS (MySQL/PostgreSQL) requires zero code changes; switching to DynamoDB would require rewriting the data layer entirely. |
| Session store for 50M users' carts | **DynamoDB** | Shopping carts are key-value lookups by userId. Predictable low-latency reads, no need for SQL. DynamoDB scales horizontally without effort; no connection pool limits. |
| Complex multi-table joins & reporting | **RDS** | Reporting queries need SQL `JOIN`, `GROUP BY`, window functions, and ad-hoc analysis. DynamoDB doesn't support joins. Consider adding Amazon Redshift or Athena for analytics. |
| Startup with unknown data model | **DynamoDB** | Schema-free — you can add new attributes without `ALTER TABLE` migrations. Scales from zero to massive. Easy to start; harder to refactor once data grows, so plan access patterns early. |

---

## Key Takeaways

- **RDS** = managed relational DB. You still manage schema, queries, and connections. Best when your app needs SQL, joins, transactions, or an existing ORM.
- **DynamoDB** = fully serverless NoSQL. Zero servers, zero connections, infinite scale. Best for simple access patterns, high throughput, and unpredictable traffic.
- **Aurora** = AWS-native MySQL/PostgreSQL-compatible engine with shared distributed storage. Up to 5× faster than standard MySQL, faster failover (~30s), and cheaper than RDS Multi-AZ for high-traffic apps.
- **Single-table design** in DynamoDB collapses multiple entity types (orders + line items + users) into one table using composite keys — reduces costs and enables efficient queries.
- Never put a low-cardinality attribute (e.g., `status = "PENDING"`) as a DynamoDB partition key — all writes will land on one partition (hot partition problem).
