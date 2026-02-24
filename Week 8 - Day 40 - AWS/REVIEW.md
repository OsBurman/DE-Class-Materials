# Day 40 — AWS Review Sheet
## Quick Reference: AWS Services, CLI Commands, Architecture Patterns

---

## Cloud Service Models

| Model | Definition | AWS Examples |
|---|---|---|
| **IaaS** | Infrastructure as a Service — raw compute, storage, network | EC2, EBS, VPC |
| **PaaS** | Platform as a Service — managed platform for your code | Beanstalk, RDS, ElastiCache |
| **SaaS** | Software as a Service — fully managed software | SQS, SNS, S3, DynamoDB, CloudWatch |
| **FaaS** | Function as a Service — run code per-invocation | Lambda |

---

## AWS Global Infrastructure

| Concept | Definition |
|---|---|
| **Region** | Geographic location (us-east-1, eu-west-1) — data stays here by default |
| **Availability Zone** | Isolated data center(s) within a region with independent power/network |
| **Edge Location** | CloudFront CDN caching points — 200+ worldwide |

**Production rule:** Deploy across at least 2 AZs for high availability.

---

## EC2 — Elastic Compute Cloud

**Instance family quick reference:**

| Family | Optimized For | Example |
|---|---|---|
| t3, t4g | Burstable (dev, small workloads) | t3.micro, t3.medium |
| m5, m6i | General purpose (balanced) | m5.large |
| c5, c6i | Compute-intensive (CPU-heavy) | c5.xlarge |
| r5, r6i | Memory-intensive (large JVM heaps) | r5.xlarge |
| p3, p4 | GPU (ML inference) | p3.xlarge |

**Pricing models:**

| Model | Best For | Savings |
|---|---|---|
| On-Demand | Unpredictable, short-term | — |
| Reserved (1–3yr) | Steady-state production | Up to 72% |
| Savings Plans | Flexible commit to $ amount | Up to 72% |
| Spot | Fault-tolerant, interruptible | Up to 90% |

**Essential EC2 CLI commands:**
```bash
aws ec2 describe-instances
aws ec2 start-instances --instance-ids i-1234567890
aws ec2 stop-instances --instance-ids i-1234567890
aws ec2 describe-security-groups
```

**SSH into EC2:**
```bash
chmod 400 my-key.pem
ssh -i my-key.pem ec2-user@{public-ip}    # Amazon Linux
ssh -i my-key.pem ubuntu@{public-ip}      # Ubuntu
ssh -i my-key.pem admin@{public-ip}       # Debian
```

---

## Security Groups

| Property | Value |
|---|---|
| Type | Stateful (return traffic auto-allowed) |
| Scope | Attached to an EC2 instance / ECS task |
| Direction | Inbound rules + Outbound rules |
| Default | Deny all inbound, allow all outbound |

**Security Group chaining pattern:**
```
Load Balancer SG: allow 80, 443 from 0.0.0.0/0
App Server SG:    allow 8080 from Load Balancer SG ID
Database SG:      allow 5432 from App Server SG ID
```

---

## VPC and Networking

```
Your VPC (10.0.0.0/16)
├── Public Subnet (10.0.1.0/24) — us-east-1a
│     ├── Application Load Balancer
│     └── NAT Gateway (for private subnet outbound internet)
├── Public Subnet (10.0.2.0/24) — us-east-1b
│     └── Application Load Balancer (Multi-AZ)
├── Private Subnet (10.0.3.0/24) — us-east-1a
│     └── EC2 / ECS Tasks (Spring Boot)
├── Private Subnet (10.0.4.0/24) — us-east-1b
│     └── EC2 / ECS Tasks (Spring Boot)
├── Private Subnet (10.0.5.0/24) — us-east-1a
│     └── RDS Primary
└── Private Subnet (10.0.6.0/24) — us-east-1b
      └── RDS Standby (Multi-AZ)
```

| Gateway | Role |
|---|---|
| Internet Gateway | Connects your VPC to the public internet |
| NAT Gateway | Lets private subnet instances make outbound internet calls — no inbound |

---

## Auto Scaling Groups

```
Launch Template → specifies: AMI, instance type, security groups, user data
ASG Config:
  Min capacity:     2   (never below this — HA across 2 AZs)
  Desired capacity: 2   (start here)
  Max capacity:     10  (never above this)
```

**Scaling policy types:**

| Type | How It Works |
|---|---|
| Target Tracking | Keep CPU at 60% — scale out/in automatically |
| Step Scaling | Add 2 instances when CPU > 70%, add 4 when CPU > 90% |
| Scheduled | Scale up Monday 8am, scale down Sunday 6pm |
| Predictive | ML-based forecast from historical patterns |

---

