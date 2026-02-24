# Exercise 06 — CORS Configuration and WebSocket Overview

## Learning Objectives
- Understand what CORS is and why browsers enforce it
- Configure CORS globally using `WebMvcConfigurer`
- Apply per-controller CORS with `@CrossOrigin`
- Understand when WebSockets are appropriate vs REST

---

## Background

### CORS (Cross-Origin Resource Sharing)
Browsers block cross-origin requests by default as a security measure. When a React app running on `http://localhost:3000` calls a Spring API on `http://localhost:8080`, the browser sends a **preflight OPTIONS request** first. Your server must respond with the correct `Access-Control-Allow-Origin` header or the browser will block the response.

Spring MVC provides two approaches:

| Approach | Scope | How |
|---|---|---|
| `@CrossOrigin` | Single controller or method | Annotation on the class/method |
| `WebMvcConfigurer` | Global / multi-origin | `addCorsMappings()` in a `@Configuration` class |

### WebSockets
WebSockets provide a **persistent, bidirectional connection** between client and server — ideal for real-time features like chat, live dashboards, or multiplayer games. Unlike REST, there is no request/response cycle; either side can send data at any time.

---

## Starter Code

| File | Status |
|---|---|
| `pom.xml` | Add `spring-boot-starter-web` |
| `LibraryApplication.java` | Complete `@SpringBootApplication` setup |
| `Book.java` | Provided as-is |
| `BookController.java` | Add `@CrossOrigin` |
| `WebConfig.java` | Implement `WebMvcConfigurer` |
| `WebSocketWorksheet.md` | Answer conceptual questions |

---

## Tasks

### 1. Add `@CrossOrigin` to `BookController.java`
- Add `@CrossOrigin(origins = "http://localhost:3000")` at the **class level**
- This allows the React dev server to call this controller

### 2. Implement `WebConfig.java`
- Annotate with `@Configuration`
- Implement `WebMvcConfigurer`
- Override `addCorsMappings(CorsRegistry registry)`:
  ```java
  registry.addMapping("/api/**")
          .allowedOrigins("http://localhost:3000")
          .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
          .allowedHeaders("*")
          .allowCredentials(true);
  ```
- **Note:** When `WebConfig` is active, it applies globally. The `@CrossOrigin` on the controller is redundant but shown for learning purposes.

### 3. Complete `WebSocketWorksheet.md`
Answer the four questions in the worksheet file.

---

## Expected Behaviour

After starting the app, a browser fetch from `http://localhost:3000` should no longer be blocked:
```javascript
// In a browser console at http://localhost:3000
fetch('http://localhost:8080/api/books')
  .then(r => r.json())
  .then(console.log)
// → should log the book list, not a CORS error
```

---

## Reflection Questions

1. What is the difference between `@CrossOrigin` on a controller and a global `WebMvcConfigurer`?
2. What does `allowCredentials(true)` enable and why can't it be combined with `allowedOrigins("*")`?
3. Why would you choose WebSockets over REST for a live sports scoreboard?
4. What Spring Boot starter would you add to enable WebSocket support?
