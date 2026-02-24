# Day 40 — AWS Part 2 Lecture Script
## S3, Beanstalk, RDS, ECR/ECS/EKS, Lambda, SNS/SQS, IAM, CloudWatch, DynamoDB, Full-Stack Deployment

**Duration:** 60 minutes
**Format:** Verbal delivery script with `[MM:SS–MM:SS]` timing markers
**Pace:** ~165 words per minute

---

## `[00:00–01:00]` Welcome Back

"Welcome back everyone. Part 1 was the foundation — we got hands-on with EC2, security groups, AMIs, EBS, VPCs, and Auto Scaling Groups. That's the infrastructure layer. Now in Part 2 we're going to zoom out and look at the managed services layer — and this is where AWS really starts to pay off.

Part 1 was: 'I have a Linux server, how do I configure it?' Part 2 is: 'I have an application, how do I run it without thinking about Linux servers?' These two layers work together, and by the end of today you'll have a complete mental model of how a real production application — your bookstore project, something like it — would live on AWS from frontend to database to async messaging to monitoring.

Fourteen services in sixty minutes. We move fast, but every single one of these you'll encounter in a real engineering role, so pay attention. Let's go."

---

## `[01:00–09:00]` Amazon S3

"S3 stands for Simple Storage Service. Don't let the name fool you — S3 is one of the most important AWS services and probably the one you'll use most often regardless of what kind of application you build.

Here's the key concept: S3 is not a file system. It's object storage. You store objects. An object has a key — think of it as a path, like 'uploads/users/42/avatar.jpg' — and it has a value, which is the file bytes. That's it. You don't create directories. You don't mount S3 like a drive. You PUT objects in and GET objects out via an API or the SDK.

Objects live in buckets. A bucket is a named container for your objects. Bucket names must be globally unique across every AWS account in the world — not just yours. So 'my-bucket' is taken. 'bookstore-assets' might be taken. You need something specific: 'bookstore-2024-production-uploads' — that kind of thing.

What can you store in S3? Profile pictures, file uploads, application backups, database snapshots, static website files, PDF invoices, log archives, Terraform state files. S3 is the answer to 'I need to store a file that's not in a database.'

Now, AWS charges you for storage, and they have tiered pricing based on how often you access data. Slide 2 shows the storage classes. Standard is for frequently accessed data — something you read daily. Standard-IA is Infrequent Access — you pay a bit less for storage but pay a retrieval fee when you read it, so it's for backups you rarely touch. Glacier classes are for archival — monthly or yearly access, very cheap storage, but retrieval takes minutes or hours. Lifecycle policies let you automatically move objects to cheaper storage classes as they age.

Versioning is important. Enable it on any bucket that holds data you can't afford to lose. When versioning is on, deleting an object doesn't actually delete it — it adds a delete marker. You can restore any previous version. You can also use versioning to roll back an application deployment artifact to a previous version if something goes wrong.

Bucket policies control access. The slide shows an example: allow public read on all objects in the bucket. You need that for static website hosting, which is our next topic."

---

## `[09:00–13:00]` S3 Static Website Hosting and CloudFront

"This is actually something you can do today on the free tier — host your React or Angular app on S3 for almost zero cost.

The process: create a bucket, enable static website hosting under the bucket's properties, set your index document to index.html, add the public read bucket policy, then sync your built frontend code to the bucket with the AWS CLI: `aws s3 sync ./dist s3://your-bucket-name --delete`. The `--delete` flag removes files from S3 that no longer exist in your local dist folder, keeping them in sync.

S3 gives you an HTTP website endpoint — something like bookstore-frontend.s3-website-us-east-1.amazonaws.com. That works. But it has problems: it's HTTP only — no HTTPS. It's an AWS domain, not your domain. And it's only served from one AWS region, so users in Tokyo are hitting a server in us-east-1.

The production solution is CloudFront. CloudFront is AWS's CDN — content delivery network. You put CloudFront in front of your S3 bucket. CloudFront gives you HTTPS with a free TLS certificate through ACM, you can attach your custom domain like 'bookstore.com', and CloudFront caches your static assets at over 200 edge locations around the world. A user in Singapore gets your React app from Singapore, not Virginia.

