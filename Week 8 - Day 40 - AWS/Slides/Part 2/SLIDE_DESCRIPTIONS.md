# Day 40 Part 2 — AWS: Managed Services, Containers, Serverless & Full-Stack Deployment
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** AWS Part 2 — Managed Services, Containers, Serverless & Deployment

**Subtitle:** S3, Beanstalk, RDS, ECR/ECS/EKS, Lambda, SNS/SQS, IAM, CloudWatch, DynamoDB

**Part 2 Learning Objectives:**
- Store and retrieve objects with Amazon S3 and host static sites
- Deploy Spring Boot applications with Elastic Beanstalk
- Provision and connect to managed databases with RDS
- Push container images to ECR and deploy them with ECS (Fargate)
- Understand EKS as managed Kubernetes on AWS
- Describe Lambda's serverless execution model
- Explain SNS and SQS as AWS-managed pub/sub and queue services
- Configure IAM roles, policies, and least-privilege access
- Monitor applications with CloudWatch metrics, logs, and alarms
- Describe DynamoDB as a fully managed NoSQL database
- Design a full-stack deployment architecture using AWS services

---

### Slide 2 — Amazon S3 — Object Storage

**Title:** Amazon S3 — Infinitely Scalable Object Storage

**What S3 is:** Not a file system. Not block storage. S3 is object storage — you store discrete objects (files) identified by a key (path-like string). Each object can be up to 5TB. S3 stores trillions of objects across AWS and has 99.999999999% (11 nines) durability — AWS replicates every object across multiple AZs within the region automatically.

**Core concepts:**
```
Bucket: A named container for objects.
  → Bucket names must be globally unique across all AWS accounts worldwide
  → Buckets live in a specific region
  → Example: "bookstore-app-uploads", "bookstore-static-assets"

Object: A file + its metadata, stored in a bucket.
  → Key: the "path" — "uploads/users/42/avatar.jpg"
  → Value: the file bytes
  → Metadata: content-type, custom headers, tags

URL pattern: https://{bucket}.s3.{region}.amazonaws.com/{key}
```

**Storage classes — tiered pricing by access frequency:**

| Class | Use Case | Retrieval |
|---|---|---|
| **S3 Standard** | Frequently accessed data | Milliseconds |
| **S3 Standard-IA** | Infrequent Access (monthly) | Milliseconds, retrieval fee |
| **S3 Glacier Instant** | Archive with instant access | Milliseconds, retrieval fee |
| **S3 Glacier Flexible** | Long-term archive (hours) | Minutes to hours |
| **S3 Glacier Deep Archive** | 7–10 year retention | 12 hours |

**Versioning:** Enable to keep multiple versions of every object. Accidental deletes become soft-deletes (delete marker). Recover any previous version. Costs more in storage — enable for critical data buckets.

**Bucket policies — access control:**
```json
{
  "Effect": "Allow",
  "Principal": "*",
  "Action": "s3:GetObject",
  "Resource": "arn:aws:s3:::bookstore-static-assets/*"
}
```
This allows public read on all objects in the bucket — required for static website hosting.

---

### Slide 3 — S3 Static Website Hosting and CloudFront

**Title:** Hosting Static Sites on S3 + CloudFront

**S3 static website hosting:**
```
1. Create bucket: "bookstore-frontend" (bucket name = domain-like name)
2. Enable "Static website hosting" in bucket properties
   → Index document: index.html
   → Error document: index.html (important for SPA routing)
3. Disable "Block all public access"
4. Add bucket policy allowing public s3:GetObject
5. Upload your built React/Angular app:
   aws s3 sync ./dist s3://bookstore-frontend --delete
6. Website available at:
   http://bookstore-frontend.s3-website-us-east-1.amazonaws.com
```

**Limitations of S3-only hosting:**
```
→ HTTP only (no HTTPS) — bad for production
→ AWS-generated domain name, not your custom domain
→ No edge caching — all requests hit the S3 bucket in us-east-1
→ Slow for global users
```

