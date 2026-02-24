# Day 40 — AWS Part 1: Speaking Script
# Cloud Computing Fundamentals, Global Infrastructure, EC2, Security Groups, AMIs, EBS, Auto Scaling Groups
# Estimated time: ~90 minutes
# Files: 01-aws-cloud-fundamentals-and-ec2.md | 02-ec2-and-infrastructure-cli.sh

---

## OPENING — The Cloud Revolution (5 minutes)

**Say:**
"Happy Friday! Last week we deployed microservices and talked about Docker and Kubernetes. Today is the day everything comes together — because today we're talking about AWS, the platform that runs a significant chunk of the modern internet.

Quick question before we start: How many apps have you used today? Your phone's weather app, Slack, maybe Netflix before bed, any banking app, probably GitHub at some point. The vast majority of those — literally billions of requests per day — run on Amazon Web Services.

Netflix? AWS. Airbnb? AWS. LinkedIn? AWS. The US government? AWS. Even Apple uses AWS for some iCloud storage.

We've spent 8 weeks building a bookstore application — Spring Boot backend, React or Angular frontend, PostgreSQL database, Kafka for messaging, Docker containers. Today you're going to learn how to put ALL of that into the cloud. Not a toy demo environment. The actual platform professionals use every single day."

**Ask the class:**
"Before we start — raise your hand if you've ever used AWS before, even just created a free account. [Pause] And raise your hand if you've heard of EC2. [Pause] Great — today we're going from 'heard of it' to 'actually deploying on it.'"

---

## SECTION 1 — Cloud Computing Fundamentals: IaaS, PaaS, SaaS (12 minutes)

**[Open: 01-aws-cloud-fundamentals-and-ec2.md → SECTION 1]**

**Before showing the file, ask:**
"Here's a question: if you wanted to host your bookstore application right now — not on your laptop, but accessible to the whole world — what would you need?"

*[Let students answer: server, database, network, domain name, etc.]*

"That's exactly right. In the old days — pre-2006 — a startup wanting to host a website would go to a data center, literally carry servers into a building, rack them, run cables, configure the network, install the OS, install software, THEN finally write code. It could take months and cost hundreds of thousands of dollars.

Then Jeff Bezos had an idea."

**[Show Section 1 — the service model table]**

**Say:**
"Cloud computing gives you three ways to use someone else's infrastructure. Let's look at the table I've drawn here — this is the most important mental model for today.

Think of it like a restaurant analogy."

**Walk through the table slowly:**

"**SaaS — Software as a Service** is like ordering delivery. You don't touch the kitchen, you don't touch the ingredients, you don't even own the plates. You just consume the final product. Gmail is SaaS — Google manages literally everything, you just use the interface. GitHub is SaaS. Slack is SaaS.

**PaaS — Platform as a Service** is like renting a fully-equipped kitchen. The kitchen is there, the stove works, the oven works, the utensils are there. You bring your own ingredients and cook. In AWS terms, Elastic Beanstalk is PaaS — Amazon manages the OS, the Java runtime, the load balancer, the scaling. You just upload your Spring Boot JAR file.

**IaaS — Infrastructure as a Service** is like leasing an empty commercial kitchen space. Four walls, electricity, plumbing. You bring everything else — the stove, the equipment, the ingredients, the staff. That's EC2. Amazon gives you a virtual server. You install the OS, install Java, install your app, manage everything.

[Scan the room]

**⚠️ Watch out:** This is a common interview question. Students sometimes mix up PaaS and IaaS. Remember the key difference: with IaaS, you manage the operating system. With PaaS, the provider manages everything up to the runtime — you just deploy code."

**Show the bookstore table:**

"Now look at this table mapping our bookstore to each model. See how EC2 is IaaS — you manage the OS and runtime? See how Beanstalk is PaaS — AWS manages all of that? And see how SES for email is actually closer to SaaS?

In real projects, you'll use a mix. You might use S3 for storage (SaaS-like), RDS for the database (PaaS-like), and EC2 for compute (IaaS). AWS gives you choices.

**Ask:** Why might a company choose IaaS over PaaS even though PaaS is easier? [Pause for answers: control, customization, compliance, legacy apps, cost optimization for large scale.]

