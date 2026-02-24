# Exercise 04 — Service Discovery and Load Balancing with Eureka

## Requirement 1 — Service Discovery Concepts

**Client-side vs server-side service discovery — and which Eureka+Spring Cloud LoadBalancer uses:**

TODO: Explain the difference between client-side and server-side discovery. State which model Eureka + Spring Cloud LoadBalancer implements.

**What a service sends to Eureka on startup, what a heartbeat is, and what happens when heartbeats stop:**

TODO: Answer here.

**Eureka self-preservation mode — what it is and when it activates:**

TODO: Answer here.

---

## Requirement 2 — Eureka Server Setup

**Maven `artifactId` for the Eureka Server dependency:**

TODO: Name the dependency.

**Annotation added to the `@SpringBootApplication` class:**

TODO: Name the annotation.

**Two `application.yml` properties that prevent the server from registering with itself:**

```yaml
# TODO: Add the two eureka.client properties here
```

---

## Requirement 3 — Eureka Client Setup

**Maven `artifactId` for the Eureka Client dependency:**

TODO: Name the dependency.

**`application.yml` property that sets the service registration name:**

```yaml
# TODO: Add spring.application.name property
```

**`application.yml` property pointing the client to the Eureka Server:**

```yaml
# TODO: Add eureka.client.serviceUrl.defaultZone property
```

**Annotation or Bean needed to enable load-balanced `RestTemplate`:**

TODO: Describe what to add and where.

---

## Requirement 4 — Load Balancing Strategies

TODO: Fill in the table.

| Strategy | How it works | Best suited for |
|---|---|---|
| Round Robin | | |
| Random | | |

---

## Requirement 5 — Registration and Discovery Flow Diagram

TODO: Draw an ASCII diagram showing:
- 2 Inventory Service instances registering with Eureka Server
- Order Service querying Eureka to get the list of Inventory instances
- Order Service using load balancing to select and call one instance

```
[Inventory Instance 1] ──?──▶ [Eureka Server]
[Inventory Instance 2] ──?──▶ [Eureka Server]
[Order Service]        ──?──▶ [Eureka Server]
[Order Service]        ──?──▶ [Inventory Instance ?]
```

---

## Requirement 6 — Consul vs Eureka Comparison

TODO: Fill in the table.

| Criterion | Netflix Eureka | HashiCorp Consul |
|---|---|---|
| Health check mechanism | | |
| Service mesh support | | |
| Multi-datacenter support | | |
| Best ecosystem | | |