**CloudFront — CDN in front of S3 (production standard):**
```
CloudFront distribution:
  → Origin: your S3 bucket
  → HTTPS: CloudFront provides free TLS via ACM (AWS Certificate Manager)
  → Custom domain: point your DNS (Route 53 or external) to CloudFront
  → Global edge network: content cached at 200+ edge locations worldwide
  → Result: HTTPS, custom domain, fast global load times

Deployment pipeline for frontend:
  CI builds React app (npm run build)
  → aws s3 sync ./dist s3://bookstore-frontend --delete
  → aws cloudfront create-invalidation --distribution-id XXXXX --paths "/*"
    (invalidate CloudFront cache so users see new version immediately)
```

**S3 other use cases:** User file uploads, application backups, database snapshots, static assets (images/PDFs), data lake storage for analytics, Terraform state files.

---

### Slide 4 — AWS Elastic Beanstalk

**Title:** Elastic Beanstalk — PaaS for Application Deployment

**What Beanstalk is:** A deployment platform where you upload your application and AWS provisions and manages the underlying infrastructure — EC2 instances, Auto Scaling Groups, Load Balancers, Security Groups. You focus on code; Beanstalk handles the ops.

**Supported platforms:** Java (Tomcat, Corretto), Node.js, Python, Ruby, PHP, .NET, Docker, Go

**Deploying a Spring Boot application:**
```bash
# Package your Spring Boot app as a JAR
mvn clean package -DskipTests

# Option 1: Console
# → Create Environment → Managed Platform → Java
# → Upload your target/*.jar
# → Beanstalk provisions EC2 + ALB + ASG automatically

# Option 2: EB CLI
eb init bookstore-backend --platform java-21 --region us-east-1
eb create bookstore-prod --elb-type application
eb deploy    # uploads JAR, performs rolling deployment
```

**What Beanstalk creates for you:**
```
Elastic Beanstalk Environment:
  ├── Application Load Balancer
  ├── Auto Scaling Group (min 1, default)
  │     └── EC2 Instance (runs your Spring Boot JAR)
  ├── Security Groups (automatically configured)
  ├── CloudWatch Alarms
  └── S3 Bucket (stores deployment artifacts)
```

**Environment variables (replace application.properties):**
```yaml
# In Beanstalk console → Configuration → Software → Environment Properties:
SPRING_DATASOURCE_URL: jdbc:postgresql://{rds-endpoint}:5432/bookstoredb
SPRING_DATASOURCE_PASSWORD: {{RDS password}}
JWT_SECRET: {{secret from Secrets Manager}}
```

**Beanstalk is ideal for:** Teams that want the benefits of EC2 + Auto Scaling without configuring it all manually. Rapid deployments without DevOps expertise. The trade-off: less control over the underlying infrastructure compared to configuring EC2/ECS yourself.

---

### Slide 5 — Amazon RDS — Managed Relational Database

**Title:** RDS — Never Patch a Database Server Again

**What RDS is:** Managed relational database service. AWS handles: hardware provisioning, database installation, OS patching, database engine patching, automated backups, monitoring, storage scaling, and failover.

