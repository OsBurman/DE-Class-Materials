# Day 40 Part 1 — AWS: Cloud Fundamentals, EC2, Networking & Scaling
## Lecture Script

**Total Time:** 60 minutes
**Pacing:** ~165 words/minute
**Part 1 Topics:** IaaS/PaaS/SaaS, AWS Overview, Regions/AZs, EC2, Security Groups, SSH, AMI, EBS, VPC, Auto Scaling Groups

---

## [00:00–01:30] Welcome — Week 8 Final Day

Good morning. Today is Day 40, the last day of Week 8, and we're ending the week with one of the most important topics in modern software engineering: Amazon Web Services. At this point you've built a full microservices application in Java and Spring Boot, containerized it with Docker, orchestrated it with Kubernetes, set up CI/CD pipelines, added event streaming with Kafka, and wired in distributed tracing. Today we put all of that somewhere real. We're moving from your laptop to the cloud.

We'll cover a lot of AWS services today because AWS is broad. Part 1 is the infrastructure foundation: the cloud computing models that frame everything, the global architecture of AWS itself, and then a focused deep dive on EC2 — virtual machines in the cloud. By the end of Part 1 you'll understand how to launch, connect to, and configure a server on AWS, and how to make that server automatically scale with demand.

Part 2 is the service catalog — S3, RDS, ECS, EKS, Lambda, SNS/SQS, IAM, CloudWatch, DynamoDB — and we'll end by connecting everything into a full deployment architecture. Let's start at the very beginning.

---

## [01:30–10:00] Cloud Computing Models and AWS Overview

Before AWS existed, running a web application meant buying servers. Physical machines. You'd procure them, rack them in a data center, cable the networking, install the operating system, patch it, handle hardware failures, manage power and cooling, and do all of that months before you knew if anyone would actually use your application. Capital expense up front, regardless of whether the business succeeded.

The cloud changed this. Instead of buying servers, you rent them. You pay for what you use, when you use it. You scale up in minutes, scale back down just as fast. You never touch physical hardware.

But "the cloud" isn't one thing. There's a spectrum of how much the cloud provider manages and how much you manage yourself.

IaaS — Infrastructure as a Service — is closest to traditional servers. AWS provides the physical hardware, the networking, the storage, and the virtualization layer. You get a virtual machine and you manage everything above that: the operating system, the runtime, the application, the configuration. EC2 is IaaS. You're renting a virtual machine. You still patch the OS, install Java, configure your app server. Full control, full responsibility above the hardware.

PaaS — Platform as a Service — goes further. AWS manages the OS and runtime too. You just bring your application code. Elastic Beanstalk is PaaS — you upload your Spring Boot JAR and Beanstalk handles deploying it, running it, monitoring it, even scaling it. RDS is PaaS for databases — you don't manage the database server OS or the database engine installation; you just connect to a hostname and run queries. ECS and EKS sit between IaaS and PaaS depending on how you configure them.

SaaS — Software as a Service — means AWS or another vendor runs the entire thing. You're just using it via API or UI. SNS, SQS, Cognito, S3 — these are all SaaS in the sense that AWS handles everything, and you interact via API. Gmail and Salesforce and GitHub are SaaS you use as a developer.

Why does this taxonomy matter? Because it tells you what you're responsible for. With EC2, you're responsible for OS patches, security hardening, runtime management. With Beanstalk or RDS, AWS handles those layers. As you design systems, you make conscious trade-offs: more control means more operational burden. Managed services reduce that burden but may constrain your choices.

AWS itself is the largest cloud provider in the world — about 32 percent market share, more than Azure and Google Cloud combined. Over 200 services. The main ways you interact with it are the console — the web UI at console.aws.amazon.com — the CLI, which you install locally and run commands like `aws ec2 describe-instances`, and the SDK, which lets your Java applications talk to AWS services programmatically.

