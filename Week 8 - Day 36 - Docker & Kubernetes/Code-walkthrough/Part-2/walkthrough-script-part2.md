# Day 36 – Docker & Kubernetes | Part 2
## Instructor Walkthrough Script — Kubernetes (~90 minutes)

> **Files covered:**
> - `01-kubernetes-architecture.md`
> - `02-kubectl-commands.sh`
> - `03-kubernetes-manifests.yaml`
>
> **Room setup before afternoon session:** kubectl installed, Docker Desktop K8s enabled (or Minikube running), terminal ready with `kubectl get nodes` returning a Ready node.

---

## OPENING (5 min)

"Welcome back from lunch. This morning we containerized the bookstore app with Docker. We can now run it consistently anywhere. But here's the next question — what happens in production when you need to run 50 containers? Across 10 servers? And one of those containers crashes at 2 AM?

You need something that watches your containers, restarts them when they die, distributes traffic across replicas, and lets you deploy new versions without downtime. That something is **Kubernetes**.

Google built Kubernetes because they've been running billions of containers a week for decades. Everything they learned is baked into this platform."

---

## SECTION 1 — What Kubernetes Is (10 min)

**Open:** `01-kubernetes-architecture.md` → Section 1

"Docker answers: 'How do I run one container?' Kubernetes answers: 'How do I run thousands of containers, reliably, across a cluster of machines, at scale?'

Look at this table."

**Walk through the Docker Alone vs Kubernetes comparison:**

"Docker alone — container fails, it stays dead. With Kubernetes, it auto-restarts. Need 10 replicas? Docker alone means ten separate `docker run` commands. Kubernetes means one YAML file with `replicas: 10`.

The paradigm shift is **declarative vs imperative**. With Docker, you give commands — do this, do that. With Kubernetes, you declare the desired state: 'I want 3 bookstore pods running, always.' Kubernetes continuously enforces that.

If a pod dies, Kubernetes notices. It creates a replacement. This is called **self-healing** and it happens automatically — no pages, no 2 AM alerts just because one container crashed."

---

## SECTION 2 — Kubernetes Architecture (15 min)

**Open:** `01-kubernetes-architecture.md` → Sections 2–4, point to the ASCII diagram

"Let's talk about what's actually inside a K8s cluster. There are two categories of nodes:

**The Control Plane** — this is the brain. **Worker Nodes** — this is where your actual containers run.

**Control Plane components — point to each:**

**API Server** — the front door. Every single thing in Kubernetes is a request to the API server. When you run `kubectl apply -f`, your terminal sends an HTTP request to the API server. It validates the request, stores the result, and notifies the relevant controllers.

**etcd** — the key-value store that holds ALL cluster state. Every object — every pod, every service, every secret — lives as an entry in etcd. If etcd goes down and you don't have a backup, your cluster state is gone. This is why production clusters run 3 or 5 etcd instances.

**Scheduler** — when a new pod needs to run, the scheduler figures out WHICH node to put it on. It looks at available CPU, memory, rules you've set, and picks the best fit.

**Controller Manager** — runs a collection of controllers. Each controller is a reconciliation loop. The ReplicaSet controller watches: 'should I have 3 pods? Let me count… I have 2. Creating 1 more.' Continuously, forever."

**Worker Node components:**

"On every worker node, three things:

**kubelet** — the node's agent. It watches the API server for 'you have a new pod to run,' then tells the container runtime to start it. It also runs health checks and reports status back.

**kube-proxy** — implements the Service abstraction. When you create a K8s Service with 3 pods behind it, kube-proxy sets up the network rules so traffic gets load-balanced across all three.

**Container runtime** — `containerd` in most modern clusters. This is what actually starts and stops containers."

**Ask the class:** "If I `kubectl apply` a Deployment, trace the path through the architecture. What happens?"

