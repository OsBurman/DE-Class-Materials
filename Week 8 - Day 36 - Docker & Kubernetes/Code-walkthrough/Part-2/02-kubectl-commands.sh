#!/usr/bin/env bash
# =============================================================================
# Day 36 – Docker & Kubernetes | Part 2
# File: 02-kubectl-commands.sh
# Topic: kubectl CLI — Managing a Kubernetes Cluster
# Domain: Bookstore Application
# =============================================================================
# Prerequisites:
#   - kubectl installed  (https://kubernetes.io/docs/tasks/tools/)
#   - A running cluster  (Docker Desktop K8s, Minikube, or kind)
#   - Manifests from 03-kubernetes-manifests.yaml applied
# =============================================================================


# =============================================================================
# SECTION 1 — CONTEXT & CLUSTER INFO
# =============================================================================

# Show the current cluster context (which cluster kubectl is talking to)
kubectl config current-context

# List all configured contexts (clusters you have credentials for)
kubectl config get-contexts

# Switch to a different context
kubectl config use-context docker-desktop

# Display cluster info (API server URL, DNS)
kubectl cluster-info

# Check kubectl version (client and server)
kubectl version --short


# =============================================================================
# SECTION 2 — NAMESPACES
# =============================================================================

# List all namespaces
kubectl get namespaces

# Create a namespace
kubectl create namespace bookstore-ns

# Set the default namespace for the current context (so you don't need -n every time)
kubectl config set-context --current --namespace=bookstore-ns

# All subsequent commands work in bookstore-ns. To target a specific namespace:
#   kubectl get pods -n bookstore-ns
# To see ALL namespaces at once:
#   kubectl get pods --all-namespaces   (or -A)


# =============================================================================
# SECTION 3 — APPLYING MANIFESTS (DECLARATIVE APPROACH)
# =============================================================================

# Apply a single manifest file  (create OR update — idempotent)
kubectl apply -f 03-kubernetes-manifests.yaml

# Apply all manifest files in a directory
kubectl apply -f ./k8s/

# Apply a manifest from a URL
kubectl apply -f https://raw.githubusercontent.com/.../deployment.yaml

# ── Dry run — preview what WOULD happen without making changes
kubectl apply -f 03-kubernetes-manifests.yaml --dry-run=client

# ── Delete resources defined in a manifest
kubectl delete -f 03-kubernetes-manifests.yaml


# =============================================================================
# SECTION 4 — GETTING RESOURCES
# =============================================================================

# ── Pods ──────────────────────────────────────────────────────────────────────

# List running pods in current namespace
kubectl get pods

# Wide output — shows IP, node, and container image
kubectl get pods -o wide

# Watch pods in real time (refreshes automatically — great for demos)
kubectl get pods -w

# List pods in a specific namespace
kubectl get pods -n bookstore-ns

# List pods across ALL namespaces
kubectl get pods -A

# ── Deployments ──────────────────────────────────────────────────────────────

kubectl get deployments
kubectl get deploy        # shorthand

# ── Services ─────────────────────────────────────────────────────────────────

kubectl get services
kubectl get svc           # shorthand

# ── ReplicaSets ──────────────────────────────────────────────────────────────

kubectl get replicasets
kubectl get rs            # shorthand

# ── ConfigMaps & Secrets ─────────────────────────────────────────────────────

kubectl get configmaps
kubectl get cm            # shorthand

kubectl get secrets

# ── Everything in the namespace ──────────────────────────────────────────────

kubectl get all
kubectl get all -n bookstore-ns

# ── Nodes ─────────────────────────────────────────────────────────────────────

kubectl get nodes
kubectl get nodes -o wide


# =============================================================================
# SECTION 5 — DESCRIBING RESOURCES (DETAILED INFO)
# =============================================================================

# Describe a pod — shows events, container state, image, volumes, probes
kubectl describe pod bookstore-deployment-5d4f8-abc12

