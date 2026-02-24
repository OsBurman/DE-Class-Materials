# Day 36 Part 2 — Kubernetes: Architecture, Workloads, Services, Config & Operations
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Day 36 Part 2: Kubernetes — Container Orchestration at Scale

**Subtitle:** K8s architecture, Pods, Deployments, Services, ConfigMaps, Secrets, kubectl, rolling updates, and scaling

**Learning Objectives:**
- Explain the problem Kubernetes solves and why Docker alone isn't enough for production
- Describe the Kubernetes control plane and worker node architecture
- Understand Pods as the fundamental deployable unit
- Write Deployment manifests to manage replicated, self-healing workloads
- Expose applications internally and externally using Services (ClusterIP, NodePort, LoadBalancer)
- Manage non-sensitive configuration with ConfigMaps
- Store sensitive data with Secrets
- Use `kubectl` to deploy, inspect, scale, and manage resources
- Perform rolling updates and rollbacks
- Scale deployments manually

---

### Slide 2 — Why Kubernetes? The Problem Docker Alone Doesn't Solve

**Title:** Why Kubernetes? Problems Docker Alone Can't Solve

**Content:**

Docker solves *packaging* — how to build and run a container on one machine. Kubernetes solves *operations* — how to run hundreds of containers across dozens of machines in production.

**Production Realities That Docker CLI Can't Handle:**

| Challenge | With Docker alone | With Kubernetes |
|-----------|------------------|----------------|
| **High availability** | Single container — if it crashes, service is down | Runs N replicas — if one dies, traffic routes to others |
| **Self-healing** | You must manually restart crashed containers | Automatically restarts failed containers |
| **Scaling** | Manual `docker run` of more containers | `kubectl scale --replicas=20` — instant |
| **Rolling updates** | Manual stop/start (downtime) | Zero-downtime rolling update built in |
| **Load balancing** | Need external tool | Built into Services |
| **Service discovery** | Manual IP management | Stable DNS names for every service |
| **Multi-machine** | Difficult to manage manually | Schedules containers across any number of nodes |
| **Config management** | `docker run -e ...` per container | ConfigMaps and Secrets injected automatically |
| **Rollbacks** | Manual re-deploy of old image | `kubectl rollout undo` — instant |

**The Mental Model Shift:**
- Docker: you say **what** to run and **where** (specific host)
- Kubernetes: you say **what** to run and **how many**, and K8s figures out **where**

**Kubernetes in the Real World:**
- Industry standard for production container orchestration
- Runs on every major cloud: AWS EKS, Google GKE, Azure AKS
- Manages containers at Google (billions of containers/week), Spotify, Uber, Airbnb, Netflix
- Open-source, originally developed by Google based on their internal "Borg" system, donated to CNCF in 2014

---

### Slide 3 — Kubernetes Architecture — Control Plane

**Title:** K8s Architecture — The Control Plane (Master Node)

**Content:**

Kubernetes has a two-tier architecture: the **Control Plane** (brain) and **Worker Nodes** (muscle). The control plane makes decisions. Worker nodes execute them.

```
┌──────────────────────────────────────────────────────┐
│                    CONTROL PLANE                      │
│                                                       │
│  ┌─────────────┐    ┌──────────┐    ┌─────────────┐  │
│  │  API Server  │    │  etcd    │    │  Scheduler  │  │
│  │ (kube-apiserver)  │(key-value│    │             │  │
│  │             │    │  store)  │    │ "where does  │  │
│  │ Central hub │    │          │    │  this pod   │  │
│  │ for all     │    │ Cluster  │    │  run?"      │  │
│  │ operations  │    │ state DB │    └─────────────┘  │
│  └─────────────┘    └──────────┘                     │
│                                     ┌─────────────┐  │
│                                     │  Controller  │  │
│                                     │  Manager    │  │
│                                     │             │  │
│                                     │ "are we at  │  │
│                                     │  desired    │  │
│                                     │  state?"    │  │
│                                     └─────────────┘  │
└──────────────────────────────────────────────────────┘
```

