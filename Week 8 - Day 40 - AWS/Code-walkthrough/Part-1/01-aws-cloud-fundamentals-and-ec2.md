# AWS — Part 1: Cloud Computing Fundamentals & EC2
# Bookstore Platform on AWS
# Day 40 — Week 8
# ============================================================
# This reference document covers every Part 1 topic:
#   1. Cloud Computing Fundamentals (IaaS, PaaS, SaaS)
#   2. AWS Overview and Global Infrastructure
#   3. AWS Regions and Availability Zones
#   4. AWS EC2 (Elastic Compute Cloud)
#   5. Launching and Configuring EC2 Instances
#   6. Security Groups and Network ACLs
#   7. SSH into EC2 Instances
#   8. Amazon Machine Images (AMI)
#   9. Elastic Block Store (EBS)
#  10. Auto Scaling Groups
# ============================================================

---

## SECTION 1 — Cloud Computing Fundamentals: IaaS, PaaS, SaaS

### What is Cloud Computing?

Cloud computing is the on-demand delivery of IT resources — servers, storage,
databases, networking, software — over the internet, with pay-as-you-go pricing.
You rent computing power instead of owning physical hardware.

### The Three Service Models

```
┌─────────────────────────────────────────────────────────────────┐
│              Cloud Service Model Comparison                     │
├──────────────┬──────────────────────────────────────────────────┤
│  YOU MANAGE  │              SERVICE MODEL                       │
├──────────────┼──────────────────────────────────────────────────┤
│ Applications │ SaaS — provider manages EVERYTHING               │
│ Data         │ (Gmail, Salesforce, GitHub, Slack)               │
├──────────────┼──────────────────────────────────────────────────┤
│ Runtime      │ PaaS — provider manages OS + runtime             │
│ Middleware   │ (Elastic Beanstalk, Heroku, Google App Engine)   │
│ OS           │ YOU just deploy your app code                    │
├──────────────┼──────────────────────────────────────────────────┤
│ OS           │ IaaS — provider manages only hardware            │
│ Middleware   │ (EC2, Azure VMs, Google Compute Engine)          │
│ Runtime      │ YOU manage everything above bare metal           │
│ Application  │                                                  │
└──────────────┴──────────────────────────────────────────────────┘
```

### Bookstore Context

| Layer | On-Premises | IaaS (EC2) | PaaS (Beanstalk) | SaaS |
|-------|-------------|------------|------------------|------|
| Servers | Buy physical servers | EC2 instances | Managed runtime | N/A |
| OS | Install & patch manually | You manage OS | AWS manages OS | N/A |
| Java Runtime | Install JDK manually | You install JDK | AWS installs JDK | N/A |
| Spring Boot App | Deploy manually | Deploy manually | Upload JAR — done | N/A |
| Database | Install MySQL manually | RDS (managed DB) | RDS | N/A |
| Email | Set up mail server | SES | SES | SendGrid (SaaS) |

### Key Cloud Benefits

1. **Trade capital expense for operating expense** — no upfront hardware investment
2. **Scale in minutes, not months** — spin up 100 servers in the time it takes to make coffee
3. **Stop guessing capacity** — scale up when traffic spikes, scale down when it drops
4. **Increase speed and agility** — developers can provision resources in minutes
5. **Go global in minutes** — deploy your bookstore to Tokyo with a single click
6. **Stop spending money on data center operations** — focus on your business, not racks

### Deployment Models

- **Public Cloud** — AWS, Azure, GCP — resources shared across customers but isolated
- **Private Cloud** — your own data center with cloud-like tooling (on-premises)
- **Hybrid Cloud** — mix of on-premises + public cloud (common in enterprise)
- **Multi-Cloud** — use multiple providers (e.g., AWS for compute + GCP for ML)

---

## SECTION 2 — AWS Overview and Global Infrastructure

### What is AWS?

Amazon Web Services (AWS) is the world's most comprehensive and broadly adopted
cloud platform, offering 200+ fully featured services from data centers globally.

**Market Position (2024):**
- AWS: ~31% of cloud market share
- Azure: ~25%
- Google Cloud: ~11%
- AWS has been the leader since 2006

### Core AWS Service Categories