Exactly. Netflix uses EC2, not Beanstalk, because they need fine-grained control over every aspect of performance at their scale. A startup doing a quick MVP might use Beanstalk to ship faster."

---

## SECTION 2 — AWS Overview and Global Infrastructure (10 minutes)

**[Scroll to SECTION 2]**

**Say:**
"Now let's talk about the scale of AWS. Look at these service categories. When people say '200+ AWS services,' this is what they mean.

Compute, storage, databases, networking, messaging, security, monitoring, developer tools, AI/ML, containers — AWS has a managed service for almost everything. You could build your entire bookstore platform — frontend CDN, backend servers, database, cache, messaging queue, email, monitoring, authentication — all within AWS. That's why companies get 'all in' on it.

Now let's understand the physical geography."

**[Show the global infrastructure diagram]**

**Say:**
"AWS infrastructure has three levels. Let me walk you through them from big to small.

**Regions** are the largest unit — a separate geographic area. There are 33 AWS regions. When you log in to AWS console and pick 'US East (N. Virginia),' you're picking a region. That region is code-named us-east-1. eu-west-1 is Ireland. ap-northeast-1 is Tokyo.

**⚠️ Critical point:** Regions are completely independent. If you launch an EC2 instance in us-east-1, it doesn't automatically exist in eu-west-1. Data doesn't automatically replicate between regions. This matters for compliance — if you're storing EU customer data, GDPR might require you keep it in eu-central-1 (Frankfurt) and NOT let it leave Europe.

**Availability Zones** are the next level down. Each region has at least 2 AZs, most have 3-4. us-east-1 has 6 AZs: 1a through 1f.

[Point to the diagram]

Each AZ is one or more physical data centers with separate power, separate cooling, separate physical security. They're connected to each other within the region by ultra-low-latency private fiber links. They're far enough apart that a natural disaster hitting one AZ won't affect the others.

Here's why this matters for us: if we run our bookstore on a single EC2 instance in a single AZ and that data center has a power outage — our site is down. But if we run EC2 instances in 1a AND 1b behind a load balancer, and 1a goes down, traffic automatically shifts to 1b. That's high availability."

**Ask the class:**
"What's the difference between a region and an availability zone? Turn to the person next to you and explain it in one sentence. [30 seconds] Good. Regions are geographic areas. AZs are data centers within a region."

**[Show the Shared Responsibility Model box]**

"Before we move on — this is critical. The Shared Responsibility Model defines who's responsible for what. AWS is responsible for the physical security, the hardware, the hypervisor, the network infrastructure. YOU are responsible for your OS, your application, your IAM configuration, encrypting your data, your Security Groups.

Think of it like an apartment building. The landlord maintains the physical building, the electrical system, the plumbing. You're responsible for locking your apartment door, installing a smoke alarm inside your unit, not leaving your window open. AWS is the landlord. You're the tenant."

---

## SECTION 3 — AWS EC2 Deep Dive (10 minutes)

**[Scroll to SECTION 4 — AWS EC2]**

**Say:**
"EC2 — Elastic Compute Cloud — is the heart of AWS. It's Amazon's IaaS compute service. An EC2 instance is a virtual machine — a piece of software that behaves like a physical computer. The physical host has many virtual machines running on it. You get a slice of that hardware.

Let's look at instance types."

**[Show instance type table]**

"AWS organizes instances into families. The letter tells you the specialty, the number tells you the generation, the size tells you how big.

t3.micro — this is our free tier instance. 2 vCPU, 1 GB RAM. Perfect for development and testing. 750 hours free per month for your first 12 months. That's enough to run one instance 24/7 all month for free.

The 't' family is 'burstable.' It means it normally runs at low CPU, but can burst to full CPU when needed — perfect for development where you have occasional traffic.

The 'm' family is 'general purpose' with a good balance of CPU and memory. m6i.large is a common production choice for our bookstore backend.

The 'c' family is compute-optimized — more CPU per dollar. Good for things like media transcoding or high-traffic APIs.

The 'r' family is memory-optimized — more RAM per dollar. Good for in-memory caches or databases.

