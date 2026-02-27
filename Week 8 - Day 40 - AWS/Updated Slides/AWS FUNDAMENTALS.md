Aws fundamentals revised · MDCopyAWS Fundamentals — 1-Hour Lecture Script & Slide Guide
REVISED VERSION

SLIDE 1: Title Slide
Content: "Introduction to AWS" | Your name | Date | "From Cloud Basics to Full-Stack Deployment"
SCRIPT:
"Good morning everyone. Today we're doing a big picture tour of AWS — Amazon Web Services. By the end of this hour you should have a solid mental model of what AWS is, why it exists, and how all the major pieces fit together. We're not going deep into every single service today — that's what the hands-on labs are for — but you should leave here knowing what each service does and when you'd reach for it. Think of this as your map before we start exploring the territory."

SLIDE 2: What Is Cloud Computing?
Content: Definition | The 3 models: IaaS / PaaS / SaaS | Quick visual diagram showing the stack
SCRIPT:
"Before we talk about AWS specifically, we need to nail down what cloud computing actually is, because this word gets thrown around a lot.
At its core, cloud computing means renting someone else's computers over the internet instead of buying and maintaining your own. That's it. Amazon, Google, and Microsoft own enormous data centers full of servers, and they rent you access to that hardware by the minute.
Now there are three models you'll hear constantly.
IaaS — Infrastructure as a Service — is the most raw form. You get virtual machines, networking, and storage. You're responsible for everything above that: the operating system, the runtime, your app. EC2, which we'll talk about a lot today, is IaaS. You get a server, you configure it however you want.
PaaS — Platform as a Service — goes a level higher. The provider manages the underlying infrastructure and you just deploy your code. Elastic Beanstalk is AWS's PaaS offering. You push your app and AWS figures out the servers, load balancers, and scaling.
SaaS — Software as a Service — is the finished product layer. Gmail is SaaS. You don't manage any infrastructure at all, you just use the software.
As developers, you'll mostly work in IaaS and PaaS. Understanding where each service sits on this spectrum is a big part of making good architectural decisions."

SLIDE 3: Why AWS?
Content: Market leader | 200+ services | Pay-as-you-go | Global reach | Used by Netflix, Airbnb, NASA
SCRIPT:
"AWS launched in 2006 and is still the dominant cloud provider with roughly 30% market share. The reason it matters to you is not because it's the flashiest — it's because it's what most companies use, which means it's what most job descriptions ask for.
The pay-as-you-go model is genuinely transformative. A startup can launch a product on the same infrastructure Netflix uses, paying only for what they consume. No upfront hardware cost, no guessing how many servers you'll need. If traffic spikes, you scale up. If it drops, you scale down and stop paying."

SLIDE 4: AWS Global Infrastructure
Content: Regions → Availability Zones → Data Centers | Map showing Regions worldwide | Current count (~33 Regions, 100+ AZs)
SCRIPT:
"AWS is a global network. Let's understand how it's organized because this affects every architectural decision you make.
At the top level you have Regions. A Region is a geographic area — US East Virginia, EU West Ireland, Asia Pacific Tokyo, and so on. Each Region is completely independent. Data in US-East does not automatically replicate to EU-West. This matters for latency, compliance, and disaster recovery.
Inside each Region you have Availability Zones, or AZs. An AZ is one or more physical data centers within that Region, connected by high-speed private fiber. The key thing is that AZs are physically separated — different power grids, different flood plains — so a failure in AZ-1 shouldn't affect AZ-2.
The practical takeaway: when you build for production, you spread your resources across multiple AZs. That's how you get high availability. You'll hear 'multi-AZ' constantly. It just means your app can survive one data center going down."

SLIDE 5: VPC — Virtual Private Cloud (NEW)
Content: Your private network inside AWS | Subnets: public vs. private | Internet Gateway | Route Tables
SCRIPT:
"Before we talk about EC2, you need to understand VPC — Virtual Private Cloud. Every resource you launch in AWS lives inside a VPC, which is essentially your own private section of the AWS network. Think of it as a walled-off neighborhood that belongs to you.
Inside a VPC you create subnets. Public subnets have a route to an Internet Gateway — the component that connects your VPC to the public internet — so resources in them can send and receive traffic from outside AWS. Private subnets don't have that route, so they're only reachable from within your VPC.
The pattern you'll use on almost every project: your web servers and load balancers go in public subnets because they need to talk to the internet. Your databases go in private subnets because they should never be directly exposed to the outside world. VPC is the foundational layer that makes that separation possible."

