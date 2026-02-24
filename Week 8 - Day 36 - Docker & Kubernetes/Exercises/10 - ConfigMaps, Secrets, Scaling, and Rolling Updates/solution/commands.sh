#!/usr/bin/env bash
# Exercise 10 — ConfigMaps, Secrets, Scaling, and Rolling Updates — SOLUTION

# ---------------------------------------------------------------------------
# Requirement 1 — Create a ConfigMap (imperative)
# --from-literal can be repeated for each key-value pair.
# ---------------------------------------------------------------------------
kubectl create configmap spring-config \
  --from-literal=SPRING_PROFILES_ACTIVE=production \
  --from-literal=SERVER_PORT=8080 \
  -n default


# ---------------------------------------------------------------------------
# Requirement 2 — Create a Secret (imperative)
# kubectl automatically base64-encodes --from-literal values.
# ---------------------------------------------------------------------------
kubectl create secret generic spring-secret \
  --from-literal=DB_USERNAME=app \
  --from-literal=DB_PASSWORD=supersecret \
  -n default


# ---------------------------------------------------------------------------
# Requirement 3 — Apply the declarative manifests
# ---------------------------------------------------------------------------
kubectl apply -f configmap.yaml
kubectl apply -f secret.yaml
kubectl apply -f deployment-with-config.yaml


# ---------------------------------------------------------------------------
# Requirement 5 — Scale the Deployment
# ---------------------------------------------------------------------------
# Scale up to 5 replicas
kubectl scale deployment spring-deployment --replicas=5 -n default

# Watch pods come up
kubectl get pods -l app=springapp -n default -w

# Scale back down to 2
kubectl scale deployment spring-deployment --replicas=2 -n default


# ---------------------------------------------------------------------------
# Requirement 6 — Perform a Rolling Update
# "set image" triggers a rolling update; the old ReplicaSet is kept for rollback.
# ---------------------------------------------------------------------------
kubectl set image deployment/spring-deployment \
  spring-container=springapp:2.0.0 \
  -n default

# Monitor progress — exits 0 when rollout completes, non-0 on failure
kubectl rollout status deployment/spring-deployment -n default


# ---------------------------------------------------------------------------
# Requirement 7 — Roll Back
# "rollout undo" reverts to the previous ReplicaSet revision.
# ---------------------------------------------------------------------------
# Undo the last rollout (reverts to springapp:1.0.0)
kubectl rollout undo deployment/spring-deployment -n default

# View full rollout history (add --revision=<n> to inspect a specific revision)
kubectl rollout history deployment/spring-deployment -n default
