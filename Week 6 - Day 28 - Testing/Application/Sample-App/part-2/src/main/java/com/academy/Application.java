package com.academy;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

/**
 * Day 28 — Part 2: Mockito & Spring Boot Testing
 * =================================================
 * Run app:   mvn spring-boot:run
 * Run tests: mvn test
 *
 * Architecture (Controller → Service → Repository) — each layer is testable:
 *   @WebMvcTest      — tests ONLY the web layer (controller)
 *   @Mock @InjectMocks — unit tests for Service with mocked Repository
 *   @DataJpaTest     — tests ONLY the JPA layer
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner seed(StudentRepository repo) {
        return args -> {
            repo.save(new Student(null, "Alice Johnson", "alice@uni.edu", "Computer Science", 3.8));
            repo.save(new Student(null, "Bob Smith",    "bob@uni.edu",   "Mathematics",      3.2));
            repo.save(new Student(null, "Carol Davis",  "carol@uni.edu", "Computer Science", 3.5));
            System.out.println("✅ Seeded 3 students");
        };
    }
}

// ── Entity ────────────────────────────────────────────────────────────────────
@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String major;
    private Double gpa;
}

// ── Repository ────────────────────────────────────────────────────────────────
interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByMajor(String major);
    List<Student> findByGpaGreaterThanEqual(double minGpa);
}

// ── Service ───────────────────────────────────────────────────────────────────
@Service
class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return repository.findById(id);
    }

    public Student createStudent(Student student) {
        boolean emailExists = repository.findAll().stream()
            .anyMatch(s -> s.getEmail().equalsIgnoreCase(student.getEmail()));
        if (emailExists) {
            throw new IllegalArgumentException("Email already exists: " + student.getEmail());
        }
        return repository.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Student not found: " + id));
        repository.deleteById(id);
    }

    public List<Student> getTopStudentsByMajor(String major, double minGpa) {
        return repository.findByMajor(major).stream()
            .filter(s -> s.getGpa() != null && s.getGpa() >= minGpa)
            .toList();
    }
}

// ── Controller ────────────────────────────────────────────────────────────────
@RestController
@RequestMapping("/api/students")
class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Student> getAll() {
        return service.getAllStudents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        return service.getStudentById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Student> create(@RequestBody Student student) {
        try {
            Student created = service.createStudent(student);
            return ResponseEntity.created(URI.create("/api/students/" + created.getId())).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.deleteStudent(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

// ── Testing Reference ─────────────────────────────────────────────────────────
@RestController
@RequestMapping("/api")
class TestingReferenceController {

    @GetMapping("/testing-reference")
    public Map<String, Object> reference() {
        Map<String, Object> ref = new LinkedHashMap<>();
        ref.put("topic", "Mockito & Spring Boot Testing");

        Map<String, String> mockito = new LinkedHashMap<>();
        mockito.put("@Mock",                 "Creates a Mockito mock — all methods return defaults");
        mockito.put("@InjectMocks",          "Creates real instance and injects @Mock fields");
        mockito.put("@ExtendWith(Mockito)",  "@ExtendWith(MockitoExtension.class) — JUnit5 + Mockito");
        mockito.put("when().thenReturn()",   "Stub: when repo.findById(1L) is called, return alice");
        mockito.put("when().thenThrow()",    "Stub: make method throw an exception");
        mockito.put("doNothing().when()",    "Stub void methods that should do nothing");
        mockito.put("verify(mock)",          "Assert method was called exactly once");
        mockito.put("verify(mock, times(n))","Assert method was called exactly N times");
        mockito.put("verify(mock, never())", "Assert method was NEVER called");
        mockito.put("any(), anyLong()",      "ArgumentMatchers — match any argument of a type");
        mockito.put("ArgumentCaptor",        "Capture the actual argument passed to a mock");
        ref.put("mockito", mockito);

        Map<String, String> springTest = new LinkedHashMap<>();
        springTest.put("@SpringBootTest",  "Loads full application context — slow, use for integration tests");
        springTest.put("@WebMvcTest",      "Loads ONLY web layer (controller) — fast, use @MockBean for services");
        springTest.put("@DataJpaTest",     "Loads ONLY JPA layer — uses in-memory H2 by default");
        springTest.put("MockMvc",          "Simulate HTTP requests without running a real server");
        springTest.put("@MockBean",        "Spring-managed Mockito mock — replaces bean in context");
        springTest.put("perform(get())",   "Execute GET request in MockMvc");
        springTest.put("andExpect()",      "Assert status code, JSON content, headers");
        springTest.put("jsonPath()",       "Assert specific JSON field values in response");
        ref.put("springBootTesting", springTest);

        Map<String, String> testTypes = new LinkedHashMap<>();
        testTypes.put("Unit Test",        "Tests ONE class in isolation — mocks all dependencies");
        testTypes.put("Integration Test", "Tests multiple layers together — may use real DB");
        testTypes.put("Slice Test",       "@WebMvcTest / @DataJpaTest — tests one layer with Spring context");
        testTypes.put("E2E Test",         "Tests entire system from HTTP request to DB and back");
        ref.put("testTypes", testTypes);

        Map<String, String> advanced = new LinkedHashMap<>();
        advanced.put("Testcontainers", "Spin up real Docker containers (PostgreSQL, etc.) in tests");
        advanced.put("WireMock",       "Mock external HTTP APIs (simulate REST responses in tests)");
        advanced.put("JaCoCo",         "Code coverage plugin — add to pom.xml, run: mvn test jacoco:report");
        ref.put("advancedTools", advanced);

        return ref;
    }
}