*(walk through: kubectl → API server → stored in etcd → Deployment controller notices → creates ReplicaSet → scheduler assigns pods to nodes → kubelet on each node starts containers)*

---

## SECTION 3 — Core Objects (10 min)

**Open:** `01-kubernetes-architecture.md` → Section 5

"Five objects you'll use constantly:

**Pod** — smallest unit. One or more containers, always scheduled together, sharing network and storage. But pods are ephemeral — their IPs change when they restart. Never reference a pod's IP directly.

**ReplicaSet** — ensures N copies of a pod are running. Self-heals automatically. You rarely create these directly — a Deployment manages them for you.

**Deployment** — wraps a ReplicaSet and adds rolling update logic. This is what you'll use for every stateless application. Handles updating your image version with zero downtime and rollback.

**Service** — a stable DNS name and IP that proxies to your pods. You talk to a Service, the Service load-balances to the pods. Services don't disappear when pods restart.

**ConfigMap and Secret** — externalize your configuration from your image. ConfigMap for non-sensitive config, Secret for passwords and API keys."

---

## SECTION 4 — Live Demo: Apply the Manifests (15 min)

**Open:** `03-kubernetes-manifests.yaml`

"Let's walk through each manifest in the file before we apply it."

**Walk through in order:**

**Namespace:**
```yaml
kind: Namespace
metadata:
  name: bookstore-ns
```
"Namespaces are logical partitions. We put everything in `bookstore-ns` so it's isolated from other apps."

**ConfigMap:**
"All non-sensitive config: app port, log level, the DB host. The DB host is `postgres-service` — the name of the K8s Service we'll create for PostgreSQL. K8s has built-in DNS: service names resolve automatically within the cluster."

**Secret:**
"Same structure as ConfigMap but for sensitive data. We use `stringData:` — plain text that K8s automatically base64-encodes on apply. Show `kubectl get secret bookstore-secret -o yaml` after we apply — you'll see base64 values."

**Deployment:**

"This is the most important one. Let me highlight key sections."

Point to `replicas: 3`:
"We want 3 pods at all times."

Point to `strategy: RollingUpdate`:
"When we update the image, `maxSurge: 1` means we can temporarily have 4 pods. `maxUnavailable: 0` means we never go below 3. So: K8s starts pod 4 with the new version, waits for it to pass the readiness probe, THEN terminates pod 1 with the old version. One at a time. Zero downtime."

Point to `livenessProbe`:
"K8s pings `/actuator/health/liveness` every 10 seconds. Three failures in a row → container restarts. This is how K8s handles a hung JVM that's not actually crashing but isn't processing requests either."

Point to `readinessProbe`:
"Similar, but if this fails, K8s just removes the pod from the load balancer — it doesn't restart it. Used during startup: the pod exists but isn't taking traffic until it's genuinely ready."

Point to `envFrom → configMapRef and secretRef`:
"This injects ALL keys from the ConfigMap and Secret as environment variables at once."

**Now apply:**
```bash
kubectl apply -f 03-kubernetes-manifests.yaml
kubectl get all -n bookstore-ns
kubectl get pods -n bookstore-ns -w
```

"Watch the pods start up. You'll see them go through Pending → ContainerCreating → Running."

```bash
kubectl logs -f <bookstore-pod-name> -n bookstore-ns
kubectl port-forward service/bookstore-nodeport 8080:80 -n bookstore-ns
# Open browser: http://localhost:8080/books
```

---

## SECTION 5 — kubectl Commands Deep Dive (10 min)

**Open:** `02-kubectl-commands.sh`

"Let me walk through the most important kubectl commands you'll use every day."

**describe — live demo:**
```bash
kubectl describe pod <bookstore-pod-name> -n bookstore-ns
```
"Scroll to Events at the bottom. This is your first debugging stop when a pod won't start. You'll see: pulled image, started container, or error messages if something went wrong."