**Control Plane Components:**

**API Server (`kube-apiserver`)**
- The central communication hub — ALL operations go through it
- Every `kubectl` command hits the API server's REST API
- Validates and processes all configuration changes
- The only component that talks to etcd

**etcd**
- A distributed, highly-available key-value store
- Stores the **entire cluster state**: what resources exist, their configuration, current status
- If etcd is lost, the cluster is lost — this is why etcd is always run with replicas in production
- Think of it as the cluster's database / "single source of truth"

**Scheduler (`kube-scheduler`)**
- Watches for newly created Pods that don't have a node assigned yet
- Selects the best worker node for each Pod based on: available resources (CPU/memory), node labels, pod constraints, affinity rules
- Does not run the Pod — just assigns it to a node and tells the API server

**Controller Manager (`kube-controller-manager`)**
- Runs a collection of controller loops that continuously check: "Is the actual state the same as the desired state?"
- **ReplicaSet controller**: if desired replicas = 3 but only 2 are running, creates a third
- **Node controller**: monitors nodes and marks them as unavailable if they stop responding
- **Deployment controller**: manages rolling updates

---

### Slide 4 — Kubernetes Architecture — Worker Nodes

**Title:** K8s Architecture — Worker Nodes

**Content:**

Worker nodes are the machines (physical or virtual) that actually run your application containers. A Kubernetes cluster has at least one worker node, but production clusters typically have tens or hundreds.

```
┌─────────────────────────────────────────────────────┐
│                    WORKER NODE                       │
│                                                      │
│  ┌──────────┐  ┌─────────────┐  ┌────────────────┐  │
│  │ kubelet  │  │ kube-proxy  │  │Container Runtime│  │
│  │          │  │             │  │ (containerd)    │  │
│  │ "make    │  │ "route      │  │                 │  │
│  │ pods run │  │  network    │  │ Actually runs   │  │
│  │  on this │  │  traffic"   │  │ containers      │  │
│  │  node"   │  │             │  │                 │  │
│  └──────────┘  └─────────────┘  └────────────────┘  │
│                                                      │
│  ┌──────────────────────────────────────────────┐    │
│  │  Pod A          Pod B          Pod C          │    │
│  │  ┌──────┐       ┌──────┐       ┌──────┐      │    │
│  │  │ App  │       │ App  │       │ App  │      │    │
│  │  └──────┘       └──────┘       └──────┘      │    │
│  └──────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────┘
```

**Worker Node Components:**

**kubelet**
- The node agent — runs on every worker node
- Watches the API server for Pods scheduled to its node
- Ensures the containers defined in a Pod are running and healthy
- Reports node and pod status back to the API server
- Performs health checks (liveness/readiness probes)

**kube-proxy**
- Runs on every worker node
- Maintains network rules (iptables/ipvs) that implement Service routing
- When you access a Service's IP, kube-proxy routes that traffic to the correct Pod(s)
- Implements load balancing across pods behind a Service

**Container Runtime**
- The software that actually runs containers
- Kubernetes uses **containerd** (same one Docker uses internally) as the standard runtime
- Note: Kubernetes does NOT require Docker — it uses containerd directly

**Node Sizing Considerations:**
- Control plane: typically dedicated nodes (not for your app workloads)
- Worker nodes: sized based on your application's CPU and memory needs
- Cloud providers: K8s-as-a-service (EKS, GKE, AKS) manages the control plane for you — you only manage worker nodes

---

### Slide 5 — Pods — The Fundamental Unit

**Title:** Pods — Kubernetes' Fundamental Deployable Unit

**Content:**

A **Pod** is the smallest deployable unit in Kubernetes. A Pod is NOT a container — it's a wrapper around one or more containers that share:
- The same **network namespace** (same IP address, same ports)
- The same **storage volumes**

