# Spring MVC Architecture & Components

## What is Spring MVC?

Spring MVC is the **web framework** layer of the Spring ecosystem. It sits on top of the Servlet API and implements the **Model-View-Controller** design pattern to cleanly separate concerns in a web application.

- **Model** — your data and business logic (entities, services, repositories)
- **View** — the response sent to the client (JSON, HTML, XML)
- **Controller** — the mapping layer between HTTP requests and your Java code

For REST APIs, the "View" is almost always **JSON** — rendered by Jackson automatically. For server-side rendered apps (Thymeleaf), the View would be an HTML template.

---

## The DispatcherServlet — The Front Controller

Every HTTP request entering a Spring MVC application passes through a single Java Servlet called the **DispatcherServlet**. This is the **Front Controller** pattern.

```
HTTP Request
     │
     ▼
┌─────────────────────────────────────────────────────────┐
│                   DispatcherServlet                     │
│  (Registered at "/" — intercepts ALL requests)          │
│                                                         │
│  1. Consults HandlerMapping → finds which @Controller   │
│     method matches this URL + HTTP method               │
│                                                         │
│  2. Consults HandlerAdapter → calls that method,        │
│     passing resolved parameters (@PathVariable, etc.)   │
│                                                         │
│  3. Method returns a value (Java object, ResponseEntity)│
│                                                         │
│  4. HttpMessageConverter (Jackson) serializes the       │
│     return value to JSON                                │
│                                                         │
│  5. DispatcherServlet writes the response               │
└─────────────────────────────────────────────────────────┘
     │
     ▼
HTTP Response (JSON body, status code, headers)
```

### Key Components

| Component | Role |
|-----------|------|
| `DispatcherServlet` | Front controller — routes every request |
| `HandlerMapping` | Finds which controller method matches the URL |
| `HandlerAdapter` | Invokes the controller method, resolves parameters |
| `HttpMessageConverter` | Serializes/deserializes Java ↔ JSON (Jackson) |
| `HandlerExceptionResolver` | Routes exceptions to `@ExceptionHandler` methods |
| `ViewResolver` | (For server-side rendering) maps view names to templates |

---

## Request Processing — Step by Step

Let's trace what happens when `GET /api/books/42` arrives:

```
1. HTTP GET /api/books/42 arrives at the server

2. DispatcherServlet receives the request
   └── Delegates to RequestMappingHandlerMapping

3. HandlerMapping scans all @Controller beans
   └── Finds: BookController.getBook(@PathVariable Long id)
             — matched by "GET" + "/api/books/{id}"

4. HandlerAdapter invokes BookController.getBook(42L)
   └── Spring extracts "42" from the URL path
       and converts it to Long via TypeConversion

5. BookController calls BookService.findById(42L)
   └── Service calls BookRepository.findById(42L)
   └── Returns Optional<Book>

6. BookController returns ResponseEntity<BookDTO>

7. MappingJackson2HttpMessageConverter serializes BookDTO to:
   {
     "id": 42,
     "title": "Effective Java",
     "author": "Joshua Bloch",
     "price": 49.99
   }

8. DispatcherServlet writes the response:
   HTTP/1.1 200 OK
   Content-Type: application/json
   Body: { ... }
```

---

## How Spring Boot Auto-Configures Spring MVC

In a Spring Boot application, you don't manually configure the `DispatcherServlet`. Spring Boot's `DispatcherServletAutoConfiguration` does all of this:

1. Registers `DispatcherServlet` mapped to `/`
2. Creates `RequestMappingHandlerMapping` to scan `@Controller` beans
3. Configures `MappingJackson2HttpMessageConverter` (from `jackson-databind` on the classpath)
4. Sets up `HandlerExceptionResolver` infrastructure

All you need is `spring-boot-starter-web` in your `pom.xml`.

---

## The Three-Layer Architecture

Spring MVC applications are organized into three layers:

```
┌─────────────────────────────────────────┐
│           Presentation Layer            │
│  @RestController, @Controller           │
│  Handles HTTP — request/response only   │
│  Validates input, maps to/from DTOs     │
└──────────────────┬──────────────────────┘
                   │ calls
┌──────────────────▼──────────────────────┐
│            Service Layer                │
│  @Service                               │
│  Business logic lives here              │
│  Orchestrates multiple repositories     │
│  Transaction boundaries                 │
└──────────────────┬──────────────────────┘
                   │ calls
┌──────────────────▼──────────────────────┐
│           Repository Layer              │
│  @Repository / JpaRepository            │
│  Database access only                   │
│  No business logic                      │
└─────────────────────────────────────────┘
```

**The rules:**
- Controllers **never** access repositories directly
- Services **never** know about HTTP (no `HttpServletRequest` in the service)
- Repositories **never** contain business logic
- Each layer depends only on the layer directly below it

---

## Spring MVC vs Spring WebFlux

Spring provides two web frameworks:

| | Spring MVC | Spring WebFlux |
|---|---|---|
| **Model** | Blocking, thread-per-request | Non-blocking, reactive |
| **Thread pool** | Tomcat thread pool | Event loop (Netty) |
| **Best for** | Standard CRUD apps, teams familiar with servlet model | High-concurrency, streaming, microservices with many I/O calls |
| **Learning curve** | Lower | Higher (requires reactive programming knowledge) |
| **Annotation overlap** | `@GetMapping`, `@RestController`, etc. | Same annotations work! |

For this course — and the vast majority of enterprise Java projects — we use **Spring MVC**.

---

## Summary

| Concept | Key Takeaway |
|---------|-------------|
| DispatcherServlet | Single entry point for ALL requests; routes to the right controller |
| HandlerMapping | Scans `@Controller` beans to find which method matches URL + HTTP verb |
| HttpMessageConverter | Jackson converts Java objects → JSON and JSON → Java objects |
| Three-layer architecture | Controller → Service → Repository; each layer has one job |
| Spring Boot auto-config | `spring-boot-starter-web` configures everything automatically |
