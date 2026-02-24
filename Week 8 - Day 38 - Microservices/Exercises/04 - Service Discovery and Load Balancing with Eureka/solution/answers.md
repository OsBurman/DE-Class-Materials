# Exercise 04 — Service Discovery and Load Balancing with Eureka — SOLUTION

---

## Requirement 1 — Service Discovery Concepts

**Client-side vs server-side service discovery:**

| | Client-Side Discovery | Server-Side Discovery |
|---|---|---|
| Who resolves the address | The **calling service** fetches the registry and picks an instance | A dedicated **load balancer or proxy** looks up the registry and forwards the call |
| Example | Eureka + Spring Cloud LoadBalancer | AWS ALB, Nginx, Kubernetes Service (kube-proxy) |
| Coupling | Client must include a discovery library | Client is decoupled from registry; it just calls a fixed proxy address |

**Eureka + Spring Cloud LoadBalancer uses client-side discovery.** The Order Service's `RestTemplate` (annotated with `@LoadBalanced`) fetches the list of Inventory Service instances from Eureka and selects one using a load-balancing strategy before making the HTTP call.

---

**What a service sends to Eureka on startup + heartbeat:**

On startup, a service (client) sends a **registration request** to the Eureka Server containing:
- `appName` — the service name (from `spring.application.name`)
- `instanceId` — a unique ID for this instance (typically `hostname:appName:port`)
- `ipAddr` / `hostName` and `port`
- `healthCheckUrl` and `statusPageUrl`
- `vipAddress` — virtual address used for routing

A **heartbeat** is a periodic `PUT` request the service sends to Eureka every **30 seconds** (configurable via `eureka.instance.lease-renewal-interval-in-seconds`) to signal it is still alive.

If a service stops sending heartbeats, Eureka waits for the **lease expiry duration** (default 90 seconds = 3 missed heartbeats) and then **de-registers** that instance, removing it from the registry. Other services will stop receiving that instance in discovery results.

---

**Eureka self-preservation mode:**

Self-preservation mode activates when Eureka Server detects that **too many instances have stopped sending heartbeats at once** — specifically when the percentage of instances renewing leases falls below a threshold (default 85%). Rather than immediately de-registering all those instances (which could be a false positive caused by a network partition rather than actual service failures), Eureka **preserves** the current registry state and stops evicting instances.

Self-preservation prevents cascading de-registration during transient network issues. It is disabled in single-node development setups (`eureka.server.enable-self-preservation: false`) but should remain enabled in production.

---

## Requirement 2 — Eureka Server Setup

**Maven `artifactId`:**
```
spring-cloud-starter-netflix-eureka-server
```

**Annotation:**
```java
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication { ... }
```

**`application.yml` properties to prevent the server from registering with itself:**
```yaml
eureka:
  client:
    register-with-eureka: false   # Don't register this server as a client in its own registry
    fetch-registry: false         # Don't fetch the registry (this IS the registry)
  server:
    enable-self-preservation: false   # Optional: disable in development for faster de-registration
```

**Full minimal `application.yml` for the Eureka Server:**
```yaml
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

---

## Requirement 3 — Eureka Client Setup

**Maven `artifactId`:**
```
spring-cloud-starter-netflix-eureka-client
```
(Included transitively by most Spring Cloud starters; just adding `spring-cloud-starter-netflix-eureka-client` is sufficient.)

**`application.yml` — service name:**
```yaml
spring:
  application:
    name: order-service    # This becomes the service ID in Eureka
```

**`application.yml` — Eureka Server URL:**
```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

**Complete `application.yml` for Order Service:**
```yaml
server:
  port: 8082

spring:
  application:
    name: order-service

eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka-server:8761/eureka/
  instance:
    prefer-ip-address: true
```

**Enabling load-balanced `RestTemplate`:**