# Describe a deployment — shows replicas, strategy, rolling update status
kubectl describe deployment bookstore-deployment

# Describe a service — shows endpoints (which pod IPs it routes to)
kubectl describe service bookstore-service

# Describe a node — shows capacity, conditions, allocatable resources
kubectl describe node docker-desktop

# ── Pro tip: use partial names with grep ─────────────────────────────────────
# kubectl get pods | grep bookstore
# kubectl describe pod $(kubectl get pods | grep bookstore | awk '{print $1}' | head -1)


# =============================================================================
# SECTION 6 — LOGS
# =============================================================================

# Get logs from a pod (container stdout/stderr)
kubectl logs bookstore-deployment-5d4f8-abc12

# Follow logs in real time (like docker logs -f)
kubectl logs -f bookstore-deployment-5d4f8-abc12

# Get last 100 lines
kubectl logs --tail=100 bookstore-deployment-5d4f8-abc12

# If a pod has MULTIPLE containers, specify container name
kubectl logs bookstore-deployment-5d4f8-abc12 -c bookstore-app

# Get logs from a PREVIOUS (crashed) container instance
kubectl logs bookstore-deployment-5d4f8-abc12 --previous

# Get logs from ALL pods in a deployment using label selector
kubectl logs -l app=bookstore --all-containers=true


# =============================================================================
# SECTION 7 — EXEC INTO A CONTAINER
# =============================================================================

# Open an interactive shell inside a running container
kubectl exec -it bookstore-deployment-5d4f8-abc12 -- /bin/sh

# Run a one-off command inside a container
kubectl exec bookstore-deployment-5d4f8-abc12 -- env

# Check the health endpoint from inside the cluster
kubectl exec bookstore-deployment-5d4f8-abc12 -- curl http://localhost:8080/actuator/health

# If the pod has multiple containers, specify with -c
kubectl exec -it bookstore-deployment-5d4f8-abc12 -c bookstore-app -- /bin/sh


# =============================================================================
# SECTION 8 — PORT FORWARDING (local access to a service or pod)
# =============================================================================

# Forward localhost:8080 → pod port 8080 (useful for testing without a NodePort/LB)
kubectl port-forward pod/bookstore-deployment-5d4f8-abc12 8080:8080

# Forward through a service (load-balanced to any ready pod)
kubectl port-forward service/bookstore-service 8080:80

# Forward in the background (add & at the end)
kubectl port-forward service/bookstore-service 8080:80 &


# =============================================================================
# SECTION 9 — SCALING
# =============================================================================

# Scale a deployment to 5 replicas imperatively
kubectl scale deployment bookstore-deployment --replicas=5

# Verify the scale-up
kubectl get pods -w

# Scale back down
kubectl scale deployment bookstore-deployment --replicas=3

# ── Tip: Declarative scaling is preferred in production ──────────────────────
#   Edit the YAML to set spec.replicas: 5, then:
#   kubectl apply -f 03-kubernetes-manifests.yaml


# =============================================================================
# SECTION 10 — ROLLING UPDATES & ROLLBACKS
# =============================================================================

# Trigger a rolling update by updating the container image
kubectl set image deployment/bookstore-deployment \
  bookstore-app=scottb/bookstore:2.0.0

# Watch the rolling update progress
kubectl rollout status deployment/bookstore-deployment

# View rollout history (shows revision numbers)
kubectl rollout history deployment/bookstore-deployment

# View details of a specific revision
kubectl rollout history deployment/bookstore-deployment --revision=2

# ── ROLLBACK ─────────────────────────────────────────────────────────────────

# Roll back to the PREVIOUS revision
kubectl rollout undo deployment/bookstore-deployment

# Roll back to a SPECIFIC revision number
kubectl rollout undo deployment/bookstore-deployment --to-revision=1

# Pause a rollout (useful if you notice problems mid-update)
kubectl rollout pause deployment/bookstore-deployment

# Resume a paused rollout
kubectl rollout resume deployment/bookstore-deployment