## Amazon S3

**Core operations:**
```bash
aws s3 mb s3://my-bucket                         # make bucket
aws s3 ls s3://my-bucket                         # list contents
aws s3 cp file.txt s3://my-bucket/path/file.txt  # upload single file
aws s3 sync ./dist s3://my-bucket --delete       # sync directory
aws s3 rm s3://my-bucket/path/file.txt           # delete object
```

**Storage classes:**

| Class | Access Frequency | Retrieval |
|---|---|---|
| Standard | Daily | Milliseconds, no fee |
| Standard-IA | Monthly | Milliseconds + retrieval fee |
| Glacier Instant | Quarterly | Milliseconds + retrieval fee |
| Glacier Flexible | Yearly | Minutes–hours |
| Deep Archive | Multi-year | 12 hours |

**Public read bucket policy:**
```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": "*",
    "Action": "s3:GetObject",
    "Resource": "arn:aws:s3:::my-bucket/*"
  }]
}
```

**Frontend deployment pipeline:**
```bash
npm run build
aws s3 sync ./dist s3://my-frontend-bucket --delete
aws cloudfront create-invalidation \
  --distribution-id EXXXXXXXXXXXXX \
  --paths "/*"
```

---

## Elastic Beanstalk

**EB CLI commands:**
```bash
eb init my-app --platform java-21 --region us-east-1
eb create my-env --elb-type application
eb deploy
eb status
eb logs
eb terminate my-env
```

**What Beanstalk manages:** EC2 instances, ALB, ASG, security groups, CloudWatch health checks, S3 artifact bucket

**What you manage:** Application code, environment variables, RDS (external)

---

## Amazon RDS

**JDBC URL format:**
```
jdbc:postgresql://{instance}.{id}.{region}.rds.amazonaws.com:5432/{dbname}
```

**Spring Boot production config:**
```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate    # NEVER use create/update in production
    open-in-view: false
```

**Multi-AZ vs Read Replica:**

| Feature | Multi-AZ | Read Replica |
|---|---|---|
| Purpose | High availability | Read scaling |
| Replication | Synchronous | Asynchronous |
| Can serve reads? | ❌ No (standby only) | ✅ Yes |
| Automatic failover? | ✅ Yes (1–2 min) | ❌ Manual |
| Cross-region? | ❌ No | ✅ Yes |

---

## Amazon ECR — Container Registry

```bash
# Authenticate
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  {account-id}.dkr.ecr.us-east-1.amazonaws.com

# Create repository
aws ecr create-repository --repository-name my-service

# Tag and push
docker tag my-service:latest \
  {account-id}.dkr.ecr.us-east-1.amazonaws.com/my-service:1.0.0
docker push {account-id}.dkr.ecr.us-east-1.amazonaws.com/my-service:1.0.0
```

---

## Amazon ECS — Container Orchestration

**ECS concepts:**

| Concept | Description |
|---|---|
| **Task Definition** | Blueprint: image, CPU/memory, ports, env vars, logging |
| **Task** | A running instance of a Task Definition |
| **Service** | Keeps N tasks running; handles rolling deployments and load balancer registration |
| **Cluster** | Logical grouping of services and infrastructure |

**Fargate vs EC2 launch type:**

| | Fargate | EC2 |
|---|---|---|
| Manage instances? | ❌ No | ✅ Yes |
| Pricing | Per vCPU + GB/second | EC2 instance rates |
| Best for | Most workloads | GPU, specific instance types |

**Rolling deployment:**
```bash
# After pushing new image to ECR:
aws ecs update-service \
  --cluster my-cluster \
  --service order-service \
  --force-new-deployment
```

---

## Amazon EKS — Managed Kubernetes

**Same kubectl you know from Day 36:**
```bash
kubectl get pods -n bookstore
kubectl apply -f deployment.yaml
kubectl rollout status deployment/order-service
kubectl logs -f deployment/order-service
```

**EKS-specific AWS integrations:**
- LoadBalancer Service → AWS ALB (via AWS Load Balancer Controller)
- ECR image pull → automatic via IAM role on worker node
- Persistent storage → EBS volumes via EBS CSI driver
- Secrets → AWS Secrets Manager via CSI driver

**ECS vs EKS decision:**

| Choose ECS when | Choose EKS when |
|---|---|
| Greenfield AWS deployment | Existing K8s investment |
| Team lacks K8s expertise | Need K8s ecosystem (Helm, Istio, Argo CD) |
| Simpler setup preferred | Multi-cloud portability required |

---

## AWS Lambda