```
┌─────────────────────────────────────────────────────────┐
│                 AWS Service Categories                  │
├─────────────────────┬───────────────────────────────────┤
│ Compute             │ EC2, Lambda, ECS, EKS, Fargate    │
│ Storage             │ S3, EBS, EFS, Glacier             │
│ Database            │ RDS, DynamoDB, ElastiCache        │
│ Networking          │ VPC, Route 53, CloudFront, ELB    │
│ Messaging           │ SNS, SQS, EventBridge             │
│ Security            │ IAM, Cognito, KMS, Shield         │
│ Monitoring          │ CloudWatch, CloudTrail, X-Ray     │
│ Developer Tools     │ CodePipeline, CodeBuild, CodeDeploy│
│ ML/AI               │ SageMaker, Rekognition, Bedrock   │
│ Containers          │ ECR, ECS, EKS, App Runner         │
└─────────────────────┴───────────────────────────────────┘
```

### AWS Global Infrastructure

AWS infrastructure is organized into three levels:

```
┌─────────────────────────────────────────────────────────────────┐
│                   AWS Global Infrastructure                     │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                  REGION (e.g. us-east-1)                 │   │
│  │  Physical geographic area with 2+ AZs                    │   │
│  │                                                          │   │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐         │   │
│  │  │ AZ: us-east│  │ AZ: us-east│  │ AZ: us-east│         │   │
│  │  │    1a      │  │    1b      │  │    1c      │         │   │
│  │  │            │  │            │  │            │         │   │
│  │  │ Data Center│  │ Data Center│  │ Data Center│         │   │
│  │  │ #1 + #2    │  │ #3 + #4    │  │ #5 + #6    │         │   │
│  │  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘         │   │
│  │        └───────────────┴───────────────┘                 │   │
│  │              Low-latency private links                   │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                 EDGE LOCATION                            │   │
│  │   CloudFront CDN nodes (300+ globally)                   │   │
│  │   Cache content close to end users                       │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

**As of 2024:**
- 33 launched Regions
- 105 Availability Zones
- 300+ Edge Locations (CloudFront)
- Data centers on every continent except Antarctica

---

## SECTION 3 — AWS Regions and Availability Zones

### Regions

A **Region** is a separate geographic area. Each region is completely independent —
data does not replicate across regions automatically (data sovereignty).

**Common Regions:**
```
Region Name             │ Region Code  │ Location
──────────────────────────────────────────────────────
US East (N. Virginia)   │ us-east-1    │ Oldest, most services
US East (Ohio)          │ us-east-2    │
US West (N. California) │ us-west-1    │
US West (Oregon)        │ us-west-2    │
EU (Ireland)            │ eu-west-1    │
EU (Frankfurt)          │ eu-central-1 │ Data sovereignty for EU
Asia Pacific (Tokyo)    │ ap-northeast-1│
Asia Pacific (Sydney)   │ ap-southeast-2│
South America (São Paulo)│ sa-east-1   │
```

**Choosing a Region — factors:**
1. **Latency** — closest to your users
2. **Compliance** — data residency laws (GDPR requires EU data stay in EU)
3. **Service availability** — not all services available in all regions
4. **Pricing** — varies by region (us-east-1 is often cheapest)

### Availability Zones (AZs)

An **Availability Zone** is one or more discrete data centers with redundant power,
networking, and connectivity in a Region.

**Key facts:**
- Each AZ is physically separated (miles apart) from other AZs
- Connected with high-bandwidth, low-latency, private fiber networking
- AZs are isolated from each other — a tornado in AZ-1a won't affect AZ-1b
- **Best practice:** deploy your bookstore app across 2+ AZs for high availability

```
Example — Bookstore High Availability:

  us-east-1a                  us-east-1b
  ┌────────────────────┐       ┌────────────────────┐
  │  EC2: bookstore-1  │       │  EC2: bookstore-2  │
  │  RDS: Primary DB   │ ───── │  RDS: Standby DB   │
  │  EBS: /data vol    │       │  EBS: /data vol    │
  └────────────────────┘       └────────────────────┘
           │                             │
           └──────────┬──────────────────┘
                  Load Balancer
                  (Elastic LB)
