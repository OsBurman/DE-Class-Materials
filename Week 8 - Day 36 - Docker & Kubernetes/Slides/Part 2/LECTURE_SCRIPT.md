# Day 36 Part 2 — Kubernetes: Architecture, Workloads, Services, Config & Operations
## Lecture Script

---

**[00:00–01:30] — Welcome Back**

Alright, welcome back. Part 1 gave you Docker — how to package an application into a container, how to run it, how to compose multi-container stacks. Now the question is: what happens in production, when you have fifty containers, spread across ten servers, and you need them to stay running 24/7, scale up when traffic spikes, and update without downtime?

That's what Kubernetes solves. Let's go.

---

**[01:30–09:00] — Why Kubernetes — The Problems Docker Alone Can't Solve**

Slide two. Let me paint the picture of a production deployment without Kubernetes and you'll immediately understand why K8s exists.

You've deployed your bookstore application. It's a Spring Boot API behind a PostgreSQL database and Redis cache. You're running it with Docker Compose on one server. Life is good.

Now the application gets popular. Traffic triples overnight. Your single Spring Boot container is pegged at 100% CPU. What do you do? Start another container manually. But now you have two containers — which one does traffic go to? You need a load balancer. Okay, you put nginx in front. Now you're maintaining a custom nginx config.

Two weeks later, you push version 2.0 of your image. How do you update without downtime? You could stop the old containers and start new ones — but that's a gap where requests fail. You could try to do it manually in parallel — but it's error-prone.

A week later, one of the servers your containers are running on has a disk failure and goes down. Your containers are dead. You need to manually restart them on a different server — if you even notice before users do.

Every single one of these problems — scaling, load balancing, zero-downtime updates, self-healing after failures, multi-server management — Kubernetes solves natively. And it solves them with a single, consistent abstraction: **desired state**.

Here's the core mental model of Kubernetes: you tell it the *desired state* — "I want three copies of my bookstore app running, using image version 1.0." Kubernetes continuously compares desired state to actual state. If they diverge — a container crashes, a node goes down, you change the desired state — K8s automatically brings actual state back into alignment with desired state. You declare what you want. K8s makes it happen and keeps it there.

The table on the slide walks through the comparison. Docker alone requires you to manage each container manually. Kubernetes manages everything for you: self-healing, rolling updates, scaling, load balancing, service discovery, configuration management. This is why every major cloud provider offers managed Kubernetes: AWS EKS, Google GKE, Azure AKS. It's the industry standard for production container operations.

A quick historical note: Kubernetes was developed by Google, based on their internal system called Borg that they'd been running for a decade. Google runs billions of container deployments per week on Borg. They open-sourced Kubernetes in 2014 and donated it to the Cloud Native Computing Foundation. It became the dominant container orchestration platform within two years.

---

**[09:00–18:00] — Kubernetes Architecture — Control Plane**

Slides three and four. Let me walk through the Kubernetes architecture. Understanding this is important — when something goes wrong, you need to know which component is involved.

Kubernetes has two types of nodes: the control plane and worker nodes. The control plane is the brain — it makes all the decisions. Worker nodes are the muscle — they actually run your application containers.

Let me take you through the control plane components one by one.

First: the **API Server**. This is the central hub of the entire cluster. Every single operation — every `kubectl` command you run, every Kubernetes controller that checks on things, every kubelet on worker nodes reporting status — all of it goes through the API server's REST API. When you run `kubectl apply -f deployment.yaml`, your kubectl client sends a REST request to the API server. The API server validates it and processes it.

Second: **etcd**. Pronounced "et-cee-dee." This is a distributed key-value store that holds the entire state of the cluster. Every resource you've created — every Deployment, every Pod, every Service, every ConfigMap — is stored in etcd. Think of it as the cluster's database. If etcd is lost without a backup, the cluster's state is gone. This is why etcd is always run with replication in production. The API server is the only component that writes to etcd directly.

Third: the **Scheduler**. When you create a new Pod, it starts in a "pending" state with no assigned node. The Scheduler watches for these unassigned Pods and picks the best worker node for each one. How does it decide? It considers available resources — which node has enough CPU and memory. Node labels, Pod affinity/anti-affinity rules, taints and tolerations. The Scheduler picks a node and tells the API server — but it doesn't actually start the Pod. That's the kubelet's job.

Fourth: the **Controller Manager**. This runs a set of control loops — processes that continuously watch the cluster state and reconcile it toward desired state. The ReplicaSet controller checks: "Does this Deployment have the right number of running Pods? If not, create more or remove extras." The Node controller watches for nodes that stop reporting heartbeats. The Deployment controller manages rolling updates. These controllers run in a loop, constantly comparing actual state to desired state.

