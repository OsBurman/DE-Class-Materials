# Exercise 08 — Kubernetes Architecture and Core Objects — SOLUTION

---

## Requirement 1 — Control Plane Components

| Component | Role |
|---|---|
| API Server | The front-end for the Kubernetes control plane; it validates and processes all REST API calls from `kubectl`, other components, and external clients, and is the only component that reads from and writes to `etcd`. |
| etcd | A highly available, distributed key-value store that holds the entire cluster state (objects, configurations, secrets); it is the single source of truth for the cluster. |
| Scheduler | Watches for newly created Pods that have no assigned node and selects a suitable worker node based on resource requirements, affinity rules, and taints/tolerations. |
| Controller Manager | Runs a collection of control-loop controllers (Node, Replication, Endpoints, ServiceAccount, etc.) that continuously reconcile the actual cluster state with the desired state stored in `etcd`. |

---

## Requirement 2 — Worker Node Components

| Component | Role |
|---|---|
| kubelet | An agent that runs on every worker node; it watches the API Server for Pods scheduled to its node and instructs the container runtime to start, stop, or restart containers, then reports their status back. |
| kube-proxy | Maintains network rules (iptables / IPVS) on each node to implement Service abstractions, forwarding traffic to the correct Pod endpoints for load balancing and service discovery. |
| Container Runtime | The software responsible for pulling images and running containers (e.g., containerd, CRI-O); it implements the Container Runtime Interface (CRI) so the kubelet can work with different runtimes. |

---

## Requirement 3 — Pod vs ReplicaSet vs Deployment

A **Pod** is the smallest deployable unit in Kubernetes and wraps one or more containers that share networking and storage. A **ReplicaSet** ensures a specified number of Pod replicas are running at all times, restarting or creating Pods when the count drops below the desired state. A **Deployment** sits above a ReplicaSet and manages its lifecycle, enabling declarative rolling updates, rollbacks, and scaling.

It is best practice to create a Deployment rather than a bare Pod because a bare Pod is not automatically restarted if it crashes or its node fails. A Deployment's ReplicaSet constantly reconciles the desired replica count, and the Deployment layer adds zero-downtime rolling updates and one-command rollbacks.

---

## Requirement 5 — Service Types

| Service Type | Accessible From | Typical Use Case |
|---|---|---|
| ClusterIP | Only from within the cluster (other Pods) | Internal microservice-to-microservice communication; the default type |
| NodePort | From outside the cluster via `<NodeIP>:<NodePort>` (port range 30000–32767) | Local development, testing, or simple external access without a cloud load balancer |
| LoadBalancer | From the internet via a cloud-provisioned external IP | Production workloads on managed Kubernetes (EKS, GKE, AKS) that need a stable public endpoint |