```

**Shared Responsibility Model:**
```
AWS Responsibility                │ Customer Responsibility
──────────────────────────────────┼──────────────────────────────────
Physical hardware security        │ OS patching and updates
Network infrastructure            │ Application security
Hypervisor                        │ Data encryption
Building/facility security        │ IAM users and permissions
                                  │ Firewall (Security Groups)
                                  │ Customer data
```

---

## SECTION 4 — AWS EC2 (Elastic Compute Cloud)

### What is EC2?

EC2 provides resizable compute capacity in the cloud. Think of it as a virtual
server (Virtual Machine) running in AWS. For our bookstore, EC2 is where we
run the Spring Boot application.

### EC2 Instance Types

AWS organizes instances into families based on use case:

```
Family   │ Use Case                  │ Example Types
─────────┼───────────────────────────┼──────────────────────────────
t3/t4g   │ General purpose burstable │ t3.micro (free tier), t3.small
m6i/m7i  │ General purpose balanced  │ m6i.large, m6i.xlarge
c6i/c7i  │ Compute optimized         │ c6i.large (CPU-heavy workloads)
r6i/r7i  │ Memory optimized          │ r6i.large (in-memory databases)
p3/p4    │ GPU instances             │ p3.2xlarge (ML training)
i3/i4    │ Storage optimized         │ i3.large (high IOPS)
```

**Instance Size Naming Convention:**
```
  t3 . micro
  ──   ─────
  │     └── Size: nano, micro, small, medium, large, xlarge, 2xlarge...
  └──── Family: t (burstable), m (general), c (compute), r (memory)

vCPU and Memory:
  t3.micro  → 2 vCPU,   1 GB RAM   → FREE TIER eligible
  t3.small  → 2 vCPU,   2 GB RAM
  t3.medium → 2 vCPU,   4 GB RAM
  m6i.large → 2 vCPU,   8 GB RAM
  m6i.xlarge→ 4 vCPU,  16 GB RAM
```

### EC2 Pricing Models

```
Model          │ Discount vs On-Demand │ Best For
───────────────┼───────────────────────┼────────────────────────────
On-Demand      │ 0% (baseline)         │ Dev/test, unpredictable load
Reserved (1yr) │ Up to 40% off         │ Steady production workloads
Reserved (3yr) │ Up to 60% off         │ Long-term steady workloads
Savings Plans  │ Up to 66% off         │ Flexible commitment model
Spot Instances │ Up to 90% off         │ Fault-tolerant batch jobs
Dedicated Host │ Most expensive        │ Compliance/license needs
```

**Free Tier:** t3.micro — 750 hours/month FREE for 12 months
**Our bookstore:** t3.micro for dev, m6i.large for production

---

## SECTION 5 — Launching and Configuring EC2 Instances

### EC2 Launch Configuration Checklist

When launching an EC2 instance, you configure these 7 components:

```
1. AMI              → What OS/software do we start with?
                      (Amazon Linux 2023, Ubuntu 22.04, custom bookstore AMI)

2. Instance Type    → How much CPU and RAM?
                      (t3.micro for dev, m6i.large for prod)

3. Key Pair         → How do we SSH in?
                      (bookstore-keypair.pem — download ONCE, save securely)

4. Network/VPC      → Which virtual network?
                      (Default VPC for learning, custom VPC for production)

5. Security Group   → Which ports are open?
                      (22 for SSH, 80 for HTTP, 443 for HTTPS, 8080 for Spring Boot)

6. Storage (EBS)    → How much disk space?
                      (8 GB root volume minimum, add data volume if needed)

7. User Data        → Bootstrap script to run on first boot
                      (install Java, pull from S3, start the app)
```

### User Data Bootstrap Script

When an EC2 instance starts for the first time, it runs the "User Data" script.
This is how we automate the setup of our bookstore server:

```bash
#!/bin/bash
# =============================================================
# EC2 User Data Script — Bookstore Application Bootstrap
# Runs automatically on FIRST BOOT of the instance
# =============================================================

# Update all OS packages
yum update -y

# Install Java 21 (Amazon Corretto)
yum install -y java-21-amazon-corretto

# Install useful tools
yum install -y git htop wget curl

# Create application user (don't run as root)
useradd -m -s /bin/bash bookstore