Now worker nodes — slide four.

**kubelet**: the node agent. Runs on every worker node. It watches the API server for Pods assigned to its node and ensures those containers are actually running. It's the bridge between the control plane's decisions and the actual container runtime on the node. It also runs health checks on Pods and reports their status back to the API server.

**kube-proxy**: handles network routing for Services. When you have a Service with a stable IP address that routes to three backend Pods, kube-proxy is what makes the traffic routing happen — it configures iptables rules on each node that forward Service traffic to the appropriate Pod.

**containerd**: the container runtime — the software that actually creates and runs containers. Kubernetes talks to containerd via the Container Runtime Interface (CRI). Important point: Kubernetes doesn't require Docker. It uses containerd directly. Docker and Kubernetes both use containerd under the hood, but K8s doesn't need the Docker daemon layer.

On managed Kubernetes services like AWS EKS or Google GKE, the cloud provider manages the control plane for you. You only see and manage worker nodes. You still use `kubectl` — it just connects to their API server.

---

**[18:00–27:00] — Pods, Deployments, and ReplicaSets**

Slides five, six, and seven. Let's talk about the workload resources — Pods, Deployments, and ReplicaSets.

**Pods first.** A Pod is the smallest deployable unit in Kubernetes — it's NOT a container. A Pod is a wrapper that can hold one or more containers. Containers in a Pod share the same IP address, the same ports, and the same storage volumes. In the vast majority of cases, you have one container per Pod. The multi-container Pod pattern — called the "sidecar pattern" — is for when a helper container needs to be tightly coupled to the main container. For example: a log-forwarding agent that reads the app's log files and ships them to Elasticsearch.

Look at the Pod YAML on slide five. Notice the `resources` section — `requests` and `limits`. Requests are guaranteed minimums: the scheduler won't place a Pod on a node that doesn't have at least that much CPU and memory available. Limits are maximums: if a container tries to use more CPU than its limit, it gets throttled. If it exceeds its memory limit, it gets OOM-killed and restarted.

`250m` CPU means 250 millicores — a quarter of one CPU core. `256Mi` means 256 mebibytes of RAM. Setting these is important in production — without limits, one misbehaving container can starve other containers on the same node.

The critical thing about labels: labels are how Kubernetes components find each other. A Service routes traffic to Pods by matching their labels. A Deployment manages Pods by label selector. Everything in Kubernetes uses labels for association — get comfortable with them.

**But here's the critical point: don't create Pods directly in production.** Pods are ephemeral. If you create a raw Pod and it crashes, nothing recreates it. It's just dead. You need a controller to manage your Pods — and that's what a Deployment is.

**Deployments** — slide six. A Deployment is what you'll create for every stateless application in Kubernetes. You tell it: I want this image, running with these environment variables, and I want three replicas. The Deployment creates a **ReplicaSet**, which ensures exactly three Pods matching the spec are running at all times.

Look at the Deployment YAML on the slide. The `spec.replicas: 3` is the desired state. The `selector.matchLabels` tells the Deployment which Pods it manages. The `template` is the Pod spec — what each Pod should look like. The `template.metadata.labels` must match the `selector.matchLabels` — this is how the Deployment finds its own Pods.

Notice the liveness and readiness probes at the bottom. These are how Kubernetes knows whether your Spring Boot app is healthy.

- **Liveness probe**: "Is this container alive?" If the liveness probe fails repeatedly, Kubernetes kills the container and restarts it. Map it to `/actuator/health/liveness`.
- **Readiness probe**: "Is this container ready to accept traffic?" If it fails, Kubernetes removes the Pod from the Service's list of endpoints — traffic stops going to it. Map it to `/actuator/health/readiness`.

The difference matters: readiness failure means "don't send traffic" but keep the container running. Liveness failure means "the container is broken — restart it." Spring Boot Actuator exposes both of these automatically when you add the `spring-boot-starter-actuator` dependency.

**ReplicaSets** — slide seven. The Deployment creates a ReplicaSet, and the ReplicaSet runs the reconciliation loop. Let me show you self-healing in action.

Run `kubectl delete pod bookstore-abc12` — simulate a crash. Immediately run `kubectl get pods`. You'll see the deleted pod in a Terminating state and a brand new pod in ContainerCreating state. Within ten to fifteen seconds, you're back to three running pods. The ReplicaSet controller detected the pod count dropped below the desired three and created a replacement automatically — no human intervention.

This is the desired state model in action. You declared desired state as three replicas. Kubernetes continuously enforces it.

---

**[27:00–38:00] — Services, ConfigMaps, and Secrets**

Slides eight, nine, and ten. Three more essential resources.