In practice, most Pods contain exactly one container. Multi-container Pods (the sidecar pattern) are used when containers must be tightly coupled — for example, a log-shipping sidecar that reads the main app's log files.

**A Pod YAML Manifest:**
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: bookstore-pod
  labels:                         # labels are key-value pairs for selection
    app: bookstore
    version: "1.0"
spec:
  containers:
    - name: bookstore
      image: myuser/bookstore:1.0
      ports:
        - containerPort: 8080
      env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: SERVER_PORT
          value: "8080"
      resources:
        requests:                 # minimum guaranteed resources
          cpu: "250m"             # 250 millicores = 0.25 CPU
          memory: "256Mi"
        limits:                   # maximum allowed resources
          cpu: "500m"
          memory: "512Mi"
```

**Key Pod Concepts:**

**Pods Are Ephemeral:**
- Pods are designed to be disposable — they can die and be replaced at any time
- A Pod's IP address changes every time it's recreated
- NEVER connect directly to a Pod IP — use a Service (stable endpoint)

**Labels:**
- Labels are key-value metadata attached to any K8s object
- Labels are how Services and Deployments find their Pods — via **label selectors**
- Example: `app: bookstore` — a Service with selector `app: bookstore` routes to all pods with that label

**Resource Requests and Limits:**
- `requests` — the scheduler guarantees this much CPU and memory for the Pod
- `limits` — the Pod is killed/throttled if it exceeds these
- `250m` CPU = 250 millicores = 0.25 of one CPU core
- `256Mi` = 256 mebibytes of RAM

**Don't create Pods directly in production — use Deployments instead.**

---

### Slide 6 — Deployments — Managing Replicated Workloads

**Title:** Deployments — Self-Healing, Versioned Pod Management

**Content:**

You almost never create Pods directly. You create **Deployments**, which manage Pods on your behalf. A Deployment is a higher-level object that:
- Maintains a desired number of Pod replicas at all times
- Performs rolling updates when you change the image
- Provides easy rollback to a previous version
- Self-heals: if a Pod crashes, the Deployment controller creates a replacement

**Deployment YAML Manifest:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: bookstore-deployment
  labels:
    app: bookstore
spec:
  replicas: 3                         # desired number of pod copies
  selector:
    matchLabels:
      app: bookstore                  # this Deployment manages Pods with this label
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1                     # max extra pods during update
      maxUnavailable: 0               # zero downtime — always keep 3 pods available
  template:                           # pod template — what each pod looks like
    metadata:
      labels:
        app: bookstore                # MUST match selector.matchLabels
    spec:
      containers:
        - name: bookstore
          image: myuser/bookstore:1.0
          ports:
            - containerPort: 8080
          envFrom:
            - configMapRef:
                name: bookstore-config       # inject all ConfigMap keys as env vars
          env:
            - name: DB_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: bookstore-secrets    # inject Secret value
                  key: DB_PASSWORD
          resources:
            requests:
              cpu: "250m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
          livenessProbe:                     # is the container alive?
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
          readinessProbe:                    # is the container ready for traffic?
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 5
```

**Liveness vs. Readiness Probes:**
- **Liveness probe**: "Is the container still alive?" If it fails, K8s kills and restarts the container.
- **Readiness probe**: "Is the container ready to receive traffic?" If it fails, K8s removes the Pod from the Service's endpoint list — no traffic is routed to it.

For Spring Boot: expose `/actuator/health/liveness` and `/actuator/health/readiness` (Spring Boot Actuator provides these automatically).

---

### Slide 7 — ReplicaSets and Self-Healing

**Title:** ReplicaSets — Maintaining Desired State

**Content:**

When you create a Deployment, it automatically creates a **ReplicaSet**. A ReplicaSet's job is simple: ensure exactly N copies of a Pod are running at all times.

**The ReplicaSet Controller Loop:**
```
Every few seconds:
  Actual pods running = 2
  Desired pods (from Deployment) = 3
  → Create 1 more Pod
```