For learning and exploration, the console is great. For production and automation, you should be using the CLI or better yet infrastructure as code — CloudFormation or Terraform — so that your infrastructure is version-controlled and reproducible, not a series of manual console clicks. Week 9 covers infrastructure as code concepts; today we'll use the console to build intuition.

And AWS has a free tier — 750 hours per month of t2.micro EC2, 5 GB of S3, 750 hours of RDS — for 12 months after account creation. For all the exercises today and for your capstone work, this free tier covers what you need.

---

## [10:00–18:00] AWS Global Infrastructure and Regions

One of AWS's most important properties is its global footprint. Let me explain how it's structured.

AWS organizes its infrastructure into Regions. A Region is a geographic area — North Virginia, Ireland, Singapore, São Paulo — where AWS has built a cluster of data centers. Each Region is completely independent. Data in us-east-1 stays in us-east-1 unless you explicitly copy it somewhere else. This is foundational for compliance — GDPR requires that European user data stays in Europe, so you deploy to eu-west-1.

Choosing a region involves four considerations. Proximity to your users — if your users are primarily in Southeast Asia, ap-southeast-1 in Singapore will give them lower latency than a server in North Virginia. Compliance — regulated industries often have data residency requirements. Service availability — new AWS services almost always launch first in us-east-1 and roll out to other regions over months. Cost — prices vary by region, and us-east-1 is generally the cheapest.

Within each region, AWS has multiple Availability Zones. An AZ is one or more physical data centers. What makes an AZ powerful is that it has completely independent infrastructure — its own power grid, its own cooling, its own network connectivity to other AZs. If the power goes out in one AZ, the other AZs in the region keep running.

AZs in a region are connected with extremely low latency — typically under a millisecond — so your application doesn't notice the cross-AZ hops. This is why you always deploy across multiple AZs in production. Put your web servers in two AZs. Put your database primary in one AZ and the standby in another. If one AZ has a problem, your application stays up.

Beyond regions and AZs, AWS has over 200 edge locations for CloudFront, their content delivery network. Edge locations are smaller AWS points of presence located closer to population centers worldwide. When you put your static assets — JavaScript, CSS, images — on CloudFront, users download them from the nearest edge location rather than your origin server in us-east-1. The latency difference between an edge location 20 miles away versus a server across the country is significant for user experience.

---

## [18:00–32:00] EC2 — Elastic Compute Cloud

Let's talk about the workhorse of AWS: EC2. EC2 stands for Elastic Compute Cloud, but the name matters less than what it is — virtual machines. You pick an operating system image, a hardware configuration, and launch. Within a minute you have a running Linux server on AWS hardware.

EC2 instance types organize the options. The type name tells you the family, the generation, and the size. t3.medium means family "t" — burstable general purpose — third generation, medium size. The "t" family has a CPU credit system: when idle, you accumulate credits; under load, you spend them. Great for workloads with bursty traffic patterns — most microservices, development environments.

The "m" family is balanced compute and memory — general-purpose servers that don't need special optimization. The "c" family is compute-optimized — when your workload is CPU-heavy. The "r" family is memory-optimized — for workloads that need large amounts of RAM, like large Redis caches or in-memory databases. And there are GPU families like "p" for machine learning.

Sizes within a family scale linearly — each size roughly doubles the vCPU and memory. t3.micro has 2 vCPU and 1GB RAM. t3.small is 2 vCPU and 2GB. t3.medium is 2 vCPU and 4GB. t3.large is 2 vCPU and 8GB. For any production microservice, t3.medium is a reasonable starting point — small enough to be economical, large enough to run a Spring Boot application without tuning.

Pricing models: On-Demand is pay per hour or per second with no commitment. It's the most expensive per unit but the most flexible — great for development and testing where you don't know how long you'll need the instance. Reserved Instances require a 1-year or 3-year commitment and can be up to 72% cheaper than On-Demand. For any workload that runs 24/7 in production, Reserved Instances pay for themselves quickly. Spot Instances bid on unused EC2 capacity and can be 90% cheaper, but AWS can reclaim them with 2 minutes' notice — only suitable for fault-tolerant batch workloads.

