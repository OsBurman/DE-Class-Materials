# Day 40 Part 1 — AWS: Cloud Fundamentals, EC2, Networking & Scaling
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Amazon Web Services — Cloud Infrastructure for Modern Applications

**Subtitle:** Part 1: Cloud Models, EC2, Networking, and Auto Scaling

**Part 1 Learning Objectives:**
- Distinguish IaaS, PaaS, and SaaS cloud computing models
- Explain AWS global infrastructure: Regions, Availability Zones, and Edge Locations
- Launch and configure an EC2 instance from scratch
- Configure Security Groups for inbound and outbound traffic
- SSH into a running EC2 instance using a key pair
- Describe Amazon Machine Images and Elastic Block Store
- Understand VPC, subnets, and the networking foundation of AWS
- Configure Auto Scaling Groups to handle variable load

---

### Slide 2 — Cloud Computing Models — IaaS, PaaS, SaaS

**Title:** Cloud Computing Models — What You Manage vs What AWS Manages

**The spectrum of cloud services:**

```
                    You Manage ←─────────────────────────────────→ AWS Manages

Traditional On-Prem:   Hardware, networking, storage, OS, runtime,
                       middleware, data, applications — ALL you

IaaS (Infrastructure as a Service):
  AWS manages:         Physical hardware, networking, storage, virtualization
  You manage:          OS, runtime, middleware, data, applications
  Examples:            EC2, EBS, VPC

PaaS (Platform as a Service):
  AWS manages:         Hardware + OS + runtime + middleware + scaling
  You manage:          Your application code and data
  Examples:            Elastic Beanstalk, RDS, Lambda

SaaS (Software as a Service):
  AWS manages:         Everything
  You manage:          Configuration, your data
  Examples:            Gmail, Salesforce, GitHub, Slack
```

**AWS services by model:**

| Model | AWS Examples | When to Use |
|---|---|---|
| **IaaS** | EC2, EBS, VPC, S3 | Full control over infrastructure; custom OS/runtime needs |
| **PaaS** | Elastic Beanstalk, RDS, ECS, EKS | Focus on application code; let AWS handle infrastructure |
| **FaaS** (Function as a Service) | Lambda | Event-driven; pay per execution; no servers to manage |
| **SaaS** | SES, SNS, SQS, Cognito | Fully managed services; plug into your app via API |

**The cloud value proposition:**
- **No upfront capital expense**: pay per use instead of buying servers
- **Elastic capacity**: scale up in minutes, scale down instantly — pay only for what you use
- **Global reach**: deploy to any region in the world with a few clicks
- **Managed services**: AWS handles patching, hardware replacement, availability

---

### Slide 3 — AWS Overview — Console, CLI, and SDK

**Title:** AWS — Getting Around the Platform

**Market position:** AWS is the world's largest cloud provider (~32% market share). Over 200 services spanning compute, storage, databases, AI/ML, networking, security, and more.

**Three ways to interact with AWS:**

**1. AWS Management Console (UI)**
```
https://console.aws.amazon.com
  → Point-and-click interface
  → Good for: learning, one-off tasks, exploration
  → Bad for: automation, repeatable infrastructure
```

**2. AWS CLI (Command Line)**
```bash
# Install
brew install awscli   # macOS

# Configure with your access key
aws configure
  AWS Access Key ID: [your access key]
  AWS Secret Access Key: [your secret key]
  Default region name: us-east-1
  Default output format: json

# Example commands
aws ec2 describe-instances
aws s3 ls s3://my-bucket
aws ecs list-clusters
```

**3. AWS SDK (programmatic)**
```java
// Java SDK v2
S3Client s3 = S3Client.builder()
    .region(Region.US_EAST_1)
    .build();

s3.putObject(PutObjectRequest.builder()
    .bucket("my-app-bucket")
    .key("uploads/image.png")
    .build(),
    RequestBody.fromFile(Path.of("image.png")));
```

**Infrastructure as Code (awareness):**
- **AWS CloudFormation**: define AWS resources in YAML/JSON templates
- **Terraform**: vendor-neutral IaC tool, very popular
- **AWS CDK**: define infrastructure in TypeScript/Python/Java code
- Week 9 covers IaC concepts. The goal: infrastructure should be code, not click sequences.

**AWS Free Tier:** AWS offers a free tier for 12 months including 750 hours/month EC2 `t2.micro`, 5GB S3 storage, 750 hours RDS, and more. Use this for practice without cost.

