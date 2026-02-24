#!/usr/bin/env bash
# Exercise 09 — Deploying and Exposing an Application with kubectl — SOLUTION

# ---------------------------------------------------------------------------
# Requirement 3 — Apply the Manifests
# "apply" creates or updates resources — safe to run repeatedly (idempotent).
# ---------------------------------------------------------------------------
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

# Alternatively apply the whole directory at once:
# kubectl apply -f .


# ---------------------------------------------------------------------------
# Requirement 4 — Inspect Resources
# ---------------------------------------------------------------------------
# 4a: List all Pods; -o wide adds IP and node columns
kubectl get pods -n default -o wide

# 4b: List all Services
kubectl get services -n default

# 4c: Describe the Deployment — shows events, replica status, strategy
kubectl describe deployment spring-deployment -n default

# 4d: Get Pods filtered by label selector
kubectl get pods -l app=springapp -n default


# ---------------------------------------------------------------------------
# Requirement 5 — View Pod Logs
# ---------------------------------------------------------------------------
# 5a: Dump logs from ALL Pods matching the label (aggregated output)
kubectl logs -l app=springapp -n default

# 5b: Stream logs in real time (Ctrl-C to stop)
kubectl logs -l app=springapp -n default -f


# ---------------------------------------------------------------------------
# Requirement 6 — Delete Resources
# ---------------------------------------------------------------------------
# 6a: Delete the Deployment (also removes its ReplicaSet and Pods)
kubectl delete deployment spring-deployment -n default

# 6b: Delete the Service
kubectl delete service spring-service -n default

# Alternative: delete using the manifest files
# kubectl delete -f deployment.yaml -f service.yaml