**logs — live demo:**
```bash
kubectl logs -f <bookstore-pod-name> -n bookstore-ns
```
"Follow the Spring Boot startup logs in real time."

**exec — live demo:**
```bash
kubectl exec -it <bookstore-pod-name> -n bookstore-ns -- /bin/sh
# Inside the container:
curl http://localhost:8080/actuator/health
env | grep DB
exit
```
"Think of exec as kubectl's version of docker exec. Essential for debugging."

---

## SECTION 6 — Self-Healing Demo (5 min)

"Here's my favorite demo to show why Kubernetes is worth learning."

```bash
# Terminal 1: watch pods continuously
kubectl get pods -n bookstore-ns -w

# Terminal 2: delete a pod manually
kubectl delete pod <bookstore-pod-name> -n bookstore-ns
```

"Watch Terminal 1. The pod goes Terminating, then IMMEDIATELY a new pod appears — ContainerCreating → Running. The Deployment noticed 'I only have 2 pods, I need 3' and reacted in seconds.

This is the control loop in action. You didn't do anything. Kubernetes fixed it."

---

## SECTION 7 — Scaling (5 min)

**Open:** `02-kubectl-commands.sh` → Section 9

```bash
kubectl scale deployment bookstore-deployment --replicas=5 -n bookstore-ns
kubectl get pods -n bookstore-ns -w
```

"Five pods in seconds. Scale back down:"

```bash
kubectl scale deployment bookstore-deployment --replicas=3 -n bookstore-ns
```

"K8s terminates excess pods gracefully. Now look at the HPA section of the manifest — `03-kubernetes-manifests.yaml` Section 12. That's Horizontal Pod Autoscaler. You set min replicas, max replicas, and a target CPU utilization. K8s scales up automatically under load and scales back down. This is how cloud-native apps handle Black Friday traffic."

---

## SECTION 8 — Rolling Update & Rollback (10 min)

**Open:** `02-kubectl-commands.sh` → Section 10

"This is the most powerful feature of Kubernetes Deployments. Let's do a live rolling update."

```bash
# Trigger a rolling update — update the image to version 2.0.0
kubectl set image deployment/bookstore-deployment \
  bookstore-app=scottb/bookstore:2.0.0 \
  -n bookstore-ns

# Watch the rollout
kubectl rollout status deployment/bookstore-deployment -n bookstore-ns
```

"Watch what happens in the pod list. One new pod starts, passes the readiness probe, then one old pod is terminated. Then again. Then again. Never below 3. Zero downtime.

Now what if the new version has a bug?"

```bash
kubectl rollout history deployment/bookstore-deployment -n bookstore-ns
kubectl rollout undo deployment/bookstore-deployment -n bookstore-ns
kubectl rollout status deployment/bookstore-deployment -n bookstore-ns
```

"Rollback complete. K8s reversed the rolling update, starting the old version pods and terminating the new ones. That's your safety net for every deployment."

---

## SECTION 9 — Service Types (5 min)

**Open:** `03-kubernetes-manifests.yaml` → sections for ClusterIP, NodePort, LoadBalancer

"Three service types you need to know:

**ClusterIP** — internal only. Other pods in the cluster reach it by service name. The postgres-service is ClusterIP — database should NEVER be exposed externally.

**NodePort** — opens a static port on every node. `http://<node-ip>:30080`. Good for testing locally.

**LoadBalancer** — on AWS/GCP/Azure, this provisions a cloud load balancer with a public IP. This is how you expose your API to the internet in production.

The DNS name for a service is always: `<service-name>.<namespace>.svc.cluster.local`. But within the same namespace, `bookstore-clusterip` resolves. Cross-namespace: `bookstore-clusterip.bookstore-ns.svc.cluster.local`."

---

## INTERVIEW QUESTIONS (5 min)

Ask the class — hands up or rapid fire:

1. **"What's the difference between a Pod and a Deployment?"**
   *(Pod = single unit, ephemeral; Deployment = manages ReplicaSet, rolling updates, self-healing)*

