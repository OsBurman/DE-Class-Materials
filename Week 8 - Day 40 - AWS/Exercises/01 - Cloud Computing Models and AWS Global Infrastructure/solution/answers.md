# Exercise 01: Cloud Computing Models and AWS Global Infrastructure — Solution

## Requirement 1 — Cloud Service Models

| Model | Full name | What the provider manages | What you manage | AWS example service |
|---|---|---|---|---|
| IaaS | Infrastructure as a Service | Physical hardware, hypervisor, networking, raw storage | OS, runtime, middleware, application, data | Amazon EC2 |
| PaaS | Platform as a Service | Physical hardware, hypervisor, OS, runtime, middleware | Application code and data | AWS Elastic Beanstalk, AWS Lambda |
| SaaS | Software as a Service | Everything — hardware through application | Only your data and user configuration | Amazon WorkMail, AWS Chime |

---

## Requirement 2 — Shared Responsibility

| Item | IaaS | PaaS | SaaS |
|---|---|---|---|
| Physical data center security | **AWS** | **AWS** | **AWS** |
| Operating system patching | **Customer** | **AWS** | **AWS** |
| Application code | **Customer** | **Customer** | **AWS** |
| Network firewalls (Security Groups) | **Customer** (you configure SGs) | **Shared** (platform configures some; you configure app-level access) | **AWS** |
| Data encryption at rest | **Shared** (AWS provides tools; customer must enable and manage keys) | **Shared** | **Shared** |

*Note: AWS's official model says "security OF the cloud" (AWS) vs "security IN the cloud" (customer). The customer's share of responsibility decreases as you move from IaaS → PaaS → SaaS.*

---

## Requirement 3 — AWS Global Infrastructure

### 3a. AWS Region

An **AWS Region** is a geographically distinct area of the world containing multiple, isolated data center clusters (Availability Zones). Each Region is fully independent — resources in one Region are isolated from other Regions by default, which supports data residency, compliance, and fault isolation.

Three real Regions:
- `us-east-1` — US East (N. Virginia)
- `eu-west-1` — Europe (Ireland)
- `ap-southeast-1` — Asia Pacific (Singapore)

### 3b. Availability Zone

An **Availability Zone (AZ)** is one or more discrete, physically separate data centers within a Region. AZs within the same Region are connected by low-latency, high-throughput private fiber links but are far enough apart that a fire, flood, or power outage at one does not affect another.

Typical count: **3 AZs per Region** (some Regions have 2, newer ones have 4–6).

### 3c. Why deploy across multiple AZs?

Deploying across multiple AZs protects against a **single data center or AZ-level failure** (power outage, cooling failure, hardware failure). If one AZ goes down, the other AZs continue serving traffic. Combined with a load balancer (ALB/NLB), the failover is automatic and invisible to users. This is the standard way to achieve high availability (99.99%+) within a single Region.

### 3d. AWS Edge Locations

An **AWS Edge Location** is a small Point-of-Presence (PoP) distributed globally (200+ locations), separate from Regions and AZs. The primary service that uses edge locations is **Amazon CloudFront** (AWS's CDN). CloudFront caches static content (images, JS, CSS, video) at the edge location geographically closest to the user, dramatically reducing latency and offloading traffic from origin servers.

### 3e. Risk of single AZ deployment

**Risk:** The entire application has a single point of failure at the AZ level. An AZ outage (which AWS has experienced historically) takes down the app completely — no redundancy.

**Fix:** Deploy at least two instances of each tier (web, app, database) across **≥2 Availability Zones**. Place an **Application Load Balancer** in front to distribute traffic. Use **Amazon RDS Multi-AZ** for automatic database failover. This ensures the application remains available even if `us-east-1a` goes down entirely.

---

## Requirement 4 — Service Model Selection

| Scenario | Model | Reason |
|---|---|---|
| Custom OS-level network driver needed | **IaaS** | You need OS-level access (root/admin) to install kernel modules — only IaaS (EC2) gives you a full OS to customize. |
| Deploy Spring Boot without managing servers | **PaaS** | Elastic Beanstalk handles OS, runtime, scaling, and load balancing — you just upload the JAR. |
| Company uses Gmail | **SaaS** | Gmail is a fully managed email application — users don't manage servers, OS, or even application logic. |
| Custom Kubernetes networking plugins | **IaaS** | CNI plugins require node-level OS access and kernel networking features — PaaS abstracts that away. Even EKS (managed Kubernetes) gives you EC2 worker nodes (IaaS) for custom plugins. |
| Startup wants Node.js app running in minutes | **PaaS** | Elastic Beanstalk or AWS App Runner accepts a Node.js app and handles all infrastructure — ideal for a team with no DevOps expertise. |

---

## Requirement 5 — AWS Account Structure

**AWS Account:**
An AWS account is the fundamental billing and security boundary in AWS. Every resource (EC2, S3, RDS, etc.) lives inside exactly one account. Accounts have separate billing, separate IAM namespaces, and separate resource quotas. Most organizations use multiple accounts (dev, staging, production) to isolate environments.

**AWS Organizations:**
AWS Organizations is a service that lets you group multiple AWS accounts under a single management account. Benefits: consolidated billing (one invoice for all accounts), Service Control Policies (SCPs) to enforce guardrails across all accounts (e.g., "no resources outside us-east-1"), and centralized security/audit tooling. Large companies use it to manage hundreds of accounts with consistent governance.

**Root user:**
The root user is the original email+password login for an AWS account. It has unrestricted access to every resource and billing setting — it cannot be restricted by IAM policies. Best practices:
1. Enable MFA on the root account immediately.
2. Never use the root user for day-to-day tasks — create an IAM user or role with only the permissions needed.
3. Store root credentials in a password manager and only use them for account-level tasks (e.g., closing the account, changing root email).
The risk: if root credentials are leaked, an attacker has unlimited access to the entire account with no way to revoke it via IAM.
