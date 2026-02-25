package com.academy;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Day 27 — Part 1: Spring Data JPA — Repositories, CRUD & Query Methods
 * =======================================================================
 * Topics covered:
 *   ✓ JpaRepository<T, ID>   — extends CrudRepository + PagingAndSortingRepository
 *   ✓ @Entity, @Table, @Id, @GeneratedValue, @Column — entity annotations
 *   ✓ Derived Query Methods  — Spring generates SQL from method name at startup
 *   ✓ @Query (JPQL)          — custom HQL-style queries using entity/field names
 *   ✓ @Query (native SQL)    — raw SQL with nativeQuery = true
 *   ✓ @Modifying + @Transactional — required for UPDATE / DELETE @Query methods
 *   ✓ Pageable + Page<T>     — pagination and sorting
 *   ✓ Optional<T>            — null-safe single-result lookups
 *   ✓ @Transactional         — service-layer transaction demarcation
 *   ✓ H2 in-memory database  — zero config; resets on restart
 *
 * Run: mvn spring-boot:run
 * H2 Console: http://localhost:8080/h2-console
 *   JDBC URL: jdbc:h2:mem:academydb   Username: sa   Password: (blank)
 *
 * Endpoints:
 *   GET    http://localhost:8080/api/students
 *   GET    http://localhost:8080/api/students?major=Computer+Science
 *   GET    http://localhost:8080/api/students?minGpa=3.5&page=0&size=5
 *   GET    http://localhost:8080/api/students/top?minGpa=3.5
 *   GET    http://localhost:8080/api/students/avg-gpa?major=Mathematics
 *   GET    http://localhost:8080/api/students/{id}
 *   POST   http://localhost:8080/api/students
 *   DELETE http://localhost:8080/api/students/{id}
 *   POST   http://localhost:8080/api/students/deactivate?threshold=2.5
 *   GET    http://localhost:8080/api/jpa-reference
 *   GET    http://localhost:8080/api/h2-info
 */
@SpringBootApplication
@Slf4j
public class Application {
    public static void main(String[] args) { SpringApplication.run(Application.class, args); }

    @Bean
    CommandLineRunner seedData(StudentRepository repo) {
        return args -> {
            log.info("Seeding 8 students across 3 majors...");
            repo.saveAll(List.of(
                Student.builder().firstName("Alice").lastName("Johnson").email("alice@uni.edu")
                    .major("Computer Science").gpa(3.8).enrollmentYear(2022).build(),
                Student.builder().firstName("Bob").lastName("Smith").email("bob@uni.edu")
                    .major("Mathematics").gpa(3.2).enrollmentYear(2021).build(),
                Student.builder().firstName("Carol").lastName("Davis").email("carol@uni.edu")
                    .major("Computer Science").gpa(3.9).enrollmentYear(2023).build(),
                Student.builder().firstName("David").lastName("Wilson").email("david@uni.edu")
                    .major("Physics").gpa(2.9).enrollmentYear(2020).build(),
                Student.builder().firstName("Eve").lastName("Martinez").email("eve@uni.edu")
                    .major("Computer Science").gpa(2.4).enrollmentYear(2022).build(),
                Student.builder().firstName("Frank").lastName("Brown").email("frank@uni.edu")
                    .major("Mathematics").gpa(3.6).enrollmentYear(2023).build(),
                Student.builder().firstName("Grace").lastName("Lee").email("grace@uni.edu")
                    .major("Physics").gpa(3.7).enrollmentYear(2021).build(),
                Student.builder().firstName("Henry").lastName("Taylor").email("henry@uni.edu")
                    .major("Mathematics").gpa(2.2).enrollmentYear(2020).build()
            ));
            log.info("Seeded {} students", repo.count());
            log.info("H2 Console → http://localhost:8080/h2-console  (JDBC URL: jdbc:h2:mem:academydb)");
        };
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  ENTITY
// ═══════════════════════════════════════════════════════════════════════════

/**
 * @Entity   — marks this class as a JPA entity (mapped to a DB table)
 * @Table    — customises the table name (default = class name)
 * @Id       — marks the primary key field
 * @GeneratedValue(IDENTITY) — auto-increment PK (DB handles generation)
 * @Column   — customises column properties (nullable, unique, name, length)
 * @Builder.Default — required to set field defaults when using Lombok @Builder
 */
@Entity
@Table(name = "students")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String major;

    private Double gpa;

    private Integer enrollmentYear;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}

// ═══════════════════════════════════════════════════════════════════════════
//  REPOSITORY
// ═══════════════════════════════════════════════════════════════════════════

/**
 * JpaRepository<Student, Long> provides out-of-the-box:
 *   save(S), saveAll(Iterable), findById(ID), findAll(), findAllById(Iterable),
 *   existsById(ID), count(), deleteById(ID), deleteAll(),
 *   flush(), saveAndFlush(S), findAll(Sort), findAll(Pageable) + more
 *
 * Spring Data generates the implementation at runtime — no @Impl class needed!
 */
interface StudentRepository extends JpaRepository<Student, Long> {