# Create app directory
mkdir -p /opt/bookstore
chown bookstore:bookstore /opt/bookstore

# Download bookstore JAR from S3 (we'll deploy it here)
# aws s3 cp s3://bookstore-artifacts/bookstore-api-1.0.0.jar /opt/bookstore/
# Note: In production, use CodeDeploy or Beanstalk instead

# Create systemd service so app starts on reboot
cat > /etc/systemd/system/bookstore.service << 'EOF'
[Unit]
Description=Bookstore Spring Boot Application
After=network.target

[Service]
User=bookstore
WorkingDirectory=/opt/bookstore
ExecStart=/usr/bin/java -jar /opt/bookstore/bookstore-api.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# Enable the service
systemctl daemon-reload
systemctl enable bookstore

# Install CloudWatch agent for monitoring (covered in Part 2)
yum install -y amazon-cloudwatch-agent

# Signal success to CloudFormation/Elastic Beanstalk
echo "Bookstore bootstrap complete!" >> /var/log/bookstore-setup.log
```

---

## SECTION 6 — Security Groups and Network ACLs

### The Two Layers of EC2 Network Security

AWS provides two complementary network security tools:

```
Internet
    │
    ▼
┌─────────────────────────────────────────────────────┐
│                    VPC                              │
│                                                     │
│  ┌───────────────────────────────────────────────┐  │
│  │              Network ACL (Subnet level)       │  │
│  │   Stateless — rules checked on BOTH directions│  │
│  │                                               │  │
│  │  ┌──────────────────────────────────────────┐ │  │
│  │  │         Security Group (Instance level)  │ │  │
│  │  │   Stateful — tracks connection state     │ │  │
│  │  │                                          │ │  │
│  │  │         EC2 Instance (bookstore)         │ │  │
│  │  └──────────────────────────────────────────┘ │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

### Security Groups

Security Groups act as a **virtual firewall** at the **instance level**.
They are **stateful** — if you allow inbound traffic, the response is
automatically allowed outbound.

**Security Group Rules for our Bookstore:**

```
Inbound Rules:
┌──────┬──────────┬───────────────┬──────────────────────────────────┐
│ Type │ Protocol │ Port Range    │ Source           │ Description   │
├──────┼──────────┼───────────────┼──────────────────┼───────────────┤
│ SSH  │ TCP      │ 22            │ MY IP (your IP)  │ Admin SSH     │
│ HTTP │ TCP      │ 80            │ 0.0.0.0/0 (all) │ Web traffic   │
│ HTTPS│ TCP      │ 443           │ 0.0.0.0/0 (all) │ Secure web    │
│Custom│ TCP      │ 8080          │ 0.0.0.0/0 (all) │ Spring Boot   │
│Custom│ TCP      │ 5432          │ sg-app-servers  │ PostgreSQL DB  │
└──────┴──────────┴───────────────┴──────────────────┴───────────────┘

Outbound Rules (default — allow all):
┌──────┬──────────┬───────────────┬──────────────────────────────────┐
│ All  │ All      │ All           │ 0.0.0.0/0       │ Allow all out  │
└──────┴──────────┴───────────────┴──────────────────┴───────────────┘
```

**⚠️ Security Best Practices:**
- **NEVER** set SSH (port 22) source to 0.0.0.0/0 — restrict to YOUR IP only
- Use separate security groups for app servers, database servers, load balancers
- Reference security group IDs as sources — not CIDR ranges — for internal traffic
- Principle of least privilege — only open ports you actually need

### Network ACLs (NACLs)

Network ACLs operate at the **subnet level** and are **stateless**.

```
Network ACL Comparison with Security Groups:

Feature            │ Security Group      │ Network ACL
───────────────────┼─────────────────────┼─────────────────────────
Level              │ Instance            │ Subnet
State              │ Stateful            │ Stateless
Rules              │ Allow only          │ Allow AND Deny
Rule evaluation    │ All rules evaluated │ Rules evaluated in order
Default behavior   │ Deny all inbound    │ Allow all (default NACL)
Use case           │ Primary defense     │ Additional subnet defense
```

**Default NACL:** Allows all inbound and outbound traffic (open by default).
**Custom NACL:** Denies all traffic by default — must add allow rules.

