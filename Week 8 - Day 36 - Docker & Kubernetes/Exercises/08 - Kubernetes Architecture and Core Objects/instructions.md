# Exercise 08 — Kubernetes Architecture and Core Objects

## Learning Objectives
By the end of this exercise you will be able to:
- Describe the role of each control-plane and worker-node component
- Explain the relationship between a Pod, a ReplicaSet, and a Deployment
- Write a basic Pod manifest in YAML
- Identify the three Service types and when to use each

---

## Background

### Cluster Architecture

```
┌──────────────────────────────────────────────┐
│                CONTROL PLANE                 │
│  ┌────────────┐  ┌──────┐  ┌─────────────┐  │
│  │ API Server │  │ etcd │  │  Scheduler  │  │
│  └────────────┘  └──────┘  └─────────────┘  │
│  ┌──────────────────────┐                    │
│  │  Controller Manager  │                    │
│  └──────────────────────┘                    │
└──────────────────────────────────────────────┘
          │  kubectl / REST
┌─────────┴──────────────────────────────┐
│              WORKER NODE               │
│  ┌─────────┐  ┌────────────┐           │
│  │ kubelet │  │ kube-proxy │           │
│  └─────────┘  └────────────┘           │
│  ┌──────────────────────────────────┐  │
│  │  Pod  [ Container | Container ]  │  │
│  └──────────────────────────────────┘  │
└────────────────────────────────────────┘
```

### Object Hierarchy
```
Deployment  (desired state — rolling updates, rollbacks)
  └── ReplicaSet  (ensures N pod replicas)
        └── Pod  (smallest deployable unit — one or more containers)
```

---

## Requirements

### Requirement 1 — Control Plane Components
For each component below, write **one sentence** explaining its role:

| Component | Role |
|---|---|
| API Server | |
| etcd | |
| Scheduler | |
| Controller Manager | |

### Requirement 2 — Worker Node Components
For each component below, write **one sentence** explaining its role:

| Component | Role |
|---|---|
| kubelet | |
| kube-proxy | |
| Container Runtime | |

### Requirement 3 — Pod vs ReplicaSet vs Deployment
Explain in 2–3 sentences the relationship between a Pod, a ReplicaSet, and a Deployment.  
Why is it best practice to create a Deployment rather than a bare Pod?

### Requirement 4 — Write a Pod Manifest
Complete `pod.yaml` to define a Pod with:
- Name: `spring-pod`
- Namespace: `default`
- Label: `app: springapp`
- One container named `spring-container`
- Image: `springapp:1.0.0`
- Container port: `8080`

### Requirement 5 — Service Types
Complete the table describing each Service type:

| Service Type | Accessible From | Typical Use Case |
|---|---|---|
| ClusterIP | | |
| NodePort | | |
| LoadBalancer | | |

---

## Deliverable
- Complete `answers.md` (Requirements 1, 2, 3, 5)
- Complete `pod.yaml` (Requirement 4)

---

## Hints
- The API Server is the single entry point for all REST calls and kubectl commands
- `etcd` is a distributed key-value store — it holds the entire cluster state
- A Deployment manages a ReplicaSet; a ReplicaSet manages Pods
- `ClusterIP` is the default Service type — not reachable outside the cluster
- `NodePort` opens a port (30000-32767) on every node
- `LoadBalancer` provisions a cloud load balancer (requires a cloud provider)