**⚠️ Watch out:** Students sometimes think bigger is always better. In cloud, you want right-sizing — use the smallest instance that meets your needs. An m6i.4xlarge costs 8x more than an m6i.large. Start small, measure, scale up if needed.

Now look at pricing. On-demand pricing is the 'no commitment' price. Reserved instances are like a 1 or 3-year contract — you commit to usage and get up to 60% off. Spot instances let you bid on unused AWS capacity — up to 90% off, but AWS can reclaim them with 2 minutes notice. Great for batch jobs, terrible for customer-facing apps."

---

## SECTION 4 — Launching EC2 Instances (10 minutes)

**[Scroll to SECTION 5 — Launching and Configuring]**

**Say:**
"When you launch an EC2 instance, you configure seven things. Let me walk through each one because they all matter.

[Read through the checklist]

**1. AMI — Amazon Machine Image.** This is your starting template. Like a disk image. Think of it as 'which pre-configured server do I want to start from?' We'll use Amazon Linux 2023 for most things — it's AWS's own optimized Linux distribution. It's free, well-supported, and has AWS tools pre-installed.

**2. Instance Type.** We just covered this — how much CPU and RAM.

**3. Key Pair.** This is how you SSH into the instance. AWS generates a public/private key pair. The public key goes on the server. You get the private key as a .pem file. You MUST download this at creation time — you cannot get it again. Keep it like a password.

**⚠️ Watch out:** This is one of the most common beginner mistakes. Students lose their .pem file and can't SSH in anymore. There's no 'forgot my password.' You have to terminate the instance and start over.

**4. Network/VPC.** Virtual Private Cloud — your isolated network in AWS. For learning, use the default VPC. For production, you'd create a custom VPC with public and private subnets.

**5. Security Group.** Virtual firewall — we'll cover this in detail next.

**6. Storage (EBS).** How much disk space. Default root volume is 8 GB — usually fine for OS + app.

**7. User Data.** A script that runs automatically on the FIRST BOOT. This is how we automate server configuration."

**[Show the User Data bootstrap script]**

"Look at this User Data script for our bookstore. When the EC2 instance boots for the first time, AWS automatically runs this script as root.

Line by line:
- `yum update -y` — update all OS packages. Always do this on a fresh server.
- `yum install -y java-21-amazon-corretto` — install Java 21. Notice we're using Amazon Corretto, Amazon's free, production-ready OpenJDK distribution.
- `useradd -m -s /bin/bash bookstore` — create a dedicated app user. Never run your application as root.
- The systemd service block — this registers our bookstore app as a system service that starts automatically on reboot and restarts if it crashes.
- `systemctl enable bookstore` — enable the service on startup.

This is infrastructure as code — instead of manually SSHing in and typing commands, we write a script that configures the server automatically every time."

---

## SECTION 5 — Security Groups and Network ACLs (12 minutes)

**[Scroll to SECTION 6]**

**Say:**
"Security Groups are one of the most important concepts in AWS. They're your first line of defense for protecting your EC2 instances. Let's understand the two layers."

**[Point to the nested diagram]**

"Think of AWS networking like this: the internet connects to your VPC. Inside the VPC, there are subnets — network segments. Network ACLs sit at the subnet boundary. Security Groups sit at the instance boundary.

Traffic from the internet has to pass through the Network ACL first, then through the Security Group, before it reaches your EC2 instance. Two layers of protection."

**[Show the Security Groups section]**

"Security Groups work on a **whitelist model** — by default, ALL inbound traffic is blocked. You add rules to explicitly allow what you want.

Look at our bookstore security group rules:
- Port 22 (SSH) — ONLY from my IP address. Not from 0.0.0.0/0. This is critical.
- Port 80 (HTTP) — from anywhere. The internet needs to reach our bookstore.
- Port 443 (HTTPS) — from anywhere. Secure web traffic.
- Port 8080 (Spring Boot) — from anywhere. For direct dev access.
- Port 5432 (PostgreSQL) — from our app server's security group. Not from the internet!

That last rule is important. Our database EC2 (or RDS instance) should NOT have port 5432 open to the internet — only to the app servers. We do this by referencing a security group ID as the source instead of a CIDR range.