**Supported engines:** PostgreSQL, MySQL, MariaDB, Oracle, SQL Server, and Amazon Aurora (AWS's own high-performance fork of MySQL/PostgreSQL)

**Creating an RDS instance:**
```
Engine:         PostgreSQL 16
Instance class: db.t3.medium (2 vCPU, 4GB)
Storage:        gp3, 20GB, auto-scaling enabled (grows if needed)
Multi-AZ:       Yes (standby in another AZ for automatic failover)
VPC:            Your VPC
Subnet group:   Private subnets (databases should NEVER be public)
Security Group: Allow 5432 from app server security group only
```

**Multi-AZ deployment:**
```
Primary: us-east-1a
  → Synchronously replicates to Standby: us-east-1b
  → If primary fails: RDS automatically promotes standby to primary
  → DNS endpoint stays the same — your app reconnects automatically
  → Typical failover time: 1–2 minutes
  → No manual intervention required
```

**Connecting from Spring Boot:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://bookstore-db.{id}.us-east-1.rds.amazonaws.com:5432/bookstoredb
    username: admin
    password: ${DB_PASSWORD}        # from environment variable or Secrets Manager
  jpa:
    hibernate:
      ddl-auto: validate            # never use create/update in production
```

**Read Replicas** (for read-heavy workloads):
```
Primary: handles all writes
Read Replica 1: asynchronously replicated — handles read traffic
Read Replica 2: same — can be in a different region for disaster recovery
Your app: direct writes to primary endpoint, reads to read replica endpoint
```

---

### Slide 6 — ECR and ECS with Fargate

**Title:** ECR + ECS — Running Containers on AWS

**Amazon ECR — Elastic Container Registry:**
Your private Docker registry on AWS. Like Docker Hub, but AWS-managed, private, and integrated with IAM and ECS.

```bash
# Authenticate Docker to ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  {account-id}.dkr.ecr.us-east-1.amazonaws.com

# Create repository
aws ecr create-repository --repository-name bookstore/order-service

# Tag and push image
docker build -t order-service .
docker tag order-service:latest \
  {account-id}.dkr.ecr.us-east-1.amazonaws.com/bookstore/order-service:1.0.0
docker push {account-id}.dkr.ecr.us-east-1.amazonaws.com/bookstore/order-service:1.0.0
```

**Amazon ECS — Elastic Container Service:**
Fully managed container orchestration. Run your Docker containers without managing Kubernetes.

**Key ECS concepts:**
```
Task Definition: blueprint for a container (image, CPU, memory, port, env vars)
  → Like a docker-compose.yml for one service

Service: maintains N running copies of a Task Definition
  → If a container crashes: ECS replaces it automatically
  → Rolling deployments: replace containers one at a time

Cluster: logical grouping of services and infrastructure
```

**ECS with Fargate — serverless containers:**
```
Fargate = no EC2 servers to manage
  → You specify: 0.5 vCPU, 1GB memory for each container
  → AWS handles: finding host, running container, networking
  → You pay: per vCPU-second and GB-second while container is running
  → No idle capacity: containers only run when your service is running

Fargate task definition (relevant fields):
  cpu: "512"        # 0.5 vCPU
  memory: "1024"    # 1GB
  image: {account-id}.dkr.ecr.us-east-1.amazonaws.com/bookstore/order-service:1.0.0
  portMappings:
    - containerPort: 8080
  environment:
    - name: SPRING_DATASOURCE_URL
      value: jdbc:postgresql://{rds-endpoint}:5432/orderdb
  logConfiguration:
    logDriver: awslogs
    options:
      awslogs-group: /ecs/order-service
      awslogs-region: us-east-1
```

**ECS vs EKS:** ECS is simpler and AWS-specific. EKS is Kubernetes — portable but more complex. Choose ECS for greenfield AWS deployments where you don't need Kubernetes portability. Choose EKS if you have existing Kubernetes experience or tooling.

---

### Slide 7 — AWS EKS — Managed Kubernetes

**Title:** EKS — Kubernetes Without the Control Plane Headache

**What EKS is:** AWS manages the Kubernetes control plane (API server, etcd, scheduler, controller manager). You manage your worker nodes (EC2 or Fargate) and your workloads. You get a standard Kubernetes API — everything from Day 36 works here.

**EKS architecture:**
```
AWS-Managed Control Plane:
  → API Server (kubectl connects here)
  → etcd (cluster state)
  → Scheduler
  → Controller Manager
  → Fully managed: HA across 3 AZs, patched and updated by AWS

Your Worker Nodes:
  → EC2 Node Groups: EC2 instances you manage (instance type, scaling)
  → Fargate Profiles: serverless pods — no EC2 nodes to manage
  → Managed Node Groups: AWS-managed EC2 with auto-scaling

Your Workloads (everything from Day 36):
  → Deployments, Services, ConfigMaps, Secrets
  → kubectl apply -f bookstore-deployment.yaml  ← exact same YAML as local
```

**EKS with AWS integrations:**
```
Load Balancer: AWS Load Balancer Controller
  → kubectl Service type=LoadBalancer → creates an AWS ALB automatically

Container Registry: ECR
  → EKS worker nodes authenticate to ECR automatically via IAM

Database: Pods connect to RDS endpoint — no Kubernetes change needed

Monitoring: CloudWatch Container Insights
  → CPU, memory, network per pod — automatic when enabled

Secrets: AWS Secrets Manager → mount as Kubernetes Secrets via CSI driver
```

**When to use EKS vs ECS:**
- EKS: existing Kubernetes investment, need Kubernetes ecosystem (Helm, Istio, Argo CD), multi-cloud portability
- ECS: AWS-only deployment, simpler setup, no Kubernetes expertise on team

---

### Slide 8 — AWS Lambda — Serverless Computing

**Title:** AWS Lambda — Run Code Without Servers

**What Lambda is:** You write a function. You upload it to Lambda. AWS runs it in response to a trigger — an HTTP request, an S3 event, a scheduled cron, a Kafka/SQS message, a DynamoDB change. You pay per invocation and per millisecond of execution time. No server to provision, no idle cost, infinite scale (within concurrency limits).

**Lambda execution model:**
```
Trigger → Lambda invokes your function handler
         → Cold start: AWS finds or creates a container (100ms–3s first time)
         → Warm invocation: reuses existing container (~1ms overhead)
         → Function runs
         → Returns response (or publishes to next trigger)
         → Container kept warm briefly, then destroyed if no more invocations

Max timeout: 15 minutes
Memory: 128MB–10GB (CPU scales proportionally with memory)
```

**Java Lambda handler:**
```java
public class OrderNotificationHandler
        implements RequestHandler<SQSEvent, String> {

    @Override
    public String handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage message : event.getRecords()) {
            String body = message.getBody();
            // Process the SQS message — send email, update DB, etc.
            log.info("Processing message: {}", body);
        }
        return "OK";
    }
}
```

**Common Lambda triggers:**

| Trigger | Use Case |
|---|---|
| API Gateway | HTTP endpoints → serverless REST API |
| S3 Event | File uploaded → process/resize/validate |
| SQS Queue | Queue message → process async task |
| CloudWatch Events (EventBridge) | Scheduled cron jobs |
| DynamoDB Stream | Data change → trigger downstream processing |
| SNS Topic | Notification fan-out processing |

**Cold start consideration for Java:** JVM cold starts on Lambda can be 3–5 seconds for Spring Boot applications. Mitigation: use provisioned concurrency (keeps containers warm — adds cost), use GraalVM native image (fast startup), or use Lambda with lightweight frameworks instead of Spring Boot. For event-driven background processing where response time isn't critical, cold starts are acceptable.

**Lambda use cases in this course's architecture:**
- Image resizing when user uploads a profile picture to S3
- Scheduled database cleanup job (EventBridge cron)
- Processing SQS messages for notifications (serverless alternative to Notification Service EC2)

---

### Slide 9 — SNS and SQS — AWS-Managed Messaging

**Title:** SNS and SQS — Cloud-Native Pub/Sub and Queues

**Context:** In Day 39 you built event-driven microservices with Kafka. SNS and SQS are AWS-managed alternatives. Instead of running and operating a Kafka cluster, you use fully managed services. Trade-off: no message replay, simpler operational model, deep AWS integration.

**Amazon SNS — Simple Notification Service (Pub/Sub):**
```
SNS Topic: "OrderPlaced"
  → Publisher: Order Service publishes an OrderPlaced message to the topic
  → Subscribers (each receive every message):
      SQS Queue: "inventory-queue"      → Inventory Service polls
      SQS Queue: "notification-queue"   → Notification Service polls
      Lambda:    notification handler   → directly invoked
      HTTP endpoint: webhook to external partner
      Email: dev-team@company.com       (alerting use case)

