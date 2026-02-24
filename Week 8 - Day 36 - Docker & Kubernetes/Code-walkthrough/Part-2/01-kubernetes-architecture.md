# Day 36 – Docker & Kubernetes | Part 2
## File: 01-kubernetes-architecture.md
## Topic: Kubernetes Overview, Architecture, and Core Objects

---

## 1. What Is Kubernetes?

Kubernetes (K8s) is an **open-source container orchestration platform** originally developed by Google and donated to the Cloud Native Computing Foundation (CNCF) in 2014.

**Core purpose:** Automate deployment, scaling, and management of containerized applications across a cluster of machines.

### Why Kubernetes?

Docker solves "how do I run one container?" Kubernetes solves "how do I run hundreds or thousands of containers reliably?"

| Challenge | Docker Alone | Kubernetes |
|---|---|---|
| Container fails | Stays dead | Auto-restarts (self-healing) |
| Need 10 replicas | Manual `docker run` × 10 | `replicas: 10` in YAML |
| Roll out a new version | Stop, replace, restart manually | Rolling update with zero downtime |
| Balance traffic across replicas | Need a separate load balancer | Built-in Service load balancing |
| Scale up under high load | Manual | HorizontalPodAutoscaler |
| Multi-host distribution | Single machine only | Schedules across entire cluster |
| Secret management | Env vars in compose | Kubernetes Secrets API |

### Declarative vs Imperative

Kubernetes uses a **declarative model**:
- You describe the **desired state** ("I want 3 replicas of the bookstore app running")
- Kubernetes continuously reconciles **actual state → desired state**
- If a pod dies, Kubernetes notices the gap and creates a replacement

This reconciliation loop is the heart of Kubernetes.

---