**Services** — slide eight. Here's the problem Services solve: every time a Pod dies and is replaced, it gets a new IP address. If your frontend Pod had hardcoded the backend Pod's IP address, every backend restart would break the connection. You'd have to manually update the frontend config every time. That's unworkable.

A Service provides a stable IP address and DNS name that never changes, even as the underlying Pods come and go. The Service watches for Pods matching its label selector and continuously updates its list of healthy backend endpoints. kube-proxy maintains the routing rules.

Let me walk through the three Service types because this comes up in every Kubernetes interview.

**ClusterIP** is the default. It assigns a virtual IP that is only reachable from INSIDE the cluster. Other Pods can connect to `http://bookstore-service:80` and get load-balanced to one of your three bookstore Pods. External traffic cannot reach a ClusterIP Service. Use ClusterIP for databases, internal APIs, caches — anything that shouldn't be directly accessible from the internet.

**NodePort** opens a specific port (between 30000 and 32767) on every worker node in the cluster. External traffic hitting ANY node on that port gets routed to the Service. `curl http://192.168.1.5:30080` would reach your bookstore Pods. NodePort is fine for development and on-premise clusters, but not ideal for production — you're exposing a specific port on your raw node IP.

**LoadBalancer** is the production standard for internet-facing services on cloud providers. Kubernetes tells the cloud provider to provision a load balancer (AWS ELB, Google Cloud Load Balancer). That load balancer gets a stable external IP or DNS name, and routes traffic through NodePort into your Pods. This is how you expose Spring Boot APIs to the internet in production on AWS EKS.

Every Service also gets a DNS name automatically: `<service-name>.<namespace>.svc.cluster.local`. Within the same namespace, you can just use the service name. So your Spring Boot datasource URL can be `jdbc:postgresql://postgres-service:5432/bookstoredb` — and that name resolves to the PostgreSQL Service's ClusterIP, which routes to the PostgreSQL Pod.

**ConfigMaps** — slide nine. Configuration should not be baked into your Docker image — that would require a new image for every environment. ConfigMaps store non-sensitive configuration as key-value pairs in Kubernetes. Your bookstore Deployment can inject all ConfigMap values as environment variables with `envFrom.configMapRef`. When you need a different config for staging vs production, you create different ConfigMaps in different namespaces — same image everywhere.

One important nuance: ConfigMap changes do NOT automatically restart running Pods. You need to restart the Deployment to pick up new values: `kubectl rollout restart deployment/bookstore-deployment`.

**Secrets** — slide ten. Secrets are like ConfigMaps but for sensitive data. The YAML looks identical, but the values are base64-encoded. Let me be very clear about base64: it is NOT encryption. It is encoding. Anyone who can read the Secret can decode it. Base64 just makes the data safe for YAML storage — it avoids special character issues.

In production, you protect Secrets with RBAC — role-based access control — so only specific Pods and users can read specific Secrets. You should also encrypt etcd at rest. And at serious scale, teams use external secret managers like AWS Secrets Manager or HashiCorp Vault, which inject secrets into Pods without storing them in etcd at all. For this course, the built-in K8s Secret resource is what you need to understand.

The practical rule: never commit a Secrets YAML file to version control. Create Secrets with `kubectl create secret generic` from the command line, or with your CI/CD pipeline pulling values from a secret manager.

---

**[38:00–47:00] — kubectl and a Full Deployment Example**

Slides eleven and twelve. Let's make this concrete with kubectl commands and a full deployment walkthrough.

`kubectl` is your primary tool for interacting with the cluster. The most important command is `kubectl apply -f filename.yaml`. Apply is idempotent — you can run it ten times and it produces the same result. If the resource doesn't exist, it creates it. If it exists, it updates it to match the YAML. This is how GitOps works: your YAML files in Git are the desired state, and `kubectl apply` makes the cluster match.

Let me walk through a full deployment of the bookstore app. Here's the sequence:

```bash
# 1. Apply the ConfigMap first
kubectl apply -f configmap.yaml

# 2. Create the Secret imperatively (don't put secrets in YAML files in source control)
kubectl create secret generic bookstore-secrets \
  --from-literal=DB_PASSWORD=password123 \
  --from-literal=JWT_SECRET=mysecretkey

# 3. Apply the Deployment
kubectl apply -f deployment.yaml

# 4. Apply the Service
kubectl apply -f service.yaml

# 5. Check everything
kubectl get all
```

After `kubectl get all`, you should see the Deployment, the ReplicaSet it created, the three Pods, and the Service. You might see Pods in `ContainerCreating` state initially — that's K8s pulling your image and starting the containers. Give it thirty seconds.

If something looks wrong — a Pod is in `CrashLoopBackOff` or `ImagePullBackOff` — these two commands are your debugging tools:

`kubectl describe pod <pod-name>` — this gives you detailed info including the Events section at the bottom. Events tell you exactly what happened: "pulled image", "created container", "started", or "back-off restarting failed container." If the image can't be pulled, you'll see "Failed to pull image" with the error message.

`kubectl logs <pod-name>` — this gives you the container's stdout output. If your Spring Boot app failed to connect to the database, the stack trace will be here.

The slide shows the full YAML manifest set. Notice how the Deployment references the ConfigMap with `envFrom.configMapRef` and the Secret with `secretKeyRef`. These inject the values as environment variables, which Spring Boot reads via its standard externalized configuration mechanism.

---

**[47:00–54:00] — Rolling Updates, Rollbacks, and Scaling**

Slides thirteen and fourteen. Let me walk through rolling updates — this is one of K8s's most valuable features.

The scenario: you've deployed version 1.0 of bookstore with three replicas. You push version 2.0 and want to deploy with zero downtime.

```bash
kubectl set image deployment/bookstore-deployment bookstore=myuser/bookstore:2.0
```

That single command triggers a rolling update. Watch what happens:

```bash
kubectl rollout status deployment/bookstore-deployment
```

You'll see: "Waiting for deployment to rollout: 1 out of 3 new replicas have been updated." Kubernetes starts one new v2.0 Pod. It waits for that Pod's readiness probe to pass. Once it passes, v2.0 is proven healthy — Kubernetes removes one v1.0 Pod and starts another v2.0. It continues this until all Pods are running v2.0.

The `maxUnavailable: 0` setting in the rolling update strategy means K8s never removes a v1.0 Pod until a v2.0 Pod is confirmed healthy. You always have at least three healthy Pods serving traffic during the entire update. Zero downtime.

The `maxSurge: 1` means K8s can have at most four Pods running at once during the update — one extra above the desired three.

Now rollbacks. You deploy v2.0 and something is wrong — requests are failing, error rates spiked. One command:

```bash
kubectl rollout undo deployment/bookstore-deployment
```

Kubernetes immediately reverses the rolling update — brings v1.0 Pods back up and removes v2.0 Pods. Same zero-downtime rolling process, just in reverse. You can also roll back to a specific revision:

```bash
kubectl rollout history deployment/bookstore-deployment  # see all revisions
kubectl rollout undo deployment/bookstore-deployment --to-revision=1  # go to v1
```

Kubernetes keeps the old ReplicaSets around (with zero replicas) specifically to enable this — so it can quickly roll back by scaling the old ReplicaSet back up.

**Scaling** is even simpler:
```bash
kubectl scale deployment bookstore-deployment --replicas=10
```

Done. K8s creates seven new Pods (from three to ten) and the Scheduler places them across your worker nodes. Scale back down the same way. No downtime, no manual work.

---

**[54:00–60:00] — Local K8s, Part 2 Summary, and Day Wrap-Up**

Slides fifteen and sixteen. Quick coverage of local K8s and our summary.

For local development and learning, you need a cluster on your laptop. The easiest options:

If you have Docker Desktop, you already have K8s. Go to Docker Desktop → Settings → Kubernetes → Enable Kubernetes. Wait two minutes. Run `kubectl get nodes` and you'll see one node called `docker-desktop`. That's a full single-node Kubernetes cluster.

If you want minikube — `brew install minikube`, then `minikube start`. It creates a single-node cluster using a VM or Docker containers. `minikube dashboard` gives you a web UI for the cluster — very helpful for visualizing Deployments and Pods graphically.

For the labs: use Docker Desktop's built-in K8s or minikube — either works. The exercises will have you deploying the bookstore application as a K8s Deployment, creating a Service to expose it, and practicing rolling updates.

Let me do a final summary of Part 2.

The architecture is control plane — API server, etcd, Scheduler, Controller Manager — and worker nodes — kubelet, kube-proxy, containerd.

The key workload resources: Pods are the unit, but Deployments are what you create in practice. Deployments manage ReplicaSets which manage Pods and provide self-healing. Services provide stable networking and load balancing.

The desired state model is the heart of Kubernetes. You declare what you want in YAML. Kubernetes makes it real and keeps it real, even when things fail.

The workflow for every deployment: `kubectl apply -f file.yaml`. For updates: `kubectl set image` or update the YAML and `kubectl apply` again. For problems: `kubectl describe` and `kubectl logs`.

Coming up tomorrow: CI/CD — how you automate the process of building Docker images, running tests, and deploying to Kubernetes without manual steps. The Docker and K8s knowledge from today is the foundation.

Good work today — two complex technologies in one day. See you tomorrow.

---

*[End of Part 2 Script — approximately 60 minutes]*
