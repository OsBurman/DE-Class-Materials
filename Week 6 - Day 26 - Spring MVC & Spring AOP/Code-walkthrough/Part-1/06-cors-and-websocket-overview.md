# CORS & WebSocket Overview

## CORS — Cross-Origin Resource Sharing

### What Is CORS?

CORS is a browser security mechanism. When JavaScript running at `http://localhost:3000` (your React app) tries to call `http://localhost:8080/api/v1/books` (your Spring Boot API), the browser blocks this request by default because the origins differ.

**Same origin** = same protocol + domain + port:
- `http://localhost:3000` ↔ `http://localhost:8080` → **Cross-origin** (different port)
- `https://myapp.com` ↔ `https://api.myapp.com` → **Cross-origin** (different subdomain)
- `http://myapp.com` ↔ `https://myapp.com` → **Cross-origin** (different protocol)

**The browser preflight request:**
```
OPTIONS /api/v1/books HTTP/1.1
Origin: http://localhost:3000
Access-Control-Request-Method: POST
Access-Control-Request-Headers: Content-Type

(Browser asks: "Is this server willing to accept a POST from localhost:3000?")

Server responds:
HTTP/1.1 200 OK
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET, POST, PUT, DELETE
Access-Control-Allow-Headers: Content-Type, Authorization
```

Only after the preflight succeeds does the browser send the actual request.

---

### Method 1: @CrossOrigin on a Single Controller or Method

```java
package com.bookstore.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

// Apply CORS to ALL methods in this controller
@CrossOrigin(
    origins = { "http://localhost:3000", "https://bookstore-app.vercel.app" },
    methods = {
        org.springframework.web.bind.annotation.RequestMethod.GET,
        org.springframework.web.bind.annotation.RequestMethod.POST,
        org.springframework.web.bind.annotation.RequestMethod.PUT,
        org.springframework.web.bind.annotation.RequestMethod.DELETE
    },
    allowedHeaders = { "Content-Type", "Authorization" },
    maxAge = 3600   // Cache preflight response for 1 hour
)
@RestController
@RequestMapping("/api/v1/books")
public class BookControllerWithCors {
    // ... controller methods
}
```

You can also narrow it to a single method:

```java
@CrossOrigin(origins = "http://localhost:3000")
@GetMapping("/{id}")
public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
    // ...
}
```

---

### Method 2: Global CORS Configuration (Recommended)

For production apps, configure CORS once in a global `WebMvcConfigurer` so you don't repeat it on every controller:

```java
package com.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                    .addMapping("/api/**")          // Apply to all /api routes
                    .allowedOrigins(
                        "http://localhost:3000",     // React dev server
                        "http://localhost:4200",     // Angular dev server
                        "https://bookstore.example.com"  // Production frontend
                    )
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    .allowedHeaders("Content-Type", "Authorization", "X-Requested-With")
                    .allowCredentials(true)         // Allow cookies + auth headers
                    .maxAge(3600);                  // Preflight cache TTL (seconds)
            }
        };
    }
}
```

### CORS with Spring Security

If you add Spring Security, Spring Security runs **before** CORS processing. You must register CORS in the `SecurityFilterChain` as well:

```java
// application.properties or SecurityConfig.java
// Spring Security must be told to use Spring MVC's CORS config:
// http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
```

We'll cover this fully on Day 29 (Spring Security).

---

### CORS Configuration Reference

| Property | Description | Example |
|----------|-------------|---------|
| `allowedOrigins` | Origins allowed to make cross-origin requests | `"http://localhost:3000"` |
| `allowedMethods` | HTTP methods allowed | `"GET", "POST", "DELETE"` |
| `allowedHeaders` | Request headers allowed | `"Authorization", "Content-Type"` |
| `exposedHeaders` | Response headers the browser can read | `"X-Total-Count"` |
| `allowCredentials` | Allow cookies and auth headers | `true` / `false` |
| `maxAge` | Preflight cache duration in seconds | `3600` |

> ⚠️ **Never use `allowedOrigins("*")` with `allowCredentials(true)`** — browsers block this combination. Either allow specific origins or don't allow credentials.

---

## WebSocket Support in Spring — Brief Overview

### What Are WebSockets?