---

### Slide 4 — AWS Global Infrastructure

**Title:** AWS Global Infrastructure — Regions, AZs, and Edge Locations

**Regions:**
```
A Region is a geographic area containing multiple data centers.
Examples: us-east-1 (N. Virginia), eu-west-1 (Ireland),
          ap-southeast-1 (Singapore), sa-east-1 (São Paulo)

As of 2024: 33 launched regions, 105 AZs worldwide

Choosing a region:
  1. Proximity to users (latency)
  2. Compliance (GDPR requires EU data to stay in EU)
  3. Service availability (new services launch in us-east-1 first)
  4. Cost (prices vary by region — us-east-1 is cheapest for most services)
```

**Availability Zones (AZs):**
```
Each Region contains 2–6 Availability Zones.
An AZ is one or more physical data centers with independent:
  - Power supply
  - Cooling systems
  - Physical security
  - Network connectivity

AZ names: us-east-1a, us-east-1b, us-east-1c, us-east-1d, us-east-1e, us-east-1f

Why multiple AZs?
  Deploy across 2+ AZs → survive one data center failure
  RDS Multi-AZ: primary in us-east-1a, standby in us-east-1b
  Auto Scaling: spread instances across AZs for high availability
```

**Edge Locations (CloudFront CDN):**
```
200+ edge locations worldwide — closer to users than Regions
CloudFront caches static content (images, JS, CSS) at edge locations
Result: fast load times globally even if your origin is only in us-east-1
```

**Architectural principle:** For production: deploy across at least 2 AZs in your primary region. Use Route 53 for DNS failover between regions if multi-region is required.

---

### Slide 5 — EC2 Introduction — Instance Types and Pricing

**Title:** EC2 — Elastic Compute Cloud

**What EC2 is:** Virtual machines running on AWS hardware. You choose the OS (via AMI), the size (instance type), the networking, and the storage.

**Instance type families:**

| Family | Optimized for | Examples | Use Cases |
|---|---|---|---|
| **t3/t4g** | Burstable general purpose | t3.micro, t3.small | Dev/test, low-traffic apps, microservices |
| **m5/m6i** | Balanced compute/memory | m5.large, m5.xlarge | App servers, caches, backend services |
| **c5/c6i** | Compute optimized | c5.large, c5.2xlarge | High-CPU workloads, batch processing |
| **r5/r6i** | Memory optimized | r5.large, r5.2xlarge | Databases, in-memory caches (Redis large) |
| **p3/p4** | GPU | p3.2xlarge | Machine learning training, video encoding |

**Instance type naming: `t3.medium`**
```
t   = family (burstable)
3   = generation (higher = newer hardware, better price/performance)
.medium = size (nano < micro < small < medium < large < xlarge < 2xlarge < ...)
Each size doubles: 1 vCPU/1GB → 2 vCPU/2GB → ...
```

**Pricing models:**

| Model | Description | Savings vs On-Demand | Use When |
|---|---|---|---|
| **On-Demand** | Pay by the hour/second, no commitment | — | Dev/test, unpredictable workloads |
| **Reserved** | 1 or 3 year commitment | Up to 72% | Steady-state production workloads |
| **Savings Plans** | Flexible commitment to $ spend/hour | Up to 66% | Mix of instance types |
| **Spot** | Bid on unused capacity, can be interrupted | Up to 90% | Batch jobs, fault-tolerant workloads |

---

### Slide 6 — Launching an EC2 Instance

**Title:** Launching EC2 — Step by Step

**Launch Wizard walkthrough:**

**Step 1: AMI Selection**
```
Amazon Linux 2023 AMI  ← AWS-optimized, includes AWS tools, free tier eligible
Ubuntu 22.04 LTS       ← Popular, familiar to many developers
Windows Server 2022    ← For .NET applications
Custom AMI             ← Your pre-configured machine image (covered next slide)
```

**Step 2: Instance Type**
```
t2.micro / t3.micro — free tier eligible; 1 vCPU, 1GB RAM
Good for: learning, development, low-traffic apps
```

**Step 3: Key Pair**
```
Create new key pair → "my-bookstore-key"
  Type: RSA
  Format: .pem (for SSH on macOS/Linux)
Download and save — you can't download it again!
```

**Step 4: Network Settings**
```
VPC: default VPC (fine for learning; custom VPC for production)
Subnet: choose an AZ (or let AWS pick)
Auto-assign public IP: Enable (needed to SSH from your laptop)
Security Group: create new — see next slide
```