Fan-out pattern: one SNS publish → multiple SQS queues / Lambda functions
```

**Amazon SQS — Simple Queue Service (Point-to-Point):**
```
SQS Queue: "order-processing-queue"
  → Producers send messages to the queue
  → Consumer instances poll and process
  → Each message delivered to ONE consumer only (competing consumers)
  → Message deleted from queue after consumer acknowledges it

Two queue types:
  Standard Queue: at-least-once delivery, best-effort ordering, nearly unlimited throughput
  FIFO Queue:     exactly-once delivery, strict ordering, 3,000 msg/sec throughput limit
```

**Spring Boot with SQS (io.awspring.cloud):**
```xml
<dependency>
  <groupId>io.awspring.cloud</groupId>
  <artifactId>spring-cloud-aws-starter-sqs</artifactId>
</dependency>
```
```java
// Send
@Autowired SqsTemplate sqsTemplate;
sqsTemplate.send("order-processing-queue", orderEvent);

// Receive
@SqsListener("order-processing-queue")
public void processOrder(OrderPlacedEvent event) {
    inventoryService.decrementStock(event);
}  // message auto-deleted on successful return
```

**SNS + SQS vs Kafka:**

| | Kafka | SNS + SQS |
|---|---|---|
| Message replay | ✅ Yes (offset reset) | ❌ No (deleted on consume) |
| Operational overhead | High (manage cluster) | Zero (fully managed) |
| Throughput | Millions/sec | High (SQS ~3,000 Standard) |
| AWS integration | Manual (MSK) | Native |
| Best for | Event streaming, audit log, replay | Simple async messaging, Lambda triggers |

---

### Slide 10 — AWS IAM — Identity and Access Management

**Title:** IAM — Who Can Do What in AWS

**IAM is the access control layer for every AWS service.** Every API call to AWS is authenticated (who are you?) and authorized (are you allowed to do this?). IAM manages both.

**IAM Concepts:**

```
IAM User: A person (developer, admin) with long-term credentials.
  → Access Key ID + Secret Access Key for CLI/SDK access
  → Username + password for console access
  → Best practice: don't use the root account; create IAM users