## 2. Kubernetes Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        CONTROL PLANE (Master Node)              │
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────────────────┐  │
│  │  API Server  │  │     etcd     │  │  Controller Manager   │  │
│  │  (Front Door)│  │ (State Store)│  │  (Reconciliation Loop)│  │
│  └──────────────┘  └──────────────┘  └───────────────────────┘  │
│           │                                                      │
│  ┌──────────────┐                                                │
│  │  Scheduler   │                                                │
│  │(Pod Placement│                                                │
│  └──────────────┘                                                │
└─────────────────────────────────────────────────────────────────┘
          │              │              │
          ▼              ▼              ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  Worker Node │  │  Worker Node │  │  Worker Node │
│              │  │              │  │              │
│  ┌─────────┐ │  │  ┌─────────┐ │  │  ┌─────────┐ │
│  │  Pod    │ │  │  │  Pod    │ │  │  │  Pod    │ │
│  │ [app]   │ │  │  │ [app]   │ │  │  │  [app]  │ │
│  └─────────┘ │  │  └─────────┘ │  │  └─────────┘ │
│              │  │              │  │              │
│  kubelet     │  │  kubelet     │  │  kubelet     │
│  kube-proxy  │  │  kube-proxy  │  │  kube-proxy  │
│  containerd  │  │  containerd  │  │  containerd  │
└──────────────┘  └──────────────┘  └──────────────┘
```

---

## 3. Control Plane Components

### 3.1 API Server (`kube-apiserver`)

- The **single entry point** for all Kubernetes operations
- Every command you run with `kubectl` is an HTTP request to the API server
- Validates and processes requests, then stores state in etcd
- Exposes a RESTful API — everything in K8s is a REST resource

```
kubectl apply -f deployment.yaml
       ↓
  HTTP POST → kube-apiserver
       ↓
  Validates YAML → stores in etcd → notifies controllers
```

### 3.2 etcd

- A distributed, consistent **key-value store**
- Stores ALL cluster state — every object (pods, deployments, services, secrets)
- The "source of truth" for the entire cluster
- Highly available — typically 3 or 5 instances in production
- If etcd is lost and you have no backup, you lose your entire cluster state

### 3.3 Scheduler (`kube-scheduler`)

- Watches for newly created Pods that haven't been assigned to a node yet
- Selects the best node to run each Pod based on:
  - Available CPU and memory
  - Node affinity / anti-affinity rules
  - Taints and tolerations
  - Resource requests and limits
- Does NOT actually start the Pod — it just writes the node assignment to etcd

### 3.4 Controller Manager (`kube-controller-manager`)

- Runs a collection of **controllers** — each one watches a resource type and reconciles state
- Key controllers:
  - **ReplicaSet Controller** — ensures the right number of Pod replicas are running
  - **Deployment Controller** — manages rolling updates
  - **Node Controller** — monitors node health, marks nodes as unhealthy
  - **Service Account Controller** — creates default service accounts for namespaces
- Each controller loop: watch → compare desired vs actual → act

---

## 4. Worker Node Components

### 4.1 kubelet

- An **agent** running on every worker node
- Watches the API server for Pods assigned to its node
- Ensures containers described in Pod specs are running and healthy
- Reports node and Pod status back to the API server
- Runs health checks (liveness/readiness probes) and restarts failing containers

### 4.2 kube-proxy

- Maintains **network rules** on each node
- Implements the Kubernetes Service abstraction (load balancing to Pod IPs)
- Uses `iptables` or `ipvs` rules to route traffic to the correct Pods
- Every time a Service is created or updated, kube-proxy updates the routing rules

### 4.3 Container Runtime

- The software that actually **runs containers** on the node
- Kubernetes supports any OCI-compliant runtime:
  - `containerd` (default in most modern clusters — what Docker Desktop uses)
  - `CRI-O` (common in OpenShift)
  - Docker (deprecated as a direct runtime in K8s 1.24+, but containerd is used under the hood)
- The kubelet communicates with the runtime via the **Container Runtime Interface (CRI)**

---

## 5. Core Kubernetes Objects

### 5.1 Pod

The **smallest deployable unit** in Kubernetes. A Pod wraps one or more containers that:
- Share the same network namespace (same IP, same ports)
- Share the same storage volumes
- Are always scheduled together on the same node

```
Pod: bookstore-pod
├── container: bookstore-app (port 8080)
└── sidecar: log-shipper (optional pattern)
     └── shared volume: /app/logs
```

> **Key insight:** Pods are ephemeral. They are created and destroyed constantly. Never rely on a Pod's IP address — use a Service instead.

### 5.2 ReplicaSet

Ensures a specified number of Pod replicas are running at all times.

```
ReplicaSet: bookstore-rs
  spec.replicas: 3
  → Pod 1 running ✓
  → Pod 2 running ✓
  → Pod 3 DEAD   → Controller creates Pod 4 (self-healing)
```

> You rarely create ReplicaSets directly — Deployments manage them for you.

### 5.3 Deployment

The standard way to deploy stateless applications. A Deployment:
- Manages a ReplicaSet
- Enables **rolling updates** (update image version with zero downtime)
- Enables **rollbacks** (undo an update)
- Provides declarative update semantics

```yaml
# In a Deployment, you declare WHAT you want:
spec:
  replicas: 3
  template:
    spec:
      containers:
      - image: scottb/bookstore:2.0   # Update this → rolling update begins
```

### 5.4 Service

A **stable network endpoint** for a set of Pods. Pods come and go; Services are permanent.

Three main Service types:

| Type | Description | Use case |
|---|---|---|
| `ClusterIP` | Internal IP only — reachable within the cluster | Default; inter-service communication |
| `NodePort` | Exposes service on a static port on every node (30000–32767) | Dev/testing; external access without cloud LB |
| `LoadBalancer` | Provisions a cloud load balancer (AWS ELB, GCP LB) | Production external traffic |
| `Ingress` | HTTP/HTTPS routing with path-based rules (uses an Ingress Controller) | Production multi-service HTTP(S) |

### 5.5 ConfigMap

Stores **non-sensitive configuration data** as key-value pairs. Decouples config from your container image.

```yaml
# Instead of baking config into the image, mount it from a ConfigMap
data:
  APP_PORT: "8080"
  LOG_LEVEL: "INFO"
  DB_HOST: "postgres-service"
```

### 5.6 Secret

Like ConfigMap but for **sensitive data** (passwords, API keys, TLS certs). Values are base64-encoded (not encrypted by default — use encryption at rest in production).

```yaml
data:
  DB_PASSWORD: c2VjcmV0MTIz   # base64 of "secret123"
```

### 5.7 Namespace

Logical partition of a cluster. Use namespaces to isolate environments (dev/staging/prod) or teams.

```
cluster
├── namespace: default       (where objects go if you don't specify)
├── namespace: kube-system   (K8s system components)
├── namespace: bookstore-dev
└── namespace: bookstore-prod
```

---

## 6. The Kubernetes Control Loop

```
┌─────────────────────────────────────────────────────┐
│                  Reconciliation Loop                 │
│                                                      │
│   Desired State           Actual State               │
│   (in etcd)               (running pods)             │
│                                                      │
│   replicas: 3    ≠        replicas: 2                │
│                                                      │
│              ↓                                       │
│     Controller notices the gap                       │
│              ↓                                       │
│     Scheduler assigns new pod to a node              │
│              ↓                                       │
│     kubelet starts the container                     │
│              ↓                                       │
│     Actual State = Desired State ✓                   │
└─────────────────────────────────────────────────────┘
```

This loop runs **continuously** — not on a timer, but event-driven. The moment something changes (a pod dies, a node becomes unavailable, you edit a Deployment), controllers react immediately.

---

## 7. Kubernetes vs Docker Compose — When to Use What

| | Docker Compose | Kubernetes |
|---|---|---|
| **Best for** | Local development | Production, multi-node clusters |
| **Setup complexity** | Low | High |
| **Scaling** | Manual (`--scale`, single host) | Automatic (multi-node) |
| **Self-healing** | No (containers stay dead) | Yes (auto-restarts, rescheduling) |
| **Rolling updates** | Manual | Built-in (zero downtime) |
| **Secret management** | `.env` file | Secrets API (+ external providers) |
| **Load balancing** | No | Built-in Service load balancing |
| **Multi-host** | No (Docker Swarm needed) | Yes (designed for it) |

---

## 8. Managed Kubernetes Offerings

In production, most teams use a managed K8s service — no need to manage the control plane yourself:

| Cloud | Service |
|---|---|
| AWS | EKS (Elastic Kubernetes Service) |
| Google Cloud | GKE (Google Kubernetes Engine) |
| Azure | AKS (Azure Kubernetes Service) |
| Local dev | Minikube, kind, Docker Desktop K8s, k3s |

---

## 9. Bookstore Application — K8s Object Map

```
Internet → LoadBalancer Service (bookstore-lb)
                ↓
         Deployment: bookstore-deployment
           ReplicaSet: bookstore-rs
             Pod 1: bookstore-app container
             Pod 2: bookstore-app container
             Pod 3: bookstore-app container
                ↓  (talks to)
         ClusterIP Service (postgres-service)
                ↓
         StatefulSet: postgres
             Pod: postgres container
               └── PersistentVolumeClaim → PersistentVolume

ConfigMap: bookstore-config  → env vars → bookstore pods
Secret:    bookstore-secret  → DB password → bookstore pods + postgres pod
Namespace: bookstore-prod    → all of the above
```