**⚠️ Critical security rule:** NEVER set SSH (port 22) to 0.0.0.0/0 in production. That means anyone in the world can attempt to SSH in. Attackers scan the internet constantly for open SSH ports. Restrict it to your IP only, or use AWS Systems Manager Session Manager instead of SSH entirely.

Security Groups are **stateful.** If you allow inbound traffic on port 80, the response traffic is automatically allowed outbound — you don't need an explicit outbound rule for responses."

**[Show the Network ACLs section]**

"Network ACLs are different from Security Groups in three important ways:

**1. Level:** Network ACLs are at the subnet level, Security Groups are at the instance level.

**2. Stateless:** Network ACLs are stateless — they don't track connections. If you allow inbound HTTP on port 80, you must ALSO add an outbound rule to allow the ephemeral response ports (1024-65535). This trips people up.

**3. Deny rules:** Security Groups can only ALLOW traffic. Network ACLs can ALLOW and DENY. You can use a Network ACL to explicitly block a specific IP address that's attacking your site.

**Ask:** Which one should you configure first when setting up a new server? [Pause] Both — but Security Groups are the primary defense. NACLs are an additional layer for subnet-level policies. In most setups, you'll spend 90% of your time on Security Groups."

---

## SECTION 6 — SSH into EC2 Instances (8 minutes)

**[Open: 02-ec2-and-infrastructure-cli.sh]**

**Say:**
"Now let's actually DO this. I'm switching to our CLI script. We'll do this step by step.

Before we run any commands, let me explain how SSH works with EC2."

**[Show Section 7 in the .md file — the SSH diagram]**

"When you create a key pair, AWS stores the public key on your instance inside `~/.ssh/authorized_keys`. You get the private key — that's your `.pem` file. When you SSH, your SSH client uses the private key to prove your identity. The server checks it against the stored public key. If they match, you're in.

The key file permissions are crucial. SSH will REFUSE to use a key file that's readable by other users — it's a security check. That's why we run `chmod 400` — owner read-only."

**[Show the SSH section in the .sh file]**

"Walk through these commands with me:

```bash
chmod 400 bookstore-keypair.pem
```
This sets the file to read-only for the owner. Required before every first SSH.

```bash
ssh -i bookstore-keypair.pem ec2-user@IP
```
`-i` specifies the identity file (your private key). `ec2-user` is the default username for Amazon Linux 2023.

**⚠️ Watch out:** The username depends on the AMI. Amazon Linux = `ec2-user`. Ubuntu = `ubuntu`. This is a very common error — students try `ec2-user` on Ubuntu and get 'Permission denied.' Always check the AMI documentation for the default username."

**[Show the SCP commands]**

"SCP is SSH's file transfer protocol. Same key, same syntax — just `scp` instead of `ssh`. This is how you manually push a JAR file to an EC2 instance during development.

In production, you wouldn't do this manually — you'd use CodeDeploy or Elastic Beanstalk for automated deployments. But knowing SCP is essential for debugging.

Port forwarding is a handy trick for testing. With `-L 8080:localhost:8080`, your laptop's port 8080 tunnels through the SSH connection to the server's port 8080. You can test your Spring Boot app from your browser as if it's running locally — without opening port 8080 to the internet."

---

## SECTION 7 — Amazon Machine Images (10 minutes)

**[Back to 01-aws-cloud-fundamentals-and-ec2.md, SECTION 8]**

**Say:**
"We've launched an EC2 instance and configured it. Now imagine you need to launch 20 more identical instances — maybe because Black Friday is coming and you're scaling up your bookstore. Do you want to install Java 21, configure your app, set up systemd, install CloudWatch agent... 20 times? Absolutely not.

That's what AMIs are for."

**[Show the AMI diagram]**

"An AMI — Amazon Machine Image — is a snapshot of an entire server configuration. It contains the root volume snapshot (OS + software), launch permissions (who can use it), and the block device mapping.

When you launch an EC2 instance, you're essentially copying an AMI and starting it up. All Amazon Linux 2 instances everywhere start from the same base AMI.

The real power is creating YOUR OWN AMIs — what we call a 'golden image.'

[Show the workflow]