**You Don't Create ReplicaSets Directly:**
- Deployments create and manage ReplicaSets for you
- When you do a rolling update, the Deployment creates a new ReplicaSet and scales down the old one
- Old ReplicaSets are kept (with 0 replicas) to enable rollbacks

**Self-Healing in Action:**
```bash
# You have 3 replicas running
kubectl get pods
# NAME                          READY   STATUS    RESTARTS   AGE
# bookstore-6d4f9b-abc12        1/1     Running   0          5m
# bookstore-6d4f9b-def34        1/1     Running   0          5m
# bookstore-6d4f9b-ghi56        1/1     Running   0          5m

# Delete one pod manually (simulating a crash)
kubectl delete pod bookstore-6d4f9b-abc12

# Immediately check again
kubectl get pods
# bookstore-6d4f9b-def34        1/1     Running   0          5m
# bookstore-6d4f9b-ghi56        1/1     Running   0          5m
# bookstore-6d4f9b-jkl78        1/1     Running   0          4s   ← NEW pod created
```

The deleted Pod is replaced within seconds — automatically, without any human intervention. This is self-healing.

**Node Failure:**
If an entire worker node goes down, K8s detects the node is unreachable (within ~5 minutes by default), marks all Pods on that node as unavailable, and reschedules them on healthy nodes. Your application continues running without data loss or manual intervention.

---

### Slide 8 — Services — Stable Networking for Pods

**Title:** Services — Stable Endpoints for Ephemeral Pods

**Content:**

Pods are ephemeral — they die and are replaced, and each new Pod gets a different IP address. If your frontend container had hardcoded the backend Pod's IP, the connection would break every time the backend Pod restarts. **Services** solve this by providing a stable network endpoint.

A Service has a fixed IP (ClusterIP) and DNS name that never changes, even as the underlying Pods come and go. It load-balances traffic across all healthy Pods matching its selector.

**Service YAML:**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: bookstore-service
spec:
  selector:
    app: bookstore               # routes traffic to pods with this label
  ports:
    - name: http
      port: 80                   # port the Service listens on (cluster-internal)
      targetPort: 8080           # port on the Pod to forward to
  type: ClusterIP                # ClusterIP | NodePort | LoadBalancer
```

**The Three Service Types:**

**`ClusterIP` (Default — internal only):**
```
Internal pod/service → ClusterIP:80 → Pod(s)
                                        ↑
                               Not reachable from outside cluster
```
Use for: databases, internal microservices, any service that should not be directly externally accessible.

**`NodePort` (External via node IP):**
```
External client → NodeIP:30080 → ClusterIP:80 → Pod(s)
```
- Opens a port (30000–32767) on EVERY worker node
- External traffic hits `<any-node-ip>:<node-port>` and gets routed to the service
- Use for: development/testing, on-premise clusters without cloud load balancers
- Not ideal for production — exposes specific ports on your node IPs

**`LoadBalancer` (External via cloud load balancer):**
```
Internet → Cloud LB (AWS ELB) → NodePort → ClusterIP → Pod(s)
```
- Provisions a cloud provider's load balancer (AWS ELB, GCP Load Balancer)
- Gets a stable external IP/DNS name from the cloud provider
- Use for: production external traffic
- Costs extra (cloud load balancers aren't free)

**Service DNS:**
Every Service gets a DNS name automatically: `<service-name>.<namespace>.svc.cluster.local`
Within the same namespace, just use `<service-name>`. Your Spring Boot app can connect to PostgreSQL using `jdbc:postgresql://postgres-service:5432/bookstoredb`.

---

### Slide 9 — ConfigMaps — Non-Sensitive Configuration

**Title:** ConfigMaps — Externalizing Application Configuration

**Content:**

A ConfigMap stores non-sensitive key-value configuration data separately from your application image. This follows the 12-factor app principle: configuration should be separate from code.