SLIDE 6: Full-Stack Architecture — The Roadmap (MOVED FROM SLIDE 24 — will revisit at the end)
Content: Architecture diagram: Route53 → CloudFront → ALB → ECS/EC2 → RDS/DynamoDB → S3
SCRIPT:
"Before we go service by service, I want to show you where we're headed. This is the architecture of a real full-stack application on AWS. A React frontend served through CloudFront and S3. An API backend running in containers behind a load balancer. A managed relational database. DNS handled by Route 53.
Every service on this diagram is something we're going to cover today. I'm not expecting you to understand all of it right now — that's the point. Keep this picture in your head as a map. Each time we introduce a new service, you'll be able to place it on this diagram and understand why it exists. We'll come back to this at the end and it should all make sense."

SLIDE 7: EC2 — Elastic Compute Cloud
Content: "Virtual machine in the cloud" | Instance types overview (t2, m5, c5, r5) | Use case examples
SCRIPT:
"EC2 is the backbone of AWS for most people. It's a virtual machine — a computer you can rent by the hour. You pick the operating system, the CPU, the memory, and the storage, and within minutes you have a running server.
Instance types follow a naming convention. The letter tells you the family — T instances are general purpose burstable, M is general purpose balanced, C is compute optimized, R is memory optimized. The number is the generation. So a t3.micro is a small third-generation general purpose instance. When you're learning or running small apps, t2.micro or t3.micro will be your friend — they're also free tier eligible.
You can spin up one EC2 instance or a thousand. You can run Windows or Linux. You can install any software you want. That flexibility is both EC2's greatest strength and its greatest responsibility — you're managing that operating system."

SLIDE 8: Launching an EC2 Instance — AMI & Instance Type
Content: AMI definition and examples (Amazon Linux, Ubuntu, Windows Server) | Instance type selection | Custom AMIs for auto scaling
SCRIPT:
"When you launch an EC2 instance, the first two decisions are what it is and how big it is.
The AMI — Amazon Machine Image — is your starting snapshot: the operating system, any pre-installed software, and baseline configuration. AWS provides official AMIs for Amazon Linux, Ubuntu, Windows Server, and more. You can also create your own AMI from an already-configured instance, which is how teams bake their application into a reusable image for auto scaling — spin up one instance, configure it exactly right, save it as an AMI, and now you can launch hundreds of identical instances from that image.
Instance type is how much CPU and RAM you're renting. For learning and small projects, start with t3.micro. For production workloads, you'll choose based on what your application actually needs — compute-heavy, memory-heavy, or balanced."

SLIDE 9: Launching an EC2 Instance — Networking, Security & Access
Content: VPC + subnet placement (public vs. private) | Security Groups (firewall rules) | Key pairs for SSH access
SCRIPT:
"The next three decisions are about where your instance lives and who can reach it.
Networking: you place your instance in a VPC and a specific subnet. Public subnet means it can be reached from the internet. Private subnet means it can only be reached from within your VPC. Your choice here is deliberate — a web server belongs in a public subnet, a database server belongs in a private one.
Security Groups are your instance-level firewall. You write rules that say what traffic is allowed in — allow SSH on port 22 from my IP, allow HTTP on port 80 from anywhere. Security groups are stateful, meaning if you allow inbound traffic, the response is automatically allowed back out without a separate rule.
Finally, key pairs. AWS uses public-key cryptography for SSH access. AWS puts the public key on your instance and you hold the private key file — a .pem file you download once at creation. That file is how you prove your identity when connecting. Don't lose it and absolutely do not commit it to a code repository."

