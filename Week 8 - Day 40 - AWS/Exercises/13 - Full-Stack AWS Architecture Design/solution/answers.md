# Exercise 13 — Full-Stack AWS Architecture Design: SOLUTION

## Part 1 — Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────────────┐
│  INTERNET                                                                │
│                                                                          │
│   [User Browser]                    [Admin Browser]                      │
│        │                                   │                             │
│        ▼                                   ▼                             │
│  [CloudFront CDN]                  [CloudFront + WAF]                    │
│  (React SPA cache)                 (IP restriction for /admin)           │
│        │                                   │                             │
│        ▼                                   │                             │
│  [S3 Static Site]                          │ API / Admin route           │
│                                            ▼                             │
└────────────────────────────────────────────┼─────────────────────────────┘
                                             │ HTTPS
┌────────────────────────────────────────────┼─────────────────────────────┐
│  VPC  10.0.0.0/16                          │                             │
│                                            ▼                             │
│  ┌─── PUBLIC SUBNETS (10.0.1.0/24 AZ-a | 10.0.2.0/24 AZ-b) ──────────┐ │
│  │   [Internet Gateway]  [ALB]  [NAT GW AZ-a]  [NAT GW AZ-b]          │ │
│  └────────────────────────┬────────────────────────────────────────────┘ │
│                           │ HTTP 8080                                     │
│  ┌─── PRIVATE SUBNETS (10.0.3.0/24 AZ-a | 10.0.4.0/24 AZ-b) ─────────┐ │
│  │                        ▼                                             │ │
│  │   [ECS Fargate]    [ECS Fargate]    [ECS Fargate]    [ECS Fargate]  │ │
│  │   Order Svc AZ-a   Order Svc AZ-b   User Svc AZ-a    User Svc AZ-b │ │
│  │          │                                │                          │ │
│  │          └────────────┬───────────────────┘                          │ │
│  │                       ▼                                              │ │
│  │              [RDS PostgreSQL Multi-AZ]                               │ │
│  │              Primary AZ-a | Standby AZ-b                            │ │
│  └──────────────────────────────────────────────────────────────────────┘ │
│                                                                            │
│   [ECR]  [Secrets Manager]  [SNS order-events]  [SQS email/sms queues]   │
│   [CloudWatch Logs/Metrics/Alarms]  [CodePipeline + CodeBuild]            │
└────────────────────────────────────────────────────────────────────────────┘

