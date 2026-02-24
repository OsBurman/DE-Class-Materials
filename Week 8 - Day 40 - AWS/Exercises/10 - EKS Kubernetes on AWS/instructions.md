# Exercise 10: EKS — Kubernetes on AWS

## Objective

Provision an Amazon EKS cluster, write Kubernetes manifests for a containerized application, deploy it, and understand how EKS differs from self-managed Kubernetes and ECS.

## Background

**Amazon EKS (Elastic Kubernetes Service)** is a managed Kubernetes control plane on AWS. AWS runs and upgrades the API server, etcd, and scheduler — you manage your worker nodes (EC2 or Fargate) and deploy workloads using standard `kubectl` commands. EKS is the right choice when your team already uses Kubernetes, needs Kubernetes-native tooling (Helm, Kustomize, service meshes), or wants portability across clouds.

## Requirements

### Part 1 — Cluster Setup (CLI)

Write the commands to:

a. **Create an EKS cluster** using `eksctl`:
   - Cluster name: `de-bootcamp-cluster`
   - Region: `us-east-1`
   - Node group: `standard-workers`, 2 nodes, `t3.medium`
   - Kubernetes version: `1.29`

b. **Update kubeconfig** so `kubectl` connects to the cluster

c. **Verify the cluster**: get nodes and get all pods in all namespaces

### Part 2 — Kubernetes Manifests

Create a `deployment.yaml` with a **Namespace**, **Deployment**, and **Service** (separated by `---`).

**Deployment** (`order-service-deployment` in namespace `order-app`):
- 3 replicas, image: `YOUR_ECR_IMAGE_URI`, containerPort: 8080
- Resource requests: CPU 100m, memory 128Mi | limits: CPU 250m, memory 256Mi
- Liveness probe: HTTP GET `/health` port 8080, initialDelay 30s, period 10s
- Readiness probe: HTTP GET `/ready` port 8080, initialDelay 10s, period 5s
- Env: `SPRING_PROFILES_ACTIVE=prod`

**Service** (`order-service-svc`):
- Type: `LoadBalancer`, port 80 → targetPort 8080

### Part 3 — Deploy and Verify (CLI)

a. Create the `order-app` namespace
b. Apply `deployment.yaml`
c. Watch rollout status
d. Get the external LoadBalancer hostname
e. Scale to 5 replicas
f. Rolling update to image tag `:v2`

### Part 4 — Reflection Questions

1. What does `eksctl create cluster` provision in AWS? List at least 5 resources.
2. **EKS vs ECS** — when would you choose EKS?
3. **Liveness probe vs Readiness probe** — what happens to a pod that fails each?
4. One pod is in `CrashLoopBackOff`. What `kubectl` commands diagnose it?
5. What is a **Kubernetes Namespace** and why is it useful in a shared cluster?

## Hints

- `aws eks update-kubeconfig --name <cluster-name> --region <region>`
- `kubectl rollout status deployment/order-service-deployment -n order-app`
- `kubectl set image deployment/order-service-deployment order-service=<new-image> -n order-app`

## Expected Output

```
Part 1: eksctl + kubectl commands
Part 2: deployment.yaml with Namespace + Deployment + Service
Part 3: kubectl commands to deploy, verify, scale, update
Part 4: 5 reflection answers
```
