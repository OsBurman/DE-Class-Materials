# Exercise 05 — Deployment Strategies: Blue-Green, Canary, and Rolling Updates
# Complete every TODO section below.

---

## Requirement 1 — Strategy Comparison Table

| | Blue-Green | Canary | Rolling Update |
|---|---|---|---|
| How traffic is split | TODO | TODO | TODO |
| Infrastructure overhead | TODO | TODO | TODO |
| Rollback speed | TODO | TODO | TODO |
| Risk exposure | TODO | TODO | TODO |
| Best for | TODO | TODO | TODO |
| Main downside | TODO | TODO | TODO |

---

## Requirement 2 — Blue-Green Deployment (Step-by-Step)

1. Initial state:
   TODO

2. Deploy new version without touching live traffic:
   TODO

3. Cut traffic over (which Kubernetes object? how?):
   TODO

4. Rollback procedure if green fails:
   TODO

---

## Requirement 3 — Canary Deployment

1. Start state: 100% to v1 — TODO describe
2. Canary step 1 (10% to v2) — TODO: how?
3. Observe — TODO: which 3-5 metrics do you monitor?
4. Promote to 100% v2 — TODO: how?
5. Abort / revert to 100% v1 — TODO: how?

In Kubernetes, which two objects or tools control the traffic split?
TODO

---

## Requirement 4 — Rolling Update YAML and kubectl Commands

### YAML strategy block:
```yaml
# TODO: Fill in the strategy block for zero-downtime rolling update
strategy:
  type: # TODO
  rollingUpdate:
    maxSurge: # TODO
    maxUnavailable: # TODO
```

### kubectl commands:
```bash
# TODO 4a: Trigger rolling update to springapp:2.0.0
# kubectl ...

# TODO 4b: Watch rollout progress
# kubectl ...

# TODO 4c: Pause the rollout mid-way
# kubectl ...

# TODO 4d: Resume the rollout
# kubectl ...
```

---

## Requirement 5 — Choosing a Strategy

| Scenario | Recommended Strategy | Justification |
|---|---|---|
| Banking app, high-risk DB schema change | TODO | TODO |
| Social media feed algorithm switch | TODO | TODO |
| Internal tool, 5 users, 30-min maintenance window | TODO | TODO |
| SaaS product, 10,000 concurrent users, zero downtime | TODO | TODO |
