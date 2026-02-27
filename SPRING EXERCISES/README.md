# Spring Boot Exercises — Full Curriculum

This folder contains **12 progressive Spring Boot exercises** designed to take you from zero to production-ready Spring Boot developer. Each exercise is a **self-contained Maven project** with starter code containing TODOs and a complete solution.

---

## 🗂️ Exercise Index

| # | Exercise | Key Concepts |
|---|----------|-------------|
| 01 | [REST Controllers](#) | `@RestController`, `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `ResponseEntity` |
| 02 | [Request Handling & DTOs](#) | `@RequestBody`, `@RequestParam`, `@PathVariable`, DTO pattern, HTTP status codes |
| 03 | [Service Layer & DI](#) | `@Service`, `@Component`, constructor injection, interfaces, business logic separation |
| 04 | [Spring Data JPA](#) | `@Entity`, `@Repository`, `JpaRepository`, H2 database, CRUD operations |
| 05 | [JPA Relationships](#) | `@OneToMany`, `@ManyToOne`, `@ManyToMany`, JPQL, derived queries, pagination |
| 06 | [Exception Handling](#) | `@ControllerAdvice`, `@ExceptionHandler`, custom exceptions, error response body |
| 07 | [Bean Validation](#) | `@Valid`, `@NotBlank`, `@Email`, `@Size`, custom validators, validation error responses |
| 08 | [Spring Security](#) | `SecurityFilterChain`, `UserDetailsService`, `BCryptPasswordEncoder`, role-based access |
| 09 | [JWT Authentication](#) | JWT generation/validation, `OncePerRequestFilter`, stateless auth, `@PreAuthorize` |
| 10 | [Testing](#) | `@SpringBootTest`, `@WebMvcTest`, `@DataJpaTest`, MockMvc, Mockito |
| 11 | [Spring AOP](#) | `@Aspect`, `@Before`, `@After`, `@Around`, pointcuts, logging & auditing |
| 12 | [Full Application](#) | Capstone: Social Media API combining all concepts |

---

## 🚀 Getting Started

### Prerequisites
```bash
# Java 17+
java -version

# Maven 3.6+
mvn -version

# Or use the Maven wrapper included in each project
./mvnw -version
```

### Recommended Tools
- **IDE**: IntelliJ IDEA (Community or Ultimate) — has excellent Spring Boot support
- **API Testing**: [Postman](https://postman.com) or [HTTPie](https://httpie.io) or VS Code REST Client
- **DB Viewer**: [H2 Console](http://localhost:8080/h2-console) (enabled in most exercises)

### How to Work on Each Exercise

1. **Open the `starter-code/` folder** as a Maven project in your IDE.

2. **Read the `README.md`** inside the exercise folder — it lists every TODO you need to complete.

3. **Run the application** to see the starting state:
   ```bash
   cd Exercise-XX/starter-code
   ./mvnw spring-boot:run
   ```
   Or run the `*Application.java` main class directly from your IDE.

4. **Implement the TODOs** in the order listed in the README.

5. **Test your endpoints** using Postman or the provided `requests.http` file.

6. **Check your work** against the `solution/` folder when you're done.

---

## 📁 Project Structure (Each Exercise)

```
Exercise-XX-Topic-Name/
├── README.md                  ← Instructions, TODOs, key concepts
├── starter-code/              ← Your starting point (has TODO comments)
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/exercise/[pkg]/
│       │   │   ├── [Name]Application.java
│       │   │   ├── controller/
│       │   │   ├── service/
│       │   │   ├── repository/
│       │   │   ├── entity/  (or model/)
│       │   │   ├── dto/
│       │   │   └── exception/  (where applicable)
│       │   └── resources/
│       │       └── application.properties
│       └── test/
│           └── java/...
└── solution/                  ← Complete working implementation
    ├── pom.xml
    └── src/...
```

---

## 🎯 Learning Path

```
Exercise 01 → 02 → 03   (Core: Controllers, DTOs, Service Layer)
Exercise 04 → 05         (Persistence: JPA & Relationships)
Exercise 06 → 07         (Robustness: Exceptions & Validation)
Exercise 08 → 09         (Security: Basic Auth & JWT)
Exercise 10              (Quality: Testing)
Exercise 11              (Cross-cutting: AOP)
Exercise 12              (Capstone: Full Application)
```

---

## ☕ Spring Boot Version

These exercises use **Spring Boot 3.3** with **Java 17**:
- ✅ Jakarta EE 10 (`jakarta.*` packages — not `javax.*`)
- ✅ Spring Data JPA with Hibernate 6
- ✅ Spring Security 6 (`SecurityFilterChain` lambda DSL)
- ✅ Bean Validation 3.0
- ✅ Virtual threads friendly
