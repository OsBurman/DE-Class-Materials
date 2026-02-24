# Day 40 — AWS Part 2: Speaking Script
# S3, Static Website Hosting, Elastic Beanstalk, RDS, ECS, ECR, EKS,
# Lambda, SNS, SQS, CloudWatch, IAM, DynamoDB, Fargate, Full-Stack Deployment
# Estimated time: ~95 minutes
# File: 01-aws-services-and-deployment.sh

---

## OPENING — Welcome Back (3 minutes)

**Say:**
"Welcome back. Before Part 1 break we covered cloud fundamentals, EC2, security groups, AMIs, EBS, and Auto Scaling Groups. That's your compute layer — the virtual servers.

Part 2 is where we cover everything ELSE. And there's a lot. But don't let the number of services intimidate you. By the end of this session you'll understand how each service fits into your bookstore architecture and why you'd choose one over another.

We're going to go start to finish on the full AWS architecture for our bookstore platform, and we're going to finish by seeing the entire thing drawn out — frontend, backend, database, messaging, monitoring, all of it in one picture.

Let's start with IAM — because before you touch ANY other AWS service, you need to get security right."

---

## SECTION 1 — IAM: Identity and Access Management (12 minutes)

**[Open: 01-aws-services-and-deployment.sh → SECTION 1]**

**Say:**
"IAM is AWS's security and identity service. It controls WHO can do WHAT in your AWS account. It's foundational — everything else depends on it being configured correctly.

Before I show the code, let me introduce the four IAM concepts you need to know."

**[Write on whiteboard or draw on screen:]**
```
IAM Concepts:
  Users  → Human identities (or apps using access keys)
  Groups → Collection of users with shared permissions
  Roles  → AWS service identities (EC2, Lambda, ECS assume roles)
  Policies → JSON documents defining permissions
```

**Say:**
"Look at Section 1a — IAM Users.

We're creating a `bookstore-app-user` user. But look at the two approaches for assigning permissions.

**Option A** attaches a managed policy — `AmazonS3FullAccess`. That's Amazon's pre-built policy that grants full S3 access. Easy to use.

**Option B** creates an inline custom policy. Look at it carefully — it says `Allow s3:GetObject, PutObject, DeleteObject` but ONLY on `arn:aws:s3:::bookstore-assets-prod/*`. Only that specific bucket. Not all of S3.

**Ask the class:** Which approach is more secure and why? [Pause] Exactly — Option B. This is the principle of least privilege. Only the permissions actually needed. Option A gives access to every S3 bucket in the account. If that user credential leaks, an attacker has access to every S3 bucket. Option B limits the blast radius to one bucket."

**[Show Section 1b — IAM Roles]**

"Now, IAM Roles. This is how services talk to other services — and this is what you'll use most in production.

Look at `bookstore-ec2-role`. Instead of putting AWS access keys in environment variables on your EC2 instance — which would leak if someone reads your process list — you create a Role that EC2 can assume.

The `--assume-role-policy-document` says: 'EC2 instances can assume this role.' We then attach policies to the role: S3 read access (for reading book images), CloudWatch Logs (for sending application logs), and SSM Session Manager (so we can SSH into instances WITHOUT a .pem key file — through the browser).

Then we create an Instance Profile and attach it to the role. When you launch an EC2 with this role, the instance automatically has these permissions. No access keys in code. No keys to rotate. The credentials rotate automatically via the metadata service.

**⚠️ Watch out:** This is a COMMON interview question. 'How do you give an EC2 access to S3 securely?' The answer is NEVER 'put access keys in the .env file.' The answer is always 'use an IAM Role attached to the instance.'"

**[Show Section 1c — IAM Groups]**

"Groups let you manage permissions for teams. Create a `BookstoreDevelopers` group, attach policies to the group, then add users to the group. When you hire a new developer, add them to the group — they instantly inherit all the right permissions.

**⚠️ Watch out — read these four mistakes.** These are real things that happen and cause security breaches:
- Using the root account for day-to-day work — root can't be restricted
- Hardcoding access keys in source code — they get pushed to GitHub, bots scan GitHub 24/7 for leaked keys
- Giving everyone AdministratorAccess — breach one account, breach everything
- Not enabling MFA — multi-factor authentication should be on EVERY account

In your capstone projects — set up IAM correctly from day one. It takes 10 minutes and saves you enormous grief."

---

## SECTION 2 — Amazon S3: Simple Storage Service (12 minutes)