2. **"What does `maxUnavailable: 0` mean in a rolling update strategy?"**
   *(Never go below the desired replica count — all old pods stay up until new ones are ready)*

3. **"What happens if you `kubectl delete pod <name>` and that pod belongs to a Deployment?"**
   *(Deployment immediately creates a replacement — self-healing)*

4. **"Why should a database Service be ClusterIP instead of LoadBalancer?"**
   *(Database should never be directly accessible from the internet — ClusterIP is internal-only)*

5. **"What's the difference between a liveness probe and a readiness probe?"**
   *(Liveness: is container alive? → restart if failed. Readiness: ready for traffic? → remove from LB if failed)*

6. **"You deployed v2.0 of the bookstore app and users are getting errors. What's the single command to fix it immediately?"**
   *(`kubectl rollout undo deployment/bookstore-deployment`)*

---

## WRAP-UP CHEAT CARD (2 min)

```
ARCHITECTURE                   OBJECTS
──────────────────────────     ──────────────────────────────────────
API Server  → entry point      Pod        → smallest unit
etcd        → state store      Deployment → manages pods + rolling update
Scheduler   → pod placement    Service    → stable network endpoint
Controller  → reconcile loop   ConfigMap  → non-sensitive config
kubelet     → node agent       Secret     → sensitive credentials
kube-proxy  → network rules    Namespace  → logical partition
containerd  → runs containers  HPA        → auto-scaling

KEY KUBECTL
──────────────────────────────────────────────────────────
kubectl apply -f <file>              Apply manifest
kubectl get pods/deploy/svc -n <ns>  List resources
kubectl describe pod <name>          Debug pod events
kubectl logs -f <pod>                Stream logs
kubectl exec -it <pod> -- sh         Shell into pod
kubectl scale deploy <n> --replicas  Manual scale
kubectl rollout undo deploy/<n>      Rollback update
kubectl port-forward svc/<n> 8080    Local access
```

---

## TRANSITION TO EXERCISES (3 min)

"For this afternoon's exercises:

1. Apply `03-kubernetes-manifests.yaml` to your local cluster
2. Verify all pods are Running with `kubectl get pods -n bookstore-ns`
3. Port-forward the service and hit the `/books` endpoint
4. **Self-healing:** delete a pod, watch it respawn
5. **Scaling:** scale the deployment to 5 replicas, then back to 2
6. **Rolling update:** update the image tag, watch `kubectl rollout status`
7. **Rollback:** undo the update and verify the old version is running

Tomorrow we get into CI/CD — Jenkins, GitHub Actions, automating everything we've been doing by hand this week."

---

## INSTRUCTOR NOTES

| Topic | Common Mistake | How to Address |
|---|---|---|
| Architecture overview | Students glaze over components | Use the ASCII diagram and trace a `kubectl apply` through each component |
| Pods vs Deployments | "Just use Pods directly" | Live demo: delete a standalone pod — it stays dead. Delete a Deployment pod — it respawns |
| Service DNS | Students hardcode IPs | Show `kubectl exec` + `curl postgres-service:5432` — resolves by name |
| Liveness vs Readiness | Confusing the two types | "Liveness = is it alive (restart if dead). Readiness = is it ready for traffic (pull from LB if not)" |
| Secrets in YAML | Students commit Secrets to git | Explain GitOps secret management — use `stringData` and `kubectl create secret`, or external vaults |
| Rolling updates | Not understanding maxSurge/maxUnavailable | Draw on whiteboard: 3 pods, update starts, 1 new comes up before 1 old goes down |
| kubectl -n namespace | Forgetting `-n` flag | Show `kubectl config set-context --current --namespace=bookstore-ns` to set default |
| Port-forward vs NodePort | Forgetting port-forward doesn't persist | Explain: port-forward is for temporary dev access; NodePort/LB for persistent external access |
