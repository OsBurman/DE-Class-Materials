# WebSocket Worksheet — Answers

---

## Question 1
**What is the fundamental difference between HTTP (REST) and WebSockets in terms of connection behaviour?**

HTTP is a **request/response** protocol: the client opens a connection, sends a request, the server replies, and the connection closes (or is reused briefly). Each interaction requires a new request from the client.

WebSockets establish a **persistent, full-duplex connection**: after an initial HTTP handshake (upgrade), the connection stays open and either the client or server can push data to the other side at any time without a new request.

---

## Question 2
**Give two real-world examples where WebSockets would be a better choice than REST polling.**

1. **Live sports scores** — scores change frequently and unpredictably; REST polling every second wastes bandwidth and adds latency. With WebSockets the server pushes updates instantly.
2. **Collaborative document editing** (e.g., Google Docs) — multiple users editing simultaneously need low-latency, bidirectional sync that REST cannot efficiently provide.

---

## Question 3
**What Spring Boot starter dependency would you add to enable WebSocket support in a Spring application?**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

---

## Question 4
**In a WebSocket architecture, what is the role of a "message broker" like STOMP?**

STOMP (Simple Text Oriented Messaging Protocol) is a sub-protocol that runs over WebSocket. It defines a messaging format with **destinations** (like `/topic/scores` or `/queue/user123`). A message broker (built-in Spring simple broker or external like RabbitMQ) receives messages on one destination and **routes** them to all subscribers. This allows Spring to handle fan-out to many clients cleanly, rather than managing raw WebSocket sessions manually.
