# Exercise 05 — Deployment Strategies — SOLUTION

---

## Requirement 1 — Strategy Comparison Table

| | Blue-Green | Canary | Rolling Update |
|---|---|---|---|
| How traffic is split | 100% to blue OR 100% to green — instant switch | Weighted split (e.g., 10% → 50% → 100%) | Traffic shifts pod-by-pod as old pods are replaced one at a time |
| Infrastructure overhead | 2× the normal infrastructure cost (two full environments) | Slightly above normal (small canary fleet + main fleet) | No overhead — pods are replaced in-place |
| Rollback speed | Instant — flip the load balancer back to blue | Fast — remove canary pods or shift weight back to 0% | Slower — must run `kubectl rollout undo` to re-deploy previous version |
| Risk exposure | Zero (new version only serves traffic after explicit promotion) | Low (only the canary % of users see the new version) | Medium (users are progressively moved to new version during rollout) |
| Best for | High-risk releases, database migrations, compliance-sensitive apps | Experimenting on production with real users; A/B testing | Standard feature releases where rolling downtime risk is acceptable |
| Main downside | Expensive (doubles infrastructure); stateful sessions need sticky routing | Complex traffic routing configuration; requires good monitoring | If the new version has a bug, up to 50% of users may hit it mid-rollout |

---

## Requirement 2 — Blue-Green Deployment (Step-by-Step)

1. **Initial state:** Two Kubernetes Deployments exist: `spring-blue` (running v1, labelled `version: blue`) and `spring-green` (idle or running an older version, labelled `version: green`). The Service selector points to `version: blue`, so 100% of traffic goes to blue.

2. **Deploy new version without touching live traffic:** Deploy v2 to `spring-green` by updating its Deployment image: `kubectl set image deployment/spring-green spring-container=springapp:2.0.0`. Run smoke tests directly against the green Service (a separate ClusterIP Service pointing only to green) to validate it before any users see it.

3. **Cut traffic over:** Patch the main Service's label selector to switch from `version: blue` to `version: green`:
   ```bash
   kubectl patch service spring-service -p '{"spec":{"selector":{"version":"green"}}}'
   ```
   This is an atomic operation — there is no period where users are partially on v1 and v2.

4. **Rollback:** Patch the Service selector back to `version: blue` in seconds. The blue Deployment is still running untouched and can serve traffic immediately.

---

## Requirement 3 — Canary Deployment

1. **Start state:** `Deployment/spring-v1` has 10 replicas. The Service selector targets `app: springapp` (both versions share this label). 100% of traffic → v1.

2. **Canary step 1 (10% to v2):** Deploy `Deployment/spring-v2` with **1 replica** (`1 / (10 + 1) ≈ 9% of traffic`). Kubernetes Service load-balances across all matching pods, so pod count controls the approximate split.

3. **Observe — metrics to monitor during canary:**
   - HTTP error rate (`5xx` responses from v2 vs v1)
   - P95 / P99 response latency
   - JVM heap usage / GC pause duration
   - Application-specific business metrics (e.g., order completion rate)
   - Log-based error count

4. **Promote to 100% v2:** Scale v2 to 10 replicas and scale v1 to 0:
   ```bash
   kubectl scale deployment spring-v2 --replicas=10
   kubectl scale deployment spring-v1 --replicas=0
   ```

5. **Abort / revert:** Scale v2 back to 0 and v1 back to 10:
   ```bash
   kubectl scale deployment spring-v2 --replicas=0
   kubectl scale deployment spring-v1 --replicas=10
   ```

**Kubernetes objects / tools for traffic splitting:**
- **Multiple Deployments + a single Service** (coarse-grained, pod-count-based weighting)
- **Nginx Ingress with `canary-weight` annotations** or **Istio VirtualService** (fine-grained percentage-based routing without changing replica counts)

---

## Requirement 4 — Rolling Update YAML and kubectl Commands

### YAML strategy block:
```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 1        # Allow one extra Pod above the desired count during the update
    maxUnavailable: 0  # Never reduce below the desired replica count — zero downtime
```

### kubectl commands:
```bash
# 4a: Trigger rolling update to springapp:2.0.0
kubectl set image deployment/spring-deployment spring-container=springapp:2.0.0 -n default

# 4b: Watch rollout progress (exits 0 on success, non-0 on failure)
kubectl rollout status deployment/spring-deployment -n default

# 4c: Pause the rollout mid-way (useful for canary-style progressive rollout)
kubectl rollout pause deployment/spring-deployment -n default

# 4d: Resume the paused rollout
kubectl rollout resume deployment/spring-deployment -n default
```

---

## Requirement 5 — Choosing a Strategy

| Scenario | Recommended Strategy | Justification |
|---|---|---|
| Banking app, high-risk DB schema change | Blue-Green | Instant rollback is essential; the cost of two environments is worth it for a schema migration that could corrupt data if partially applied |
| Social media feed algorithm switch | Canary | A small % of users can validate the new algorithm with real engagement data before it rolls out to everyone |
| Internal tool, 5 users, 30-min maintenance window | Rolling Update | With only 5 users and a maintenance window, the simplest strategy is fine — no need for duplicate infrastructure or complex routing |
| SaaS product, 10,000 concurrent users, zero downtime | Blue-Green or Canary | Blue-green for instant cutover and rollback; canary if A/B testing is desired — either is better than rolling for zero-downtime guarantees at this user scale |