**[Scroll to SECTION 2]**

**Say:**
"S3 — Simple Storage Service — is probably the most used AWS service of all. It's object storage: you upload files (objects), they're stored in buckets, you retrieve them via URL or API.

S3 is:
- **Infinitely scalable** — you can store bytes or exabytes, no planning needed
- **99.999999999% durable** (eleven nines) — AWS stores your data across multiple AZs automatically
- **Cheap** — $0.023 per GB per month for standard storage (about $2.30/month for 100 GB)
- **Globally accessible** — any computer with internet can retrieve objects

For our bookstore, S3 stores: book cover images, user-uploaded profile photos, static website files, deployment artifacts (JAR files), and log archives."

**[Show bucket creation]**

"Notice the bucket name: `bookstore-assets-${ACCOUNT_ID}-$(date +%Y%m%d)`. S3 bucket names must be **globally unique across ALL AWS accounts worldwide**. If someone else has `bookstore-assets`, you can't use it. Including your account ID in the name is a common pattern to ensure uniqueness.

**⚠️ Watch out:** There's a difference between us-east-1 and every other region for bucket creation. In us-east-1, you just run `create-bucket`. In any other region, you MUST add `--create-bucket-configuration LocationConstraint=eu-west-1`. Students forget this and get an error in eu-west-1.

Look at what we enable on the bucket:
- Versioning — keeps every version of every object. If you accidentally overwrite a book cover, you can restore the previous version.
- Encryption — AES-256 server-side encryption. Objects are encrypted at rest.
- Public access block — IMPORTANT. By default, S3 buckets are private. We explicitly block public access."

**[Show Section 2a — CRUD operations]**

"The `aws s3 cp` command copies files. The `aws s3 sync` command is more powerful — it compares source and destination and only uploads changed files. This is what you use to deploy your React build: `aws s3 sync dist/ s3://your-bucket/`.

The presigned URL is a great feature. Let's say you have a private S3 bucket with user invoices. You don't want to make the bucket public. But when a user downloads their invoice, you need to give them temporary access. `aws s3 presign` generates a URL with an embedded signature valid for up to 7 days (3600 seconds = 1 hour here). Anyone with that URL can download the file — for that time window only. After it expires, the URL stops working."

**[Show Section 2d — Lifecycle Policies]**

"Lifecycle policies are a set-and-forget cost optimization. Look at this policy: book covers in the `covers/` folder transition from STANDARD storage ($0.023/GB) to STANDARD_IA (Infrequent Access, $0.0125/GB) after 90 days — books published 3 months ago get accessed less often. After a year, they go to GLACIER ($0.004/GB) — archival storage.

Temp files auto-delete after 7 days. This alone can save significant money on a production system.

**Ask:** Why not put ALL data in GLACIER right away if it's cheapest? [Pause] Great answer — retrieval time. GLACIER takes minutes to hours to retrieve. STANDARD serves instantly. You only archive data you don't need quickly."

---

## SECTION 3 — Static Website Hosting on S3 (8 minutes)

**[Scroll to SECTION 3]**

**Say:**
"One of S3's most useful features for us as full-stack developers: you can host your entire React or Angular application directly from S3. No server needed for the frontend.

Here's the workflow: `npm run build` in your React project creates a `dist/` folder with `index.html`, JavaScript bundles, and CSS. You sync that `dist/` to an S3 bucket with website hosting enabled. That's your frontend deployment. Done.

[Show the code]

Notice the difference here from the private bucket: first we run `delete-public-access-block` to allow public access, then `put-bucket-website` to set `index.html` as the entry point and `error.html` for 404s, then `put-bucket-policy` to allow anonymous `s3:GetObject` — anyone can download objects.

**⚠️ Watch out:** For single-page applications (React Router, Angular Router), you need BOTH `IndexDocument` and `ErrorDocument` pointing to `index.html`. Why? Because if a user bookmarks `bookstore.com/books/123`, they're requesting `/books/123` from S3. S3 doesn't have a file at that path — it 404s. With `ErrorDocument: index.html`, S3 returns `index.html` for any 404, and React Router handles the routing client-side. If you set `error.html` instead, navigation from bookmarks will show your error page instead of the book.

The website URL is `http://BUCKET-NAME.s3-website-us-east-1.amazonaws.com`. For HTTPS in production, you put CloudFront in front of S3 — CloudFront handles SSL termination and CDN caching globally."

---

## SECTION 4 — Elastic Beanstalk (8 minutes)