**Step 5: Storage**
```
Root volume: 8GB gp3 (default)
Can add additional EBS volumes — covered in EBS slide
```

**Step 6: Advanced — User Data (optional)**
```bash
#!/bin/bash
# This script runs once when the instance first starts
yum update -y
yum install -y java-21-amazon-corretto
# Start your application, install dependencies, etc.
```

**User data** is how you automate EC2 bootstrap — install software, configure services. This is the simplest form of IaC for EC2.

---

### Slide 7 — Security Groups and Network ACLs

**Title:** Security Groups — The Firewall for Your EC2 Instances

**Security Groups:** Virtual firewalls that control inbound and outbound traffic for EC2 instances (and other AWS resources). Stateful — if you allow inbound traffic, the response is automatically allowed out.

**Inbound rules:**
```
Type        Protocol   Port Range   Source           Description
SSH         TCP        22           My IP (x.x.x.x/32)  Allow SSH from your IP only
HTTP        TCP        80           0.0.0.0/0           Allow web traffic from anywhere
HTTPS       TCP        443          0.0.0.0/0           Allow HTTPS from anywhere
Custom TCP  TCP        8080         0.0.0.0/0           Spring Boot app port
PostgreSQL  TCP        5432         sg-app-server-id    Allow only from app server SG
```

**Security Group chaining — most important pattern:**
```
Internet → [SG: web-sg (allows 80, 443 from 0.0.0.0/0)]
             → EC2 App Server

App Server → [SG: db-sg (allows 5432 ONLY from web-sg)]
               → RDS Database

The database SG allows port 5432 only from the app server's security group ID.
No direct internet access to the database — only the app server can reach it.
This is how you create public/private tiers.
```

**Outbound rules:**
```
By default: all outbound traffic allowed (0.0.0.0/0)
Restrict outbound when: compliance requirements, zero-trust networking
```

**Security Group vs Network ACL:**

| Feature | Security Group | Network ACL |
|---|---|---|
| Applies to | EC2 instance (or ENI) | Entire subnet |
| Stateful? | ✅ Yes — responses auto-allowed | ❌ No — must allow both directions |
| Rules | Allow rules only (no explicit deny) | Allow + Deny rules |
| Evaluation | All rules evaluated | Rules evaluated in order by number |
| Default | No inbound, all outbound | Allow all (default VPC) |

**For this course:** Security Groups are what you configure per-service. Network ACLs are an additional layer you add for compliance or extra security.

---

### Slide 8 — SSH Into EC2

**Title:** Connecting to Your EC2 Instance via SSH

**The key pair system:**
```
When you create a key pair:
  AWS generates: public key (stored on the EC2 instance in ~/.ssh/authorized_keys)
                 private key (downloaded as .pem file — keep this secret!)

SSH authentication:
  Client presents private key → EC2 verifies against stored public key → access granted
  No password needed — cryptographic proof of identity
```

**Step-by-step connection:**

```bash
# 1. Set correct permissions on the key file (required — SSH refuses keys that are too open)
chmod 400 ~/Downloads/my-bookstore-key.pem

# 2. Find your EC2 public IP or DNS from the AWS console
# Example: ec2-52-90-123-45.compute-1.amazonaws.com

# 3. SSH command
ssh -i ~/Downloads/my-bookstore-key.pem ec2-user@ec2-52-90-123-45.compute-1.amazonaws.com

# Default usernames by AMI:
# Amazon Linux 2023: ec2-user
# Ubuntu:            ubuntu
# Debian:            admin
# RHEL:              ec2-user or root
```

**Troubleshooting connection issues:**
```
"Permission denied (publickey)":
  → Wrong username for the AMI
  → Key permissions not 400 (chmod 400 key.pem)
  → Using wrong key file

"Connection timed out":
  → Security Group doesn't allow port 22 from your IP
  → Instance is in a private subnet (no public IP)
  → Instance hasn't fully started yet — wait 1-2 minutes

"WARNING: UNPROTECTED PRIVATE KEY FILE":
  → Fix with: chmod 400 my-key.pem
```

**Once connected — basic exploration:**
```bash
# Check your instance metadata
curl http://169.254.169.254/latest/meta-data/instance-id
curl http://169.254.169.254/latest/meta-data/instance-type

# Check available disk space
df -h

# Check memory
free -h

# Check OS
cat /etc/os-release
```

---

