# Exercise 05 — Deployment Strategies: Blue-Green, Canary, and Rolling Updates

## Objective
Compare and apply the three primary deployment strategies — blue-green, canary, and rolling update — by designing the traffic routing, rollback procedure, and ideal use case for each.

## Background
How you release new software to production is as important as what you release. A bad deployment strategy can cause downtime, expose all users to a buggy release, or make rollbacks slow and risky. The three strategies covered here represent the spectrum from safest-but-expensive (blue-green) to balanced (canary) to operationally simple (rolling).

---

## Requirements

### Requirement 1 — Strategy Comparison Table
Complete the comparison table for all three strategies:

| | Blue-Green | Canary | Rolling Update |
|---|---|---|---|
| How traffic is split | | | |
| Infrastructure overhead | | | |
| Rollback speed | | | |
| Risk exposure | | | |
| Best for | | | |
| Main downside | | | |

### Requirement 2 — Blue-Green Deployment
Describe a blue-green deployment step-by-step for a Spring Boot application deployed to Kubernetes. Your answer must cover:
1. The initial state (what "blue" and "green" mean)
2. How you deploy the new version without touching live traffic
3. How you cut traffic over (which Kubernetes object do you change, and how?)
4. How you roll back if the green deployment fails

### Requirement 3 — Canary Deployment
Describe a canary deployment that gradually shifts traffic from v1 to v2:
1. Start state: 100% traffic to v1
2. Canary step 1: route 10% of traffic to v2
3. Observe: what metrics do you monitor during the canary phase?
4. Promote: how do you shift to 100% v2?
5. Abort: how do you immediately revert to 100% v1?

In Kubernetes, what two objects (or tools) would you use to control the traffic split?

### Requirement 4 — Rolling Update in Kubernetes
Write the key YAML snippet for a Deployment's `strategy` block that implements a zero-downtime rolling update (no pods unavailable during the rollout, one extra pod allowed).

Then write the `kubectl` command to:
1. Trigger a rolling update to image `springapp:2.0.0`
2. Watch the rollout progress
3. Pause the rollout mid-way
4. Resume the rollout

### Requirement 5 — Choosing a Strategy
For each scenario below, recommend the best deployment strategy and justify your choice in one sentence:

| Scenario | Recommended Strategy | Justification |
|---|---|---|
| A banking app releasing a high-risk database schema change | | |
| A social media feed switching recommendation algorithms | | |
| A small internal tool with 5 users and a 30-minute maintenance window allowed | | |
| A SaaS product with 10,000 concurrent users where any downtime costs money | | |

---

## Hints
- Blue-green: two identical environments, a load balancer or Service selector switches between them
- Canary: one environment with weighted traffic routing (Nginx Ingress, Istio, or multiple Deployments with different replica counts)
- In Kubernetes, a Service `selector` pointing at a `version` label controls which Pods receive traffic
- `kubectl rollout pause` and `kubectl rollout resume` are the pause/resume commands

## Expected Output
Completed `answers.md` with all five requirements filled in.
