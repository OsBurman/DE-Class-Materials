#!/usr/bin/env bash
# Exercise 10 — ConfigMaps, Secrets, Scaling, and Rolling Updates
# Complete each TODO with the correct kubectl command.

# ---------------------------------------------------------------------------
# Requirement 1 — Create a ConfigMap (imperative)
# ---------------------------------------------------------------------------
# TODO 1: Create ConfigMap spring-config with two literal key-value pairs
# kubectl create configmap ...


# ---------------------------------------------------------------------------
# Requirement 2 — Create a Secret (imperative)
# ---------------------------------------------------------------------------
# TODO 2: Create generic Secret spring-secret with DB_USERNAME and DB_PASSWORD
# kubectl create secret generic ...


# ---------------------------------------------------------------------------
# Requirement 3 — Apply the declarative manifests
# (After completing configmap.yaml and secret.yaml)
# ---------------------------------------------------------------------------
# TODO 3a: Apply configmap.yaml
# kubectl apply ...

# TODO 3b: Apply secret.yaml
# kubectl apply ...

# TODO 3c: Apply deployment-with-config.yaml
# kubectl apply ...


# ---------------------------------------------------------------------------
# Requirement 5 — Scale the Deployment
# ---------------------------------------------------------------------------
# TODO 5a: Scale spring-deployment to 5 replicas
# kubectl scale ...

# TODO 5b: Scale spring-deployment back to 2 replicas
# kubectl scale ...


# ---------------------------------------------------------------------------
# Requirement 6 — Perform a Rolling Update
# ---------------------------------------------------------------------------
# TODO 6a: Update the spring-container image to springapp:2.0.0
# kubectl set image ...

# TODO 6b: Watch the rollout progress
# kubectl rollout status ...


# ---------------------------------------------------------------------------
# Requirement 7 — Roll Back
# ---------------------------------------------------------------------------
# TODO 7a: Undo the last rollout
# kubectl rollout undo ...

# TODO 7b: View rollout history
# kubectl rollout history ...