SLIDE 10: Security Groups vs Network ACLs
Content: Side-by-side comparison table | Stateful vs. Stateless | Instance level vs. Subnet level
SCRIPT:
"Security Groups and Network ACLs — or NACLs — are both ways to control traffic, but they operate at different levels and behave differently.
Security Groups are attached to individual instances. They're stateful — you only need to define inbound rules and the responses are automatically allowed. They only support allow rules, no explicit deny.
NACLs operate at the subnet level. They're stateless — you have to define both inbound and outbound rules explicitly. They support both allow and deny rules and are evaluated in order by rule number.
For most of what you'll build, Security Groups are your primary tool. NACLs are an additional defense layer for more complex network architectures. The mental model: a Security Group is the lock on your front door, a NACL is the gate at the entrance of your neighborhood."

SLIDE 11: Connecting to EC2 — SSH
Content: How SSH works with key pairs | Default usernames by AMI type | Common connection issues
SCRIPT:
"SSH — Secure Shell — is how you open a terminal session on a remote Linux server. When you connect to an EC2 instance, you're authenticating with the private key file you downloaded when the instance was created, rather than a password.
The username you connect as depends on the AMI. Amazon Linux uses ec2-user. Ubuntu uses ubuntu. CentOS uses centos. These are just the default users baked into each image.
Two things trip people up most often. First, your private key file needs to have restricted permissions on your local machine — if it's too open, SSH refuses to use it as a security precaution. Second, your instance needs to actually be reachable: it should be in a public subnet, have an internet gateway attached to the VPC, and its security group must allow inbound traffic on port 22. If any one of those three things is wrong, the connection will fail."

SLIDE 12: EBS — Elastic Block Store
Content: "Persistent storage attached to EC2" | Volume types: gp3, io2, st1 | Snapshots | EC2 is ephemeral, EBS persists independently
SCRIPT:
"EBS is the persistent storage that attaches to your EC2 instance — think of it as the hard drive. This distinction matters: EC2 instances are ephemeral compute. If you terminate an instance, by default the root EBS volume is deleted too, but you can configure it to persist. The compute and the storage are separate things.
Volume types matter for performance. gp3 is the general purpose SSD and is what you'll use the vast majority of the time. io2 is high-performance SSD for databases that need consistent, guaranteed IOPS. st1 is throughput-optimized HDD for big sequential workloads like log processing.
Snapshots are point-in-time backups of EBS volumes stored in S3. You can restore from a snapshot or use one to create a new AMI. Build the habit of snapshotting before making significant changes to a production server — it's your undo button."

SLIDE 13: Auto Scaling Groups
Content: Diagram showing ASG with min/max/desired | Scaling policies | Integration with load balancer
SCRIPT:
"Auto Scaling Groups are how AWS makes your EC2 infrastructure elastic — meaning it grows and shrinks with demand.
You define three numbers: minimum instances, maximum instances, and desired capacity. AWS will always try to maintain your desired count. Scaling policies let you adjust that count automatically based on metrics. If CPU goes above 70% for five minutes, add two instances. If CPU drops below 30%, remove instances down to your minimum.
Auto Scaling works hand-in-hand with a Load Balancer. New instances are automatically registered with the load balancer and start receiving traffic. Terminated instances are deregistered cleanly. This combination — Auto Scaling Group plus load balancer — is the fundamental pattern for scalable, fault-tolerant web applications on AWS. If you understand nothing else from today, understand this pattern."

SLIDE 14: Application Load Balancer (NEW)
Content: What a load balancer does | ALB vs. NLB | Listeners and target groups | Works with Auto Scaling
SCRIPT:
"A load balancer sits in front of your application and distributes incoming requests across multiple instances or containers. Without one, all your traffic hits a single server — which is a single point of failure and a bottleneck. With a load balancer in front of an Auto Scaling Group, traffic is spread across however many healthy instances are running, and if one goes down, the load balancer stops sending it traffic automatically.
AWS offers a few types. The Application Load Balancer — ALB — works at the HTTP layer and is what you'll use for web apps and APIs. It can route requests based on URL path, hostnames, headers, and more. The Network Load Balancer — NLB — works at the TCP layer for ultra-low-latency or non-HTTP traffic.
The key concepts in an ALB: a Listener is the port and protocol it accepts traffic on — port 443 for HTTPS, for example. A Target Group is the set of instances or containers that receive the traffic. Rules on the listener decide which target group handles which requests."

