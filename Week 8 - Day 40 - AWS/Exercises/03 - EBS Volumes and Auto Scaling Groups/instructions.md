# Exercise 03: EBS Volumes and Auto Scaling Groups

## Objective

Understand how Elastic Block Store (EBS) provides persistent storage for EC2 instances, and how Auto Scaling Groups automatically adjust capacity to handle load changes.

## Background

EC2 instances are ephemeral by default — the root volume is deleted when the instance terminates. **EBS** provides durable, persistent block storage that persists independently of instance lifecycle. **Auto Scaling Groups (ASGs)** solve a different problem: as traffic fluctuates, manually provisioning and deprovisioning EC2 instances is impractical. ASGs automate this based on policies you define, ensuring you run the minimum number of instances needed while scaling out during peak load and back in when load drops.

## Requirements

### Part 1 — EBS Volume Types and Use Cases

1. Complete the table comparing EBS volume types:

   | Type | Name | Max IOPS | Max Throughput | Best for |
   |---|---|---|---|---|
   | `gp3` | | | | |
   | `io2` | | | | |
   | `st1` | | | | |
   | `sc1` | | | | |

2. Answer the following:
   - An EC2 instance has a **root EBS volume**. The instance is terminated. What happens to the root volume by default? How do you change this behavior?
   - What is an **EBS Snapshot**? Where are snapshots stored and what are two use cases for them?
   - What is **EBS Multi-Attach** and when would you use it? What is a limitation?
   - An application writes 100,000 IOPS at 256 KB blocks. Which EBS type should you choose and why?

### Part 2 — Auto Scaling Groups (Concepts)

3. Define each component:

   | Component | Definition |
   |---|---|
   | Launch Template | |
   | Auto Scaling Group (ASG) | |
   | Desired Capacity | |
   | Minimum Capacity | |
   | Maximum Capacity | |
   | Scaling Policy | |
   | Health Check | |
   | Cooldown Period | |

4. Explain the three types of scaling policies and when to use each:

   | Policy Type | How it works | When to use |
   |---|---|---|
   | Target Tracking | | |
   | Step Scaling | | |
   | Scheduled Scaling | | |

5. **Scenario analysis.** An e-commerce app uses an ASG with min=2, desired=4, max=8. It is 11:55 PM on Black Friday:

   a. A spike hits: CPU jumps to 95% across all instances. The target tracking policy targets 60% CPU. What does the ASG do? How many instances will it aim for?

   b. Five minutes later, 8 instances are running but CPU is still 90%. What happens?

   c. At 3 AM, traffic drops. CPU falls to 10%. What does the ASG do? What is the floor?

   d. An instance fails its EC2 health check. What does the ASG do?

### Part 3 — Integration: EBS + ASG

6. When Auto Scaling launches a new instance from a Launch Template, does the new instance get the same EBS volume as the terminated instance? Explain.

7. Your stateless API service uses an ASG. Where should session state be stored and why? (Hint: think about what happens when an instance is terminated mid-session.)

8. You want all new ASG instances to start with a pre-formatted EBS volume containing application configuration. Describe the approach using a **Launch Template** and an **EBS Snapshot**.

## Hints

- `gp3` is the default general-purpose SSD; `io2` is provisioned IOPS SSD for databases requiring consistent sub-millisecond latency.
- `st1` (throughput-optimized HDD) and `sc1` (cold HDD) cannot be used as boot volumes.
- An ASG's **desired capacity** is the number of instances it tries to maintain; scaling policies adjust this number up or down within min/max bounds.
- **Target tracking** is the simplest policy — you set a metric target (e.g., 60% CPU) and AWS automatically adds/removes instances to maintain it.
- **Cooldown period** prevents thrashing — after a scaling action, the ASG waits before taking another action to let the new instances start handling traffic.

## Expected Output

This is a conceptual exercise. Your answers should include:

```
Requirement 1: EBS type comparison table filled in + 4 written answers
Requirement 2: Component definitions table + scaling policy table + 4 scenario answers
Requirement 3: 3 written design answers
```
