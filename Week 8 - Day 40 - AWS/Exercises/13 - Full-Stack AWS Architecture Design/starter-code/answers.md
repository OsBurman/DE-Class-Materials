# Exercise 13 — Full-Stack AWS Architecture Design: Your Answers

## Part 1 — Architecture Diagram

```
TODO: Draw the architecture below.
Show: browser → CloudFront → S3 (frontend) and browser → ALB → ECS → RDS
Include VPC boundary, public/private subnets, AZs, SNS/SQS fan-out.
```

---

## Part 2 — Service-by-Service Design

| Component | AWS Service | Configuration Notes |
|---|---|---|
| React frontend hosting | TODO | |
| React frontend CDN | TODO | |
| Order Service (containerized) | TODO | |
| User Service (containerized) | TODO | |
| Load balancing | TODO | |
| PostgreSQL database | TODO | |
| Email/SMS notifications | TODO | |
| Admin dashboard access control | TODO | |
| Container image storage | TODO | |
| Secrets (DB password, API keys) | TODO | |
| Monitoring & logging | TODO | |
| CI/CD pipeline | TODO | |

---

## Part 3 — Networking Design

### VPC Subnet Layout
```
VPC: 10.0.0.0/16

Public Subnet AZ-a:  TODO
Public Subnet AZ-b:  TODO
Private Subnet AZ-a: TODO
Private Subnet AZ-b: TODO
```

### Public vs Private Placement
| Resource | Subnet | Reason |
|---|---|---|
| TODO | Public | TODO |
| TODO | Private | TODO |

### Security Group Rules

**ALB SG:**
| Direction | Protocol | Port | Source | Reason |
|---|---|---|---|---|
| TODO | | | | |

**ECS Task SG:**
| Direction | Protocol | Port | Source | Reason |
|---|---|---|---|---|
| TODO | | | | |

**RDS SG:**
| Direction | Protocol | Port | Source | Reason |
|---|---|---|---|---|
| TODO | | | | |

---

## Part 4 — IAM Design

| Role | Assumed By | Permissions | Reason |
|---|---|---|---|
| `ecs-task-execution-role` | ECS/Fargate | TODO | |
| `order-service-task-role` | Order Service container | TODO | |
| `ci-cd-deploy-role` | GitHub Actions | TODO | |
| `rds-monitoring-role` | RDS Enhanced Monitoring | TODO | |

---

## Part 5 — Failure Scenario Analysis

| Failure | What happens | How it recovers |
|---|---|---|
| One ECS task crashes (OOM) | TODO | TODO |
| Entire AZ goes down | TODO | TODO |
| RDS primary fails | TODO | TODO |
| Bad deployment — 500 errors | TODO | TODO |
| Traffic spikes 10× | TODO | TODO |

---

## Part 6 — Cost Optimization

**1.** Feature to reduce costs during 9pm–9am idle: TODO

**2.** CloudFront with 95% same-region traffic — use it anyway? TODO

**3.** S3 feature for PDFs rarely accessed after 90 days: TODO

**4.** RDS db.r6g.2xlarge at 8% CPU — what to do? TODO