Launching an instance: you start with selecting an AMI — the Amazon Machine Image, which we'll cover shortly. Then choose your instance type. Then configure networking: which VPC, which subnet, whether to assign a public IP. Then add storage. Then add to a security group. Then review and launch.

The key pair is how you'll SSH in. AWS generates a public/private key pair. The public key gets installed on the instance. You download the private key as a .pem file and keep it safe — AWS doesn't store it. If you lose the .pem file, you lose access to that instance.

---

## [32:00–43:00] Security Groups, SSH, AMIs, and EBS

Let me go deeper on a few of the most important EC2 concepts.

Security Groups are virtual firewalls. Every EC2 instance has at least one security group, and security groups control what traffic can reach it and what it can send out. They operate at the instance level — not the subnet level.

The most important pattern is Security Group chaining. Instead of allowing your database to be reached from any IP address, you allow it to be reached only from your application servers' security group. This means even if an attacker gets into your network, they can't reach your database unless they're on an actual application server instance. The application server's security group allows inbound HTTP on port 80 from the internet. The database's security group allows inbound port 5432 only from the application server security group. Two rules. Clean, precise, and much safer than IP-based rules that go stale as instances come and go.

Security groups are stateful — you allow an inbound connection, the response traffic is automatically allowed out. You don't need separate outbound rules for response traffic.

SSH. To connect to your Linux EC2 instance, you use the SSH command with your .pem key file. First: `chmod 400 ~/Downloads/my-key.pem`. This changes the file permissions so only you can read it. SSH will refuse to use a key file that's too permissive — it's a security check. Then: `ssh -i ~/Downloads/my-key.pem ec2-user@{public-ip-or-dns}`. For Amazon Linux, the default username is `ec2-user`. For Ubuntu, it's `ubuntu`. For RHEL, it's `ec2-user` or `root` depending on the version.

Common failures: "Connection timed out" usually means the security group doesn't allow SSH from your IP. Check that port 22 is open and that the source is either your specific IP or 0.0.0.0/0 for testing. "Permission denied" means wrong username or wrong key file. "Unprotected private key file" means you forgot the chmod 400.

Amazon Machine Images. When you launch an EC2 instance, you're creating a running copy of an AMI. The AMI contains the OS and any pre-installed software and configuration. AWS provides quick-start AMIs — Amazon Linux 2023, Ubuntu, Windows. But the powerful use of AMIs is creating your own.

Imagine you're configuring Auto Scaling Groups — when traffic spikes, AWS automatically launches new EC2 instances to handle the load. Those new instances need to have your application installed and running. If you rely on a User Data script to install Java, download your application, and start it, that process takes several minutes per instance. During a traffic spike, new capacity comes online slowly. But if you bake all of that into a custom AMI — install Java, place your JAR, configure systemd to start the app — then every new instance launches with everything ready. New instances become healthy in under a minute. This is called a "golden AMI" pattern and it's a production best practice.

EBS — Elastic Block Store — is the persistent storage that backs your EC2 instances. The root volume of your instance is an EBS volume. When you write files to the instance's file system, you're writing to EBS. EBS volumes persist even if you stop the instance — they're just not attached to anything when the instance is off. You can configure the root volume to delete on termination (the default) or to persist. For additional data volumes, they persist by default.

The important limitation: EBS volumes are locked to a single Availability Zone. A volume in us-east-1a cannot be mounted by an instance in us-east-1b. To move data between AZs, you take a snapshot — a point-in-time copy stored in S3 — and restore from it in the target AZ.

For most workloads, gp3 is the right volume type — solid-state, configurable IOPS and throughput, reasonable cost. For high-performance databases with strict IOPS requirements, io2 provides higher performance at higher cost. Instance store — the physical disk on the host — is faster than any EBS volume since there's no network hop, but it's ephemeral: everything on instance store is lost when the instance stops or terminates. Never use instance store for anything you need to keep.