IAM Group: A collection of IAM users.
  → Attach policies to the group; all users inherit them
  → Example: "developers" group with permission to EC2/ECS/ECR

IAM Role: An identity assumed by a service or application (not a person).
  → EC2 instance assumes a role → gets permission to read from S3
  → ECS task assumes a role → gets permission to write to SQS
  → Lambda assumes a role → gets permission to write to DynamoDB
  → No long-term credentials — temporary tokens auto-rotated

IAM Policy: A JSON document defining permissions.
  → Attached to users, groups, or roles
```

**IAM Policy example — least privilege:**
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:PutObject"
      ],
      "Resource": "arn:aws:s3:::bookstore-uploads/*"
    },
    {
      "Effect": "Allow",
      "Action": "sqs:SendMessage",
      "Resource": "arn:aws:sqs:us-east-1:{account-id}:order-placed"
    }
  ]
}
```

**IAM Role for EC2/ECS (no credentials in code):**
```
Without IAM Role:
  → Store AWS access key in application.properties or environment variable
  → Credentials at risk of exposure in code, logs, AMI snapshots

With IAM Role (correct approach):
  → Assign role to EC2 instance or ECS task at launch
  → Role allows specific S3/SQS/DynamoDB permissions
  → Application uses the SDK → SDK automatically fetches temporary credentials
    from instance metadata (http://169.254.169.254/...) — no credentials in code
  → Credentials automatically rotated every hour by AWS
```

**The principle of least privilege:**
> Grant only the permissions needed for the specific task. An order service should be able to write to SQS. It should NOT have admin access to all AWS services.

---

### Slide 11 — AWS CloudWatch — Monitoring and Logging

**Title:** CloudWatch — Unified Observability for AWS

**CloudWatch is AWS's native observability platform:** metrics, logs, alarms, dashboards, and anomaly detection — all in one service.

**CloudWatch Logs from Spring Boot:**
```xml
<!-- pom.xml -->
<dependency>
  <groupId>ca.pjer</groupId>
  <artifactId>logback-awslogs-appender</artifactId>
  <version>1.6.0</version>
</dependency>
```
```xml
<!-- logback-spring.xml -->
<appender name="CLOUDWATCH" class="ca.pjer.logback.AwsLogsAppender">
  <logGroupName>/bookstore/order-service</logGroupName>
  <logStreamUuidPrefix>order-</logStreamUuidPrefix>
  <logRegion>us-east-1</logRegion>
  <maxBatchLogEvents>50</maxBatchLogEvents>
</appender>
```

Or: if running on ECS with `awslogs` log driver (shown in ECS slide), CloudWatch log streams are created automatically — no code changes needed.