**NACL Rules example (numbered, evaluated in order, first match wins):**
```
Rule # │ Type  │ Protocol │ Port  │ Source        │ Allow/Deny
───────┼───────┼──────────┼───────┼───────────────┼────────────
100    │ HTTP  │ TCP      │ 80    │ 0.0.0.0/0    │ ALLOW
200    │ HTTPS │ TCP      │ 443   │ 0.0.0.0/0    │ ALLOW
300    │ SSH   │ TCP      │ 22    │ 10.0.0.0/8   │ ALLOW
*      │ ALL   │ ALL      │ ALL   │ 0.0.0.0/0    │ DENY  (catch-all)
```

---

## SECTION 7 — SSH into EC2 Instances

### SSH Connection Flow

```
Your Laptop                          EC2 Instance (bookstore)
    │                                       │
    │  1. You have: bookstore-keypair.pem   │
    │  2. EC2 has: matching public key      │
    │                                       │
    │ ──── SSH on port 22 (TCP) ─────────►  │
    │ ◄─── Authenticates with key pair ───  │
    │ ◄─── Bash shell ────────────────────  │
```

### Key Pair Types

| Type | Format | OS | Notes |
|------|--------|----|-------|
| RSA | .pem | Linux/Mac (native), Windows (WSL) | Classic, widely supported |
| ED25519 | .pem | Linux/Mac (native) | More secure, faster |
| PPK | .ppk | Windows (PuTTY) | Convert from .pem with PuTTYgen |

### SSH Commands Reference

```bash
# ============================================================
# SSH INTO EC2 — Commands Reference
# Replace 'ec2-XX-XX-XX-XX.compute-1.amazonaws.com' with
# your actual Public DNS from the EC2 console.
# ============================================================

# Fix key file permissions (REQUIRED — SSH refuses world-readable keys)
chmod 400 bookstore-keypair.pem

# SSH into Amazon Linux 2 / Amazon Linux 2023
# Default user for Amazon Linux: ec2-user
ssh -i bookstore-keypair.pem ec2-user@ec2-XX-XX-XX-XX.compute-1.amazonaws.com

# SSH into Ubuntu
# Default user for Ubuntu: ubuntu
ssh -i bookstore-keypair.pem ubuntu@ec2-XX-XX-XX-XX.compute-1.amazonaws.com

# SSH into RHEL / CentOS
# Default user: ec2-user or centos

# SSH with port forwarding (access Spring Boot on local port 8080)
ssh -i bookstore-keypair.pem -L 8080:localhost:8080 \
    ec2-user@ec2-XX-XX-XX-XX.compute-1.amazonaws.com

# Copy files TO instance (scp)
scp -i bookstore-keypair.pem bookstore-api.jar \
    ec2-user@ec2-XX-XX-XX-XX.compute-1.amazonaws.com:/opt/bookstore/

# Copy files FROM instance
scp -i bookstore-keypair.pem \
    ec2-user@ec2-XX-XX-XX-XX.compute-1.amazonaws.com:/var/log/app.log \
    ./app.log

# ⚠️ Common error: "WARNING: UNPROTECTED PRIVATE KEY FILE!"
# Fix: chmod 400 bookstore-keypair.pem

# ⚠️ Common error: "Connection refused"
# Fix: Check Security Group has SSH (port 22) open for your IP

# ⚠️ Common error: "Permission denied (publickey)"
# Fix: Wrong username! Check AMI → Amazon Linux=ec2-user, Ubuntu=ubuntu
```

### Default EC2 Users by AMI

```
AMI                          │ Default Username
─────────────────────────────┼──────────────────
Amazon Linux 2 / 2023        │ ec2-user
Ubuntu                       │ ubuntu
Debian                       │ admin
RHEL                         │ ec2-user
CentOS                       │ centos
SUSE                         │ ec2-user
```

---

## SECTION 8 — Amazon Machine Images (AMI)

### What is an AMI?

An AMI (Amazon Machine Image) is a **template** for an EC2 instance containing:
- The root volume (OS + software)
- Launch permissions (who can use it)
- Block device mapping (which EBS volumes to attach)

Think of an AMI as a **snapshot of an entire server** — you can use it to
launch 1 instance or 100 identical instances.