SLIDE 15: Amazon S3 — Simple Storage Service
Content: Object storage concept | Buckets and objects | 99.999999999% durability | Global namespace for buckets
SCRIPT:
"S3 is AWS's object storage service and one of its oldest and most widely used. Unlike EBS which is a block device attached to one server, S3 is a massive distributed file store you access over HTTP from anywhere.
The model is simple: you have buckets, which are containers, and objects, which are files. Each object has a key — essentially its filename and path — and can be up to 5 terabytes. Bucket names must be globally unique across all of AWS.
S3 is not a file system — you can't mount it like a drive and edit files in place. You upload, download, and delete whole objects. But it's incredibly durable — Amazon promises 11 nines of durability, meaning if you store 10 million objects you'd expect to lose one every 10,000 years.
S3 is used for storing user uploads, hosting static assets, backups, data lakes, and more. If you need to store a file and access it from the internet, S3 is usually your answer."

SLIDE 16: Hosting a Static Website on S3
Content: What "static" means | S3 static hosting concept | CloudFront for production (HTTPS + caching)
SCRIPT:
"S3 has a feature that lets you serve a bucket's contents as a website. This is useful for anything that's purely files — a React app after it's been built, plain HTML documentation, a marketing landing page. No server required, no backend code running, just files delivered over HTTP.
When static hosting is enabled on a bucket, S3 assigns a public URL and serves requests for your files directly. Any request that comes in gets matched to an object in the bucket by its key. That's the entire model — it's just file delivery.
For production, most teams put CloudFront in front of S3, which we'll cover in a moment. CloudFront adds HTTPS, caches your content at edge locations around the world so it loads faster for users everywhere, and removes the need to make your S3 bucket publicly accessible directly. But S3 is the origin — the source of truth for the files."

SLIDE 17: Route 53 & CloudFront (NEW)
Content: Route 53: DNS service | Routing policies (latency, failover, weighted) | CloudFront: CDN, edge locations, HTTPS termination
SCRIPT:
"Two more pieces before we see the full picture come together.
Route 53 is AWS's DNS service. DNS is what translates a human-readable domain name — like myapp.com — into an IP address that computers can route to. When you buy a domain and want it to point at your AWS infrastructure, Route 53 is how you manage that. It also supports routing policies: you can route users to the nearest Region based on latency, set up automatic failover to a backup if your primary goes down, or split traffic between two environments by percentage.
CloudFront is AWS's content delivery network. It has edge locations — essentially small caches — in cities all over the world. When a user in Tokyo requests your app, CloudFront serves it from a cache in Tokyo rather than from a server in Virginia. That means faster load times globally. CloudFront also handles HTTPS termination, so your users get a secure connection without you needing to manage SSL certificates on every server. Together, Route 53 and CloudFront are how your app gets a real domain and loads fast for users everywhere."

SLIDE 18: Elastic Beanstalk
Content: PaaS layer over EC2 | Supported platforms | What it manages for you | Deploy via zip or CLI
SCRIPT:
"Elastic Beanstalk is AWS's PaaS offering. You give it your application code, tell it what platform you're using — Node.js, Python, Java, Docker, Ruby, and others — and it handles provisioning EC2 instances, load balancers, auto scaling, security groups, and health monitoring automatically.
Under the hood it's still EC2 and the other services we've already talked about. Beanstalk just removes the manual setup and wires everything together for you. You can deploy by uploading a zip file or using the EB command-line tool.
The trade-off is less control over the individual pieces. For most standard web applications that's a fine trade — you get a working, scalable deployment without deep AWS expertise. For specialized infrastructure requirements you'll want to manage things yourself, but for getting an application deployed quickly, Beanstalk is hard to beat."