The process is: Start with a base AMI → launch an instance → SSH in → install and configure everything exactly right → create an AMI from that configured instance → use THAT AMI for all future launches.

Look at the comparison I've written:

Without custom AMI: launch → User Data runs → 4-5 minutes to get ready
With custom AMI: launch → 60 seconds to ready

**Why does this matter?** Auto Scaling. When your bookstore gets a traffic spike at 2pm, Auto Scaling tries to add instances. If each instance takes 5 minutes to bootstrap, your users are getting errors for 5 minutes while new capacity comes online. With a golden AMI, new instances are ready in under a minute."

**[Show the CLI commands for creating an AMI]**

"Look at `aws ec2 create-image`. This is how you programmatically snapshot a configured instance into an AMI.

The `--no-reboot` flag tells AWS to take the snapshot without stopping the instance first. It's faster but potentially slightly inconsistent — like taking a snapshot of a running database. For a clean snapshot, let AWS stop the instance first.

Notice the naming convention: `bookstore-api-$(date +%Y%m%d-%H%M)`. Always include a timestamp in AMI names — you'll thank yourself later when you're looking at 20 AMIs and need to know which is newest.

**Ask:** What's the difference between an AMI and an EBS Snapshot? [Pause for answers]

Good distinction. An EBS Snapshot is just a backup of a single volume. An AMI wraps one or more snapshots with metadata — instance type recommendations, permissions, block device mappings — into a complete server template. You launch instances from AMIs. You restore data from snapshots."

---

## SECTION 8 — Elastic Block Store (10 minutes)

**[Scroll to SECTION 9]**

**Say:**
"When you launch an EC2 instance, it has storage. That storage — in most cases — is EBS: Elastic Block Store.

EBS is AWS's virtual hard drive service. When you attach an EBS volume to an EC2 instance, it behaves exactly like a physical hard drive — the operating system sees a block device. You format it, mount it, read and write files. Except it's actually a high-performance network-attached storage system."

**[Show the volume types table]**

"There are several EBS volume types. In 2024, the answer for 'which type should I use?' is almost always **gp3** — General Purpose SSD v3. It's cheaper per GB than gp2 and better in every way. gp3 gives you 3000 IOPS baseline and 125 MB/s throughput by default, and you can increase those independently.

For our bookstore:
- Root volume: gp3, 20 GB — OS + Spring Boot JAR
- Data volume: gp3, 100 GB — book cover images, user uploads
- RDS instance: might use io2 for provisioned IOPS if the database needs consistent low-latency

For cost-sensitive archival storage, sc1 (cold HDD) at $0.015/GB is excellent — think once-a-month audit logs.

**⚠️ Watch out:** EBS volumes are tied to an Availability Zone. You cannot directly attach a volume in us-east-1a to an instance in us-east-1b. To move data, you create a snapshot → restore in the new AZ. This is also how you cross regions."

**[Show the Key Concepts section]**

"Five things to know about EBS:

1. **Persistence** — EBS data survives instance stop/start. If you stop an EC2, your data on EBS is still there. This is different from Instance Store, which is wiped when the instance stops.

2. **Snapshots** — point-in-time backups stored in S3. Incremental — only blocks that changed since the last snapshot are stored. Cheap, fast, reliable.

3. **Encryption** — always enable it. Uses AWS KMS. Check 'encrypt this volume' when creating. You can't encrypt an existing unencrypted volume in-place — you have to snapshot it and restore encrypted.

4. **Multi-Attach** — io1 and io2 can attach to multiple instances at once. Used for clustered databases. Rarely needed for most applications.

5. **Root vs Data volumes** — by default, the root volume is deleted when you terminate an instance. Data volumes persist. For production, consider setting 'Delete on termination = false' for root volumes too.

**Ask:** What's the difference between stopping and terminating an EC2 instance? [Pause] Stopping is like shutting down your laptop — data preserved, you can restart. Terminating is like throwing away your laptop — instance gone, root volume deleted by default. This is a common mistake that costs people their data."

---

## SECTION 9 — Auto Scaling Groups (12 minutes)

**[Scroll to SECTION 10]**

**Say:**
"Last topic for Part 1, and it's a big one. Auto Scaling Groups — or ASGs — are how AWS handles the 'we can't predict traffic' problem.

