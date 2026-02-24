# Exercise 13: Deploying a Full-Stack Application to AWS

## Objective

Design the complete AWS architecture for a production-grade full-stack Order Management System. This is the capstone exercise for Day 40 — synthesizing EC2/ECS, RDS, S3, IAM, CloudWatch, SNS/SQS, and networking into a coherent, deployable architecture.

## The System

You are the cloud architect for an **Order Management System** with:
- **React frontend** — static single-page application
- **Order Service** — Spring Boot REST API (containerized)
- **User Service** — Spring Boot REST API (containerized)
- **PostgreSQL database** — shared relational database
- **Notification system** — sends confirmation emails/SMS when orders are placed
- **Admin dashboard** — internal-only, not publicly accessible

**Requirements:** Highly available (no single point of failure), scalable (handles 10× traffic spikes), secure (least privilege, no public DB), observable (alarms on errors and latency).

## Requirements

### Part 1 — Architecture Diagram

Draw the architecture as ASCII art showing: all AWS services, VPC boundary, public/private subnets, traffic flow from browser to database, and which components live in public vs private subnets.

### Part 2 — Service-by-Service Design

| Component | AWS Service | Configuration Notes |
|---|---|---|
| React frontend hosting | | |
| React frontend CDN | | |
| Order Service (containerized) | | |
| User Service (containerized) | | |
| Load balancing | | |
| PostgreSQL database | | |
| Email/SMS notifications | | |
| Admin dashboard access control | | |
| Container image storage | | |
| Secrets (DB password, API keys) | | |
| Monitoring & logging | | |
| CI/CD pipeline | | |

### Part 3 — Networking Design

1. Design the VPC (`10.0.0.0/16`) with 2 public and 2 private subnets (one each per AZ). Include CIDR blocks and what goes in each.
2. Which resources go in public vs private subnets, and why?
3. Define security group rules for: ALB, ECS tasks, and RDS.

### Part 4 — IAM Design

| Role Name | Assumed By | Permissions Needed | Reason |
|---|---|---|---|
| `ecs-task-execution-role` | ECS/Fargate | | |
| `order-service-task-role` | Order Service container | | |
| `ci-cd-deploy-role` | GitHub Actions / CodePipeline | | |
| `rds-monitoring-role` | RDS Enhanced Monitoring | | |

### Part 5 — Failure Scenario Analysis

| Failure | What happens | How it recovers |
|---|---|---|
| One ECS task crashes (OOM) | | |
| An entire AZ goes down | | |
| RDS primary instance fails | | |
| Bad deployment causes 500 errors | | |
| Traffic spikes 10× in 5 minutes | | |

### Part 6 — Cost Optimization

1. Order Service is idle 9pm–9am. What AWS feature reduces costs overnight?
2. 95% of React frontend traffic is in the same region. Use CloudFront anyway?
3. 500 GB of order PDFs rarely accessed after 90 days. What S3 feature cuts cost?
4. RDS `db.r6g.2xlarge` running at 8% CPU. What should you do?

## Expected Output

```
Part 1: ASCII architecture diagram
Part 2: Complete service table
Part 3: CIDR layout + security group rules
Part 4: IAM roles table
Part 5: Failure scenario table
Part 6: 4 cost answers
```
