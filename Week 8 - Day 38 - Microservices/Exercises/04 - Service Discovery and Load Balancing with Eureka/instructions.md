# Exercise 04: Service Discovery and Load Balancing with Eureka

## Objective

Understand how service discovery eliminates hard-coded service addresses in a microservices system and how client-side load balancing distributes traffic across multiple service instances.

## Background

In a microservices system, each service is deployed as multiple instances for redundancy and scalability. The Order Service cannot hard-code `http://inventory-service-host-1:8081` because that instance may be replaced, scaled to five replicas, or moved to a different IP at any time by Kubernetes or Docker. **Service discovery** solves this: services register themselves with a registry (Eureka or Consul), and callers ask the registry "where is Inventory Service right now?" before making a call. **Client-side load balancing** (Spring Cloud LoadBalancer) then picks which instance to call.

## Requirements

1. **Service discovery concepts.** Answer the following:
   - What is the difference between **client-side service discovery** and **server-side service discovery**? Identify which Eureka + Spring Cloud LoadBalancer uses.
   - What does a service send to the Eureka Server when it starts up? What is a **heartbeat** and what happens if a service stops sending heartbeats?
   - What is the **Eureka self-preservation mode** and when does it activate?

2. **Spring Cloud Eureka setup — Server.** A Eureka Server is a standard Spring Boot application with two special additions. List:
   - The Maven `artifactId` of the Spring Cloud Eureka Server dependency
   - The annotation added to the `@SpringBootApplication` class
   - The two `application.yml` properties that prevent the Eureka Server from registering with itself

3. **Spring Cloud Eureka setup — Client.** An Order Service registers with the Eureka Server and calls an Inventory Service by name. List:
   - The Maven `artifactId` of the Spring Cloud Eureka Client dependency
   - The `application.yml` property that sets the service name used for registration
   - The `application.yml` property that points the client to the Eureka Server URL
   - The annotation or Spring Bean needed to enable client-side load balancing with `RestTemplate`

4. **Load balancing strategies.** Spring Cloud LoadBalancer supports two built-in strategies. Complete the table:

   | Strategy | How it works | Best suited for |
   |---|---|---|
   | Round Robin | | |
   | Random | | |

5. **Registration and discovery flow diagram.** Draw an ASCII diagram showing:
   - Inventory Service (2 instances) registering with Eureka Server on startup
   - Order Service querying Eureka to discover Inventory Service instances
   - Order Service using client-side load balancing to choose an instance and make a call

6. **Consul vs Eureka comparison.** Complete the table:

   | Criterion | Netflix Eureka | HashiCorp Consul |
   |---|---|---|
   | Health check mechanism | | |
   | Service mesh support | | |
   | Multi-datacenter support | | |
   | Best ecosystem | | |

## Hints

- In **client-side discovery**, the client (Order Service) fetches the registry and picks an instance itself. In **server-side discovery**, the client calls a load balancer/proxy that looks up the registry and forwards the call.
- The `@LoadBalanced` annotation on a `RestTemplate` `@Bean` makes it automatically resolve service names (like `http://inventory-service/...`) using the discovery registry.
- Eureka clients cache the registry locally — if the Eureka Server goes down briefly, clients can still route calls using their cached registry copy.
- Spring Boot application names (`spring.application.name`) become the service ID in the Eureka registry — this is the name other services use to call them.

## Expected Output

This is a configuration and concepts exercise. Your answers should include written explanations, property snippets, and an ASCII diagram.

```
Requirement 1 — Service discovery concepts: [written answers]

Requirement 2 — Eureka Server setup:
  Dependency: spring-cloud-starter-netflix-eureka-server
  Annotation: @EnableEurekaServer
  Properties:
    register-with-eureka: false
    fetch-registry: false

Requirement 3 — Eureka Client setup: [dependency, annotation, properties]

Requirement 4 — Load balancing strategies table: [filled in]

Requirement 5 — ASCII diagram:
  [Inventory Instance 1] ──register──▶ [Eureka Server]
  [Inventory Instance 2] ──register──▶ [Eureka Server]
  [Order Service] ──discover──▶ [Eureka Server] ──returns: [inst1, inst2]
  [Order Service] ──round-robin──▶ [Inventory Instance 1]

Requirement 6 — Eureka vs Consul table: [filled in]
```
