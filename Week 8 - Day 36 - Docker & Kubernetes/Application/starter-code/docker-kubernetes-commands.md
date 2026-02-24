# Docker & Kubernetes Command Reference — Day 36

## Part 1: Docker Commands

Fill in the command for each action:

### Image Management
| Action | Command |
|--------|---------|
| Build image from Dockerfile in current dir, tag as `my-app:1.0` | `TODO` |
| List all local images | `TODO` |
| Remove an image by name | `TODO` |
| Push image to Docker Hub | `TODO` |
| Pull image from Docker Hub | `TODO` |

### Container Lifecycle
| Action | Command |
|--------|---------|
| Run container, detached, port 8080→8080, named `my-container` | `TODO` |
| List running containers | `TODO` |
| List all containers (including stopped) | `TODO` |
| Stop a running container | `TODO` |
| Remove a stopped container | `TODO` |
| View container logs (follow) | `TODO` |
| Execute bash inside a running container | `TODO` |

### Docker Compose
| Action | Command |
|--------|---------|
| Start all services (detached) | `TODO` |
| Stop all services | `TODO` |
| View service logs | `TODO` |
| Rebuild and restart | `TODO` |
| Remove containers, networks, and volumes | `TODO` |

---

## Part 2: Kubernetes Commands

### Cluster & Namespace
| Action | Command |
|--------|---------|
| Get cluster info | `TODO` |
| List all namespaces | `TODO` |
| Apply all YAML files in k8s/ directory | `TODO` |
| Switch to `academy` namespace | `TODO` |

### Pods & Deployments
| Action | Command |
|--------|---------|
| List pods in `academy` namespace | `TODO` |
| Describe a pod | `TODO` |
| View pod logs | `TODO` |
| Exec into a pod | `TODO` |
| Scale deployment to 3 replicas | `TODO` |
| Rolling restart of a deployment | `TODO` |
| Check rollout status | `TODO` |

### Services & Networking
| Action | Command |
|--------|---------|
| List services | `TODO` |
| Get the external IP of a LoadBalancer service | `TODO` |
| Port-forward pod 8080 to local 9090 | `TODO` |

---

## Part 3: Reflection Questions

1. **Why use a multi-stage Dockerfile?** What problem does it solve?

   TODO

2. **What is the difference between a Docker `volume` and a `bind mount`?**

   TODO

3. **When would you choose `ClusterIP` vs `LoadBalancer` vs `NodePort`?**

   TODO

4. **What does the `readinessProbe` do and why is it different from `livenessProbe`?**

   TODO

5. **What is the purpose of the `HorizontalPodAutoscaler`?**

   TODO
