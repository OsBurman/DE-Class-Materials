# Day 36 — Docker & Kubernetes Review

## Quick Reference: Docker

### Core Concepts

| Concept | Definition |
|---|---|
| **Image** | Read-only blueprint/template — layers built by Dockerfile instructions |
| **Container** | Running instance of an image — adds a thin writable layer on top |
| **Dockerfile** | Instructions file that builds an image layer by layer |
| **Registry** | Storage for Docker images (DockerHub, ECR, GHCR) |
| **Docker Compose** | Tool to define and run multi-container apps from a single YAML file |
| **Volume** | Persistent storage that survives container restarts and removals |
| **Network** | Virtual network letting containers communicate with each other |

---

### Dockerfile Instructions Quick Reference

| Instruction | Purpose | Example |
|---|---|---|
| `FROM` | Base image | `FROM eclipse-temurin:21-jre-jammy` |
| `WORKDIR` | Set working directory | `WORKDIR /app` |
| `COPY` | Copy files from build context | `COPY target/*.jar app.jar` |
| `ADD` | Like COPY but can extract tars / fetch URLs (prefer COPY) | `ADD app.tar.gz /app` |
| `RUN` | Execute command during build (creates a layer) | `RUN mvn clean package -DskipTests` |
| `ENV` | Set environment variable (available at runtime) | `ENV JAVA_OPTS="-Xmx512m"` |
| `ARG` | Build-time variable (not available at runtime) | `ARG JAR_FILE=target/*.jar` |
| `EXPOSE` | Document a port (does NOT publish it) | `EXPOSE 8080` |
| `ENTRYPOINT` | Main process — not easily overridden | `ENTRYPOINT ["java", "-jar", "app.jar"]` |
| `CMD` | Default arguments to ENTRYPOINT — overridable | `CMD ["--spring.profiles.active=prod"]` |
| `USER` | Switch to non-root user | `USER appuser` |
| `LABEL` | Metadata | `LABEL version="1.0"` |
| `VOLUME` | Declare a mount point | `VOLUME /data` |

---

### Standard Spring Boot Multi-Stage Dockerfile

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve -q
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup
COPY --from=build /app/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Xmx512m", "-jar", "app.jar"]
```

---

### Docker CLI — Images

| Command | Description |
|---|---|
| `docker build -t name:tag .` | Build image from Dockerfile in current directory |
| `docker build -t name:tag --no-cache .` | Build forcing fresh layers (no cache) |
| `docker images` | List all local images |
| `docker pull ubuntu:22.04` | Pull image from registry |
| `docker tag name:tag user/name:tag` | Tag image for pushing to registry |
| `docker push user/name:tag` | Push image to registry |
| `docker rmi name:tag` | Remove a local image |
| `docker image prune` | Remove all unused images |
| `docker history name:tag` | Show image layers and sizes |
| `docker login` | Authenticate to Docker Hub |

---

### Docker CLI — Containers

| Command | Description |
|---|---|
| `docker run -d -p 8080:8080 --name api myapp:1.0` | Run detached, map ports, name the container |
| `docker run -it ubuntu bash` | Run interactive with a terminal |
| `docker run --rm myapp:1.0` | Remove container automatically when stopped |
| `docker run -e SPRING_PROFILES_ACTIVE=prod myapp:1.0` | Pass environment variable |
| `docker ps` | List running containers |
| `docker ps -a` | List all containers including stopped |
| `docker stop api` | Gracefully stop container (SIGTERM + SIGKILL after timeout) |
| `docker kill api` | Force stop immediately (SIGKILL) |
| `docker rm api` | Remove stopped container |
| `docker logs api` | View container stdout/stderr |
| `docker logs -f api` | Follow logs in real time |
| `docker exec -it api bash` | Open shell in running container |
| `docker inspect api` | Full container metadata as JSON |
| `docker stats` | Live CPU/memory usage for all containers |
| `docker container prune` | Remove all stopped containers |

---

### Docker Volumes

| Type | Description | Usage |
|---|---|---|
| **Named volume** | Managed by Docker, stored in Docker's area | `-v myvolume:/data/db` |
| **Bind mount** | Maps a host directory to a container path | `-v /host/path:/container/path` |
| **tmpfs** | Stored in host memory only, not persisted | `--tmpfs /tmp` |

```bash
docker volume create myvolume
docker volume ls
docker volume rm myvolume
docker volume prune
```

---

### Docker Networking

| Network Type | Description |
|---|---|
| `bridge` (default) | Isolated virtual network; containers communicate by name within the same network |
| `host` | Container shares host network stack directly |
| `none` | No network access |

```bash
docker network create my-network
docker run --network my-network --name db postgres:16
docker run --network my-network --name api myapp:1.0
# "api" container can reach "db" container by hostname "db"
```

---

### Docker Compose Reference

```yaml
services:
  app:
    build: .                              # build from local Dockerfile
    image: myapp:1.0                     # or use pre-built image
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/bookstoredb
    depends_on:
      db:
        condition: service_healthy        # wait for DB health check
    networks:
      - app-network

  db:
    image: postgres:16
    environment:
      POSTGRES_DB: bookstoredb
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: secret
    volumes:
      - postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U admin -d bookstoredb"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - app-network