# =============================================================================
# SECTION 11 — DELETING RESOURCES
# =============================================================================

# Delete a single pod (Deployment will immediately create a replacement)
kubectl delete pod bookstore-deployment-5d4f8-abc12

# Delete a deployment (and all pods it manages)
kubectl delete deployment bookstore-deployment

# Delete a service
kubectl delete service bookstore-service

# Delete a configmap
kubectl delete configmap bookstore-config

# Delete everything defined in a manifest
kubectl delete -f 03-kubernetes-manifests.yaml

# Delete everything in a namespace (DANGEROUS)
kubectl delete all --all -n bookstore-ns

# Delete a namespace (and everything in it)
kubectl delete namespace bookstore-ns


# =============================================================================
# SECTION 12 — IMPERATIVE OBJECT CREATION (quick demos)
# =============================================================================

# Create a deployment imperatively (not recommended for production)
kubectl create deployment bookstore-deployment \
  --image=scottb/bookstore:1.0.0 \
  --replicas=3

# Expose the deployment as a service
kubectl expose deployment bookstore-deployment \
  --port=80 \
  --target-port=8080 \
  --type=NodePort

# Create a configmap from literal values
kubectl create configmap bookstore-config \
  --from-literal=APP_PORT=8080 \
  --from-literal=LOG_LEVEL=INFO

# Create a secret from literal values
kubectl create secret generic bookstore-secret \
  --from-literal=DB_PASSWORD=supersecret

# ── Generate YAML from imperative commands (dry-run) ─────────────────────────
# Great for scaffolding YAML quickly:
kubectl create deployment bookstore-deployment \
  --image=scottb/bookstore:1.0.0 \
  --replicas=3 \
  --dry-run=client \
  -o yaml


# =============================================================================
# SECTION 13 — LABELS & SELECTORS
# =============================================================================

# Add a label to a running pod
kubectl label pod bookstore-deployment-5d4f8-abc12 environment=production

# Show labels in the pod listing
kubectl get pods --show-labels

# Filter pods by label
kubectl get pods -l app=bookstore

# Filter by multiple labels
kubectl get pods -l app=bookstore,environment=production

# Remove a label (append -)
kubectl label pod bookstore-deployment-5d4f8-abc12 environment-


# =============================================================================
# SECTION 14 — RESOURCE USAGE (requires metrics-server)
# =============================================================================

# Show CPU and memory usage per pod
kubectl top pods

# Show CPU and memory usage per node
kubectl top nodes

# (Install metrics-server with:)
# kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml


# =============================================================================
# SECTION 15 — QUICK REFERENCE CHEAT CARD
# =============================================================================
#
#  CONTEXT                       APPLY / DELETE
#  ────────────────────────────  ────────────────────────────────────
#  kubectl config get-contexts   kubectl apply -f <file>
#  kubectl config use-context    kubectl delete -f <file>
#  kubectl cluster-info          kubectl apply -f <dir>/
#
#  GET                           DESCRIBE
#  ────────────────────────────  ────────────────────────────────────
#  kubectl get pods [-w] [-A]    kubectl describe pod <name>
#  kubectl get deploy/svc/cm     kubectl describe deployment <name>
#  kubectl get all               kubectl describe node <name>
#
#  LOGS / EXEC                   SCALE / ROLLOUT
#  ────────────────────────────  ────────────────────────────────────
#  kubectl logs -f <pod>         kubectl scale deploy <n> --replicas=5
#  kubectl logs -l app=<name>    kubectl rollout status deploy/<n>
#  kubectl exec -it <pod> -- sh  kubectl rollout undo deploy/<n>
#  kubectl port-forward svc/..   kubectl set image deploy/<n> c=img:v2
#
#  NAMESPACE
#  ─────────────────────────────────────────────
#  kubectl get pods -n <ns>
#  kubectl create namespace <ns>
#  kubectl get all -n <ns>
#  kubectl config set-context --current --namespace=<ns>