**Why ConfigMaps?**
- Different environments (dev, staging, prod) need different configuration
- If you bake config into the image, you need different images per environment
- With ConfigMaps, one image works everywhere — inject different config per environment

**ConfigMap YAML:**
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: bookstore-config
data:
  SPRING_PROFILES_ACTIVE: "prod"
  SERVER_PORT: "8080"
  LOG_LEVEL: "INFO"
  SPRING_DATASOURCE_URL: "jdbc:postgresql://postgres-service:5432/bookstoredb"
  SPRING_DATASOURCE_USERNAME: "admin"
```

**Two Ways to Use a ConfigMap in a Pod:**

**Option 1: Inject all keys as environment variables (most common):**
```yaml
spec:
  containers:
    - name: bookstore
      envFrom:
        - configMapRef:
            name: bookstore-config    # ALL keys become env vars
```

**Option 2: Inject a specific key:**
```yaml
env:
  - name: SPRING_PROFILES_ACTIVE
    valueFrom:
      configMapKeyRef:
        name: bookstore-config
        key: SPRING_PROFILES_ACTIVE
```

**Option 3: Mount as a file (useful for config files):**
```yaml
volumeMounts:
  - name: config-volume
    mountPath: /app/config
volumes:
  - name: config-volume
    configMap:
      name: bookstore-config
```

**kubectl commands:**
```bash
kubectl create configmap bookstore-config \
  --from-literal=SPRING_PROFILES_ACTIVE=prod \
  --from-literal=LOG_LEVEL=INFO

kubectl get configmap bookstore-config
kubectl describe configmap bookstore-config
```

---

### Slide 10 — Secrets — Sensitive Data Management

**Title:** Secrets — Handling Sensitive Configuration

**Content:**

Secrets are like ConfigMaps but for sensitive data: passwords, tokens, TLS certificates, API keys. They are base64-encoded (not encrypted by default) and stored in etcd. The key difference from ConfigMaps is access control — RBAC policies restrict who can read Secrets.

**Creating a Secret:**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: bookstore-secrets
type: Opaque
data:
  DB_PASSWORD: cGFzc3dvcmQxMjM=         # base64 of "password123"
  JWT_SECRET: c3VwZXJzZWNyZXRrZXk=      # base64 of "supersecretkey"
```

**Generating base64 values:**
```bash
echo -n "password123" | base64          # cGFzc3dvcmQxMjM=
echo "cGFzc3dvcmQxMjM=" | base64 -d    # password123 (decode)
```

**Creating Secrets imperatively (better — avoids base64 in YAML):**
```bash
kubectl create secret generic bookstore-secrets \
  --from-literal=DB_PASSWORD=password123 \
  --from-literal=JWT_SECRET=supersecretkey
```

**Using Secrets in a Deployment:**
```yaml
# As environment variables (most common)
env:
  - name: SPRING_DATASOURCE_PASSWORD
    valueFrom:
      secretKeyRef:
        name: bookstore-secrets
        key: DB_PASSWORD
  - name: JWT_SECRET
    valueFrom:
      secretKeyRef:
        name: bookstore-secrets
        key: JWT_SECRET

# Mount as files (for TLS certs, SSH keys)
volumeMounts:
  - name: certs
    mountPath: /app/certs
    readOnly: true
volumes:
  - name: certs
    secret:
      secretName: tls-certs
```

**Important Security Notes:**
- Base64 is encoding, NOT encryption — anyone who can access etcd can decode Secrets
- Production security: encrypt etcd at rest, use RBAC to restrict Secret access, or use external secret managers (AWS Secrets Manager, HashiCorp Vault)
- Never commit Secrets YAML to version control — use `kubectl create secret` or external secret management
- Spring Boot: Secrets injected as env vars are read by `application.yml` via `${DB_PASSWORD}`

---

### Slide 11 — kubectl CLI — Essential Commands

**Title:** kubectl — The Kubernetes CLI

**Content:**

`kubectl` (pronounced "kube-control" or "kube-cuddle" depending on who you ask) is the CLI for interacting with a Kubernetes cluster. It communicates with the API server.

**Applying Manifests:**
```bash
# Apply a single file (create or update)
kubectl apply -f deployment.yaml

# Apply all YAML files in a directory
kubectl apply -f ./k8s/

# Delete resources defined in a file
kubectl delete -f deployment.yaml
```

**Viewing Resources:**
```bash
# List resources
kubectl get pods                           # pods in default namespace
kubectl get pods -n kube-system            # pods in kube-system namespace
kubectl get pods -o wide                   # more detail: node, IP
kubectl get pods --watch                   # live updates
kubectl get deployments
kubectl get services
kubectl get configmaps
kubectl get all                            # all resource types

# Detailed description (events, status, config)
kubectl describe pod bookstore-abc12
kubectl describe deployment bookstore-deployment
kubectl describe service bookstore-service

# Get raw YAML of a running resource
kubectl get deployment bookstore-deployment -o yaml
```

**Logs and Exec:**
```bash
kubectl logs bookstore-pod-abc12                    # pod logs
kubectl logs -f bookstore-pod-abc12                 # follow
kubectl logs deployment/bookstore-deployment        # logs from any pod in deployment
kubectl logs bookstore-pod-abc12 -c sidecar-name   # specific container in multi-container pod

kubectl exec -it bookstore-pod-abc12 -- bash        # shell in pod
kubectl exec bookstore-pod-abc12 -- env             # list env vars
```

**Scaling:**
```bash
kubectl scale deployment bookstore-deployment --replicas=5
kubectl scale deployment bookstore-deployment --replicas=1    # scale down
```

**Port Forwarding (for local testing without a Service):**
```bash
kubectl port-forward pod/bookstore-pod-abc12 8080:8080
kubectl port-forward service/bookstore-service 8080:80
# Now localhost:8080 forwards to the pod/service
```

**Cluster Info:**
```bash
kubectl cluster-info
kubectl get nodes
kubectl get nodes -o wide
kubectl top nodes                  # CPU/memory usage (requires metrics-server)
kubectl top pods
```

---

### Slide 12 — Full Deployment Example — Spring Boot on Kubernetes

**Title:** Deploying a Spring Boot App — Complete Manifest Set

**Content:**

A complete Kubernetes deployment for a Spring Boot application consists of these resources in order:

```
ConfigMap → Secret → Deployment → Service
```

**1. ConfigMap:**
```yaml
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: bookstore-config
data:
  SPRING_PROFILES_ACTIVE: "prod"
  SPRING_DATASOURCE_URL: "jdbc:postgresql://postgres-service:5432/bookstoredb"
  SPRING_DATASOURCE_USERNAME: "admin"
  SERVER_PORT: "8080"
```

**2. Secret:**
```bash
kubectl create secret generic bookstore-secrets \
  --from-literal=DB_PASSWORD=yourPassword \
  --from-literal=JWT_SECRET=yourJwtSecret
```

**3. Deployment:**
```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: bookstore-deployment
spec:
  replicas: 3
  selector:
    matchLabels:
      app: bookstore
  template:
    metadata:
      labels:
        app: bookstore
    spec:
      containers:
        - name: bookstore
          image: myuser/bookstore:1.0
          ports:
            - containerPort: 8080
          envFrom:
            - configMapRef:
                name: bookstore-config
          env:
            - name: SPRING_DATASOURCE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: bookstore-secrets
                  key: DB_PASSWORD
          resources:
            requests: { cpu: "250m", memory: "256Mi" }
            limits:   { cpu: "500m", memory: "512Mi" }
```

**4. Service:**
```yaml
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: bookstore-service
spec:
  selector:
    app: bookstore
  ports:
    - port: 80
      targetPort: 8080
  type: LoadBalancer    # or ClusterIP for internal, NodePort for dev
```