Add `@LoadBalanced` to the `RestTemplate` bean definition:
```java
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced   // Tells Spring Cloud LoadBalancer to intercept calls and resolve service names
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

Then in the Order Service, call Inventory Service by its registered name — **not** by a hard-coded URL:
```java
// Spring Cloud LoadBalancer resolves "inventory-service" to a real IP:port via Eureka
ResponseEntity<AvailabilityResponse> response = restTemplate.getForEntity(
    "http://inventory-service/inventory/42/availability",
    AvailabilityResponse.class
);
```

---

## Requirement 4 — Load Balancing Strategies

| Strategy | How it works | Best suited for |
|---|---|---|
| Round Robin | Requests are distributed in order: instance 1, instance 2, instance 3, instance 1, ... Each instance receives an equal share over time | Stateless services where all instances are roughly equivalent in capacity and latency |
| Random | Each request picks a random instance from the available list | Environments where some instances may briefly be slower (e.g., after a cold start) — random distribution avoids stampeding a slow instance that round-robin would keep hitting |

Spring Cloud LoadBalancer uses **Round Robin** by default. You can switch to Random by providing a custom `ReactorLoadBalancer<ServiceInstance>` bean.

---

## Requirement 5 — Registration and Discovery Flow Diagram

```
STARTUP PHASE — Services register with Eureka:

[inventory-service (Instance 1)]  ──── POST /eureka/apps/INVENTORY-SERVICE ────▶  [Eureka Server :8761]
  host: 10.0.1.11, port: 8081             (registration: appName, ip, port)          │
                                                                                      │ Stores in registry
[inventory-service (Instance 2)]  ──── POST /eureka/apps/INVENTORY-SERVICE ────▶  [Eureka Server :8761]
  host: 10.0.1.12, port: 8081                                                         │
                                                                                      │
[order-service]  ──── POST /eureka/apps/ORDER-SERVICE  ─────────────────────────▶  [Eureka Server :8761]
  host: 10.0.1.20, port: 8082


HEARTBEAT PHASE (every 30 seconds):

[inventory-service (Instance 1)] ──── PUT /eureka/apps/INVENTORY-SERVICE/... ──▶ [Eureka Server]  (I'm alive)
[inventory-service (Instance 2)] ──── PUT /eureka/apps/INVENTORY-SERVICE/... ──▶ [Eureka Server]  (I'm alive)


DISCOVERY + CALL PHASE — Order Service calls Inventory Service:

[Order Service]  ──── GET /eureka/apps/INVENTORY-SERVICE ──────────────────────▶ [Eureka Server]
                 ◀─── Returns: [{ ip: 10.0.1.11, port: 8081 },
                                { ip: 10.0.1.12, port: 8081 }]  ─────────────────

[Order Service]  ─── @LoadBalanced RestTemplate resolves "inventory-service" ──▶ Spring Cloud LoadBalancer
                 ─── Round-robin selects Instance 1 (first call) ───────────────▶ [Instance 1: 10.0.1.11:8081]
                 ◀─── HTTP 200 { "available": true, "stockLevel": 14 }

[Order Service]  ─── Next call: round-robin selects Instance 2 ─────────────────▶ [Instance 2: 10.0.1.12:8081]
```

---

## Requirement 6 — Consul vs Eureka Comparison

| Criterion | Netflix Eureka | HashiCorp Consul |
|---|---|---|
| Health check mechanism | Heartbeat-based — services push `PUT` requests every 30s; Eureka deregisters instances that miss 3 heartbeats | Consul actively **pulls** health checks (HTTP endpoint, TCP, script, gRPC); more flexible and catches more failure modes |
| Service mesh support | No native service mesh — only registry and discovery | Native **Consul Connect** service mesh with mutual TLS between services; replaces the need for a separate mesh like Istio |
| Multi-datacenter support | Limited — requires complex peering setups; not designed for multi-region | First-class multi-datacenter support built in; global service catalog federation across data centers |
| Best ecosystem | Spring Cloud / Netflix OSS ecosystems; easiest integration for Spring Boot via auto-configuration | Polyglot / HashiCorp ecosystem; used alongside Vault (secrets), Terraform (IaC); popular in Kubernetes environments |