SLIDE 19: Amazon RDS — Relational Database Service
Content: Managed relational database | Supported engines: MySQL, Postgres, Aurora, SQL Server, Oracle | Multi-AZ explained | Automated backups and patching
SCRIPT:
"RDS is managed relational databases. Instead of spinning up an EC2 instance, installing PostgreSQL yourself, and then being responsible for backups, patches, and failover — RDS handles all of that for you.
You pick your database engine, your instance size, your storage, and whether you want Multi-AZ deployment. Multi-AZ means AWS maintains a synchronous standby replica in a different Availability Zone. If the primary instance fails, RDS automatically promotes the standby — typically in under two minutes — and your application reconnects without any changes to its connection string.
RDS also handles automated backups on a schedule you define and applies software patches during maintenance windows. The trade-off is less access — you can't SSH into an RDS instance the way you can an EC2 instance. But for production databases, that is absolutely the right trade. You want a managed service handling your database infrastructure, not a server you have to babysit."

SLIDE 20: Containers on AWS — The Mental Map
Content: Container vs VM diagram | ECR → ECS/Fargate relationships | EKS one-liner | Quick overview before detail
SCRIPT:
"AWS has several container services and they can be confusing at first, so let me give you the mental map before we cover each one.
ECR — Elastic Container Registry — is just Docker Hub but hosted by AWS. You push your container images there. That's its whole job.
ECS — Elastic Container Service — is AWS's own container orchestration. You tell it what containers to run, how many, and with what resources, and it handles placing and running them.
EKS — Elastic Kubernetes Service — is managed Kubernetes. Choose it if your team already uses Kubernetes or needs the Kubernetes ecosystem specifically. Otherwise ECS is simpler and a better starting point.
Fargate is the serverless compute engine for containers. Instead of managing EC2 instances that your containers run on, Fargate runs your containers directly and you pay only for what they actually use.
Short version: ECR stores images. ECS or EKS orchestrates them. Fargate removes the server management entirely."

SLIDE 21: ECS — Elastic Container Service
Content: Task Definition → Service → Cluster | Launch types: EC2 vs Fargate | Key terms defined
SCRIPT:
"ECS has three core concepts. A Task Definition is the blueprint for your container — it specifies what image to run, how much CPU and memory to allocate, what ports to expose, environment variables, and so on. Think of it like a Docker Compose file.
A Service maintains a desired number of running tasks. If a task crashes, the service starts a replacement. Services also integrate with your Application Load Balancer so traffic is automatically routed to healthy tasks.
A Cluster is the logical grouping of your tasks and services. If you use the EC2 launch type, the cluster contains EC2 instances and your tasks run on them. If you use Fargate, AWS manages the underlying compute entirely.
ECS is a good starting point for most teams deploying containers on AWS. It's simpler than EKS and integrates cleanly with the rest of the AWS ecosystem."

SLIDE 22: AWS Fargate
Content: Serverless containers | No EC2 to manage | Pay per vCPU/memory per second | Works with ECS
SCRIPT:
"Fargate deserves its own explanation because it genuinely changes the operational model for containers. Without Fargate, even though ECS manages your containers, someone still has to manage the EC2 instances those containers actually run on — patching the OS, managing capacity, updating the AMI, all of it.
With Fargate, you define your container's CPU and memory requirements, and AWS figures out where to physically run it. You never interact with a server. You pay only for what your container uses, billed per second.
This is particularly valuable for teams that want to minimize operations overhead, for microservices architectures where you might have many small services that don't individually justify dedicated servers, and for workloads that are irregular or unpredictable. Fargate lets you focus entirely on the application."

SLIDE 23: AWS Lambda — Serverless Functions
Content: Event-driven execution | Supported runtimes | 15-minute max | Pay per invocation | Common triggers
SCRIPT:
"Lambda is serverless at the function level. You write a function in Python, Node.js, Java, Go, or several other runtimes, upload it, and Lambda runs it in response to events. No servers to provision, no scaling to configure — Lambda handles all of that automatically.
Common triggers: an HTTP request through API Gateway, a file uploaded to S3, a message arriving in a queue, a scheduled time-based event, or a change in a database. Lambda responds to the event, runs your function, and shuts down.
You pay per invocation and per millisecond of execution time. If nothing triggers your function, you pay nothing. This makes Lambda extremely cost-effective for irregular workloads.
The constraints to know: functions can run for at most 15 minutes, and there's a brief cold start delay when a function that hasn't been invoked recently gets called for the first time. Lambda is excellent for event-driven tasks, lightweight API backends, and glue code that connects services. It's not suited for long-running processes."