**Deploy everything:**
```bash
kubectl apply -f configmap.yaml
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

kubectl get all                          # verify everything is running
kubectl describe deployment bookstore-deployment
```

---

### Slide 13 — Rolling Updates and Rollbacks

**Title:** Rolling Updates and Rollbacks — Zero-Downtime Deployments

**Content:**

Kubernetes Deployments perform **rolling updates** by default — new pods are brought up before old pods are taken down, ensuring continuous availability.

**How a Rolling Update Works:**
```
Initial state: 3 pods running v1.0
            [v1.0] [v1.0] [v1.0]

Step 1: Start 1 new v2.0 pod
            [v1.0] [v1.0] [v1.0] [v2.0]

Step 2: v2.0 passes readiness probe — remove 1 v1.0
            [v1.0] [v1.0] [v2.0]

Step 3: Start another v2.0
            [v1.0] [v1.0] [v2.0] [v2.0]

Step 4: Second v2.0 ready — remove second v1.0
            [v1.0] [v2.0] [v2.0]

...continues until all pods are v2.0
            [v2.0] [v2.0] [v2.0]

At no point are there fewer than 3 healthy pods (maxUnavailable: 0)
```

**Performing a Rolling Update:**
```bash
# Update the image version — triggers rolling update automatically
kubectl set image deployment/bookstore-deployment bookstore=myuser/bookstore:2.0

# Watch the rollout in real time
kubectl rollout status deployment/bookstore-deployment
# Waiting for deployment "bookstore-deployment" rollout to finish: 1 out of 3 updated...
# Waiting for deployment "bookstore-deployment" rollout to finish: 2 out of 3 updated...
# deployment "bookstore-deployment" successfully rolled out

# View rollout history
kubectl rollout history deployment/bookstore-deployment
# REVISION  CHANGE-CAUSE
# 1         <none>
# 2         <none>
```

**Rolling Back:**
```bash
# Rollback to the previous version
kubectl rollout undo deployment/bookstore-deployment

# Rollback to a specific revision
kubectl rollout undo deployment/bookstore-deployment --to-revision=1

# Add an annotation (change cause) for history tracking
kubectl annotate deployment bookstore-deployment \
  kubernetes.io/change-cause="Update to v2.0: new checkout feature"
```

**Strategy Configuration (in Deployment spec):**
```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 1          # max EXTRA pods above desired during update
    maxUnavailable: 0    # max pods unavailable during update (0 = zero downtime)
```

---

### Slide 14 — Scaling and Self-Healing

**Title:** Scaling Applications in Kubernetes

**Content:**

**Manual Scaling:**
```bash
# Scale up
kubectl scale deployment bookstore-deployment --replicas=10

# Scale down
kubectl scale deployment bookstore-deployment --replicas=2

# Watch pods being created/terminated
kubectl get pods --watch
```

**Self-Healing — What Happens When Things Go Wrong:**

```
Scenario 1: Pod crashes
  Before: [pod1] [pod2] [pod3]
  Pod3 crashes
  K8s detects: desired=3, actual=2
  Action: Creates pod4 automatically
  After:  [pod1] [pod2] [pod4]   (within seconds)

Scenario 2: Worker node goes down
  Before: Node A [pod1, pod2], Node B [pod3]
  Node A becomes unreachable
  K8s detects after ~5 minutes: pods on Node A are unschedulable
  Action: Reschedules pod1 and pod2 to Node B or Node C
  After:  Node B [pod3, pod1, pod2] — all pods running again
```

**Horizontal Pod Autoscaler (HPA) — Awareness Level:**
Kubernetes can automatically scale Pods based on CPU/memory utilization:
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: bookstore-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: bookstore-deployment
  minReplicas: 2
  maxReplicas: 20
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70   # scale up if avg CPU > 70%
```
HPA is awareness-level for this course — production implementation requires metrics-server.

---

### Slide 15 — Running K8s Locally — minikube

**Title:** Local Kubernetes with minikube

**Content:**

For local development and learning, you need a local Kubernetes cluster. The most common options:

**minikube — Single-Node Cluster:**
```bash
# Install (macOS)
brew install minikube

