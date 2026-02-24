# Exercise 03: EBS Volumes and Auto Scaling Groups

## Part 1 — EBS Volume Types

### 1. EBS Type Comparison Table

| Type | Name | Max IOPS | Max Throughput | Best for |
|---|---|---|---|---|
| `gp3` | TODO | TODO | TODO | TODO |
| `io2` | TODO | TODO | TODO | TODO |
| `st1` | TODO | TODO | TODO | TODO |
| `sc1` | TODO | TODO | TODO | TODO |

### 2. Written Questions

**Root EBS volume on instance termination — default behavior and how to change:**

TODO:

**What is an EBS Snapshot? Where stored? Two use cases:**

TODO:

**What is EBS Multi-Attach? When use it? Limitation:**

TODO:

**100,000 IOPS at 256 KB blocks — which EBS type?**

TODO:

---

## Part 2 — Auto Scaling Groups (Concepts)

### 3. Component Definitions

| Component | Definition |
|---|---|
| Launch Template | TODO |
| Auto Scaling Group (ASG) | TODO |
| Desired Capacity | TODO |
| Minimum Capacity | TODO |
| Maximum Capacity | TODO |
| Scaling Policy | TODO |
| Health Check | TODO |
| Cooldown Period | TODO |

### 4. Scaling Policy Types

| Policy Type | How it works | When to use |
|---|---|---|
| Target Tracking | TODO | TODO |
| Step Scaling | TODO | TODO |
| Scheduled Scaling | TODO | TODO |

### 5. Black Friday Scenario (min=2, desired=4, max=8)

**5a. CPU spikes to 95%, target tracking policy targets 60% CPU:**

TODO:

**5b. 5 minutes later, 8 instances running but CPU still 90%:**

TODO:

**5c. 3 AM, CPU falls to 10%:**

TODO:

**5d. One instance fails its health check:**

TODO:

---

## Part 3 — EBS + ASG Integration

### 6. Does a new ASG instance get the same EBS volume as the terminated one?

TODO:

### 7. Where should session state be stored for a stateless ASG service?

TODO:

### 8. How to pre-format new ASG instances with an EBS Snapshot?

TODO:
