package com.academy;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Day 26 — Part 1: Spring MVC — REST API with Validation & Error Handling
 * =========================================================================
 * Topics covered:
 *   ✓ @RestController — combines @Controller + @ResponseBody
 *   ✓ @RequestMapping, @GetMapping, @PostMapping, @PutMapping, @DeleteMapping, @PatchMapping
 *   ✓ @PathVariable — extract values from the URL path
 *   ✓ @RequestParam — extract query string parameters
 *   ✓ @RequestBody — deserialize JSON body to Java object (Jackson)
 *   ✓ ResponseEntity<T> — control status code + headers + body
 *   ✓ Bean Validation API — @Valid, @NotBlank, @NotNull, @Size, @Min, @Max, @Email, @Pattern
 *   ✓ @ControllerAdvice + @ExceptionHandler — global error handling
 *   ✓ DTOs vs Entities, object mapping strategy
 *   ✓ CORS basics with @CrossOrigin
 *   ✓ Service layer pattern, Repository layer pattern
 *
 * Run: mvn spring-boot:run
 * Endpoints:
 *   GET    http://localhost:8080/api/students
 *   GET    http://localhost:8080/api/students/1
 *   POST   http://localhost:8080/api/students       (JSON body required)
 *   PUT    http://localhost:8080/api/students/1
 *   PATCH  http://localhost:8080/api/students/1
 *   DELETE http://localhost:8080/api/students/1
 *   GET    http://localhost:8080/api/mvc-reference
 */
@SpringBootApplication
@Slf4j
public class Application {
    public static void main(String[] args) { SpringApplication.run(Application.class, args); }

    @Bean
    CommandLineRunner startup() {
        return args -> {
            log.info("╔════════════════════════════════════════════════════════════╗");
            log.info("║  Day 26 Part 1 — Spring MVC REST API Demo                  ║");
            log.info("║  Base URL: http://localhost:8080/api                       ║");
            log.info("╚════════════════════════════════════════════════════════════╝");
        };
    }
}

// ─── DTOs ─────────────────────────────────────────────────────────────────
@Data @Builder @NoArgsConstructor @AllArgsConstructor
class StudentDto {
    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be 2–100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    private String email;

    @NotBlank(message = "Major is required")
    private String major;

    @NotNull(message = "GPA is required")
    @Min(value = 0, message = "GPA cannot be negative")
    @Max(value = 4, message = "GPA cannot exceed 4.0")
    @DecimalMin(value = "0.0") @DecimalMax(value = "4.0")
    private Double gpa;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age must be realistic")
    private Integer age;
}

// ─── Service Layer ────────────────────────────────────────────────────────
@org.springframework.stereotype.Service
@Slf4j
class StudentService {
    private final Map<Integer, StudentDto> store = new java.util.concurrent.ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(1);

    StudentService() {
        // Seed data
        save(StudentDto.builder().name("Alice Johnson").email("alice@uni.edu").major("Computer Science").gpa(3.8).age(20).build());
        save(StudentDto.builder().name("Bob Smith").email("bob@uni.edu").major("Mathematics").gpa(3.2).age(22).build());
        save(StudentDto.builder().name("Carol Davis").email("carol@uni.edu").major("Computer Science").gpa(3.9).age(19).build());
    }

    public List<StudentDto> findAll(String major) {
        return store.values().stream()
            .filter(s -> major == null || s.getMajor().equalsIgnoreCase(major))
            .sorted(Comparator.comparing(StudentDto::getId))
            .collect(Collectors.toList());
    }

    public Optional<StudentDto> findById(int id) { return Optional.ofNullable(store.get(id)); }

    public StudentDto save(StudentDto dto) {
        if (dto.getId() == null) dto.setId(counter.getAndIncrement());
        store.put(dto.getId(), dto);
        log.info("Saved student id={} name={}", dto.getId(), dto.getName());
        return dto;
    }

    public boolean delete(int id) {
        return store.remove(id) != null;
    }
}

// ─── Controller ───────────────────────────────────────────────────────────
@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")  // Allow all origins for demo; restrict in production
@Slf4j
class StudentController {

    private final StudentService service;
    StudentController(StudentService service) { this.service = service; }