volumes:
  postgres-data:

networks:
  app-network:
```

| Compose Command | Description |
|---|---|
| `docker compose up -d` | Start all services in background |
| `docker compose up --build` | Rebuild images then start |
| `docker compose down` | Stop and remove containers and networks |
| `docker compose down -v` | Also remove volumes |
| `docker compose ps` | List compose services and status |
| `docker compose logs -f app` | Follow logs for the `app` service |
| `docker compose restart app` | Restart one service |
| `docker compose exec app bash` | Shell into a running service |
| `docker compose build` | Rebuild all images without starting |

---

### Docker Best Practices

- Use JRE base images for runtime, not JDK (`eclipse-temurin:21-jre-jammy`)
- Run as a non-root user (`USER appuser`)
- Use multi-stage builds to keep images small (~260MB vs ~700MB)
- Add `.dockerignore` — exclude `target/`, `.git/`, `*.md`, `*.log`
- Never hardcode secrets in a Dockerfile or image layer
- Add `-XX:+UseContainerSupport` — lets JVM respect container memory limits
- Pin image versions — avoid `latest` in production (use semantic versions or git SHAs)
- Cache dependencies separately: `COPY pom.xml .` → `RUN mvn dependency:resolve` → `COPY src .`

---

---

## Quick Reference: Kubernetes

### Core Architecture

| Component | Location | Role |
|---|---|---|
| **API Server** | Control Plane | Central hub — all communication goes through it |
| **etcd** | Control Plane | Distributed key-value store — cluster state database |
| **Scheduler** | Control Plane | Assigns pending Pods to worker nodes |
| **Controller Manager** | Control Plane | Runs reconciliation loops — enforces desired state |
| **kubelet** | Worker Node | Node agent — ensures assigned Pods are running |
| **kube-proxy** | Worker Node | Network routing rules for Services |
| **containerd** | Worker Node | Container runtime — actually creates/runs containers |

---

### Kubernetes Resource Types

| Resource | Purpose |
|---|---|
| **Pod** | Smallest unit — one or more containers sharing a network and storage namespace |
| **Deployment** | Manages a ReplicaSet; declares desired replica count, image, config; supports rolling updates |
| **ReplicaSet** | Ensures N Pod replicas always running (usually managed by Deployments, not created directly) |
| **Service** | Stable network endpoint with load balancing over a set of Pods |
| **ConfigMap** | Non-sensitive key-value configuration injected as env vars or files |
| **Secret** | Sensitive data (passwords, tokens) stored base64-encoded, injected as env vars or files |
| **Namespace** | Logical partition within a cluster for multi-team or multi-environment isolation |
| **HPA** | Horizontal Pod Autoscaler — auto-scales replicas based on CPU/memory metrics |

---

### Service Types

| Type | Accessibility | Use Case |
|---|---|---|
| `ClusterIP` (default) | Internal only — other Pods in the cluster | Internal APIs, databases, caches |
| `NodePort` | External via `<NodeIP>:<NodePort>` (30000–32767) | Dev/on-premise — direct node access |
| `LoadBalancer` | External via cloud load balancer (external IP/DNS) | Production internet-facing APIs on cloud |

**Service DNS format:** `<service-name>.<namespace>.svc.cluster.local`
**Within same namespace:** just use `<service-name>`

---

### Essential YAML Skeletons

**Deployment:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: bookstore-deployment
  namespace: default
spec:
  replicas: 3
  selector:
    matchLabels:
      app: bookstore
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
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
          resources:
            requests:
              cpu: "250m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
          envFrom:
            - configMapRef:
                name: bookstore-config
          env:
            - name: DB_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: bookstore-secrets
                  key: DB_PASSWORD
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 15
            periodSeconds: 5
```

**Service:**
```yaml
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
  type: ClusterIP
```

**ConfigMap:**
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: bookstore-config
data:
  SPRING_PROFILES_ACTIVE: "prod"
  SERVER_PORT: "8080"
  LOG_LEVEL: "INFO"