**Handler interface:**
```java
// SQS trigger
public class MyHandler implements RequestHandler<SQSEvent, String> {
    public String handleRequest(SQSEvent event, Context ctx) { ... }
}

// API Gateway trigger
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent,
                                                   APIGatewayProxyResponseEvent> {
    public APIGatewayProxyResponseEvent handleRequest(
        APIGatewayProxyRequestEvent event, Context ctx) { ... }
}
```

**Lambda limits:**

| Limit | Value |
|---|---|
| Max execution time | 15 minutes |
| Memory | 128MB – 10GB |
| Concurrent executions | 1,000 (default, can increase) |
| Deployment package size | 50MB zip / 250MB unzipped |

**Lambda triggers:**

| Trigger | Use Case |
|---|---|
| API Gateway | HTTP → REST API endpoint |
| S3 Event | File upload → process/resize |
| SQS | Queue message → async worker |
| EventBridge (cron) | Scheduled jobs |
| DynamoDB Streams | Change data capture |

---

## SNS and SQS

**SNS CLI:**
```bash
aws sns create-topic --name order-placed
aws sns publish --topic-arn arn:aws:sns:us-east-1:{id}:order-placed \
  --message '{"orderId":"42","total":59.99}'
```

**SQS CLI:**
```bash
aws sqs create-queue --queue-name order-processing
aws sqs send-message --queue-url {url} --message-body '{"orderId":"42"}'
aws sqs receive-message --queue-url {url}
aws sqs delete-message --queue-url {url} --receipt-handle {handle}
```

**Spring Cloud AWS SQS:**
```java
// Send
sqsTemplate.send("queue-name", payload);

// Receive
@SqsListener("queue-name")
public void handle(MyEvent event) { ... }
// Auto-deletes on success. Auto-requeues on exception.
```

**SNS/SQS vs Kafka:**

| | SNS + SQS | Kafka / MSK |
|---|---|---|
| Message replay | ❌ No | ✅ Yes |
| Operational overhead | Zero | High (or MSK cost) |
| Max throughput | ~3,000 msg/sec (FIFO) | Millions/sec |
| AWS integration | Native | Via MSK |
| Serverless consumer | ✅ Lambda trigger | ❌ Needs consumer service |

**SQS FIFO vs Standard:**

| | Standard | FIFO |
|---|---|---|
| Ordering | Best-effort | Strict |
| Delivery | At-least-once | Exactly-once |
| Throughput | Unlimited | 3,000 msg/sec |

---

## AWS IAM

**IAM entity types:**

| Entity | Purpose | Credentials |
|---|---|---|
| User | Human developer/admin | Access key + password |
| Group | Collection of users | Inherits from group policy |
| Role | Assumed by AWS service | Temporary (auto-rotated) |
| Policy | JSON permissions document | Attached to user/group/role |

**Policy JSON structure:**
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["s3:GetObject", "s3:PutObject"],
      "Resource": "arn:aws:s3:::my-bucket/*"
    },
    {
      "Effect": "Deny",
      "Action": "s3:DeleteObject",
      "Resource": "*"
    }
  ]
}
```

**IAM best practices:**
- ✅ Never use the root account for day-to-day work
- ✅ Enable MFA on root and all IAM users
- ✅ Use IAM Roles for EC2/ECS/Lambda — never hardcode access keys
- ✅ Apply least privilege — grant only what the service needs
- ✅ Rotate IAM user access keys regularly
- ✅ Store secrets (DB passwords) in Secrets Manager, not environment variables

**EC2/ECS IAM Role pattern:**
```
ECS Task Definition → Task Execution Role (pull from ECR, write to CloudWatch Logs)
ECS Task Definition → Task Role (app-specific: read S3, write SQS)

Your app SDK code:
  AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
  // SDK auto-fetches credentials from instance metadata — no keys in code
```

---

## AWS CloudWatch

**Log query (Logs Insights):**
```sql
fields @timestamp, @message
| filter level = "ERROR"
| sort @timestamp desc
| limit 100

-- Find by trace ID (OTel correlation):
fields @timestamp, @message
| filter @message like "traceId={your-trace-id}"
| sort @timestamp desc
```

**Creating an alarm (CLI):**
```bash
aws cloudwatch put-metric-alarm \
  --alarm-name "High-CPU-Order-Service" \
  --metric-name CPUUtilization \
  --namespace AWS/ECS \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold \
  --evaluation-periods 2 \
  --alarm-actions arn:aws:sns:us-east-1:{id}:ops-alerts
```

**Key metrics by service:**

| Service | Key Metrics |
|---|---|
| EC2 / ECS | CPUUtilization, MemoryUtilization |
| RDS | DatabaseConnections, ReadLatency, FreeStorageSpace |
| SQS | ApproximateNumberOfMessagesVisible (consumer lag) |
| Lambda | Invocations, Errors, Duration, Throttles |
| ALB | TargetResponseTime, HTTPCode_Target_5XX_Count |

---

## Amazon DynamoDB

**Table structure:**
```
Table: "UserSessions"
Primary Key:
  Partition Key (PK): "userId"   ← required, determines data distribution
  Sort Key (SK):      "sessionId"  ← optional, enables range queries
