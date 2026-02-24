# Exercise 01: Cloud Computing Models and AWS Global Infrastructure

## Objective

Understand the three cloud service models (IaaS, PaaS, SaaS), the AWS global infrastructure layout, and how AWS Regions and Availability Zones provide fault tolerance and low latency.

## Background

Before deploying a single resource on AWS, you need a clear mental model of what cloud computing is, what problem it solves, and how AWS organizes its physical data centers globally. These concepts underpin every architectural decision you'll make — choosing the right service model (IaaS vs PaaS) and the right region affects cost, compliance, latency, and resilience.

## Requirements

1. **Cloud service models.** Define each model and provide a real-world AWS service example for each:

   | Model | Full name | What the provider manages | What you manage | AWS example service |
   |---|---|---|---|---|
   | IaaS | | | | |
   | PaaS | | | | |
   | SaaS | | | | |

2. **Shared responsibility model.** For each item in the table below, mark whether it is AWS's responsibility or the customer's responsibility in each model:

   | Item | IaaS | PaaS | SaaS |
   |---|---|---|---|
   | Physical data center security | | | |
   | Operating system patching | | | |
   | Application code | | | |
   | Network firewalls (Security Groups) | | | |
   | Data encryption at rest | | | |

3. **AWS Global Infrastructure.** Answer the following:

   a. What is an **AWS Region**? Name three real AWS Regions and their geographic locations.

   b. What is an **Availability Zone (AZ)**? How many AZs does a typical region have?

   c. Why does AWS recommend deploying across **multiple AZs**? What failure does this protect against?

   d. What is an **AWS Edge Location**? Name the AWS service that uses edge locations and explain what it does.

   e. A company's SLA requires 99.99% uptime. Their application runs only in `us-east-1a`. What is the single biggest architectural risk and what is the fix?

4. **Service model selection.** For each scenario, choose the most appropriate model (IaaS / PaaS / SaaS) and justify in one sentence:

   | Scenario | Model | Reason |
   |---|---|---|
   | Your team needs full OS-level access to install a custom network driver | | |
   | You want to deploy a Spring Boot app without managing servers or OS patches | | |
   | Your company uses Gmail for email | | |
   | You need to run a Kubernetes cluster with custom networking plugins | | |
   | A startup wants to deploy a Node.js app in minutes without DevOps expertise | | |

5. **AWS account structure.** Briefly explain:
   - What is an **AWS account**?
   - What is **AWS Organizations** and why would a company use it?
   - What is the **root user** and why should it not be used for day-to-day tasks?

## Hints

- **IaaS = infrastructure as a service**: you rent raw compute, storage, and network. You still install the OS, runtime, and app.
- **PaaS = platform as a service**: the provider manages OS + runtime; you only deploy your code.
- **SaaS = software as a service**: everything is managed; you just use the app.
- An **Availability Zone** is one or more discrete data centers within a Region, connected by low-latency private links. A Region has at least 2 AZs (most have 3).
- **Edge Locations** are separate from Regions — they are smaller PoPs used for content delivery caching (CloudFront).

## Expected Output

This is a conceptual exercise. Your answers should include:

```
Requirement 1 — Table with all three rows filled (IaaS/PaaS/SaaS definitions + examples)

Requirement 2 — Shared responsibility table: all 15 cells filled

Requirement 3 — Written answers:
  a. Region definition + 3 real examples (e.g., us-east-1 N. Virginia, eu-west-1 Ireland, ap-southeast-1 Singapore)
  b. AZ definition + typical count (3 per region)
  c. Multi-AZ protects against single data center / AZ failure
  d. Edge location = CloudFront CDN PoP; caches static content closer to users
  e. Risk = AZ outage takes down entire app; fix = deploy across ≥2 AZs with load balancer

Requirement 4 — Pattern selection table: all 5 rows filled

Requirement 5 — AWS account/Organizations/root user explanations
```