When you deploy a new version, run the invalidation command to clear CloudFront's cache: `aws cloudfront create-invalidation --distribution-id XXXXX --paths '/*'`. Without this, users might see the cached old version for 24 hours."

---

## `[13:00–19:00]` AWS Elastic Beanstalk

"Now for the backend. You have a Spring Boot application packaged as a JAR. How do you run it on AWS? Option one is what you'd do after Part 1: spin up an EC2 instance, SSH in, install Java, copy the JAR, write a systemd service, configure the security group manually. That works, but it's a lot of ops work and it's a single server — no auto scaling, no load balancer.

Elastic Beanstalk is option two: the PaaS approach. You upload your JAR and Beanstalk provisions everything automatically — EC2 instances, an Application Load Balancer, an Auto Scaling Group, Security Groups, CloudWatch health checks. You manage the application. AWS manages the infrastructure.

The slide shows what Beanstalk creates for you. There's an ALB handling incoming traffic, an Auto Scaling Group running your EC2 instances, security groups, and S3 for storing deployment artifacts. You didn't configure any of that — Beanstalk did it from the platform settings you chose.

You provide environment variables through the Beanstalk console — no database URL or password ever goes in your JAR file. Beanstalk injects them as environment variables at runtime, and Spring Boot reads them through the environment because Spring properties can be overridden by environment variables: set SPRING_DATASOURCE_URL in Beanstalk and it overrides whatever's in application.properties.

Deployments are simple: `eb deploy` from the CLI, or upload through the console. Beanstalk does a rolling deployment by default — replaces instances one at a time so there's no downtime.

When would you use Beanstalk over ECS? Beanstalk is fastest path to a running application. If you're a team of two developers who just want to ship code and not manage Kubernetes or ECS task definitions, Beanstalk is excellent. The trade-off is less control and less portability than ECS. Many real companies use Beanstalk successfully for years. It's not a beginner toy."

---

## `[19:00–27:00]` Amazon RDS

"Your Spring Boot backend is running on Beanstalk or ECS. It needs a database. You could manually run PostgreSQL on an EC2 instance — we covered that possibility in Part 1 with EBS and private subnets. But almost every professional team chooses RDS instead.

RDS is managed relational database. You pick an engine — PostgreSQL, MySQL, MariaDB, Oracle, SQL Server — and AWS handles everything below the SQL level: hardware, the OS, the database engine installation and patching, automated backups, storage management, and high availability configuration.

From your Spring Boot application's perspective, RDS is just a PostgreSQL server with a hostname. Your JDBC URL is `jdbc:postgresql://your-db.{random-id}.us-east-1.rds.amazonaws.com:5432/bookstoredb`. Same driver, same Hibernate, same JPA repositories. You don't change any application code to use RDS versus a local database.

Let me explain Multi-AZ because this is important. When you create an RDS instance with Multi-AZ enabled, AWS creates two database servers: a primary in one availability zone and a synchronous standby in a different availability zone. Every write to the primary is synchronously replicated to the standby before the write is acknowledged. If the primary's server fails — hardware failure, network issue, even just an OS patch reboot — AWS automatically promotes the standby to primary and updates the DNS endpoint. Your application's connection string doesn't change. The failover takes one to two minutes with no manual intervention. That's Multi-AZ: automatic failover, zero data loss, minimal downtime.

Read Replicas are different and serve a different purpose. A Read Replica is asynchronously replicated from the primary. It's not for high availability — it's for performance. If your application is doing a lot of reads — reports, product catalog searches — you can direct those reads to a Read Replica and take load off the primary. Read Replicas can also be in a different region, which gives you a disaster recovery option.

The slide shows the Spring Boot configuration. Notice `ddl-auto: validate` — in production, never use `create`, `create-drop`, or `update`. You want Hibernate to validate that the schema matches your entities, but you never want it to modify the database schema on startup. Schema changes in production go through proper database migration scripts with Flyway or Liquibase."

---

## `[27:00–37:00]` ECR and ECS with Fargate

"Alright, let's talk containers on AWS. You've been packaging Spring Boot apps as Docker images since Day 36. ECR and ECS are how those images get deployed at scale.

ECR — Elastic Container Registry — is your private Docker image registry on AWS. Think of it as Docker Hub, but private and integrated with AWS IAM for access control. Before you can push an image to ECR, you authenticate Docker with a temporary token from AWS: `aws ecr get-login-password | docker login`. Then you tag your image with the ECR repository URI and push it. The slide shows the full sequence.

ECR integrates natively with ECS and EKS. When an ECS task or EKS pod needs to pull your image, it uses the IAM role attached to the task or node to authenticate to ECR automatically. No Docker Hub credentials, no secrets to manage.

Now, ECS. Elastic Container Service is AWS's native container orchestration. It's simpler than Kubernetes but handles everything you need to run containers reliably in production.

ECS has three key concepts. First: the Task Definition. This is your container's blueprint — what image to run, how much CPU and memory, what ports to expose, what environment variables to inject, how to configure logging. Think of it as a docker-compose.yml for a single service. Second: the Service. The service says 'run three copies of this task definition at all times.' If a container crashes, the service replaces it. The service also integrates with your ALB to register new containers as targets automatically. Third: the Cluster. The cluster is the grouping — all your services run in a cluster.

Now, when you create an ECS service you have to decide the launch type: EC2 or Fargate.

EC2 launch type means you provision EC2 instances that form the worker pool, and ECS schedules containers on those instances. You manage the instances — patching, sizing, scaling the pool.

Fargate launch type is what most teams use for new workloads. With Fargate there are no EC2 instances. You define your task with a CPU and memory requirement — say 512 CPU units and 1GB memory — and AWS finds capacity to run your container somewhere in their infrastructure. You never see the host. You pay per vCPU-second and memory-second while your container runs.

The slide shows a complete Fargate task definition. Pay attention to the logConfiguration section — `awslogs` driver sends your container's stdout and stderr directly to CloudWatch Logs automatically. Every `System.out.println()` and SLF4J log statement lands in CloudWatch without any configuration in your Spring Boot code.

For a rolling deployment with ECS: when you push a new image to ECR and update the service, ECS starts new tasks with the new image and only terminates old tasks after the new ones pass health checks. Zero downtime. This is the deployment target for your Day 37 CI/CD pipelines — GitHub Actions builds the image, pushes to ECR, runs `aws ecs update-service`, and ECS handles the rolling deployment."

---

## `[37:00–41:00]` Amazon EKS

"If you need Kubernetes on AWS, you use EKS — Elastic Kubernetes Service. AWS manages the control plane: the API server, etcd, the scheduler. You manage your worker nodes.

From a developer perspective, EKS is identical to the Kubernetes you learned on Day 36. Same `kubectl` commands. Same Deployment and Service YAML. Same Helm charts. The difference from a Minikube or kind cluster is that when you create a Service with LoadBalancer type, EKS automatically provisions an AWS Application Load Balancer. When your Deployment scales up, EKS pulls your image from ECR using the IAM role on the worker node. It's standard Kubernetes with AWS integrations wired in.

You'd choose EKS over ECS if your team already has Kubernetes expertise, if you need the Kubernetes ecosystem — Helm, Istio, Argo CD, Cert Manager — or if you want your application to be portable across AWS and other cloud providers, since standard Kubernetes manifests work anywhere.

You'd choose ECS over EKS if you're starting fresh and don't have a Kubernetes requirement. ECS is simpler to configure, easier to debug, and has less operational overhead. The question is really: does your team have K8s expertise and do you need K8s-specific tooling? If yes, EKS. If no, ECS."

---

## `[41:00–47:00]` AWS Lambda

"Lambda is serverless computing. You write a function. AWS runs it in response to events. You pay per invocation and per millisecond of execution time. When nothing is calling your function, you pay nothing.

The execution model: a trigger fires — maybe an HTTP request through API Gateway, maybe a new file uploaded to S3, maybe a message in an SQS queue. Lambda spins up a container to run your function. If it's the first invocation in a while, there's a cold start — AWS has to provision a new container and start the JVM. For Java, that cold start can be 3 to 5 seconds for a heavyweight application. After the first invocation, the container stays warm and subsequent invocations run fast.

The slide shows a Java Lambda handler processing SQS messages. The interface is `RequestHandler<SQSEvent, String>` — the input type is `SQSEvent` because our trigger is SQS, and the return type is String. When the function returns successfully, the SQS messages are deleted. If the function throws an exception, the messages go back to the queue and retry — or end up in the dead letter queue after the retry limit.

Lambda fits naturally into your microservices architecture for specific use cases: image resizing when a user uploads a profile picture to S3 — that's a classic Lambda pattern. Scheduled cleanup jobs — archive orders older than seven years every Sunday at midnight. Processing notifications from an SQS queue without running a persistent Notification Service container 24/7. Lambda lets that notification function run only when there are messages to process.

Lambda is not great for long-running processes, anything needing warm connections maintained over time like a database connection pool, or anything with strict latency requirements where a cold start is unacceptable. For your Spring Boot backend, ECS is the right choice. Lambda is for specific event-driven tasks at the edge of your architecture."

---

## `[47:00–53:00]` SNS, SQS, IAM

"Two related services: SNS and SQS. You already know Kafka from Day 39. SNS and SQS are the AWS-managed alternative to running a Kafka cluster.

SNS is pub/sub — publish to a topic, all subscribers receive it. You create a topic called 'order-placed.' Your Order Service publishes to it. Multiple SQS queues subscribe: inventory-queue, notification-queue. When an order is placed, SNS delivers that event to every subscriber simultaneously. This is the fan-out pattern. You can also subscribe Lambda functions or HTTP endpoints to SNS topics directly.

SQS is a queue — one producer writes messages, consumers poll and process them. Each message is delivered to one consumer. This is the work queue pattern. Multiple consumer instances can poll the same queue, but each message is only processed once because SQS makes it invisible to other consumers while one consumer is processing it — that's the visibility timeout. If your consumer crashes before it finishes, the visibility timeout expires and the message reappears for another consumer to process.

How is this different from Kafka? Kafka messages are retained on disk and can be replayed. Consumer groups track their offset and can reset to any point in history. SQS messages are deleted after successful processing — no replay. SNS/SQS is fully managed — zero cluster operations. Kafka has dramatically higher throughput for event streaming scenarios. Choose SNS/SQS when you want simple async messaging without managing a cluster and you don't need replay. Use MSK — Managed Streaming for Kafka — when you need Kafka's replay capability and streaming semantics.

The Spring Cloud AWS starter makes SQS very clean in Spring Boot. Send with `SqsTemplate.send()`. Receive by annotating a method with `@SqsListener`. Message is auto-deleted on successful return, auto-released on exception.

Now IAM — critical. IAM is access control for every AWS service. Every API call to AWS is checked against IAM. There are four key concepts. IAM Users are human identities with long-term credentials — access key plus secret for CLI access. IAM Groups are collections of users that share permissions. IAM Roles are identities assumed by services, not people. IAM Policies are JSON documents that define what actions are allowed on which resources.

The most important pattern for backend developers: IAM Roles for EC2 and ECS. Never store AWS credentials in your application code or environment variables. Instead, assign an IAM Role to your EC2 instance or ECS task. The Spring Boot AWS SDK automatically fetches temporary credentials from the instance metadata endpoint. Those credentials are rotated hourly. If someone gets into your container, they can't extract long-lived credentials. The slide shows the policy example — specific S3 actions on a specific bucket, specific SQS SendMessage on a specific queue. That's least privilege — grant only what the service actually needs."

---

## `[53:00–58:00]` CloudWatch and DynamoDB

"CloudWatch is AWS's unified observability platform. You've seen it referenced throughout the day — ECS awslogs driver sends to CloudWatch, Auto Scaling alarms are CloudWatch alarms. Now let's understand it as a system.

CloudWatch has three main parts. Metrics are numerical time-series data. AWS automatically publishes metrics for everything: EC2 CPU utilization, RDS database connections, SQS queue depth, Lambda error count. You can also publish custom application metrics from Spring Boot using Micrometer. Logs are raw text log streams. Your ECS containers send logs to CloudWatch Log Groups via the awslogs driver — no code changes. Each container instance creates a Log Stream within the Log Group. Alarms watch a metric and trigger an action when a threshold is crossed: CPU over 80% for five minutes fires an SNS notification to your team's email and triggers an Auto Scaling policy to add more containers.

CloudWatch Logs Insights is a query language over your log streams. You can search across all instances of your service simultaneously: 'show me every ERROR log from the past hour.' 'Show me all logs matching this trace ID' — that's where Logs Insights plus OTel from Day 38 become very powerful together. In Week 9 you'll look at this integration more deeply.

DynamoDB: fully managed NoSQL database. Key-value and document model. No servers to provision or patch. Scales to any traffic level automatically.

The data model: every DynamoDB table has a partition key — required — and an optional sort key. Together these form the primary key. Every other attribute is schema-free. The partition key determines which internal partition stores your data. High cardinality is critical: userId is a good partition key, order status is a terrible partition key — you'd have 90% of your data on 'COMPLETED' and 10% split across 'PENDING' and 'SHIPPED.'

When to use DynamoDB instead of RDS? DynamoDB when your access patterns are simple and high-volume — user sessions, shopping carts, game leaderboards, real-time event logs. When you need per-request billing with no idle cost. When your schema is flexible and evolving. Use RDS when you need complex SQL with joins, reporting queries, strict ACID transactions across many rows, or when your data is naturally relational."

---

## `[58:00–60:00]` Full-Stack Architecture and Wrap-Up

"The last slide pulls everything together into a single deployment diagram. Your React frontend is built and synced to S3 behind CloudFront. CloudFront handles HTTPS and global caching. An Application Load Balancer routes API requests to ECS services running in private subnets on Fargate. Each service has its own RDS database in a private subnet — no public internet access to your database. Async events flow through SNS to SQS queues, consumed by more ECS services or Lambda functions. All services emit logs and metrics to CloudWatch. All services run with IAM roles — no hardcoded credentials anywhere. Images come from ECR. Secrets come from Secrets Manager.

That is a production-grade cloud architecture. Every component we've discussed today has a place in that diagram.

Week 8 is now complete. Look at the week summary slide. You started Monday with Docker and Kubernetes — understanding how to package and orchestrate containers. Tuesday was CI/CD — automating your build and deployment pipeline. Wednesday was microservices architecture — patterns, resilience, distributed tracing. Thursday was Kafka — event-driven architecture and async communication. And today you deployed all of it to the cloud.

Week 9 is integration week — we bring all of this together, review the full stack, deepen on observability and testing strategies, and you'll start planning your capstone project. Good work today. See you next time."

---

*End of Part 2 Lecture Script*
