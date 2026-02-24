# Microservices Architecture — Startup & API Flow Guide
# Day 38

## Startup Order (IMPORTANT — start in this order!)

1. **service-registry** (port 8761) — Eureka server must be up first
2. **product-service** (port 8081)
3. **order-service** (port 8082)
4. **notification-service** (port 8083)
5. **api-gateway** (port 8080) — start last

```bash
# From the microservices-parent directory:
# In 5 separate terminals:
cd service-registry     && mvn spring-boot:run
cd product-service      && mvn spring-boot:run
cd order-service        && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd api-gateway          && mvn spring-boot:run
```

## Service Discovery

Visit: http://localhost:8761 — you should see all 4 services registered.

## API Flow (via gateway on port 8080)

### Get all products
GET http://localhost:8080/api/products

### Create an order (triggers Feign call to product-service)
POST http://localhost:8080/api/orders
Content-Type: application/json
{
  "productId": "P001",
  "quantity": 2,
  "customerId": "C001"
}

### Get all orders
GET http://localhost:8080/api/orders

### Check notifications
GET http://localhost:8080/api/notifications

---

## Architecture Diagram

```
                  ┌─────────────────────────────────┐
     Client       │         API Gateway :8080        │
   (browser/     │   Routes → lb://service-name     │
    Postman)  ──►│   Discovers via Eureka           │
                  └─────────┬────────────────────────┘
                             │ load-balanced via Eureka
              ┌──────────────┼─────────────────┐
              │              │                 │
              ▼              ▼                 ▼
    ┌──────────────┐ ┌──────────────┐ ┌──────────────────────┐
    │product-service│ │order-service │ │notification-service  │
    │   :8081      │ │   :8082      │ │       :8083          │
    └──────────────┘ └──────┬───────┘ └──────────────────────┘
                             │ FeignClient
                             └──────────────► product-service
                                              (via Eureka)

    ┌──────────────────────────────┐
    │  Eureka Service Registry     │
    │         :8761                │
    │  All services register here  │
    └──────────────────────────────┘
```

## TODO Discussion Questions

1. What happens if product-service goes down while creating an order?
   (Hint: observe the circuit breaker fallback)

2. What is the benefit of routing through the API Gateway instead of calling services directly?

3. In a real production system, how would you handle the case where two order-service
   instances both try to reduce stock for the same product simultaneously?