```

---

### Probe Comparison

| Probe | Failure Action | Spring Boot Actuator Endpoint |
|---|---|---|
| `livenessProbe` | Kill and restart the container | `/actuator/health/liveness` |
| `readinessProbe` | Remove Pod from Service endpoints (stop sending traffic) | `/actuator/health/readiness` |

**Required Spring Boot config:**
```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
```

---

### kubectl Cheat Sheet

**Apply / Delete:**
```bash
kubectl apply -f file.yaml           # create or update resource
kubectl apply -f ./k8s/              # apply all YAML files in directory
kubectl delete -f file.yaml          # delete resource defined in YAML
kubectl delete pod my-pod            # delete a specific resource by name
```

**Get / Inspect:**
```bash
kubectl get pods                     # list Pods
kubectl get pods -o wide             # with node and IP info
kubectl get pods --watch             # watch for changes
kubectl get deployments
kubectl get services
kubectl get all                      # all resource types
kubectl describe pod <pod-name>      # detailed info + Events
kubectl describe deployment <name>
```

**Logs / Debug:**
```bash
kubectl logs <pod-name>              # container stdout
kubectl logs -f <pod-name>           # follow in real time
kubectl logs deployment/<name>       # logs from Deployment (picks a pod)
kubectl exec -it <pod-name> -- bash  # shell into pod
kubectl port-forward pod/<name> 8080:8080  # tunnel local port to pod
kubectl port-forward svc/<name> 8080:80    # tunnel to service
```

**Scale / Update:**
```bash
kubectl scale deployment <name> --replicas=5
kubectl set image deployment/<name> <container>=<image>:<tag>
kubectl rollout status deployment/<name>
kubectl rollout history deployment/<name>
kubectl rollout undo deployment/<name>
kubectl rollout undo deployment/<name> --to-revision=2
kubectl rollout restart deployment/<name>
```

**Secrets:**
```bash
kubectl create secret generic my-secrets \
  --from-literal=DB_PASSWORD=secret123 \
  --from-literal=JWT_KEY=supersecret
kubectl get secrets
kubectl describe secret my-secrets    # shows key names, not values
```

**Cluster Info:**
```bash
kubectl cluster-info
kubectl get nodes
kubectl top nodes                      # CPU/memory usage (requires metrics-server)
kubectl top pods
```

---

### Rolling Update Strategy Settings

| Field | Description | Recommended |
|---|---|---|
| `maxUnavailable` | Max pods that can be unavailable during update | `0` (zero-downtime) |
| `maxSurge` | Max extra pods above desired during update | `1` |

---

### Local Kubernetes Options

| Tool | How It Works | Best For |
|---|---|---|
| **Docker Desktop K8s** | Built-in single-node cluster | Easiest setup on Mac/Windows |
| **minikube** | Single-node cluster in VM or Docker | Flexible, widely used |
| **kind** | K8s nodes as Docker containers | CI pipelines, multi-node local testing |
| **k3s** | Lightweight K8s (fewer resources) | Raspberry Pi, resource-constrained environments |

```bash
# minikube quick start
minikube start
minikube status
minikube dashboard        # open web UI
kubectl get nodes         # should show one node
minikube stop
```

---

### Common Troubleshooting

| Symptom | Cause | Fix |
|---|---|---|
| `ImagePullBackOff` | K8s can't pull the image | Check image name/tag; verify registry auth; `kubectl describe pod` for error |
| `CrashLoopBackOff` | Container starts and crashes repeatedly | `kubectl logs <pod>` to see the app error |
| `Pending` (Pod) | No node has enough resources, or node selector mismatch | `kubectl describe pod` — check Events for scheduler message |
| `OOMKilled` | Container exceeded memory limit | Increase memory limit in Deployment YAML |
| `Terminating` (stuck) | Finalizer blocking deletion | `kubectl delete pod <name> --force --grace-period=0` |
| Service not routing | Label selector mismatch | Verify `spec.selector` in Service matches `metadata.labels` in Pod |

---

### Docker vs Kubernetes Analogy Map

| Docker Concept | Kubernetes Equivalent |
|---|---|
| `docker run` | `kubectl apply -f pod.yaml` |
| `docker ps` | `kubectl get pods` |
| `docker logs` | `kubectl logs` |
| `docker exec -it` | `kubectl exec -it` |
| `docker stop` | `kubectl delete pod` (Deployment recreates it) |
| `docker-compose.yml` | Kubernetes YAML manifests (Deployment + Service + ConfigMap) |
| `docker compose up` | `kubectl apply -f ./k8s/` |
| Environment variable | ConfigMap / Secret env injection |
| Port mapping (`-p 8080:8080`) | Service NodePort / LoadBalancer |
| Volume (`-v`) | PersistentVolumeClaim + PersistentVolume |

---

*Day 36 — Docker & Kubernetes*
*Next: Day 37 — CI/CD & DevOps (automated build, test, deploy pipelines)*