Let me tell you a story. Imagine it's November 20th. You're running your bookstore on 1 EC2 instance, CPU running at 20%. Life is good. Then Black Friday hits — 50,000 users hit your site at once. Your single instance's CPU goes to 100%. Response times go from 200ms to 30 seconds. Eventually the server crashes. Your customers are getting errors. You lose sales.

Option A: Always run 10 instances 'just in case.' You're paying for 10 instances 364 days a year when you only need them 1 day a year. Expensive.

Option B: Auto Scaling Group — automatically add instances when needed, automatically remove them when not. Pay only for what you use."

**[Show the ASG architecture diagram]**

"Look at this diagram. The ASG manages a fleet of EC2 instances across multiple AZs. It has three critical numbers:

- **Min** — minimum number of instances. ALWAYS at least 1, usually 2 for production (one per AZ for high availability)
- **Max** — maximum number. The ceiling. Set this so you don't get a surprise bill if something goes wrong.
- **Desired** — where you want to be right now. The ASG works to maintain this count, replacing unhealthy instances automatically.

The ASG sits behind a Load Balancer. Traffic comes in, the load balancer distributes it across all healthy instances in the ASG."

**[Show the three ASG components]**

"Three components make up an ASG:

**Launch Template** — the blueprint. It specifies what each new instance should look like: which AMI, which instance type, which key pair, which security group, which IAM role, what user data script. Every new instance launched by the ASG uses this template.

**Scaling Policies** — when to scale. Look at this table. Target Tracking is the easiest and most powerful: 'Keep average CPU at 60%.' ASG automatically adds instances when CPU goes above 60%, removes them when it drops well below. Step scaling lets you define multiple steps — if CPU is 71-80% add 1 instance, if 81-90% add 2, if 91-100% add 5. Scheduled scaling is for predictable patterns — scale up at 9am every weekday before your users arrive.

**Health Checks** — when to replace. This is subtle but critical. EC2-level health checks only check whether the virtual machine is responding to hardware checks. But your Spring Boot app could be running, throwing 500 errors on every request, and EC2 health check still says 'healthy.' ELB health checks actually check your app — they hit `/actuator/health` and check for a 200 response. If your app is broken, ELB marks the instance unhealthy, ASG terminates it and launches a replacement."

**[Show Target Tracking policy example]**

"Look at how simple Target Tracking is. Just tell AWS 'keep CPU near 60%.' That's it. AWS figures out when to add and remove instances. This is the recommended scaling policy for most applications.

Scheduled scaling is for when you know your traffic patterns. If you know your bookstore sees a spike every weekday morning when people browse books over coffee, schedule scale-up at 8:45am and scale-down at 7pm.

**Ask:** Why 60% CPU and not 80% or 90% as the target? [Pause for answers] Good thinking. If you target 90% and traffic spikes, you're already at 90% — by the time new instances launch (which takes 1-2 minutes), you might be at 100% and serving errors. By targeting 60%, you have a 40% buffer. New instances come online before you're overwhelmed."

**[Show the CLI section — SECTION 10]**

"Let me walk you through the CLI commands quickly. In Section 10 of the shell script, we:

1. Create a Launch Template with `aws ec2 create-launch-template` — passing JSON configuration
2. Get the subnet IDs for multi-AZ deployment
3. Create the ASG with `aws autoscaling create-auto-scaling-group` — min 1, max 4, desired 2, spread across multiple subnets
4. Add a Target Tracking policy targeting 60% CPU
5. Add scheduled actions for morning scale-up and evening scale-down

The commands for working with an existing ASG are equally important:
- `describe-auto-scaling-groups` — check current state
- `set-desired-capacity` — manually force a scale event
- `describe-scaling-activities` — audit trail of why instances were added or removed

The scaling activities log is gold for debugging. It tells you: 'instance terminated because health check failed' or 'instance launched because target tracking policy triggered.'"

---

## CLOSING — Part 1 Summary (3 minutes)

**[Show the Quick Reference at the bottom of the .md file]**

**Say:**
"Let's tie Part 1 together with the cheat sheet. In one column we have the concepts, in the other the key facts.