    // ── Derived Query Methods ────────────────────────────────────────────────
    // Spring reads the method name and generates the JPQL automatically.
    // Rules: findBy / countBy / existsBy / deleteBy + field names + keywords

    /** SELECT s FROM Student s WHERE s.major = ?1 */
    List<Student> findByMajor(String major);

    /** SELECT s FROM Student s WHERE s.gpa >= ?1 */
    List<Student> findByGpaGreaterThanEqual(double minGpa);

    /** WHERE major = ? AND active = true */
    List<Student> findByMajorAndActiveTrue(String major);

    /** WHERE LOWER(firstName) LIKE LOWER('%?%') */
    List<Student> findByFirstNameContainingIgnoreCase(String name);

    /** Paginated version of findByMajor — supports page/size/sort */
    Page<Student> findByMajor(String major, Pageable pageable);

    /** Combined filter with pagination */
    Page<Student> findByMajorAndGpaGreaterThanEqual(String major, double minGpa, Pageable pageable);

    // ── JPQL Queries (@Query) ────────────────────────────────────────────────
    // Uses entity class names and field names (NOT table/column names).
    // :minGpa is a named parameter bound with @Param.

    @Query("SELECT s FROM Student s WHERE s.gpa > :minGpa ORDER BY s.gpa DESC")
    List<Student> findTopStudents(@Param("minGpa") double minGpa);

    @Query("SELECT AVG(s.gpa) FROM Student s WHERE s.major = :major")
    Double findAvgGpaByMajor(@Param("major") String major);

    // ── Native SQL Query ─────────────────────────────────────────────────────
    // nativeQuery = true → plain SQL using actual table/column names.
    // Useful for DB-specific features (window functions, LIMIT/OFFSET).

    @Query(value = "SELECT * FROM students WHERE major = :major LIMIT :lim", nativeQuery = true)
    List<Student> findTopByMajorNative(@Param("major") String major, @Param("lim") int limit);

    // ── Modifying Query ──────────────────────────────────────────────────────
    // @Modifying is REQUIRED for any @Query that modifies data (UPDATE / DELETE).
    // @Transactional is required alongside @Modifying (or on the calling service method).

    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.active = false WHERE s.gpa < :threshold")
    int deactivateLowGpa(@Param("threshold") double threshold);
}

// ═══════════════════════════════════════════════════════════════════════════
//  SERVICE
// ═══════════════════════════════════════════════════════════════════════════

@Service
@Transactional(readOnly = true)   // all methods read-only by default; override per method
@Slf4j
class StudentService {

    private final StudentRepository repo;
    StudentService(StudentRepository repo) { this.repo = repo; }

    /** Paginated list — optional major and minGpa filters */
    public Page<Student> findAll(String major, Double minGpa, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        if (major != null && minGpa != null) return repo.findByMajorAndGpaGreaterThanEqual(major, minGpa, pageable);
        if (major != null)                   return repo.findByMajor(major, pageable);
        return repo.findAll(pageable);
    }

    /** Optional wraps the result — caller decides what to do when absent */
    public Optional<Student> findById(Long id) {
        return repo.findById(id);
    }

    public List<Student> findTopStudents(double minGpa) {
        return repo.findTopStudents(minGpa);
    }

    public Double findAvgGpa(String major) {
        return repo.findAvgGpaByMajor(major);
    }

    @Transactional   // override class-level readOnly = true
    public Student save(Student student) {
        log.info("Saving student: {}", student.getEmail());
        return repo.save(student);
    }

