# Day 37 – CI/CD & DevOps | Part 2
## File: 01-deployment-strategies.md
## Topic: Deployment Strategies — Blue-Green, Canary, Rolling Updates

---

## Overview

A **deployment strategy** defines how you move from the current version of your application (v1) to the new version (v2) while:
- Minimizing or eliminating downtime
- Controlling the blast radius if the new version has a bug
- Enabling fast rollback

| Strategy | Downtime | Rollback speed | Resource cost | Risk |
|---|---|---|---|---|
| **Recreate** | Yes (brief) | Slow (redeploy v1) | Low | High |
| **Rolling Update** | No | Medium (rollout undo) | Low | Medium |
| **Blue-Green** | No | Instant (flip switch) | 2× (two envs) | Low |
| **Canary** | No | Fast (shift traffic) | Medium | Very Low |

---

## Strategy 1: Recreate (Simple, Not Recommended for Production)

```
Current state: [v1] [v1] [v1]

Step 1: Kill all v1
        [    ] [    ] [    ]   ← DOWNTIME here

Step 2: Start all v2
        [v2] [v2] [v2]
```

**Use case:** Development environments, background jobs where downtime is acceptable.

```bash
# Kubernetes Recreate strategy
# In your Deployment YAML:
spec:
  strategy:
    type: Recreate   # All old pods terminated before new pods start
```

**Problem:** Users get a 503 error during the gap between terminating v1 and starting v2.

---

## Strategy 2: Rolling Update (Kubernetes Default)

```
Start:   [v1] [v1] [v1]

Step 1:  [v2] [v1] [v1]   ← Start v2 pod, terminate one v1 pod
Step 2:  [v2] [v2] [v1]   ← Start v2 pod, terminate one v1 pod
Step 3:  [v2] [v2] [v2]   ← Complete

Traffic is always being served — no downtime.
```

**Kubernetes configuration:**

```yaml
# deployment.yaml
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1           # Allow 1 extra pod above desired count during update
      maxUnavailable: 0     # Never go below desired count during update
  template:
    spec:
      containers:
        - name: bookstore-app
          image: scottb/bookstore:2.0.0   # Update this to trigger rolling update
```

**Triggering a rolling update in the pipeline:**

```bash
# Method 1: Update image tag in YAML and apply
kubectl set image deployment/bookstore-deployment \
  bookstore-app=scottb/bookstore:2.0.0 \
  -n bookstore-ns

# Method 2: Apply updated deployment.yaml
kubectl apply -f k8s/deployment.yaml

# Watch the rollout progress
kubectl rollout status deployment/bookstore-deployment -n bookstore-ns

# Verify the new pods are running the new image
kubectl get pods -n bookstore-ns -o=jsonpath='{range .items[*]}{.metadata.name}{"\t"}{.spec.containers[0].image}{"\n"}{end}'
```

**Rollback:**

```bash
kubectl rollout undo deployment/bookstore-deployment -n bookstore-ns
kubectl rollout status deployment/bookstore-deployment -n bookstore-ns
```

**Limitations:**
- During the rollout, BOTH v1 and v2 pods serve traffic simultaneously
- If v1 and v2 have incompatible API or database schema changes, requests can go to either version
- Not ideal for database migrations — requires backward-compatible migrations

---

## Strategy 3: Blue-Green Deployment

**Concept:** Run TWO identical environments (Blue = current, Green = new). Switch all traffic from Blue to Green instantly.

```
                 ┌──────────────────────────────────────────┐
                 │            Load Balancer / Ingress         │
                 └──────────────────────────────────────────┘
                        │                       │
                    Route: 100%             Route: 0%
                        │                       │
              ┌─────────────────┐     ┌─────────────────┐
              │  BLUE (v1.0)    │     │  GREEN (v2.0)   │
              │  [v1] [v1] [v1] │     │  [v2] [v2] [v2] │
              │  ✅ Live         │     │  ⏳ Standby      │
              └─────────────────┘     └─────────────────┘

After cutover:
                        │                       │
                    Route: 0%             Route: 100%
                        │                       │
              ┌─────────────────┐     ┌─────────────────┐
              │  BLUE (v1.0)    │     │  GREEN (v2.0)   │
              │  [v1] [v1] [v1] │     │  [v2] [v2] [v2] │
              │  ⏳ Standby      │     │  ✅ Live          │
              └─────────────────┘     └─────────────────┘
```