SLIDE 24: SNS and SQS — Messaging Services (TRIMMED TO AWARENESS LEVEL)
Content: SNS = pub/sub notifications | SQS = message queue | Common pattern: SNS → SQS → Lambda | Decoupling concept
SCRIPT:
"SNS and SQS are AWS's managed messaging services, and they're the backbone of event-driven architecture on AWS. You don't need to go deep on these today, but you should know what they are and why they exist.
SNS — Simple Notification Service — is a pub/sub system. You publish a message to a topic and all subscribers receive it. One message, many recipients.
SQS — Simple Queue Service — is a message queue. Producers put messages in, consumers pull messages out and process them at their own pace. If your consumer goes down, the messages wait in the queue until it recovers.
The reason these matter: they let different parts of your system communicate without being directly dependent on each other. That decoupling is what makes distributed systems resilient. You'll encounter these when you start building multi-service architectures."

SLIDE 25: DynamoDB (TRIMMED TO AWARENESS LEVEL)
Content: Managed NoSQL database | Fully serverless | Single-digit millisecond latency | Best for high-volume, low-complexity workloads
SCRIPT:
"DynamoDB is AWS's managed NoSQL database. It's fully serverless — no instances to provision — and built for high throughput and low latency at any scale. Amazon's own shopping cart runs on DynamoDB.
Unlike RDS, DynamoDB doesn't use SQL or support complex joins. It stores data as flexible items rather than rigid rows. This makes it a poor fit for highly relational data, but an excellent fit for high-volume, lower-complexity workloads: session data, user profiles, leaderboards, IoT event data. If your data doesn't need the relational model, DynamoDB is worth knowing about."

SLIDE 26: AWS IAM — Identity and Access Management (Part 1)
Content: Users | Groups | Roles | Policies | JSON policy structure overview
SCRIPT:
"IAM is how you control who can do what in your AWS account. It might be the most important service to understand well, because misconfigured IAM is the source of most AWS security incidents.
The four core concepts. Users are individual identities — a person or an application that needs to interact with AWS. Groups are collections of users that share the same permissions — an 'Developers' group, an 'Ops' group. Roles are identities that can be assumed temporarily — an EC2 instance can assume a role to gain permission to read from S3, or a Lambda function can assume a role to write to DynamoDB. Policies are JSON documents that define what actions are allowed or denied on what resources. Policies get attached to users, groups, or roles.
IAM is global — it's not specific to any Region. Everything you do in AWS flows through IAM."

SLIDE 27: AWS IAM — Security Best Practices (Part 2)
Content: Principle of least privilege | Lock the root account | Use MFA | Never use root for day-to-day work
SCRIPT:
"A few IAM rules that should become second nature.
The principle of least privilege: grant only the permissions actually needed and nothing more. If your EC2 instance needs to read from one specific S3 bucket, give it read access to that one bucket — not full S3 access, and definitely not full admin access. The blast radius of a compromised credential is exactly as large as the permissions attached to it.
The root account of your AWS account — the one tied to the email address you used to sign up — has unrestricted access to everything. It should be locked away: enable multi-factor authentication on it, don't create access keys for it, and don't use it for day-to-day work. Create IAM users for everything instead. If the root account is ever compromised, you have no higher authority to recover from. Treat it accordingly."

SLIDE 28: CloudWatch — Monitoring and Logging
Content: Metrics | Logs | Alarms | Dashboards | Integration across AWS services
SCRIPT:
"CloudWatch is AWS's observability service. Nearly every AWS service automatically sends metrics to CloudWatch — EC2 CPU usage, RDS connection count, Lambda invocation errors, ECS memory utilization. You can visualize these on dashboards and set alarms that fire when a metric crosses a threshold.
CloudWatch Logs is where application logs live. EC2 instances can ship logs via the CloudWatch agent. Lambda automatically logs to CloudWatch. ECS routes container logs there. You can search logs, create metric filters to extract numeric data from log lines, and set alarms on specific log patterns.
A good CloudWatch setup is what lets you know your application has a problem before your users start complaining. Don't wait until you're in production to think about monitoring — set up alarms and dashboards early. And set a billing alarm too: a CloudWatch alarm on your account's estimated charges is one of the first things to configure in any AWS account."