What we covered today in Part 1:
- **IaaS vs PaaS vs SaaS** — know the difference, know which AWS service is which
- **Global Infrastructure** — Regions and AZs, data sovereignty, high availability across AZs
- **EC2** — virtual servers, instance families, pricing models
- **Launching instances** — the 7 configuration components, User Data for automation
- **Security Groups** — virtual firewall, stateful, whitelist model
- **Network ACLs** — subnet level, stateless, allow and deny
- **SSH** — key pairs, chmod 400, default usernames per AMI
- **AMIs** — server templates, golden images, fast scaling
- **EBS** — persistent block storage, gp3 is the right choice, snapshots
- **Auto Scaling Groups** — fleet management, min/max/desired, scaling policies

After the break we're going into Part 2 which covers the rest of AWS: S3, Elastic Beanstalk, RDS, containers on ECS and EKS, Lambda, messaging with SNS and SQS, monitoring with CloudWatch, IAM security, DynamoDB, and we'll finish by deploying our entire bookstore application to AWS.

Take 10 minutes. Grab water. When you come back we're deploying."

---

## TIMING GUIDE

| Section | Content | Time |
|---------|---------|------|
| Opening | Cloud revolution hook, class poll | 5 min |
| Section 1 | IaaS/PaaS/SaaS, service models | 12 min |
| Section 2 | AWS overview, global infrastructure, Regions, AZs | 10 min |
| Section 3 | EC2 overview, instance types, pricing | 10 min |
| Section 4 | Launching instances, User Data | 10 min |
| Section 5 | Security Groups, Network ACLs | 12 min |
| Section 6 | SSH key pairs, CLI demo | 8 min |
| Section 7 | AMIs, golden images | 10 min |
| Section 8 | EBS volumes, types, snapshots | 10 min |
| Section 9 | Auto Scaling Groups | 12 min |
| Closing | Summary, part 1 wrap-up | 3 min |
| **Total** | | **~92 min** |

---

## INSTRUCTOR NOTES

1. **Free tier warning:** At the start, remind students to ONLY use t3.micro instances and remember to terminate/stop instances at the end of the day. Unexpected AWS bills from forgotten instances are a real student concern.

2. **AWS Console vs CLI:** Some students prefer the visual Console. The CLI is shown here because it's more teachable and scriptable, but acknowledge the Console is valid. Many professionals use a mix of both.

3. **Key pair loss:** Emphasize the key pair download step multiple times. Students who miss this step cannot SSH in. If it happens, show them how to create a new key pair and attach it using EC2 Instance Connect or Systems Manager.

4. **Region selection:** Tell students to use `us-east-1` for everything today. It has the most services and most tutorials use it. Mixing regions mid-demo will cause confusion.

5. **IAM setup:** If students don't have IAM users set up yet, walk them through it quickly: IAM → Users → Create user → AdministratorAccess policy → Create access keys → download credentials. This should have been done before class.

6. **Diagram-first approach:** For Section 4 (Architecture), draw the nested VPC/Subnet/SecurityGroup/Instance diagram on a whiteboard first before showing the file. Visual explanation lands better.

---

## FREQUENTLY ASKED QUESTIONS

**Q: Do I need a credit card for AWS free tier?**
A: Yes. AWS requires a credit card even for free tier. They charge a $1 temporary authorization. Free tier limits mean you won't be charged for 750 hours/month of t3.micro for the first 12 months.

**Q: What if I forget to terminate my EC2 instance?**
A: AWS will continue charging you. For a t3.micro it's about $0.0116/hour — ~$8.50/month if left running 24/7. Always terminate instances you're not using. You can set up AWS Budgets alerts to notify you when costs exceed a threshold.

**Q: Can I run Windows on EC2?**
A: Yes! There are Windows AMIs available. The default username for Windows EC2 is `Administrator` and you connect via RDP (port 3389) instead of SSH.

**Q: What's the difference between stopping and hibernating an EC2 instance?**
A: Stopping powers off the instance (preserves EBS, releases RAM). Hibernating saves the RAM contents to EBS before stopping, so when you start again, it resumes exactly where it left off — like laptop hibernate. Useful for instances with expensive in-memory state.