**[Scroll to SECTION 4]**

**Say:**
"We just covered S3 as basically IaaS-adjacent for object storage. Now let's talk about the full PaaS experience: Elastic Beanstalk.

Remember from Part 1 — PaaS is the restaurant with a fully equipped kitchen. You just bring the ingredients. With Elastic Beanstalk, you literally just run `eb deploy` and hand AWS your JAR file. AWS handles everything else.

[Show the EB CLI commands — comment-block style since EB CLI isn't in standard AWS CLI]

`eb init` — initialize Elastic Beanstalk for your project, select the platform (Java 21 on Amazon Linux 2023).
`eb create` — spin up the environment. This automatically creates: EC2 instances, an Auto Scaling Group, an Application Load Balancer, security groups, CloudWatch alarms — the full stack.
`eb deploy` — packages your app (or uses the JAR you point it at) and triggers a rolling deployment.

[Show the .ebextensions config]

The `.ebextensions` folder in your project root is Elastic Beanstalk's customization hook. It's YAML files that configure the environment. Look at what we can set:
- Environment variables (Spring profiles, DB URL)
- Health check path (`/actuator/health` — always configure this)
- Instance type and Auto Scaling min/max sizes
- Commands to run on deployment

This is how you configure Beanstalk without touching the AWS console.

[Show the comparison table]

Beanstalk vs raw EC2: Beanstalk is easier — minutes to set up — but less flexible. For your capstone projects, Beanstalk is probably the right choice. You'll have a working deployment fast without managing EC2 infrastructure. For a Netflix-scale production system, you'd want more control."

---

## SECTION 5 — Amazon RDS (10 minutes)

**[Scroll to SECTION 5]**

**Say:**
"RDS — Relational Database Service — is managed PostgreSQL (or MySQL, Oracle, SQL Server, etc.) running on AWS. AWS handles backups, patching, Multi-AZ failover, and monitoring. You handle your schema and queries.

Compare this to running PostgreSQL on EC2: on EC2 you're responsible for installing PostgreSQL, setting up replication, configuring backups, monitoring disk space, applying security patches. With RDS, all of that is managed.

[Show the DB Subnet Group]

Before creating the RDS instance, we create a DB Subnet Group. This tells RDS which subnets it's allowed to use. For production, you'd put RDS in private subnets — no direct internet access. Only your application servers (in public or private subnets) can reach the database.

We also create a dedicated security group for RDS that ONLY allows traffic on port 5432 from within the VPC. Your database should never be accessible from the internet.

[Show the create-db-instance command]

Look at these parameters:
- `--db-instance-class db.t3.micro` — free tier eligible
- `--engine postgres --engine-version 16.1` — PostgreSQL version
- `--allocated-storage 20 --max-allocated-storage 100` — starts at 20 GB, auto-scales up to 100 GB as needed
- `--multi-az` — **THIS IS IMPORTANT**. Multi-AZ creates a synchronous standby replica in a different AZ. If the primary DB fails, RDS automatically fails over to the standby in ~60 seconds. For production, always use Multi-AZ.
- `--backup-retention-period 7` — keeps automated backups for 7 days. Point-in-time recovery.
- `--storage-encrypted` — encrypt the data at rest.

**Ask:** What's the connection string for your Spring Boot app to connect to RDS? [Pause] It would be `jdbc:postgresql://bookstore-db.xxxxx.us-east-1.rds.amazonaws.com:5432/bookstore`. You get the endpoint hostname from `describe-db-instances`. Put the password in AWS Secrets Manager — never in application.yml committed to Git.

**⚠️ Watch out:** Creating an RDS instance takes 5-10 minutes. If you demo this in class, start the creation first and then continue teaching — check back on it later."

---

## SECTION 6 — ECR: Elastic Container Registry (8 minutes)

**[Scroll to SECTION 6]**

**Say:**
"ECR is AWS's private Docker container registry. It's like Docker Hub, but it lives in your AWS account. Your ECS tasks and EKS pods pull images from ECR.

Why ECR over Docker Hub?
- Private by default — images stay in your account
- Integrated with ECS and EKS — no credentials to configure, IAM handles auth
- Lifecycle policies — auto-delete old images to control storage costs
- Image scanning — ECR automatically scans for known security vulnerabilities on every push

[Show repository creation]

We create two repos: `bookstore/api` for the Spring Boot backend and `bookstore/frontend` for the React app.

The `--image-scanning-configuration scanOnPush=true` means every time someone pushes an image, AWS scans it for vulnerabilities. You get a report in the ECR console. If your image has a critical CVE, you'll know before deploying it.

[Show Docker auth]

`aws ecr get-login-password | docker login` — this authenticates your Docker client with ECR. The token is valid for 12 hours. In CI/CD pipelines, you run this at the start of every build job.

[Show the Dockerfile example]

This is the Dockerfile for our Spring Boot app. Very minimal: start from Corretto JDK 21, copy the JAR, expose port 8080, run it. That's all you need.

[Show lifecycle policy]

The lifecycle policy keeps only the last 10 versioned images and deletes untagged images after 7 days. Without this, you'll accumulate hundreds of stale images and pay for storage you don't need. Always configure lifecycle policies."

---

## SECTION 7 — ECS: Elastic Container Service + Fargate (12 minutes)

**[Scroll to SECTION 7]**

**Say:**
"ECS is where we actually run our Docker containers in AWS. ECS has two concepts: Task Definitions and Services.

Think of it like this:
- **Task Definition** = docker-compose.yml for a single service. What image, how much CPU/RAM, which ports, what environment variables, how to collect logs.
- **Service** = 'keep N copies of this task running at all times.' It's like an Auto Scaling Group but for containers.

Let's walk through the Task Definition carefully."

**[Show task definition]**

"Look at the `register-task-definition` command:
- `--cpu 512` = 0.5 vCPU
- `--memory 1024` = 1 GB RAM
- `--requires-compatibilities FARGATE` — this task runs on Fargate (no EC2 nodes)
- `--network-mode awsvpc` — required for Fargate, each task gets its own network interface

In `container-definitions`:
- The image URI points to our ECR repo
- `portMappings` exposes port 8080
- `environment` sets Spring Boot environment variables
- `secrets` — look at this! The DB password is NOT in the environment block. It references AWS Secrets Manager by ARN. At runtime, ECS retrieves the secret value and injects it. Your password never appears in plaintext in your task definition or in `docker inspect` output.
- `logConfiguration` with `awslogs` — container logs go to CloudWatch automatically.
- `healthCheck` — runs `curl localhost:8080/actuator/health` every 30 seconds. If it fails 3 times, ECS kills and replaces the task.

**⚠️ Watch out:** Students sometimes ask 'where do I put environment variables in ECS?' The answer is: non-sensitive values in `environment`, sensitive values (passwords, API keys) in `secrets` referencing Secrets Manager or Parameter Store. Never put passwords in task definitions — they're visible to anyone with IAM access.

[Show ECS Cluster and Service]

The cluster is just a logical grouping — with `FARGATE` and `FARGATE_SPOT` capacity providers, no EC2 instances to manage.

The Service has `desired-count 2` — always keep 2 containers running. `deployment-configuration minimumHealthyPercent=50,maximumPercent=200` means during a rolling update, we can drop to 1 running container (50% of 2) and run up to 4 (200% of 2) simultaneously.

`update-service --force-new-deployment` is the deploy command. ECS pulls the latest image and replaces old tasks with new ones, keeping the service running throughout.

[Show Fargate notes block]

Fargate is the serverless layer on top of ECS. With EC2 launch type, you manage EC2 instances. With Fargate, you don't. You define CPU and memory, Fargate finds capacity for you. This is why Fargate is usually the right choice for ECS — one less thing to manage."

---

## SECTION 8 — EKS: Elastic Kubernetes Service (8 minutes)

**[Scroll to SECTION 8]**

**Say:**
"EKS is AWS's managed Kubernetes service. If ECS is AWS's proprietary container orchestrator, EKS is Kubernetes — the industry standard. Same functionality, different API.

The key question: ECS or EKS? Look at the comparison table.

ECS is simpler, AWS-native, less to learn. EKS gives you the full Kubernetes ecosystem — Helm charts, Istio service mesh, custom operators, all the CNCF tooling. EKS is more portable — if you want to migrate from AWS to Google Cloud someday, your Kubernetes manifests work unchanged.

For your bootcamp projects: ECS is the right choice. Less to learn, faster to deploy.
In industry: many large companies use EKS when they have Kubernetes expertise on the team.

[Show the eksctl config]

`eksctl` is a CLI tool that makes creating EKS clusters easy. This YAML config creates:
- A cluster called `bookstore-eks`
- A managed node group with t3.medium instances, 2 nodes by default, scales 1-5
- Add-ons: EBS driver (for persistent volumes), CoreDNS, kube-proxy, VPC CNI
- CloudWatch logging for all control plane components

[Show the Kubernetes manifest]

This is a standard Kubernetes deployment and service — identical to what we'd write for any Kubernetes cluster, AWS or not. Notice:
- `readinessProbe` and `livenessProbe` hit our Spring Boot Actuator endpoints
- `resources.requests` and `limits` — always set these in production so one container can't starve others
- `type: LoadBalancer` on the Service — EKS automatically creates an AWS Application Load Balancer when you apply this

On EKS, the deploy command is `kubectl apply -f deployment.yaml` — same as any other Kubernetes cluster."

---

## SECTION 9 — AWS Lambda (10 minutes)

**[Scroll to SECTION 10]**

**Say:**
"Lambda is serverless compute. No instances, no containers, no infrastructure. You write a function. AWS runs it when triggered. You pay only for the time the function actually runs — in milliseconds.

For our bookstore, a perfect Lambda use case: when a user uploads a book cover image, automatically resize it to create a thumbnail. This is event-driven — we don't need this running all the time. It runs once per upload, for a fraction of a second, and we pay only for that.

[Show the Lambda function code]

Look at this Node.js function. It's an S3 event handler. When triggered, `event.Records[0].s3` gives us the bucket name and the object key. In a real function, we'd download the image, resize with a library like Sharp, and upload the thumbnail. For the demo, we log and return a success response.

Lambda function structure:
- `exports.handler` — the entry point. Always this signature.
- `event` — the trigger payload (S3 event, SNS message, API Gateway request, etc.)
- Always `async/await` — return a Promise.

[Show the Lambda creation]

`--runtime nodejs20.x` — Node.js 20. Lambda also supports Python, Java, Go, .NET, Ruby.
`--handler index.handler` — the filename (`index.js`) and the export name (`handler`).
`--timeout 30` — Lambda times out after 30 seconds. For image resizing that's fine. Default is 3 seconds.
`--memory-size 512` — 512 MB RAM. Lambda pricing is based on duration × memory.

**Important:** Lambda needs an IAM Role! We created `bookstore-lambda-role` that allows Lambda to execute (`AWSLambdaBasicExecutionRole`) and access S3 (`AmazonS3FullAccess`). Lambda can't touch S3 without that role.

[Show S3 trigger]

`put-bucket-notification-configuration` sets S3 to call our Lambda whenever a new object is created in the `covers/` prefix. This is the trigger. Whenever someone uploads a book cover, Lambda fires automatically. No polling, no cron job — event-driven.

Lambda scales automatically. If 1,000 books are uploaded simultaneously, Lambda runs 1,000 concurrent invocations in parallel. You pay for 1,000 × duration. You don't provision 1,000 servers.

**Ask:** When would you NOT use Lambda? [Pause for answers] Lambda has limits: 15-minute max runtime, no persistent local storage, cold start latency (50-500ms on first invocation). For a long-running report generator, use ECS. For a constantly-running API, use ECS. For event-driven short tasks — image resize, email send, notification, data transform — Lambda is perfect."

---

## SECTION 10 — SNS and SQS (8 minutes)

**[Scroll to SECTION 11]**

**Say:**
"We covered Kafka in Day 39 as a managed, high-throughput streaming platform. Today we're looking at AWS's cloud-managed messaging — SNS and SQS. Lighter weight, simpler setup, tight AWS integration.

SNS is pub/sub — one publisher, many subscribers.
SQS is a queue — producers add messages, consumers read and delete them.

[Show SNS section]

`sns create-topic` creates a topic. For our bookstore, `bookstore-order-events`.

We subscribe:
- An email address — for admin notifications
- An HTTPS endpoint — to call a webhook when an order is placed
- An SQS queue — to fan out to another service (coming up next)

`sns publish` sends a message to ALL subscribers simultaneously. This is the fan-out pattern. One order event → email, webhook, AND inventory queue all get it.

[Show SQS section]

`sqs create-queue` creates the inventory queue. Key attributes:
- `MessageRetentionPeriod: 86400` — messages kept for 24 hours if not consumed
- `VisibilityTimeout: 30` — when a consumer reads a message, it becomes 'invisible' for 30 seconds. If the consumer doesn't delete it within 30 seconds (processing failed), it becomes visible again for another consumer to pick up.
- `ReceiveMessageWaitTimeSeconds: 20` — long polling. Instead of the consumer hammering the queue every second, it waits up to 20 seconds for a message. Reduces empty requests, reduces cost.

We also create a Dead Letter Queue. If a message fails processing 3 times (`maxReceiveCount=3`), SQS moves it to the DLQ instead of discarding it. This is exactly what we built with `@DltHandler` in Kafka yesterday — same concept, different technology.

`sns subscribe` with protocol `sqs` wires the SNS topic to this SQS queue. Order placed → SNS publishes → all subscribers notified, including the inventory SQS queue.

**Ask:** What's the difference between SNS+SQS and Kafka? [Pause]
SNS/SQS: simpler, serverless, managed, good for up to millions of messages/day, no replay (SQS auto-deletes consumed messages). Kafka: higher throughput (billions/day), message retention and replay, consumer groups, exactly-once semantics. For most applications, SNS/SQS is sufficient and simpler. Kafka is for high-throughput streaming needs."

---

## SECTION 11 — CloudWatch (8 minutes)

**[Scroll to SECTION 12]**

**Say:**
"CloudWatch is AWS's monitoring and observability service. It collects logs, metrics, and events from virtually every AWS service automatically. You just have to configure what to look at and when to alert.

Three main pillars:

**Logs** — any text output. EC2 system logs, application logs, Lambda execution logs, RDS slow query logs. In Section 12a we create a log group `/bookstore/api` with 30-day retention. Without retention policy, logs are kept FOREVER and you pay for storage indefinitely. Always set retention.

`aws logs tail /bookstore/api --follow` is your `tail -f` for cloud logs. You can also filter: `filter-log-events --filter-pattern "ERROR"` — shows only lines containing ERROR from the last hour.

**Metrics** — numeric time-series data. CPU utilization, request count, error rate, latency. Every AWS service publishes metrics automatically. In Section 12b we create alarms.

Look at this alarm: alert when 5xx errors exceed 10 in a 5-minute window, for 2 consecutive periods. When it fires, it publishes to our SNS topic — which sends an email to the admin.

In Section 12c — custom metrics. Your Spring Boot application can publish custom metrics to CloudWatch using the AWS SDK or Spring Boot Actuator with the CloudWatch metrics export. Metrics like 'orders per minute,' 'active sessions,' 'payment processing time.' Then you alarm on those custom metrics.

**Dashboards** — in Section 12d, we create a dashboard showing orders per minute and CPU utilization side-by-side. In the AWS console, this renders as an interactive graph. You can share dashboards with stakeholders who don't have AWS access.

**⚠️ Watch out:** CloudWatch logs and custom metrics cost money. A high-volume application can generate significant CloudWatch costs. Always configure log retention and be thoughtful about metric cardinality (don't create a unique metric per user ID!)."

---

## SECTION 12 — DynamoDB (8 minutes)

**[Scroll to SECTION 13]**

**Say:**
"DynamoDB is AWS's NoSQL database. Fully managed, serverless, millisecond latency at any scale. You pay per request or reserve capacity.

For our bookstore, DynamoDB is perfect for shopping carts and session data because:
- High read/write throughput (thousands of cart updates per second during Black Friday)
- Flexible schema (different users have different items in their carts)
- Low latency (cart operations need to be instant)
- No DB instance to manage (serverless — just pay per request)

[Show table creation]

We create `BookstoreShoppingCarts`. The key schema is critical:
- `userId` is the **Partition Key** (HASH) — distributes data across partitions
- `cartId` is the **Sort Key** (RANGE) — orders items within a partition

This allows: `GetItem(userId="user-123", cartId="cart-456")` — instant O(1) lookup. And: `Query(userId="user-123")` — get ALL carts for this user, efficiently.

`--billing-mode PAY_PER_REQUEST` — pay per read/write operation. No capacity planning needed. Perfect for variable workloads.

[Show View History table with TTL]

`BookstoreViewHistory` tracks which books each user viewed. We enable TTL with `expiresAt`. When your code writes a view record, set `expiresAt` to `now + 90 days` (as a Unix epoch timestamp). DynamoDB automatically deletes expired records. No cron job, no cleanup script — it's built in.

[Show CRUD operations]

`put-item` — create or replace. Notice the DynamoDB type system: `S` for String, `N` for Number, `L` for List, `M` for Map. This is how DynamoDB stores nested objects — a list of maps for cart items.

`get-item` by primary key — fastest possible read. Direct lookup in the partition.

`update-item` with `--update-expression` — only update specified fields without overwriting the whole item. The `SET totalAmount = :total` expression syntax is DynamoDB's update language.

`query` with a `KeyConditionExpression` — get all items matching the partition key. This is how you get all carts for user-123. Much more efficient than a table scan.

**⚠️ Watch out:** DynamoDB does NOT support joins or complex SQL queries. You design your table for your access patterns upfront. If you need to query by `cartId` without knowing the `userId`, you'd need a Global Secondary Index (GSI). This is a core DynamoDB concept — access-pattern-driven design."

---

## SECTION 13 — Full-Stack Deployment Architecture (10 minutes)

**[Scroll to SECTION 14]**

**Say:**
"Now let's tie everything together. Look at this architecture diagram — this is our complete bookstore platform on AWS.

[Walk through the diagram top to bottom]

Traffic comes in from the internet to **Route 53** — AWS's DNS service. `bookstore.com` resolves to our services.

For the frontend: Route 53 points to **CloudFront** (our CDN), which serves content from **S3**. Our React build lives in S3. CloudFront caches it at edge locations worldwide — a user in Tokyo gets the app served from a Tokyo edge node, not from us-east-1. Fast, HTTPS, global.

For the API: Route 53 points to the **Application Load Balancer**, which distributes traffic across our **ECS Fargate** tasks (2-4 Spring Boot containers). The ALB does health checks on `/actuator/health`.

The Spring Boot containers connect to **RDS PostgreSQL** (Multi-AZ, private subnet) for relational data, and **DynamoDB** for shopping carts and sessions.

When an order is placed, the API publishes to **SNS**. SNS fans out to multiple **SQS** queues (inventory, payment, notifications). Lambda functions handle event-driven work like image resizing.

Monitoring: every service logs to **CloudWatch**. Alarms alert via SNS email when error rates spike. Dashboards show business metrics.

Security: **IAM** controls who can do what. All resources in a VPC. Security groups limit traffic. Secrets Manager stores passwords. S3 buckets are private except the frontend bucket.

[Show the deployment checklist]

This checklist is your playbook for deploying the full bookstore. Bookmark it. It breaks down into four phases: Foundation (one-time setup), Build & Push (every release), Deploy Backend, and Post-Deployment Verification.

The CI/CD pipeline automates phases 2-4. When you merge to main, the pipeline:
1. Runs tests
2. Builds the Docker image
3. Pushes to ECR
4. Deploys frontend to S3
5. Triggers ECS rolling deployment
6. Verifies health checks

You only do Phase 1 once. After that, `git push` handles deployment."

**[Show the Blue/Green deployment block]**

"Blue/Green deployment is the zero-downtime deployment strategy. Instead of replacing running containers in-place (which causes brief instability), you spin up a COMPLETELY NEW set of containers alongside the old ones, test them, then switch the load balancer to send all traffic to the new set.

Rollback is instant — flip the load balancer back to blue. No rebuilding, no re-deploying.

AWS CodeDeploy automates blue/green for ECS. This is what production deployments look like at Netflix, Amazon, and most enterprise companies."

---

## CLOSING — Day 40 Wrap-Up (5 minutes)

**[Show the Quick Reference at the bottom of the script]**

**Say:**
"Let's look at the final quick reference card. Every service we covered today in one place.

What you built knowledge of in Part 2:
- **IAM** — users, roles, policies, principle of least privilege
- **S3** — object storage, static website hosting, lifecycle policies
- **Elastic Beanstalk** — PaaS deployment, just upload your JAR
- **RDS** — managed PostgreSQL, Multi-AZ, automated backups
- **ECR** — private Docker registry, image scanning, lifecycle policies
- **ECS + Fargate** — container orchestration, serverless containers
- **EKS** — managed Kubernetes, for when you need Kubernetes portability
- **Lambda** — serverless functions, event-driven compute
- **SNS + SQS** — managed pub/sub and queues, DLQ for reliability
- **CloudWatch** — logs, metrics, alarms, dashboards
- **DynamoDB** — serverless NoSQL, millisecond reads, TTL
- **Full-Stack Deployment** — the architecture pattern for production

This is Week 8, Day 40 — the last teaching day of the program. Next week is your final week: integration projects, review, interview prep, and capstone planning.

You've gone from 'Hello World in Java' to deploying a full-stack Spring Boot application to AWS with containers, databases, messaging, monitoring, and security. That is an enormous amount of ground covered in 8 weeks.

Take a few minutes to let that sink in. Then we'll move into the final exercises."

**[Final question for the class]:**
"Before we close — one question. Given everything we deployed today, if you had to pick ONE AWS service that surprised you the most or that you think will be most useful in your career, what would it be? [Take 2-3 answers from students]"

---

## TIMING GUIDE

| Section | Content | Time |
|---------|---------|------|
| Opening | Bridge from Part 1 | 3 min |
| Section 1 | IAM — users, roles, groups, best practices | 12 min |
| Section 2 | S3 — buckets, CRUD, lifecycle | 12 min |
| Section 3 | S3 static website hosting | 8 min |
| Section 4 | Elastic Beanstalk | 8 min |
| Section 5 | RDS | 10 min |
| Section 6 | ECR | 8 min |
| Section 7 | ECS + Fargate | 12 min |
| Section 8 | EKS | 8 min |
| Section 9 | Lambda | 10 min |
| Section 10 | SNS + SQS | 8 min |
| Section 11 | CloudWatch | 8 min |
| Section 12 | DynamoDB | 8 min |
| Section 13 | Full-stack architecture + deployment | 10 min |
| Closing | Summary + final reflection | 5 min |
| **Total** | | **~130 min (with breaks) / ~95 min (no break)** |

*Consider a 5-minute break after ECS (Section 7) for this lengthy Part 2.*

---

## INSTRUCTOR NOTES

1. **AWS cost awareness:** Remind students throughout that every service we demo costs money if left running. Have them enable AWS Budgets with a $5/month alert email so they're notified before anything significant accumulates.

2. **ECS Task Execution Role:** The ECS section requires a pre-existing IAM role named `ecsTaskExecutionRole`. This is a standard role AWS creates for you when you first use ECS via the console. If students get an error about this role, they can run: `aws iam create-role --role-name ecsTaskExecutionRole --assume-role-policy-document '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":"ecs-tasks.amazonaws.com"},"Action":"sts:AssumeRole"}]}'` then `aws iam attach-role-policy --role-name ecsTaskExecutionRole --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy`.

3. **RDS creation time:** Creating an RDS instance takes 5-10 minutes. Kick off the creation at the start of the RDS section and revisit the `describe-db-instances` command at the end.

4. **Lambda ZIP packaging:** The Lambda demo packages a local .js file. Students who don't have Node.js installed locally can use the AWS console's inline editor for demo purposes, or simply observe.

5. **Architecture diagram emphasis:** Spend extra time on Section 14 (full-stack deployment). This is the synthesis moment — the one diagram that ties the whole course together. It's worth drawing slowly, piece by piece.

6. **SNS vs Kafka bridge:** Students who were engaged yesterday in Kafka will naturally ask about SNS/SQS vs Kafka. Use the comparison in Section 10 to reinforce: SNS/SQS for simpler, cloud-native messaging; Kafka for high-throughput streams and replay. Both are valid. Real systems often use both.

7. **Final day energy:** This is the last teaching day of the program. Students may feel a mix of accomplishment and anxiety about next week. Acknowledge it. The closing reflection question is important — let students articulate what they've learned. It reinforces retention.

---

## FREQUENTLY ASKED QUESTIONS

**Q: Do I need to learn all 200+ AWS services?**
A: Absolutely not. Focus on the core services we covered today. In job postings and real projects, 90% of work involves EC2, S3, RDS, IAM, ECS or EKS, Lambda, and CloudWatch. The others you learn when you need them. AWS provides certifications (Solutions Architect Associate is the most valuable entry-level cert) that give you a structured path.

**Q: ECS or EKS for my capstone project?**
A: ECS with Fargate. It's faster to set up, easier to debug, and you'll spend less time on infrastructure configuration. EKS is worth learning after the bootcamp if you want to deepen your containerization skills.

**Q: Is DynamoDB a replacement for PostgreSQL (RDS)?**
A: No — they're complementary. RDS/PostgreSQL is for relational data with complex queries, joins, and ACID transactions (orders, products, users, inventory). DynamoDB is for high-throughput key-value or document data where you know your access patterns and need scale (carts, sessions, preferences, event logs).

**Q: How does Elastic Beanstalk handle database migrations?**
A: Beanstalk doesn't run migrations for you. Common patterns: (1) run `flyway migrate` in a `container_commands` in `.ebextensions` with `leader_only: true` — only one instance runs the migration during deployment; (2) run migrations manually before deploying; (3) run migrations as a separate job in your CI/CD pipeline before the `eb deploy` step. Option 3 is cleanest and most common.