SLIDE 29: AWS Secrets Manager (NEW)
Content: Stores sensitive configuration (passwords, API keys, tokens) | Injected at runtime | Access controlled by IAM | Rotation support
SCRIPT:
"Secrets Manager is where you store sensitive configuration values — database passwords, API keys, third-party service tokens — anything that should never be hardcoded in your application or checked into source control.
Instead of putting a database password in an environment variable or a config file, you store it in Secrets Manager and grant your application's IAM role permission to retrieve it at runtime. Your code fetches the secret by name, and Secrets Manager returns the value. The actual credentials never live in your codebase.
Secrets Manager also supports automatic rotation — it can periodically change a database password and update the stored value automatically, without downtime. This is the right way to handle credentials in any production application."

SLIDE 30: Full-Stack Deployment on AWS — Revisited
Content: Same architecture diagram as Slide 6: Route53 → CloudFront → ALB → ECS/Fargate → RDS → S3 | Each service labeled with its role
SCRIPT:
"Let's come back to the architecture diagram we opened with. It should look very different now.
Your frontend — a React app — is built and uploaded to S3, served through CloudFront for caching and HTTPS globally. Route 53 points your domain at CloudFront.
Your API backend is deployed as Docker containers on ECS with Fargate, sitting behind an Application Load Balancer. An Auto Scaling policy adjusts the number of containers based on load.
Your database is RDS PostgreSQL in a private subnet, Multi-AZ for reliability, accessible only to your ECS containers — not from the public internet.
Database credentials and API keys are stored in Secrets Manager and retrieved by your containers at runtime. IAM roles give each component exactly the permissions it needs and nothing more.
CloudWatch collects logs and metrics from every layer. Alarms notify your team via SNS if something goes wrong.
At the start of this lecture this was just a diagram with labels. Every piece of it now has a story."

SLIDE 31: Recap — The Mental Model
Content: Grouped summary: Compute | Storage | Database | Containers | Serverless | Security | Messaging | Monitoring | Networking & Delivery
SCRIPT:
"Let's close with a clean mental model organized by category.
Compute: EC2 for virtual machines, Auto Scaling Groups to scale them, Elastic Beanstalk to deploy without managing servers manually.
Storage: S3 for object storage and static websites, EBS for block storage attached to EC2.
Databases: RDS for managed relational databases, DynamoDB for managed NoSQL.
Containers: ECR to store images, ECS to orchestrate them, Fargate to remove server management, EKS if you need Kubernetes.
Serverless: Lambda for event-driven functions.
Security: IAM for access control and identity, Secrets Manager for credentials, Security Groups for instance-level firewalls.
Messaging: SNS for pub/sub, SQS for queues.
Monitoring: CloudWatch for metrics, logs, and alarms.
Networking and Delivery: VPC for your private network, Route 53 for DNS, CloudFront for global caching and HTTPS, ALB for load balancing.
If you can explain what each of these does and when you'd reach for it, you're in excellent shape. The next step is hands-on time — which is exactly what the labs are for."

SLIDE 32: What's Next
Content: Lab exercises listed | Resources: AWS Free Tier, AWS docs, AWS Skill Builder | Billing alarm reminder | Free Tier limits
SCRIPT:
"Everything we covered today has a free tier component. EC2 t2.micro, S3, Lambda, RDS — AWS gives you enough to build and experiment for free for 12 months. Use it.
The best way to learn AWS is to deploy things in a safe environment and break them intentionally. Spin up an EC2 instance. Deploy something to Beanstalk. Put a static site on S3. Set up an RDS database. Connect services together. Each time you do it, the relationships between the pieces get clearer.
One important habit: set up a billing alarm in CloudWatch for a low threshold — ten dollars is a good starting point — so you're notified before any unexpected charges accumulate. And when you're done with practice resources, delete them. Idle resources in AWS still cost money.
Any questions before we move into the lab?"