**Kubernetes implementation using labels and Service selector:**

```yaml
# Service — traffic selector (switch by changing this label)
# ─────────────────────────────────────────────────────────
apiVersion: v1
kind: Service
metadata:
  name: bookstore-service
  namespace: bookstore-ns
spec:
  selector:
    app: bookstore
    slot: blue              # ← Change this to "green" to cut over
  ports:
    - port: 80
      targetPort: 8080

---
# Blue Deployment (currently live)
# ─────────────────────────────────
apiVersion: apps/v1
kind: Deployment
metadata:
  name: bookstore-blue
  namespace: bookstore-ns
spec:
  replicas: 3
  selector:
    matchLabels:
      app: bookstore
      slot: blue
  template:
    metadata:
      labels:
        app: bookstore
        slot: blue
    spec:
      containers:
        - name: bookstore-app
          image: scottb/bookstore:1.0.0

---
# Green Deployment (new version — deployed but not live)
# ──────────────────────────────────────────────────────
apiVersion: apps/v1
kind: Deployment
metadata:
  name: bookstore-green
  namespace: bookstore-ns
spec:
  replicas: 3
  selector:
    matchLabels:
      app: bookstore
      slot: green
  template:
    metadata:
      labels:
        app: bookstore
        slot: green
    spec:
      containers:
        - name: bookstore-app
          image: scottb/bookstore:2.0.0
```

**Blue-Green cutover commands:**

```bash
# Step 1: Deploy green (new version) — not yet live
kubectl apply -f k8s/bookstore-green-deployment.yaml

# Step 2: Wait for green to be fully ready
kubectl rollout status deployment/bookstore-green -n bookstore-ns

# Step 3: Run smoke tests against green BEFORE cutting over
kubectl port-forward deployment/bookstore-green 8081:8080 -n bookstore-ns &
curl http://localhost:8081/actuator/health
curl http://localhost:8081/books

# Step 4: Cut over — switch the Service selector to green
kubectl patch service bookstore-service -n bookstore-ns \
  -p '{"spec":{"selector":{"slot":"green"}}}'

# Step 5: Verify (check the Service endpoints now point to green pods)
kubectl describe service bookstore-service -n bookstore-ns

# ROLLBACK: switch back to blue (instant!)
kubectl patch service bookstore-service -n bookstore-ns \
  -p '{"spec":{"selector":{"slot":"blue"}}}'

# Step 6: After confidence — scale down blue (or keep as warm standby)
kubectl scale deployment bookstore-blue --replicas=0 -n bookstore-ns
```

**Advantages:**
- Zero downtime cutover
- Instant rollback (flip the switch back)
- Green environment fully tested before receiving any real traffic

**Disadvantages:**
- Requires 2× the infrastructure (costs more)
- Database schema changes must be backward compatible during the transition period

---

## Strategy 4: Canary Deployment

**Concept:** Gradually shift a small percentage of real production traffic to the new version. Monitor closely. Roll out to 100% only if metrics look healthy.

```
Step 1: 5% canary
  [v2-canary] [v1] [v1] [v1] [v1] [v1] [v1] [v1] [v1] [v1]
        5% traffic →↑        95% traffic →

Step 2: 25% canary (metrics look good)
  [v2] [v2] [v2] [v1] [v1] [v1] [v1] [v1] [v1] [v1]

Step 3: 50% → 100% (after monitoring confirms no errors)
  [v2] [v2] [v2] [v2] [v2] [v2] [v2] [v2] [v2] [v2]
```

**Simple Kubernetes canary using replica ratios:**