    /**
     * GET /api/students          — all students
     * GET /api/students?major=CS — filter by major
     * GET /api/students?sort=gpa&order=desc
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false) String major,
            @RequestParam(defaultValue = "name") String sort) {
        log.debug("GET /api/students major={} sort={}", major, sort);
        List<StudentDto> list = service.findAll(major);
        if ("gpa".equals(sort)) list.sort(Comparator.comparingDouble(StudentDto::getGpa).reversed());
        return ResponseEntity.ok(Map.of("total", list.size(), "data", list));
    }

    /** GET /api/students/{id} — single student by ID */
    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getOne(@PathVariable int id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/students — create; @Valid triggers Bean Validation
     * Returns 201 + Location header
     */
    @PostMapping
    public ResponseEntity<StudentDto> create(@Valid @RequestBody StudentDto dto) {
        dto.setId(null); // ignore any supplied id
        StudentDto saved = service.save(dto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header(HttpHeaders.LOCATION, "/api/students/" + saved.getId())
            .body(saved);
    }

    /** PUT /api/students/{id} — full replacement */
    @PutMapping("/{id}")
    public ResponseEntity<StudentDto> replace(@PathVariable int id, @Valid @RequestBody StudentDto dto) {
        if (service.findById(id).isEmpty()) return ResponseEntity.notFound().build();
        dto.setId(id);
        return ResponseEntity.ok(service.save(dto));
    }

    /** PATCH /api/students/{id} — partial update (no @Valid — partial data) */
    @PatchMapping("/{id}")
    public ResponseEntity<StudentDto> patch(@PathVariable int id, @RequestBody Map<String, Object> updates) {
        return service.findById(id).map(student -> {
            if (updates.containsKey("name"))  student.setName((String) updates.get("name"));
            if (updates.containsKey("major")) student.setMajor((String) updates.get("major"));
            if (updates.containsKey("gpa"))   student.setGpa(((Number) updates.get("gpa")).doubleValue());
            return ResponseEntity.ok(service.save(student));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /api/students/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        return service.delete(id)
            ? ResponseEntity.noContent().<Void>build()
            : ResponseEntity.notFound().build();
    }
}

// ─── Global Exception Handler ─────────────────────────────────────────────
@RestControllerAdvice
@Slf4j
class GlobalExceptionHandler {

    /** Handle Bean Validation failures (@Valid) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        log.warn("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
            "status",    400,
            "error",     "Validation Failed",
            "timestamp", LocalDateTime.now().toString(),
            "errors",    errors
        ));
    }

    /** Handle any uncaught exception */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unhandled error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "status",  500,
            "error",   "Internal Server Error",
            "message", ex.getMessage()
        ));
    }
}

// ─── MVC Reference ────────────────────────────────────────────────────────
@RestController
@RequestMapping("/api")
class MvcReferenceController {
    @GetMapping("/mvc-reference")
    public Map<String, Object> reference() {
        return Map.of(
            "annotations", Map.of(
                "@RestController", "@Controller + @ResponseBody — JSON responses by default",
                "@RequestMapping", "Maps a URL to a class or method",
                "@GetMapping / @PostMapping etc", "Shorthand for @RequestMapping(method=...)",
                "@PathVariable", "URL template: /students/{id}",
                "@RequestParam", "Query string: /students?major=CS",
                "@RequestBody", "Deserialize JSON body → Java object (Jackson)",
                "@Valid", "Trigger Bean Validation on the annotated parameter",
                "@ResponseStatus", "Set default status code for a method",
                "@CrossOrigin", "Enable CORS for a controller or method"
            ),
            "validationAnnotations", Map.of(
                "@NotNull", "Field must not be null",
                "@NotBlank", "String must not be null, empty, or whitespace",
                "@Size(min,max)", "String/collection size constraint",
                "@Min/@Max", "Numeric minimum/maximum",
                "@Email", "Must be a valid email format",
                "@Pattern(regexp)", "Must match regex",
                "@Positive / @PositiveOrZero", "Number must be positive"
            ),
            "layerPattern", Map.of(
                "Controller", "Handles HTTP request/response; delegates to service",
                "Service",    "Business logic; orchestrates repositories",
                "Repository", "Database access (JPA/JDBC); returns domain objects",
                "DTO",        "Data Transfer Object — what the API sends/receives",
                "Entity",     "JPA-mapped class — matches database table structure"
            )
        );
    }
}