### Slide 9 — Amazon Machine Images (AMI)

**Title:** AMIs — Pre-Configured Templates for EC2 Instances

**What an AMI contains:**
```
An AMI is a snapshot of a configured machine, including:
  → Operating system (Amazon Linux, Ubuntu, Windows, etc.)
  → Pre-installed software (Java, nginx, database)
  → Configuration (users, security settings, environment variables)
  → One or more EBS snapshots (the disk contents)

Launching an EC2 instance = creating a running copy of an AMI
```

**Types of AMIs:**

| Type | Source | Use When |
|---|---|---|
| **AWS Quick Start AMIs** | Amazon-provided | Starting point — clean OS |
| **AWS Marketplace AMIs** | Third-party vendors | Pre-configured software stacks (Bitnami WordPress, etc.) |
| **Community AMIs** | Public contributions | Specialized configurations |
| **Custom AMIs** | You create them | Your app pre-installed and configured |

**Creating a custom AMI from a running instance:**
```
1. Launch base EC2 instance (Amazon Linux 2023)
2. SSH in and configure:
   - Install Java 21
   - Install your application JAR
   - Configure systemd service to start app on boot
3. In AWS Console: EC2 → Instances → select instance
   → Actions → Image and templates → Create image
4. AMI is created (takes 5–15 minutes)
5. Launch new instances from your custom AMI — pre-configured, ready to go immediately

Use case: Auto Scaling Group launches need your app pre-installed.
Without custom AMI: each new instance runs User Data script (slow).
With custom AMI: instances start with everything ready in seconds.
```

**AMI IDs are region-specific** — if you copy an AMI to another region, it gets a new AMI ID.

---

### Slide 10 — Elastic Block Store (EBS)

**Title:** EBS — Persistent Block Storage for EC2

**What EBS is:** Persistent network-attached storage for EC2 instances. Like an external hard drive that survives instance termination (if configured). An EC2 instance's operating system, application files, and data typically live on EBS volumes.

**EBS volume types:**

| Type | Use Case | Performance |
|---|---|---|
| **gp3** (General Purpose SSD) | Root volumes, most workloads | 3,000–16,000 IOPS, configurable throughput |
| **gp2** (legacy gp3 predecessor) | Legacy workloads | IOPS scales with size |
| **io2 Block Express** | Databases, high-perf workloads | Up to 256,000 IOPS — most expensive |
| **st1** (Throughput HDD) | Big data, log processing | High throughput, low IOPS |
| **sc1** (Cold HDD) | Infrequent access archival | Lowest cost |

**Key EBS properties:**
```
Availability Zone bound: EBS volumes live in one AZ.
  → A volume in us-east-1a CANNOT attach to an EC2 in us-east-1b.
  → Solution: create a snapshot, restore in the new AZ.

Snapshots:
  → Point-in-time backup of an EBS volume
  → Stored in S3 (managed by AWS)
  → Used to: backup volumes, move data between AZs/regions, create AMIs

Delete on termination:
  → Root volume: deleted when EC2 terminates BY DEFAULT
  → Additional volumes: NOT deleted by default → persist after instance termination
  → Configure based on your data retention needs
```

**EBS vs Instance Store:**
```
EBS:
  → Network-attached, persists after stop/terminate (if configured)
  → Can be detached and reattached to another instance
  → Slower than instance store (network overhead)

Instance Store (ephemeral):
  → Physically on the host server
  → Fastest possible storage (no network hop)
  → LOST when instance stops or terminates — never use for critical data
  → Good for: temporary data, buffers, caches
```

---

### Slide 11 — VPC and Networking Fundamentals

**Title:** VPC — Your Private Network in AWS

**What a VPC is:** A Virtual Private Cloud is your own isolated, private network within AWS. Every resource — EC2 instances, RDS databases, ECS containers — lives inside a VPC. You define the IP address range, subnets, routing, and internet access rules.

**Default VPC vs custom VPC:**
```
Default VPC (created automatically per region):
  → CIDR: 172.31.0.0/16
  → Public subnets in each AZ
  → Internet Gateway attached — all resources can reach the internet
  → Fine for learning and development

Custom VPC (production standard):
  → You define the CIDR block (e.g., 10.0.0.0/16)
  → Public subnets: resources with public IPs (load balancers, NAT gateways)
  → Private subnets: resources without public IPs (app servers, databases)
  → Explicit routing and access control
```