```yaml
# v1 Deployment — stable (9 replicas = 90% traffic)
apiVersion: apps/v1
kind: Deployment
metadata:
  name: bookstore-stable
  namespace: bookstore-ns
spec:
  replicas: 9
  selector:
    matchLabels:
      app: bookstore
  template:
    metadata:
      labels:
        app: bookstore
        track: stable
    spec:
      containers:
        - name: bookstore-app
          image: scottb/bookstore:1.0.0

---
# v2 Deployment — canary (1 replica = ~10% traffic)
apiVersion: apps/v1
kind: Deployment
metadata:
  name: bookstore-canary
  namespace: bookstore-ns
spec:
  replicas: 1             # 1 out of 10 total = 10% of traffic
  selector:
    matchLabels:
      app: bookstore
  template:
    metadata:
      labels:
        app: bookstore
        track: canary
    spec:
      containers:
        - name: bookstore-app
          image: scottb/bookstore:2.0.0

---
# Service — selects ALL pods with app=bookstore (both stable and canary)
apiVersion: v1
kind: Service
metadata:
  name: bookstore-service
  namespace: bookstore-ns
spec:
  selector:
    app: bookstore        # Matches BOTH stable and canary pods
```

**Canary progression script:**

```bash
# Start: 1 canary, 9 stable (10% canary traffic)
kubectl scale deployment bookstore-canary  --replicas=1 -n bookstore-ns
kubectl scale deployment bookstore-stable  --replicas=9 -n bookstore-ns

# Monitor error rate for 10 minutes
# If error rate is normal:

# 25% canary
kubectl scale deployment bookstore-canary  --replicas=3 -n bookstore-ns
kubectl scale deployment bookstore-stable  --replicas=7 -n bookstore-ns

# 50% canary (after another 10 minutes of healthy metrics)
kubectl scale deployment bookstore-canary  --replicas=5 -n bookstore-ns
kubectl scale deployment bookstore-stable  --replicas=5 -n bookstore-ns

# Full rollout — 100% on canary (now becomes "stable")
kubectl scale deployment bookstore-canary  --replicas=10 -n bookstore-ns
kubectl scale deployment bookstore-stable  --replicas=0  -n bookstore-ns

# ROLLBACK — spike in errors detected: send all traffic back to stable
kubectl scale deployment bookstore-canary  --replicas=0  -n bookstore-ns
kubectl scale deployment bookstore-stable  --replicas=10 -n bookstore-ns
```

**Advantages:**
- Gradually expose real users to the new version
- Catch bugs with minimal user impact (only 5–10% of users affected)
- Data-driven rollout decisions based on real error rates and latency

**Disadvantages:**
- Complex to implement properly (needs traffic-weighted routing, not just replica ratios)
- Requires robust monitoring to make rollout/rollback decisions
- Full traffic-weight control requires a service mesh (Istio, Linkerd) or Ingress controller (NGINX, Argo Rollouts)

---

## Choosing the Right Strategy

| Situation | Recommended strategy |
|---|---|
| Simple internal service, downtime OK | Recreate |
| Stateless service, low risk change | Rolling Update |
| Critical service, database migration | Blue-Green |
| High-risk feature, need production data | Canary |
| Big-bang release, all-or-nothing | Blue-Green |
| Gradual feature release to real users | Canary |

---

## CI/CD Pipeline Integration

```
# In your GitHub Actions CD pipeline:

# Rolling update:
- name: Deploy rolling update
  run: |
    kubectl set image deployment/bookstore-deployment \
      bookstore-app=${{ env.IMAGE_NAME }}:${{ env.ARTIFACT_VERSION }} \
      -n bookstore-ns
    kubectl rollout status deployment/bookstore-deployment -n bookstore-ns

# Blue-green cutover:
- name: Deploy green and cut over
  run: |
    kubectl apply -f k8s/bookstore-green-deployment.yaml
    kubectl rollout status deployment/bookstore-green -n bookstore-ns
    kubectl patch service bookstore-service -n bookstore-ns \
      -p '{"spec":{"selector":{"slot":"green"}}}'

# Canary start (manual approval before progressing):
- name: Deploy canary (10%)
  run: |
    kubectl apply -f k8s/bookstore-canary-deployment.yaml
    echo "Canary deployed at 10%. Monitor metrics, then approve to proceed."
```