# Start a local single-node cluster
minikube start

# Check cluster status
minikube status
kubectl get nodes
# NAME       STATUS   ROLES           AGE   VERSION
# minikube   Ready    control-plane   1m    v1.28.0

# Open the Kubernetes dashboard
minikube dashboard

# Get the cluster IP (for NodePort services)
minikube ip

# SSH into the minikube node
minikube ssh

# Stop the cluster
minikube stop

# Delete the cluster (start fresh)
minikube delete
```

**Docker Desktop Built-in Kubernetes:**
- Docker Desktop includes a single-node K8s cluster
- Enable in Docker Desktop → Settings → Kubernetes → Enable Kubernetes
- No separate install needed if you have Docker Desktop
- `kubectl` automatically configured to point to it

**Other Options:**
| Tool | Description |
|------|-------------|
| `kind` | Kubernetes in Docker — nodes are Docker containers; fast for CI |
| `k3s` | Lightweight K8s — ideal for Raspberry Pi, edge, or minimal VMs |
| Docker Desktop K8s | Single-node, built into Docker Desktop |

**Deploying to minikube:**
```bash
# minikube has its own image cache — build images into it
eval $(minikube docker-env)      # point Docker CLI to minikube's Docker daemon
docker build -t bookstore:1.0 .  # image is now inside minikube

# Deploy
kubectl apply -f k8s/

# Access a NodePort or LoadBalancer service
minikube service bookstore-service --url
```

---

### Slide 16 — Part 2 Summary — Kubernetes Quick Reference

**Title:** Part 2 Summary — Kubernetes Essentials

**Content:**

**Architecture Quick Reference:**

| Component | Location | Responsibility |
|-----------|----------|---------------|
| API Server | Control plane | Central communication hub |
| etcd | Control plane | Cluster state store |
| Scheduler | Control plane | Pod placement decisions |
| Controller Manager | Control plane | Maintain desired state |
| kubelet | Worker node | Run pods, report status |
| kube-proxy | Worker node | Service network routing |
| containerd | Worker node | Actually run containers |

**Resource Quick Reference:**

| Resource | What It Does |
|----------|-------------|
| `Pod` | Smallest deployable unit — one or more containers |
| `Deployment` | Manages replicated, self-healing Pods + rolling updates |
| `ReplicaSet` | Maintains N pod replicas (managed by Deployment) |
| `Service` | Stable network endpoint; ClusterIP / NodePort / LoadBalancer |
| `ConfigMap` | Non-sensitive key-value configuration |
| `Secret` | Sensitive data (passwords, tokens) — base64 encoded |
| `HPA` | Auto-scale pods based on CPU/memory |
| `Namespace` | Logical cluster partition for multi-team isolation |

**kubectl Cheat Sheet:**
```bash
kubectl apply -f file.yaml              # create or update resources
kubectl get pods / deployments / services / all
kubectl describe pod <name>            # full details + events
kubectl logs -f <pod>                  # follow pod logs
kubectl exec -it <pod> -- bash         # shell in pod
kubectl scale deployment <name> --replicas=N
kubectl set image deployment/<name> <container>=<image>:<tag>
kubectl rollout status deployment/<name>
kubectl rollout undo deployment/<name>
kubectl port-forward svc/<name> 8080:80
kubectl delete -f file.yaml
```

**Day 36 Complete — Key Concepts:**
- Docker: containers vs VMs, Dockerfile, multi-stage builds, Compose
- K8s: desired state model, control plane, worker nodes
- Pods are ephemeral → use Services for stable endpoints
- Deployments manage replicas, rolling updates, and rollbacks
- ConfigMaps for config, Secrets for credentials
- `kubectl apply -f` is the deployment workflow