```
AMI Contents:
┌──────────────────────────────────────────────────────┐
│                      AMI                            │
│  ┌────────────────────────────────────────────────┐ │
│  │ Root Volume Snapshot                           │ │
│  │   OS:          Amazon Linux 2023               │ │
│  │   Java:        Amazon Corretto 21              │ │
│  │   App:         bookstore-api.jar               │ │
│  │   Config:      application.yml (prod)          │ │
│  │   Scripts:     startup.sh, health-check.sh     │ │
│  └────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────┐ │
│  │ Launch Permissions                             │ │
│  │   Public / Private / Shared with account IDs  │ │
│  └────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────┘
```

### AMI Types

```
Source          │ Description                        │ Example
────────────────┼────────────────────────────────────┼──────────────────────
AWS Managed     │ Official AMIs from AWS             │ Amazon Linux 2023
AWS Marketplace │ Pre-configured commercial AMIs     │ Bitnami WordPress
Community       │ Public AMIs from AWS users         │ Various (verify!)
Your Own        │ Custom AMIs you create             │ bookstore-v1.0-ami
```

### Creating a Custom Bookstore AMI

```bash
# Workflow: Start with base AMI → install software → create custom AMI

# Step 1: Launch base EC2 (Amazon Linux 2023)
# Step 2: SSH in and install everything
sudo yum update -y
sudo yum install -y java-21-amazon-corretto
sudo mkdir -p /opt/bookstore

# Step 3: Deploy and configure the app
sudo aws s3 cp s3://bookstore-builds/bookstore-api.jar /opt/bookstore/
sudo systemctl enable bookstore

# Step 4: Create AMI from this instance (stop instance first for clean snapshot)
# In AWS Console: EC2 → Instance → Actions → Image and templates → Create image
# OR via CLI:
aws ec2 create-image \
    --instance-id i-0abc12345def67890 \
    --name "bookstore-api-v1.0-$(date +%Y%m%d)" \
    --description "Bookstore Spring Boot AMI — Java 21, pre-configured" \
    --no-reboot

# Step 5: Use this AMI in Launch Templates / Auto Scaling Groups
# Now every new instance launches with Java, app, and config already installed!
# Instead of a 5-minute User Data script, instances are ready in ~60 seconds.
```

### Why Custom AMIs Matter

```
Without Custom AMI (User Data approach):
  Launch EC2 → Run User Data → Install Java (2 min) → Install app (1 min)
  → Configure (1 min) → READY (4-5 min total)

With Custom AMI (Golden Image approach):
  Launch EC2 from AMI → READY (< 60 seconds)

→ Critical for Auto Scaling! When traffic spikes, you need new instances
  ready in 60 seconds, not 5 minutes.
```

---

## SECTION 9 — Elastic Block Store (EBS)

### What is EBS?

EBS (Elastic Block Store) provides persistent block storage for EC2 instances.
Think of it as a virtual hard drive that you attach to a virtual server.

**Key characteristic: EBS volumes persist independently of the EC2 instance.**
You can stop an EC2 instance, detach the EBS volume, attach it to a new instance,
and all your data is still there.

### EBS Volume Types

```
Type              │ Use Case               │ IOPS        │ Throughput  │ Cost
──────────────────┼────────────────────────┼─────────────┼─────────────┼────────
gp3               │ General purpose SSD    │ Up to 16,000│ Up to 1,000 │ $0.08/GB
  (recommended)   │ Boot volumes, dev      │             │ MB/s        │ /month
──────────────────┼────────────────────────┼─────────────┼─────────────┼────────
gp2               │ General purpose SSD    │ Up to 16,000│ 250 MB/s    │ $0.10/GB
  (legacy)        │ Older default          │ (burstable) │             │ /month
──────────────────┼────────────────────────┼─────────────┼─────────────┼────────
io2 Block Express │ Provisioned IOPS SSD   │ Up to 256K  │ 4,000 MB/s  │ $0.125/GB
                  │ High-perf databases    │             │             │ /month
──────────────────┼────────────────────────┼─────────────┼─────────────┼────────
st1               │ Throughput-optimized   │ 500 IOPS    │ 500 MB/s    │ $0.045/GB
                  │ HDD - Big Data         │             │             │ /month
──────────────────┼────────────────────────┼─────────────┼─────────────┼────────
sc1               │ Cold HDD               │ 250 IOPS    │ 250 MB/s    │ $0.015/GB
                  │ Infrequent access      │             │             │ /month
```