---

## [43:00–54:00] VPC and Auto Scaling Groups

VPC — Virtual Private Cloud — is your private network inside AWS. Every resource you create in AWS lives inside a VPC: your EC2 instances, your RDS databases, your ECS containers. The VPC defines the IP address space and the networking topology.

AWS creates a default VPC in each region when you open an account. The default VPC uses the 172.31.0.0/16 address range and has public subnets in each AZ. Everything in the default VPC gets a public IP address by default. It's fine for learning and prototyping.

For production, you create a custom VPC with a split architecture: public subnets and private subnets. Public subnets house the things that legitimately need to be reachable from the internet: your load balancers and NAT gateways. Private subnets house everything that should NOT be directly reachable: your application servers, your databases, your cache clusters.

The Internet Gateway connects your public subnets to the internet. Resources in public subnets can receive inbound connections. Resources in private subnets cannot receive inbound connections from the internet — they have no public IPs. But they still need to be able to make outbound connections — to download packages, call external APIs, reach AWS services. That's where the NAT Gateway comes in. It sits in the public subnet and lets private subnet resources make outbound connections while remaining unreachable for inbound.

This architecture — load balancer in public, app server in private, database in private — is the foundational AWS deployment pattern. Even without deep networking knowledge, knowing this pattern protects you from a common mistake: accidentally putting your database on a public IP.

Auto Scaling Groups. This is the mechanism that makes EC2 elastic. An ASG manages a fleet of EC2 instances and automatically adjusts the count based on demand.

You configure three numbers: minimum, desired, and maximum. Minimum is the floor — the ASG will never go below this count, even if it wanted to save money. You typically set this to at least 2 to ensure high availability across AZs. Desired is where the ASG starts. Maximum is the ceiling — cost guard, prevents runaway scaling.

The ASG is tied to a Launch Template that describes exactly what kind of instance to create. The Launch Template specifies the AMI, instance type, security groups, key pair, and any user data. When the ASG needs to scale out, it uses the Launch Template to launch a new instance.

Scaling policies control when the ASG scales. Target tracking is the easiest: set a target metric — keep average CPU at 60% — and the ASG scales in or out automatically to maintain that target. When traffic increases and CPU climbs above 60%, the ASG adds instances. When traffic drops and CPU falls below 60%, it removes instances. You're not setting thresholds and rules manually; you're setting a desired steady state and letting the ASG figure out how to get there.

The ASG also does health checks. If an instance fails its health check — the load balancer can't get a 200 from its health endpoint — the ASG terminates it and launches a replacement. This is self-healing infrastructure. You don't need someone to notice a crashed server and manually replace it; it happens automatically.

---

## [54:00–60:00] Part 1 Summary and Part 2 Preview

Let me assemble the mental model before we break.

AWS's global infrastructure — Regions and AZs — gives you the building blocks for resilient, globally distributed applications. Multiple AZs protect against data center failures. Multiple regions protect against regional failures and satisfy data residency requirements.

EC2 gives you virtual machines at any scale and cost profile. You choose the instance type for your workload, the pricing model for your budget, and the AMI as the starting image. Security Groups give you precise, chained access control. The VPC gives you a private network with public and private subnet tiers. EBS gives you persistent, snapshotable block storage. And Auto Scaling Groups give you elastic capacity that adjusts automatically with demand and self-heals when instances fail.

This is the infrastructure foundation. In Part 2 we build the service layer on top of it. S3 for object storage. RDS for managed databases. ECR and ECS for running your Docker containers — connecting directly to Day 36's Docker work. EKS for managed Kubernetes. Lambda for event-driven serverless functions. SNS and SQS as the AWS-managed alternatives to the Kafka you built in Day 39. IAM for access control across all services. CloudWatch for metrics and logs. And DynamoDB for managed NoSQL. Plus we'll put it all together in a full-stack deployment architecture.

Take a short break. See you back for Part 2.

---

*End of Part 1 Lecture Script*