HTTP is a **request-response** protocol — the client always initiates. WebSockets establish a **persistent, bidirectional connection** between client and server. Either side can send messages at any time without the other side polling.

```
Traditional HTTP polling (wasteful):
Client → GET /updates → Server: "nothing new"   (every 1 second)
Client → GET /updates → Server: "nothing new"
Client → GET /updates → Server: "here's an update!"

WebSocket (efficient):
Client → WS CONNECT → Server: connection established
Server → "here's an update!" → Client (pushed immediately, no polling)
Server → "another update!"   → Client
Client → "client message"    → Server (bidirectional)
```

### Use Cases for WebSockets

- **Real-time chat** — messages pushed to all users immediately
- **Live notifications** — "Your order shipped!"
- **Collaborative editing** — Google Docs-style concurrent editing
- **Live dashboards** — stock prices, sensor readings, sports scores
- **Multiplayer games** — player positions, game state

---

### Spring WebSocket Dependencies

```xml
<!-- Already included in spring-boot-starter-web -->
<!-- For STOMP messaging over WebSocket, add: -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

---

### WebSocket Configuration (Overview)

```java
package com.bookstore.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

// Enable WebSocket message handling with STOMP
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Clients connect to WebSocket at: ws://localhost:8080/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();  // SockJS fallback for browsers without WS support
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Messages prefixed with /app are routed to @MessageMapping methods
        config.setApplicationDestinationPrefixes("/app");

        // Simple in-memory broker for topics (subscriptions) and queues (direct)
        config.enableSimpleBroker("/topic", "/queue");
    }
}

// WebSocket controller — handles messages from clients
@org.springframework.stereotype.Controller
class BookNotificationController {

    // Client sends: STOMP SEND /app/new-order
    // Server broadcasts to: /topic/order-updates
    @MessageMapping("/new-order")
    @SendTo("/topic/order-updates")
    public String notifyNewOrder(String orderId) {
        return "New order placed: " + orderId;
    }
}
```

### JavaScript Client (Frontend Side — Awareness)

```javascript
// Using SockJS + STOMP.js
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, () => {
    // Subscribe to order updates
    stompClient.subscribe('/topic/order-updates', (message) => {
        console.log('Received:', message.body);
    });
});

// Send a message
stompClient.send('/app/new-order', {}, '{"orderId": "ORD-42"}');
```

---

### STOMP — Why Use It Over Raw WebSocket?

STOMP (Simple Text Oriented Message Protocol) adds a message protocol on top of WebSocket's raw binary channel:

| Feature | Raw WebSocket | STOMP over WebSocket |
|---------|--------------|----------------------|
| Message format | Raw bytes/text | Structured frame (SEND/SUBSCRIBE/MESSAGE) |
| Topics/Subscriptions | Manual | Built-in (`/topic/x`) |
| Routing | Manual | `@MessageMapping` annotations |
| Broadcast | Manual | `@SendTo` |
| Spring integration | Limited | Full (@MessageMapping, SimpMessagingTemplate) |

---

### WebSocket vs Server-Sent Events (SSE)

| | WebSocket | Server-Sent Events |
|---|---|---|
| Direction | Bidirectional | Server → Client only |
| Protocol | WS/WSS | HTTP/HTTPS |
| Reconnection | Manual | Automatic |
| Spring support | `@EnableWebSocketMessageBroker` | `SseEmitter` |
| Best for | Chat, games, collaboration | Notifications, live feeds, progress |

> For most notification use cases (stock prices, order updates, system alerts), **Server-Sent Events** are simpler than WebSockets because they use standard HTTP and reconnect automatically.

---

## Summary

| Feature | Key Point |
|---------|-----------|
| CORS | Browser security; configure with `@CrossOrigin` or global `WebMvcConfigurer` |
| Global CORS | `WebMvcConfigurer.addCorsMappings()` — configure once for all endpoints |
| `allowCredentials` | Never combine with `allowedOrigins("*")` |
| WebSocket | Persistent bidirectional connection; use for real-time features |
| STOMP | Message protocol over WebSocket; Spring supports via `@EnableWebSocketMessageBroker` |
| SSE | Simpler alternative for server-push-only scenarios |
