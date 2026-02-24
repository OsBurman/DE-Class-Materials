# Exercise 09 — Deploying and Exposing an Application with kubectl

## Learning Objectives
By the end of this exercise you will be able to:
- Write a Kubernetes Deployment manifest that manages multiple Pod replicas
- Write a Service manifest that exposes the Deployment via NodePort
- Apply manifests with `kubectl apply -f`
- Use `kubectl get`, `describe`, `logs`, and `delete` to manage resources
- Verify the application is reachable through the Service

---

## Background

A **Deployment** tells Kubernetes: *"I want 3 replicas of this Pod running at all times."*  
A **Service** tells Kubernetes: *"Route traffic to any Pod matching this label selector."*

```
User ──► NodePort Service ──► (label selector: app=springapp) ──► Pod 1
                                                               ──► Pod 2
                                                               ──► Pod 3
```

### YAML manifest skeleton
```yaml
apiVersion: apps/v1
kind: Deployment
metadata: …
spec:
  replicas: 3
  selector:
    matchLabels:
      app: springapp
  template:           # Pod template
    metadata:
      labels:
        app: springapp
    spec:
      containers: …
```

---

## Requirements

### Requirement 1 — Deployment Manifest
Complete `deployment.yaml` to create a Deployment named `spring-deployment` with:
- Namespace: `default`
- Label on the Deployment itself: `app: springapp`
- `replicas: 3`
- Selector `matchLabels: app: springapp`
- Pod template label: `app: springapp`
- One container: name `spring-container`, image `springapp:1.0.0`, containerPort `8080`
- Container resources: request `cpu: 100m, memory: 128Mi`; limit `cpu: 500m, memory: 512Mi`

### Requirement 2 — Service Manifest
Complete `service.yaml` to create a NodePort Service named `spring-service` with:
- Namespace: `default`
- Label selector: `app: springapp` (matches Pods created by the Deployment)
- `port: 80` (the port the Service listens on inside the cluster)
- `targetPort: 8080` (the container port)
- `nodePort: 30080` (the port opened on every node)
- `type: NodePort`

### Requirement 3 — Apply the Manifests
Write the `kubectl` commands to apply both manifests.

### Requirement 4 — Inspect Resources
Write the `kubectl` commands to:
1. List all Pods in the `default` namespace
2. List all Services in the `default` namespace
3. Describe the `spring-deployment` Deployment
4. Get detailed information about the first Pod (you may use a label selector)

### Requirement 5 — View Pod Logs
Write the `kubectl` command to:
- Print logs from all Pods with label `app=springapp` (use `-l`)
- Follow logs in real time

### Requirement 6 — Delete Resources
Write the `kubectl` commands to delete both the Deployment and the Service.

---

## Deliverable
- Complete `deployment.yaml` and `service.yaml` in `starter-code/`
- Complete `commands.sh` with all kubectl commands

---

## Hints
- `kubectl apply -f <file>` — creates or updates resources from a YAML file
- `kubectl apply -f <directory>` — applies all YAML files in a directory
- `kubectl get pods -n default -o wide` — shows Pod IPs and nodes
- `kubectl describe deployment spring-deployment`
- `kubectl logs -l app=springapp -f`
- `kubectl delete -f <file>` — deletes resources defined in the file
- Resource limits prevent a single Pod from starving the node