Other attributes: schema-free
```

**Java SDK v2:**
```java
// Get item
GetItemRequest get = GetItemRequest.builder()
    .tableName("UserSessions")
    .key(Map.of(
        "userId", AttributeValue.fromS("42"),
        "sessionId", AttributeValue.fromS("sess-abc")
    ))
    .build();
GetItemResponse response = dynamoDbClient.getItem(get);

// Put item
PutItemRequest put = PutItemRequest.builder()
    .tableName("UserSessions")
    .item(Map.of(
        "userId", AttributeValue.fromS("42"),
        "sessionId", AttributeValue.fromS("sess-abc"),
        "token", AttributeValue.fromS("eyJ...")
    ))
    .build();
dynamoDbClient.putItem(put);
```

**Capacity modes:**

| Mode | Best For | Pricing |
|---|---|---|
| On-Demand | Variable/unpredictable traffic | Per request |
| Provisioned | Steady predictable traffic | Per RCU/WCU hour |

**DynamoDB vs RDS:**

| Use Case | DynamoDB | RDS |
|---|---|---|
| Complex SQL with joins | ❌ | ✅ |
| High-scale key lookups | ✅ | ❌ |
| Flexible evolving schema | ✅ | ❌ (migrations required) |
| ACID transactions | Limited | ✅ |
| Serverless / no idle cost | ✅ | ❌ |

---

## Service Selection Quick Reference

**Compute:**
```
Full Linux VM / full control?          → EC2
Managed app platform (upload JAR)?    → Elastic Beanstalk
Docker containers, AWS-native?        → ECS + Fargate
Docker containers, Kubernetes?        → EKS
Event-driven short functions?         → Lambda
```

**Database:**
```
Relational SQL?                        → RDS (PostgreSQL / Aurora)
Key-value / document, massive scale?   → DynamoDB
Cache / session?                       → ElastiCache (Redis)
```

**Storage:**
```
Objects / files?                       → S3
Block storage for EC2?                 → EBS
Shared file system?                    → EFS
```

**Messaging:**
```
Fan-out (1 → many)?                   → SNS
Task queue (1 → 1)?                   → SQS
High-throughput streaming + replay?   → Amazon MSK (Kafka)
```

**Networking:**
```
DNS?                                   → Route 53
CDN?                                   → CloudFront
Load Balancer?                         → Application Load Balancer (ALB)
Private network?                       → VPC + Subnets + Security Groups
```

---

## Full-Stack Deployment Architecture

```
[Users]
   │
[Route 53] ──────────────────────────────────────────────────────────────┐
   │                                                                       │
[CloudFront CDN]                                                          │
   ├─ S3: React/Angular SPA (npm run build → aws s3 sync)                │
   └─ ALB: API requests ──────────────────────────────────────────────────┘
             │
   ┌─────────┼──────────────────┐
   ▼         ▼                  ▼
ECS/Fargate ECS/Fargate     ECS/Fargate
order-svc   catalog-svc     user-svc
(from ECR)  (from ECR)      (from ECR)
   │             │               │
   ▼             ▼               ▼
 RDS PG       RDS PG           RDS PG
(private)    (private)        (private)
   │
SNS Topic "order-placed"
   ├── SQS "inventory-queue" → ECS inventory-svc
   └── SQS "notification-queue" → Lambda (send email)

All services:
  CloudWatch Logs (awslogs ECS log driver)
  CloudWatch Metrics + Alarms
  IAM Roles (no hardcoded credentials)
  Secrets Manager (DB passwords, JWT secrets)
  ECR (private image registry)

CI/CD (Day 37):
  GitHub Actions → mvn test → docker build → ECR push → ECS rolling deploy
```

---

## Week 8 Service Map

| Day | Technology | AWS Equivalent |
|---|---|---|
| Day 36 | Docker | ECR (registry) + ECS/EKS (orchestration) |
| Day 36 | Kubernetes | EKS (managed control plane) |
| Day 37 | CI/CD | CodePipeline / GitHub Actions → ECR + ECS |
| Day 37 | Observability | CloudWatch |
| Day 38 | Microservices | ECS Services or EKS Pods |
| Day 38 | OTel Tracing | CloudWatch X-Ray / Container Insights |
| Day 39 | Kafka | Amazon MSK (managed Kafka) / SNS + SQS |
| Day 40 | All services | EC2, S3, RDS, ECS, EKS, Lambda, etc. |