**Our Bookstore:**
- Root volume: gp3, 20 GB (OS + app)
- Data volume: gp3, 100 GB (book images, uploads)
- RDS database: io2 (provisioned IOPS for consistent DB performance)

### EBS Key Concepts

```
1. AVAILABILITY ZONE BOUND
   EBS volumes live in ONE AZ.
   To move data across AZs → create a Snapshot → restore in new AZ

2. SNAPSHOTS
   Point-in-time backup of an EBS volume.
   Stored in S3 (managed by AWS, not your S3 bucket).
   Incremental — only changed blocks are saved after first snapshot.
   Use for: backups, copying across regions, creating AMIs

3. ENCRYPTION
   Enable at creation time (cannot encrypt an unencrypted volume in-place).
   Uses AWS KMS keys.
   Encrypted in transit between EC2 and EBS.

4. MULTI-ATTACH (io1/io2 only)
   Attach one EBS volume to multiple EC2 instances in same AZ.
   Used for clustered databases (Oracle RAC).

5. ROOT vs DATA VOLUMES
   Root volume: Contains the OS — deleted when instance terminates (by default).
   Data volume: Additional storage — persists when instance terminates.
   → Set "Delete on termination = false" for root if you need data to persist.
```

### EBS vs Instance Store

```
Feature              │ EBS                      │ Instance Store
─────────────────────┼──────────────────────────┼────────────────────────
Persistence          │ Persists beyond instance  │ LOST when instance stops
Durability           │ Replicated within AZ      │ Not replicated
Type                 │ Network-attached          │ Physically on host
Performance          │ High, configurable        │ Highest (local NVMe)
Use case             │ Root vols, databases      │ Temp files, caches
Bookstore use        │ App data, DB files        │ Build cache, temp files
```

---

## SECTION 10 — Auto Scaling Groups

### The Problem Auto Scaling Solves

```
Without Auto Scaling — Bookstore Black Friday Problem:

Monday 9am:     100 users → 1 EC2 instance → CPU 20% → Great!
Black Friday:   50,000 users → 1 EC2 instance → CPU 100% → CRASH!

Option A: Always run 10 instances → $$$$ wasted 364 days/year
Option B: Auto Scaling → run 1 instance normally, scale to 50 when needed
```

### Auto Scaling Group Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                   Auto Scaling Group (ASG)                      │
│                                                                 │
│  Desired: 2    Min: 1    Max: 10                                 │
│                                                                 │
│  us-east-1a              us-east-1b              us-east-1c     │
│  ┌──────────────┐        ┌──────────────┐                       │
│  │ EC2 Instance │        │ EC2 Instance │        ← (healthy)    │
│  │ (bookstore)  │        │ (bookstore)  │                       │
│  └──────────────┘        └──────────────┘                       │
│          │                       │                              │
│          └──────────┬────────────┘                              │
│                     │                                           │
│         ┌───────────▼────────────┐                              │
│         │   Application Load     │                              │
│         │   Balancer (ALB)       │  ← Distributes traffic       │
│         └───────────▲────────────┘                              │
│                     │                                           │
│              SCALE OUT: CPU > 70% for 5 min → add instance      │
│              SCALE IN:  CPU < 30% for 30 min → remove instance  │
└─────────────────────────────────────────────────────────────────┘
```

### ASG Key Components

**1. Launch Template (what to launch)**
```
Launch Template specifies:
  - AMI ID           → bookstore-api-v1.0-ami
  - Instance type    → t3.medium (dev) / m6i.large (prod)
  - Key pair         → bookstore-keypair
  - Security group   → sg-bookstore-app
  - IAM role         → bookstore-ec2-role (for S3/CloudWatch access)
  - User data        → bootstrap script (or empty if using custom AMI)
  - EBS volumes      → 20 GB gp3 root
