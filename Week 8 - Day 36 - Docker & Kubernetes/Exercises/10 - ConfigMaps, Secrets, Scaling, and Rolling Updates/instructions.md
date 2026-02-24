# Exercise 10 — ConfigMaps, Secrets, Scaling, and Rolling Updates

## Learning Objectives
By the end of this exercise you will be able to:
- Create a ConfigMap to inject non-sensitive configuration into Pods
- Create a Secret to inject sensitive values (encoded in base64)
- Reference ConfigMaps and Secrets as environment variables in a Deployment
- Scale a Deployment up and down imperatively
- Perform a rolling update by changing the container image
- Monitor rollout status and roll back a failed deployment

---

## Background

### ConfigMaps vs Secrets

| | ConfigMap | Secret |
|---|---|---|
| Purpose | Non-sensitive configuration (URLs, feature flags) | Sensitive data (passwords, tokens, certs) |
| Storage | Plain text in etcd | Base64-encoded in etcd (encrypt at rest with KMS) |
| Injection | Env vars or mounted files | Env vars or mounted files |

### Rolling Update Strategy
```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 1          # Extra Pods allowed above desired count during update
    maxUnavailable: 0    # Pods allowed to be unavailable during update
```
This ensures zero downtime: new Pods are created before old ones are terminated.

---

## Requirements

### Requirement 1 — Create a ConfigMap (imperative)
Write the `kubectl` command to create a ConfigMap named `spring-config` in the `default` namespace with the following literal key-value pairs:
- `SPRING_PROFILES_ACTIVE=production`
- `SERVER_PORT=8080`

### Requirement 2 — Create a Secret (imperative)
Write the `kubectl` command to create a generic Secret named `spring-secret` in the `default` namespace with:
- `DB_USERNAME=app`
- `DB_PASSWORD=supersecret`

### Requirement 3 — ConfigMap and Secret Manifests
Complete `configmap.yaml` and `secret.yaml` (declarative equivalents of Requirements 1 and 2).  
For the Secret, the values must be base64-encoded.

> **Tip:** `echo -n 'app' | base64` → `YXBw`  
> `echo -n 'supersecret' | base64` → `c3VwZXJzZWNyZXQ=`

### Requirement 4 — Inject ConfigMap and Secret into a Deployment
Complete `deployment-with-config.yaml`: a Deployment (3 replicas) that:
- Injects all keys from `spring-config` as environment variables using `envFrom.configMapRef`
- Injects `DB_USERNAME` and `DB_PASSWORD` from `spring-secret` using `env.valueFrom.secretKeyRef`
- Uses the `springapp:1.0.0` image with resource requests/limits (same as Exercise 09)

### Requirement 5 — Scale the Deployment
Write the `kubectl` command to scale `spring-deployment` to **5 replicas**, then back to **2 replicas**.

### Requirement 6 — Perform a Rolling Update
Write the `kubectl` command to update the `spring-container` container in `spring-deployment` to use the image `springapp:2.0.0`.  
Then write the command to watch the rollout status.

### Requirement 7 — Roll Back
Write the `kubectl` command to undo the last rollout of `spring-deployment`.  
Then write the command to view the rollout history.

---

## Deliverable
- Complete `configmap.yaml`, `secret.yaml`, `deployment-with-config.yaml` in `starter-code/`
- Complete `commands.sh` with all kubectl commands

---

## Hints
- `kubectl create configmap <name> --from-literal=KEY=VALUE`
- `kubectl create secret generic <name> --from-literal=KEY=VALUE`
- `envFrom` injects **all** keys from a ConfigMap/Secret as env vars
- `env.valueFrom.secretKeyRef` injects a **specific** Secret key
- `kubectl scale deployment <name> --replicas=<n>`
- `kubectl set image deployment/<name> <container>=<image>:<tag>`
- `kubectl rollout status deployment/<name>`
- `kubectl rollout undo deployment/<name>`
- `kubectl rollout history deployment/<name>`