**Key VPC components:**
```
VPC: 10.0.0.0/16  (65,536 possible IPs)
  │
  ├── Public Subnet: 10.0.1.0/24 (us-east-1a)
  │     EC2 App Server (public IP: 52.x.x.x)
  │     Load Balancer
  │     ↑ Internet Gateway → routes traffic in/out from internet
  │
  ├── Private Subnet: 10.0.2.0/24 (us-east-1a)
  │     RDS Database (no public IP — not reachable from internet)
  │     ↑ NAT Gateway (in public subnet) → allows outbound internet from private subnet
  │
  └── Private Subnet: 10.0.3.0/24 (us-east-1b)
        RDS Standby (Multi-AZ — different AZ for failover)
```

**Internet Gateway:** Allows resources in public subnets to receive inbound traffic from the internet and make outbound connections.

**NAT Gateway:** Allows resources in private subnets to make outbound internet connections (e.g., download packages, call external APIs) without being reachable from the internet.

---

### Slide 12 — Auto Scaling Groups

**Title:** Auto Scaling Groups — Elastic Capacity Management

**The problem auto scaling solves:**
```
Fixed capacity: you provision for peak load
  → Most of the time: paying for idle servers
  → At peak: maybe still not enough

Auto Scaling: capacity adjusts automatically with demand
  → Normal traffic: 2 instances
  → Evening peak: scales to 8 instances automatically
  → 3 AM: scales back to 2 instances
  → Only pay for what you use
```

**Auto Scaling Group components:**

**Launch Template:** The blueprint for new instances the ASG creates.
```
Launch Template specifies:
  → AMI ID (your custom AMI with app pre-installed)
  → Instance type (t3.medium)
  → Key pair
  → Security groups
  → User data script (if not using custom AMI)
  → IAM instance profile (permissions for the EC2 instance)
```

**ASG Configuration:**
```
Min capacity:     2   (never fewer than 2 instances — HA guarantee)
Desired capacity: 2   (start with 2)
Max capacity:    10   (never more than 10 — cost cap)

AZs: [us-east-1a, us-east-1b, us-east-1c]
     → ASG spreads instances evenly across AZs
     → If one AZ goes down, ASG replaces the instance in a healthy AZ
```

**Scaling policies:**

| Policy Type | How It Works | Example |
|---|---|---|
| **Target Tracking** | Maintain a target metric | Keep average CPU at 60% |
| **Step Scaling** | Scale by N when metric crosses threshold | Add 2 instances when CPU > 80% |
| **Scheduled** | Scale at specific times | Scale up Fridays at 5PM; down at midnight |
| **Predictive** | ML-based future demand forecast | AWS predicts and scales proactively |

**Health checks:**
```
ASG monitors instance health.
If an instance fails a health check (HTTP 200 from /actuator/health):
  → ASG terminates the unhealthy instance
  → Launches a replacement automatically
  → Self-healing infrastructure
```

---

### Slide 13 — Part 1 Summary

**Title:** Part 1 Summary — AWS Infrastructure Foundations

**The EC2 + Networking mental model:**
```
Internet
   │
Internet Gateway (attached to VPC)
   │
Load Balancer (public subnet, us-east-1a + us-east-1b)
   │
Security Group: allow 80/443 from 0.0.0.0/0
   │
Auto Scaling Group:
   ├── EC2 Instance (private subnet, us-east-1a)  AMI: custom app AMI
   └── EC2 Instance (private subnet, us-east-1b)  EBS: gp3 root volume
          │
      Security Group: allow 8080 from load balancer SG only
          │
      RDS Database (private subnet)
      Security Group: allow 5432 from app server SG only
```

**Key decisions for every EC2 deployment:**

| Decision | Options | Recommendation |
|---|---|---|
| AMI | AWS quick start vs custom | Custom AMI for ASG; quick start for dev |
| Instance type | Family + size | Start small (t3.medium); profile before upsizing |
| Pricing | On-Demand vs Reserved vs Spot | On-Demand for dev; Reserved for steady prod |
| Storage | EBS gp3 vs instance store | EBS gp3 for anything that must persist |
| Placement | Public vs private subnet | App servers in private; LB in public |
| Access | Security Groups | Principle of least privilege; chain SGs |

**Coming in Part 2:** S3, Elastic Beanstalk, RDS, containers on AWS (ECR/ECS/EKS), Lambda, SNS/SQS, IAM, CloudWatch, DynamoDB, and deploying your full-stack application.

---

*End of Part 1 Slide Descriptions*
