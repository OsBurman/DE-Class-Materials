# Exercise 03: EBS Volumes and Auto Scaling Groups — Solution

## Part 1 — EBS Volume Types

### 1. EBS Type Comparison Table

| Type | Name | Max IOPS | Max Throughput | Best for |
|---|---|---|---|---|
| `gp3` | General Purpose SSD | 16,000 IOPS | 1,000 MB/s | General workloads, boot volumes, dev/test, most web apps |
| `io2` | Provisioned IOPS SSD (io2 Block Express) | 256,000 IOPS | 4,000 MB/s | High-performance databases (Oracle, SQL Server, SAP HANA) requiring consistent sub-millisecond latency |
| `st1` | Throughput Optimized HDD | 500 IOPS | 500 MB/s | Big data, data warehouses, log processing — sequential read/write at high throughput |
| `sc1` | Cold HDD | 250 IOPS | 250 MB/s | Infrequently accessed cold data; lowest cost EBS option |

### 2. Written Questions

**Root EBS volume on instance termination:**
By default, the root EBS volume has `DeleteOnTermination=true` — it is automatically deleted when the instance is terminated. To preserve it, change this setting either at launch time (`--block-device-mappings '[{"DeviceName":"/dev/xvda","Ebs":{"DeleteOnTermination":false}}]'`) or modify it on a running instance via `aws ec2 modify-instance-attribute`. Non-root EBS volumes attached at launch have `DeleteOnTermination=false` by default and persist after termination.