**CloudWatch Metrics:**
```
AWS automatically provides metrics for all managed services:
  EC2:    CPUUtilization, NetworkIn, StatusCheckFailed
  RDS:    DatabaseConnections, FreeStorageSpace, ReadLatency
  ECS:    CPUUtilization, MemoryUtilization per service and task
  SQS:    ApproximateNumberOfMessagesVisible (queue depth → consumer lag)
  Lambda: Invocations, Duration, Errors, Throttles

Spring Boot with Micrometer:
  → /actuator/prometheus endpoint emits custom metrics
  → CloudWatch agent or Container Insights collects them
  → Custom metrics: orders per second, payment latency, error rate
```

**CloudWatch Alarms:**
```
Alarm: "Order Service CPU > 80% for 5 minutes"
  → Action: notify SNS topic → email to dev team
  → Action: trigger Auto Scaling policy → scale out

Alarm: "RDS FreeStorageSpace < 5GB"
  → Notification: alert before disk full

Alarm: "SQS QueueDepth > 1000"
  → Consumer lag alert — same concept as Kafka consumer lag from Day 39
```

**CloudWatch Log Insights (query your logs):**
```sql
-- Find all errors in the last hour
fields @timestamp, @message
| filter level = "ERROR"
| sort @timestamp desc
| limit 100

-- Find all requests for a specific traceId (OTel correlation)
fields @timestamp, @message
| filter @message like "traceId=abc123"
| sort @timestamp desc
```

**CloudWatch in Week 9:** Week 9 reviews observability and cloud monitoring. CloudWatch is the AWS implementation of what you'll review conceptually.

---

### Slide 12 — DynamoDB Basics

**Title:** DynamoDB — Fully Managed NoSQL at Any Scale

**What DynamoDB is:** Serverless, fully managed key-value and document NoSQL database. No servers to provision. Scales automatically to any traffic level. Single-digit millisecond response times at any scale.

**When to use DynamoDB over PostgreSQL/RDS:**
- Massive scale with simple access patterns (user sessions, shopping carts, game leaderboards)
- Variable or unpredictable schema (event logs, user settings)
- Need true serverless scaling — no idle cost, scales to zero
- Global tables: replicate to multiple regions automatically

**When NOT to use DynamoDB:**
- Complex SQL queries with multiple joins
- ACID transactions across many items (possible but complex)
- Reporting/analytics workloads (not designed for ad-hoc queries)

**DynamoDB data model:**
```
Table: "UserSessions"

Primary Key (required):
  → Partition Key (PK): userId         ← determines which partition stores the item
  → Sort Key (SK): sessionId           ← sorts items within a partition (optional)

Item (like a row):
  {
    "userId": "42",             ← Partition Key
    "sessionId": "sess-abc123", ← Sort Key
    "token": "eyJhbGci...",
    "expiresAt": 1705350000,
    "deviceType": "mobile",
    "lastActive": "2024-01-15T14:32:00Z"
    // Any additional attributes — schema-free
  }
```

**Access patterns — DynamoDB is designed around your access patterns:**
```java
// Get all sessions for a user
QueryRequest request = QueryRequest.builder()
    .tableName("UserSessions")
    .keyConditionExpression("userId = :uid")
    .expressionAttributeValues(Map.of(":uid", AttributeValue.fromS("42")))
    .build();

// Get a specific session
GetItemRequest request = GetItemRequest.builder()
    .tableName("UserSessions")
    .key(Map.of(
        "userId", AttributeValue.fromS("42"),
        "sessionId", AttributeValue.fromS("sess-abc123")
    ))
    .build();
```

**Capacity modes:**
- **On-Demand**: pay per read/write request — automatic scaling, no capacity planning, higher per-request cost
- **Provisioned**: specify read/write capacity units — lower cost at steady predictable load, requires capacity planning

---

### Slide 13 — Deploying Full-Stack Applications to AWS

**Title:** Full-Stack on AWS — Putting It All Together

**Complete bookstore architecture on AWS:**