    @Transactional
    public boolean delete(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public int deactivateLowGpa(double threshold) {
        int count = repo.deactivateLowGpa(threshold);
        log.info("Deactivated {} students with GPA < {}", count, threshold);
        return count;
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  CONTROLLER
// ═══════════════════════════════════════════════════════════════════════════

@RestController
@RequestMapping("/api/students")
@Slf4j
class StudentController {

    private final StudentService service;
    StudentController(StudentService service) { this.service = service; }

    /**
     * GET /api/students
     * GET /api/students?major=Computer+Science
     * GET /api/students?minGpa=3.5
     * GET /api/students?major=Mathematics&minGpa=3.0&page=0&size=5
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false) String major,
            @RequestParam(required = false) Double minGpa,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("GET /api/students major={} minGpa={} page={} size={}", major, minGpa, page, size);
        Page<Student> studentPage = service.findAll(major, minGpa, page, size);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total",      studentPage.getTotalElements());
        response.put("totalPages", studentPage.getTotalPages());
        response.put("page",       page);
        response.put("size",       size);
        response.put("data",       studentPage.getContent());
        return ResponseEntity.ok(response);
    }

    /** GET /api/students/top?minGpa=3.5 — JPQL query demo */
    @GetMapping("/top")
    public List<Student> getTop(@RequestParam(defaultValue = "3.5") double minGpa) {
        return service.findTopStudents(minGpa);
    }

    /** GET /api/students/avg-gpa?major=Mathematics — aggregate JPQL demo */
    @GetMapping("/avg-gpa")
    public ResponseEntity<Map<String, Object>> avgGpa(@RequestParam String major) {
        Double avg = service.findAvgGpa(major);
        if (avg == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("major", major, "averageGpa", avg));
    }

    /** GET /api/students/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<Student> getOne(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /** POST /api/students */
    @PostMapping
    public ResponseEntity<Student> create(@RequestBody Student student) {
        student.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(student));
    }

    /** DELETE /api/students/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.notFound().build();
    }

    /**
     * POST /api/students/deactivate?threshold=2.5
     * Demonstrates @Modifying @Transactional @Query
     */
    @PostMapping("/deactivate")
    public ResponseEntity<Map<String, Object>> deactivate(
            @RequestParam(defaultValue = "2.5") double threshold) {
        int count = service.deactivateLowGpa(threshold);
        return ResponseEntity.ok(Map.of(
            "deactivated", count,
            "threshold",   threshold,
            "query",       "UPDATE Student s SET s.active = false WHERE s.gpa < :threshold"
        ));
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  REFERENCE ENDPOINTS
// ═══════════════════════════════════════════════════════════════════════════

@RestController
@RequestMapping("/api")
class JpaReferenceController {

    @GetMapping("/jpa-reference")
    public Map<String, Object> jpaReference() {
        return Map.of(
            "repositoryHierarchy", Map.of(
                "Repository<T,ID>",                  "Base marker interface — no methods",
                "CrudRepository<T,ID>",              "save, findById, findAll, delete, count, existsById",
                "PagingAndSortingRepository<T,ID>",  "findAll(Pageable), findAll(Sort)",
                "JpaRepository<T,ID>",               "Adds flush, saveAndFlush, findAllById, deleteAllInBatch, etc."
            ),
            "derivedQueryMethods", Map.of(
                "findBy<Field>",                  "Equality: findByMajor(String major)",
                "findBy<Field>GreaterThanEqual",  ">=: findByGpaGreaterThanEqual(double gpa)",
                "findBy<Field>Containing",        "LIKE %x%: findByFirstNameContainingIgnoreCase(String x)",
                "findBy<A>And<B>",                "AND: findByMajorAndActiveTrue(String major)",
                "findBy<A>Or<B>",                 "OR condition",
                "countBy<Field>",                 "Returns long count instead of list",
                "existsBy<Field>",                "Returns boolean",
                "deleteBy<Field>",                "Deletes matching records and returns count"
            ),
            "queryAnnotations", Map.of(
                "@Query(JPQL)",       "Uses entity class names and field names: SELECT s FROM Student s WHERE s.gpa > :min",
                "@Query(native SQL)", "Raw SQL against actual table/column names: SELECT * FROM students WHERE ...",
                "@Param(\"name\")",  "Binds named parameter in @Query expressions",
                "@Modifying",         "REQUIRED for @Query that performs UPDATE or DELETE",
                "@Transactional",     "REQUIRED alongside @Modifying; provides the active transaction"
            ),
            "pagination", Map.of(
                "Pageable",         "Interface carrying page number, page size, and Sort",
                "PageRequest.of()", "Factory: PageRequest.of(0, 10, Sort.by(\"lastName\").ascending())",
                "Page<T>",          "Holds content list + totalElements + totalPages + hasNext() etc.",
                "Slice<T>",         "Like Page but skips the total-count query — more efficient for large datasets"
            ),
            "entityAnnotations", Map.of(
                "@Entity",                      "Marks the class as a JPA-managed entity",
                "@Table(name=\"students\")",     "Maps to a specific DB table name",
                "@Id",                          "Marks the primary key field",
                "@GeneratedValue(IDENTITY)",    "Auto-increment PK — database handles generation",
                "@Column(nullable, unique)",    "Applies NOT NULL and UNIQUE constraints",
                "@Transient",                   "Field is NOT persisted to the database"
            )
        );
    }

    @GetMapping("/h2-info")
    public Map<String, Object> h2Info() {
        return Map.of(
            "consoleUrl",    "http://localhost:8080/h2-console",
            "jdbcUrl",       "jdbc:h2:mem:academydb",
            "username",      "sa",
            "password",      "(leave blank)",
            "note",          "H2 is an in-memory database — data resets every time the application restarts",
            "tip",           "Open H2 Console in a browser, enter the JDBC URL, click Connect, then run SQL queries",
            "usefulQueries", List.of(
                "SELECT * FROM STUDENTS;",
                "SELECT MAJOR, COUNT(*), AVG(GPA) FROM STUDENTS GROUP BY MAJOR;",
                "SELECT * FROM STUDENTS WHERE GPA >= 3.5 ORDER BY GPA DESC;",
                "SELECT FIRST_NAME, LAST_NAME, GPA FROM STUDENTS WHERE ACTIVE = TRUE ORDER BY GPA DESC;"
            )
        );
    }
}