Traffic flows:
1. User → CloudFront → S3 (static React assets, cached at edge)
2. React app API calls → CloudFront → ALB → ECS Order/User Service → RDS
3. Order placed → ECS publishes to SNS → fan-out to SQS queues → Lambda sends email/SMS
4. Admin → CloudFront (WAF IP restriction) → ALB path rule /admin/* → Admin ECS service
```

---

## Part 2 — Service-by-Service Design

| Component | AWS Service | Configuration Notes |
|---|---|---|
| React frontend hosting | **S3** | Static website hosting; all public access blocked except via CloudFront Origin Access Control |
| React frontend CDN | **CloudFront** | Distribution over S3. HTTPS enforced. Custom domain + ACM cert. Cache-Control headers for JS/CSS. |
| Order Service (containerized) | **ECS Fargate** | 2+ tasks across 2 AZs in private subnets. Auto Scaling on ALB request count. ECR image. |
| User Service (containerized) | **ECS Fargate** | Same pattern. ALB path-based routing: `/api/orders/*` → Order Svc, `/api/users/*` → User Svc |
| Load balancing | **ALB (Application Load Balancer)** | In public subnets. HTTPS listener port 443. Path-based rules. Target groups per service with `/actuator/health` checks. |
| PostgreSQL database | **RDS PostgreSQL Multi-AZ** | `db.t3.medium` in private subnets. Multi-AZ standby. Automated backups 7 days. No public access. |
| Email/SMS notifications | **SNS + SQS + SES** | SNS topic `order-events` fans out to SQS queues. Lambda consumers send email via SES and SMS via SNS. |
| Admin dashboard access control | **CloudFront WAF** | WAF rule allows only company IP CIDR range on `/admin/*` paths. Or Cognito User Pools for auth. |
| Container image storage | **Amazon ECR** | Private repos per service. Image scanning on push. Lifecycle policy retains last 10 images. |
| Secrets (DB password, API keys) | **AWS Secrets Manager** | DB creds auto-rotated. ECS task role has `secretsmanager:GetSecretValue`. Injected as env vars at startup. |
| Monitoring & logging | **Amazon CloudWatch** | Container logs via `awslogs` driver. Custom metrics for latency/errors. Alarms on error rate and CPU. On-call dashboard. |
| CI/CD pipeline | **CodePipeline + CodeBuild** | GitHub → CodeBuild builds Docker image → pushes to ECR → updates ECS service (rolling deploy). |

---

## Part 3 — Networking Design

### VPC Subnet Layout

```
VPC: 10.0.0.0/16

Public Subnet AZ-a  (us-east-1a): 10.0.1.0/24  — ALB nodes, NAT Gateway
Public Subnet AZ-b  (us-east-1b): 10.0.2.0/24  — ALB nodes, NAT Gateway
Private Subnet AZ-a (us-east-1a): 10.0.3.0/24  — ECS tasks, RDS primary
Private Subnet AZ-b (us-east-1b): 10.0.4.0/24  — ECS tasks, RDS standby

Internet Gateway: attached to VPC (public subnets route 0.0.0.0/0 → IGW)
NAT Gateway AZ-a: elastic IP in 10.0.1.0/24 (private AZ-a outbound → NAT AZ-a)
NAT Gateway AZ-b: elastic IP in 10.0.2.0/24 (private AZ-b outbound → NAT AZ-b)
```

### Public vs Private Placement

| Resource | Subnet | Reason |
|---|---|---|
| ALB | Public | Must accept internet traffic on port 443 |
| NAT Gateway | Public | Needs an Elastic IP and internet route |
| ECS Fargate Tasks | Private | Never directly reachable from the internet; only via ALB |
| RDS PostgreSQL | Private | Database must not be publicly accessible |
| ECR / Secrets Manager | VPC Endpoints | Keep traffic on AWS network; private subnets avoid NAT costs for AWS API calls |

### Security Group Rules

**ALB Security Group (`alb-sg`):**

| Direction | Protocol | Port | Source | Reason |
|---|---|---|---|---|
| Inbound | HTTPS | 443 | 0.0.0.0/0 | Accept HTTPS from internet |
| Inbound | HTTP | 80 | 0.0.0.0/0 | Redirect to HTTPS |
| Outbound | TCP | 8080 | `ecs-sg` | Forward requests to ECS containers |

**ECS Task Security Group (`ecs-sg`):**

| Direction | Protocol | Port | Source | Reason |
|---|---|---|---|---|
| Inbound | TCP | 8080 | `alb-sg` | Only accept traffic from ALB |
| Outbound | TCP | 5432 | `rds-sg` | Connect to PostgreSQL |
| Outbound | HTTPS | 443 | 0.0.0.0/0 | Reach ECR, Secrets Manager, SNS (via NAT) |

**RDS Security Group (`rds-sg`):**

| Direction | Protocol | Port | Source | Reason |
|---|---|---|---|---|
| Inbound | TCP | 5432 | `ecs-sg` | Only ECS tasks can connect to the database |
| Outbound | — | — | — | RDS does not initiate connections |

---

## Part 4 — IAM Design

| Role | Assumed By | Permissions | Reason |
|---|---|---|---|
| `ecs-task-execution-role` | ECS/Fargate agent | `ecr:GetAuthorizationToken`, `ecr:BatchGetImage`, `logs:CreateLogStream`, `logs:PutLogEvents`, `secretsmanager:GetSecretValue` | Agent pulls image from ECR, writes logs, injects secrets at task start |
| `order-service-task-role` | Running app code | `secretsmanager:GetSecretValue`, `sns:Publish` (order-events topic), `sqs:SendMessage` (if needed) | App-level AWS calls — publish events, read secrets. Never reuse execution role for this. |
| `ci-cd-deploy-role` | CodePipeline / GitHub Actions | `ecr:BatchCheckLayerAvailability`, `ecr:PutImage`, `ecs:UpdateService`, `ecs:RegisterTaskDefinition`, `iam:PassRole` (scoped) | Deploy pipeline pushes new images and updates the ECS service |
| `rds-monitoring-role` | RDS Enhanced Monitoring | `AmazonRDSEnhancedMonitoringRole` managed policy | Allows RDS to publish OS-level metrics (memory, disk, network) to CloudWatch every 1–60s |

---

## Part 5 — Failure Scenario Analysis

| Failure | What happens | How it recovers |
|---|---|---|
| One ECS task crashes (OOM) | ECS detects the stopped task. ALB health check fails → deregisters the target. | ECS replaces the crashed task to maintain desired count. ALB reroutes to healthy tasks. Zero user impact with ≥2 tasks. |
| Entire AZ goes down | ALB detects unhealthy targets in failed AZ. ECS tasks in that AZ are unreachable. RDS standby is in the other AZ. | ALB routes 100% traffic to surviving AZ tasks. RDS promotes standby to primary (~60–120s). ECS may scale up additional tasks in the healthy AZ. |
| RDS primary fails | Multi-AZ detects failure. Standby has all committed data (synchronous replication). | RDS promotes standby, updates endpoint DNS CNAME. App reconnects via connection pool retry. Downtime: ~60–120s. |
| Bad deployment — 500 errors | Rolling deployment: new tasks fail `/actuator/health` checks repeatedly. ECS deployment circuit breaker triggers. CloudWatch alarm on `5XX_Count` fires → SNS alert. | ECS auto-rollback to previous task definition revision. Old tasks were kept running (minimumHealthyPercent=50) during rollout. |
| Traffic spikes 10× | ALB request latency rises. ECS Service Auto Scaling (Target Tracking on requests/task) triggers scale-out. Fargate launches new tasks in ~30–60s. | Stateless containers scale horizontally up to configured max. New tasks begin serving traffic once health checks pass. No manual intervention needed. |

---

## Part 6 — Cost Optimization

**1.** Scheduled Auto Scaling for overnight idle:

Configure **ECS Scheduled Scaling** actions: scale in to 1 task at 9pm, scale back out at 9am. Combined with Target Tracking Auto Scaling during the day, you get right-sized capacity 24/7. For non-critical background workers, use **Fargate Spot** (up to 70% cheaper, subject to interruption with 2-min warning).

**2.** CloudFront with 95% same-region traffic — yes, always:

- **Security**: CloudFront + OAC is the correct secure way to serve S3 without making the bucket public. Without it you'd need a public bucket or complex bucket policies.
- **AWS Shield Standard**: included free with CloudFront — DDoS protection at the edge.
- **S3 cost reduction**: CloudFront caches JS/CSS at the edge, drastically reducing S3 GET request charges for repeated visits.
- **Free TLS + custom domain**: ACM certs are free on CloudFront; the 5% of global users get significantly better performance.

**3.** S3 Lifecycle Policy for PDFs after 90 days:

Create an **S3 Lifecycle rule** to transition objects to **S3 Glacier Instant Retrieval** after 90 days (~$0.004/GB vs $0.023/GB for Standard — ~83% cheaper). For PDFs never accessed after 180 days, add a second transition to **S3 Glacier Deep Archive** (~$0.00099/GB — ~96% cheaper than Standard). Objects remain retrievable; Glacier Instant has millisecond retrieval; Deep Archive has hours.

**4.** Overprovisioned RDS at 8% CPU:

1. **Right-size**: move to `db.r6g.large` or `db.t4g.medium`. Confirm with `EXPLAIN ANALYZE` on slowest queries that the smaller instance handles the load.
2. **Verify with Performance Insights**: confirm 8% avg isn't masking brief spikes before downsizing.
3. **Graviton**: ensure you're on a `g` suffix instance type (r6g, t4g) for 35% better price/performance vs x86.
4. **Reserved Instances**: commit to a 1-year RI for ~40% savings vs On-Demand if this DB runs 24/7.