```
Users (browsers + mobile)
        │
        ▼
Route 53 (DNS) → CloudFront (CDN)
                     │
          ┌──────────┴──────────────────┐
          ▼                             ▼
S3 (React/Angular SPA)    Application Load Balancer (HTTPS)
                                        │
                          ┌─────────────┼────────────┐
                          ▼             ▼             ▼
                      ECS Service   ECS Service   ECS Service
                      order-svc     catalog-svc   user-svc
                      (Fargate)     (Fargate)     (Fargate)
                          │             │             │
                   ECR images      ECR images    ECR images
                          │             │             │
                          └──────┬──────┘             │
                                 ▼                    ▼
                           RDS PostgreSQL         RDS PostgreSQL
                           (orders DB)            (users DB)
                                 │
                                 ▼
                         SNS Topic "order-placed"
                          │               │
                    SQS Queue         SQS Queue
                    (inventory)       (notification)
                          │               │
                   ECS Service       Lambda Function
                   inventory-svc     (send email)
                          │
                   RDS PostgreSQL
                   (inventory DB)

All services:
  → CloudWatch Logs (via ECS awslogs driver)
  → CloudWatch Metrics (via Container Insights)
  → IAM Roles (no hardcoded credentials)
  → ECR (private image registry)
  → Secrets Manager (DB passwords, JWT secret)
```

**CI/CD pipeline connecting to Day 37:**
```
GitHub push → GitHub Actions:
  1. mvn test (unit + integration tests)
  2. docker build → tag with Git SHA
  3. aws ecr push (push image to ECR)
  4. aws ecs update-service --force-new-deployment
     (ECS performs rolling deployment)
```

---

### Slide 14 — AWS Service Decision Guide

**Title:** Choosing the Right AWS Service

**Compute decision:**

```
Need a full Linux server with complete control?  → EC2
Want AWS to manage the server config?            → Elastic Beanstalk
Running Docker containers?                       → ECS (Fargate) or EKS
Event-driven, short-lived functions?             → Lambda
```

**Database decision:**

```
Relational data with SQL?                        → RDS (PostgreSQL / MySQL / Aurora)
Key-value / document at massive scale?           → DynamoDB
Cache / session storage?                         → ElastiCache (Redis / Memcached)
```

**Messaging decision:**

```
Fan-out (one publish → many subscribers)?       → SNS
Task queue (one message → one worker)?          → SQS
High-throughput event streaming with replay?    → Amazon MSK (managed Kafka)
Serverless event-driven triggers?               → EventBridge
```

**Storage decision:**

```
Unstructured files / objects?                   → S3
Block storage for EC2?                          → EBS
Shared file system across multiple EC2?         → EFS (Elastic File System)
```

**Access control:**
```
All of it → IAM (users, roles, policies — every service)
```

---

### Slide 15 — Week 8 Wrap-Up

**Title:** Week 8 Complete — The Full Modern Backend Stack

**What you've built this week:**

| Day | Topic | Key Skills |
|---|---|---|
| Day 36 | Docker & Kubernetes | Containerization, pods, deployments, services |
| Day 37 | CI/CD | GitHub Actions, pipelines, deployment strategies |
| Day 38 | Microservices | Service decomposition, circuit breakers, OTel tracing |
| Day 39 | Kafka | Event-driven architecture, Spring Kafka, error handling |
| **Day 40** | **AWS** | **Cloud infrastructure, managed services, full-stack deployment** |

**The complete stack:**
```
Frontend:    React / Angular → S3 + CloudFront (Day 40)
Backend:     Spring Boot microservices → ECS on Fargate (Day 40)
Database:    PostgreSQL on RDS (Day 40) / DynamoDB for NoSQL (Day 40)
Messaging:   Kafka (Day 39) or SNS/SQS (Day 40)
Containers:  Docker (Day 36) → ECR registry (Day 40)
Orchestration: Kubernetes (Day 36) or ECS/EKS (Day 40)
CI/CD:       GitHub Actions (Day 37) → deploy to ECS (Day 40)
Security:    Spring Security + JWT (Day 29–30) + IAM (Day 40)
Observability: OTel tracing (Day 38) + CloudWatch (Day 40)
```

**Week 9 Preview:** Integration Week — tying everything together, full-stack integration patterns, security review, testing strategies, performance optimization, and capstone project planning.

---

*End of Part 2 Slide Descriptions*