**EBS Snapshot:**
An EBS Snapshot is a point-in-time backup of an EBS volume stored in **Amazon S3** (managed by AWS; you don't see the bucket directly). Snapshots are incremental — only changed blocks since the last snapshot are stored.
Two use cases:
1. **Disaster recovery / backup**: restore a volume to a previous state after accidental deletion or data corruption.
2. **AMI creation / cross-region copying**: create a custom AMI from a snapshot (for Auto Scaling golden images) or copy a snapshot to another Region for multi-region deployments.

**EBS Multi-Attach:**
EBS Multi-Attach allows a single `io1` or `io2` EBS volume to be attached to **up to 16 EC2 instances simultaneously** (within the same AZ). Use case: clustered database applications (e.g., Oracle RAC) where multiple nodes need shared block storage. Limitation: **only available for io1/io2 volumes**; the instances must be in the same Availability Zone; and the application must manage concurrent writes (EBS does not handle distributed locking — you need a cluster-aware file system like GFS2 or OCFS2).

**100,000 IOPS at 256 KB blocks:**
Choose **io2 (Block Express)**. At 256 KB per I/O, 100,000 IOPS = 25,600 MB/s theoretical — but in practice, you need `io2` for guaranteed provisioned IOPS above the `gp3` max of 16,000. `io2 Block Express` supports up to 256,000 IOPS and 4,000 MB/s throughput, making it the only EBS type that can deliver this workload (a high-performance relational database like Oracle or Microsoft SQL Server).

---

## Part 2 — Auto Scaling Groups (Concepts)

### 3. Component Definitions

| Component | Definition |
|---|---|
| Launch Template | A versioned configuration blueprint that tells the ASG how to create new EC2 instances — specifying AMI, instance type, key pair, security groups, IAM role, and user data. Replaces the older Launch Configuration (which was immutable). |
| Auto Scaling Group (ASG) | A logical group of EC2 instances managed together. The ASG ensures the number of running instances stays within the configured bounds and responds to scaling policies and health checks. |
| Desired Capacity | The target number of instances the ASG tries to maintain at all times. Scaling policies adjust this number up/down within min/max bounds. |
| Minimum Capacity | The floor — the ASG will never terminate instances below this count, even under zero load. Guarantees minimum availability. |
| Maximum Capacity | The ceiling — the ASG will never launch more instances than this, even if demand requires it. Prevents runaway cost. |
| Scaling Policy | A rule that triggers the ASG to increase (scale out) or decrease (scale in) the desired capacity based on a metric, schedule, or target. |
| Health Check | A periodic check (EC2 status check or ELB health check) that determines if an instance is healthy. Unhealthy instances are terminated and replaced. |
| Cooldown Period | A waiting period after a scaling action during which no further scaling actions are taken. Allows new instances time to start and begin handling traffic before another scale event. |

### 4. Scaling Policy Types

| Policy Type | How it works | When to use |
|---|---|---|
| Target Tracking | You define a target metric value (e.g., 60% CPU utilization). AWS continuously adjusts instance count to keep the metric at the target. Works like a thermostat. | Default choice for most workloads — simple to configure and automatically handles both scale-out and scale-in. |
| Step Scaling | You define CloudWatch alarms at multiple thresholds and specify how many instances to add/remove at each step (e.g., +2 at 70% CPU, +4 at 90% CPU). Gives more precise control over the scaling response. | When you need fine-grained control over scaling increments — e.g., an app that behaves very differently at 70% vs 90% load. |
| Scheduled Scaling | You define specific date/time rules to change desired capacity (e.g., every Friday at 5 PM, set desired=10). Proactive rather than reactive. | When traffic patterns are predictable — e.g., business-hours web apps, Black Friday pre-scaling. |

### 5. Black Friday Scenario

**5a. CPU spikes to 95%, target policy targets 60% CPU:**
The ASG calculates the required instance count: `desiredInstances = ceil(currentInstances × (currentMetric / targetMetric)) = ceil(4 × (95/60)) = ceil(6.33) = 7`. The ASG sets desired capacity to 7 and launches 3 new instances (from 4 to 7). It stays within max=8.

**5b. 8 instances running, CPU still 90%:**
The ASG has hit **maximum capacity (8)**. It cannot launch any more instances regardless of the scaling policy. The system must handle the load with 8 instances or the application will degrade. To handle more, the team would need to increase `max` in the ASG config, or right-size to larger instance types.

**5c. 3 AM, CPU falls to 10%:**
The target tracking policy detects the metric is well below the 60% target. It calculates `ceil(8 × (10/60)) = ceil(1.33) = 2`. The ASG scale-in cooldown fires and begins terminating instances until desired capacity reaches 2. The floor is **min=2** — the ASG will never go below 2 instances regardless of how low traffic drops.

**5d. Instance fails its health check:**
The ASG marks the instance as unhealthy, terminates it, and immediately launches a replacement to maintain desired capacity. If the ASG is attached to a load balancer, the ELB health check also removes the instance from the target group before termination, preventing traffic from being routed to it. The replacement instance is launched from the Launch Template.

---

## Part 3 — EBS + ASG Integration

### 6. New ASG instance EBS volume

No. When an ASG launches a new instance, it creates a **fresh EBS root volume** from the AMI snapshot specified in the Launch Template. It does not inherit, copy, or reuse the EBS volume of any terminated instance. This is why ASG-managed workloads should be **stateless** — any data stored only on the root volume of a terminated instance is lost.

### 7. Session state storage

Session state should be stored in an **external, shared data store** outside the EC2 instance — such as **Amazon ElastiCache (Redis)** or **Amazon DynamoDB**. Here's why: when the ASG terminates an instance mid-session (due to scale-in or instance failure), the in-memory or local-disk session data is destroyed. If all instances share a Redis cluster, any instance can retrieve the session for any user, making the app truly stateless and ASG-compatible.

### 8. Pre-formatted EBS from Snapshot in Launch Template

1. Create a **formatted EBS volume** on a reference EC2 instance with the desired configuration files pre-installed.
2. Take an **EBS Snapshot** of that volume (`aws ec2 create-snapshot`).
3. In the **Launch Template**, add a block device mapping that references the snapshot ID as an additional EBS volume:
   ```json
   "BlockDeviceMappings": [{
     "DeviceName": "/dev/xvdb",
     "Ebs": { "SnapshotId": "snap-XXXXXXXXXX", "VolumeType": "gp3", "DeleteOnTermination": true }
   }]
   ```
4. Add **user data** (bootstrap script) to mount the volume at the desired path on first launch.
5. Every new instance launched by the ASG gets a fresh copy of the pre-configured volume automatically.