```

**2. Scaling Policies (when to scale)**
```
Policy Type             │ Description                    │ Bookstore Example
────────────────────────┼────────────────────────────────┼──────────────────────────
Target Tracking         │ Maintain target metric value   │ Keep avg CPU at 60%
Step Scaling            │ Scale by steps based on alarms │ CPU>70% → add 2 / CPU>90% → add 5
Simple Scaling          │ Single step, cooldown period   │ CPU>80% alarm → add 1 instance
Scheduled Scaling       │ Time-based                     │ Scale up at 9am, down at 6pm
Predictive Scaling      │ ML-based forecast              │ Learns weekly traffic patterns
```

**3. Health Checks (when to replace)**
```
EC2 Health Check:  Instance responds to EC2 status checks
                   (hardware failure → replace instance)

ELB Health Check:  Load balancer health check passes
                   (app returns 200 on /actuator/health → healthy)
                   (app returns 500 or timeout → unhealthy → replace)

⚠️ Use ELB health checks in production — more accurate than EC2-level checks
   A running EC2 doesn't mean your Spring Boot app is actually working!
```

### Scaling Policies Example — Bookstore

```
Target Tracking Policy (recommended for most cases):
  Metric:        Average CPU Utilization
  Target value:  60%
  
  Result: ASG automatically adds/removes instances to keep avg CPU near 60%
  
  Monday 9am:    2 instances, CPU 20% → scale in to 1 instance
  Monday 11am:   1 instance, CPU 65% → scale out to 2 instances
  Black Friday:  CPU hits 90% → adds instances → stabilizes at 60%

Scheduled Policy (for predictable patterns):
  Rule 1: Every weekday at 8:45am → set desired to 3 (before business hours)
  Rule 2: Every weekday at 7pm → set desired to 1 (after hours)
  Rule 3: Black Friday → set desired to 8 (planned event)
```

### ASG Lifecycle Hooks

```
Normal Launch flow:
  Pending → InService → (running normally) → Terminating → Terminated

With Lifecycle Hook (bookstore example):
  Pending → Pending:Wait ← [your script runs: warm up caches, register with Consul]
         → Pending:Proceed → InService

  Terminating → Terminating:Wait ← [your script: drain connections, de-register]
             → Terminating:Proceed → Terminated

⚠️ Without lifecycle hooks, the load balancer may send traffic to an instance
   before your Spring Boot app has fully started. Use /actuator/health to
   ensure the ELB only marks instances healthy after app is ready.
```

---

## QUICK REFERENCE — Part 1

```
┌─────────────────────────────────────────────────────────────────┐
│             AWS Part 1 — Key Concepts Cheat Sheet               │
├────────────────────────┬────────────────────────────────────────┤
│ IaaS                   │ You manage OS+runtime+app (EC2)        │
│ PaaS                   │ You manage only app (Beanstalk)        │
│ SaaS                   │ You just use the software (Gmail)      │
├────────────────────────┼────────────────────────────────────────┤
│ Region                 │ Geographic area (us-east-1)            │
│ Availability Zone      │ Data center cluster within region      │
│ Edge Location          │ CloudFront CDN node                    │
├────────────────────────┼────────────────────────────────────────┤
│ EC2                    │ Virtual server (rent compute)          │
│ t3.micro               │ Free tier — 2 vCPU, 1 GB RAM           │
│ AMI                    │ Server template (OS + software)        │
│ Key Pair               │ SSH authentication (.pem file)         │
├────────────────────────┼────────────────────────────────────────┤
│ Security Group         │ Virtual firewall (instance level)      │
│ Network ACL            │ Subnet-level firewall (stateless)      │
│ Default SSH user       │ Amazon Linux = ec2-user, Ubuntu = ubuntu│
├────────────────────────┼────────────────────────────────────────┤
│ EBS gp3                │ General purpose SSD (recommended)      │
│ EBS Snapshot           │ Point-in-time backup → cross-AZ/region │
│ Instance Store         │ Fast, local, NON-PERSISTENT storage    │
├────────────────────────┼────────────────────────────────────────┤
│ Auto Scaling Group     │ Fleet management (min/max/desired)     │
│ Launch Template        │ ASG's blueprint for new instances      │
│ Target Tracking Policy │ Keep metric at target value            │
│ ELB Health Check       │ Replace unhealthy app instances        │
└────────────────────────┴────────────────────────────────────────┘
```
