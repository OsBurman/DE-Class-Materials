package com.academy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Day 27 — Part 2: Hibernate ORM & JPA Relationships
 * ====================================================
 * Topics covered:
 *   ✓ @OneToOne   — one entity relates to exactly one other entity
 *   ✓ @OneToMany  — one entity has a collection of related entities
 *   ✓ @ManyToOne  — many entities belong to one parent; owns the FK column
 *   ✓ @ManyToMany — many-to-many; requires a join table
 *   ✓ mappedBy    — designates the inverse (non-owning) side of a relationship
 *   ✓ @JoinColumn — customises the FK column name on the owning side
 *   ✓ @JoinTable  — customises the join table for @ManyToMany
 *   ✓ fetch=EAGER — related data loaded immediately with the parent
 *   ✓ fetch=LAZY  — related data loaded on first access (default for collections)
 *   ✓ N+1 problem — explained in the reference endpoint
 *   ✓ cascade types — ALL, PERSIST, MERGE, REMOVE, REFRESH, DETACH
 *   ✓ Enrollment entity — @ManyToMany with extra columns (grade, semester)
 *   ✓ @JsonIgnoreProperties / @JsonIgnore — prevent circular JSON serialisation
 *   ✓ JPQL examples — JOIN FETCH, aggregation, named params
 *
 * Schema:
 *   departments (1) ──── (many) courses
 *   students    (many) ── (many) courses  via enrollments table
 *                                          (with extra columns: grade, semester)
 *
 * Run: mvn spring-boot:run
 * H2 Console: http://localhost:8080/h2-console  (JDBC URL: jdbc:h2:mem:academydb)
 *
 * Endpoints:
 *   GET  http://localhost:8080/api/departments
 *   GET  http://localhost:8080/api/courses
 *   GET  http://localhost:8080/api/students
 *   GET  http://localhost:8080/api/students/{id}/courses
 *   POST http://localhost:8080/api/students/{studentId}/enroll/{courseId}
 *   GET  http://localhost:8080/api/enrollments
 *   GET  http://localhost:8080/api/relationships-reference
 */
@SpringBootApplication
@Slf4j
public class Application {
    public static void main(String[] args) { SpringApplication.run(Application.class, args); }

    @Bean
    CommandLineRunner seedData(
            DepartmentRepository deptRepo,
            CourseRepository     courseRepo,
            StudentRepository    studentRepo,
            EnrollmentRepository enrollRepo) {
        return args -> {
            // ── 2 Departments ──────────────────────────────────────────────
            Department cs   = deptRepo.save(Department.builder().name("Computer Science").building("Tech Hall").build());
            Department math = deptRepo.save(Department.builder().name("Mathematics").building("Science Block").build());

            // ── 4 Courses (ManyToOne → Department) ────────────────────────
            Course cs101 = courseRepo.save(Course.builder().code("CS101").title("Intro to Computer Science").credits(3).department(cs).build());
            Course cs201 = courseRepo.save(Course.builder().code("CS201").title("Data Structures").credits(4).department(cs).build());
            Course ma101 = courseRepo.save(Course.builder().code("MA101").title("Calculus I").credits(4).department(math).build());
            Course ma201 = courseRepo.save(Course.builder().code("MA201").title("Linear Algebra").credits(3).department(math).build());

            // ── 5 Students ────────────────────────────────────────────────
            Student alice = studentRepo.save(Student.builder().name("Alice Johnson").email("alice@uni.edu").gpa(3.8).build());
            Student bob   = studentRepo.save(Student.builder().name("Bob Smith").email("bob@uni.edu").gpa(3.2).build());
            Student carol = studentRepo.save(Student.builder().name("Carol Davis").email("carol@uni.edu").gpa(3.9).build());
            Student dave  = studentRepo.save(Student.builder().name("Dave Wilson").email("dave@uni.edu").gpa(2.9).build());
            Student eve   = studentRepo.save(Student.builder().name("Eve Martinez").email("eve@uni.edu").gpa(3.5).build());

            // ── Enrollments (ManyToMany with extra data) ──────────────────
            enrollRepo.saveAll(List.of(
                Enrollment.builder().student(alice).course(cs101).grade("A").semester("Fall 2024").build(),
                Enrollment.builder().student(alice).course(ma101).grade("B+").semester("Fall 2024").build(),
                Enrollment.builder().student(bob).course(cs101).grade("B").semester("Fall 2024").build(),
                Enrollment.builder().student(bob).course(cs201).grade("A-").semester("Spring 2025").build(),
                Enrollment.builder().student(carol).course(cs201).grade("A+").semester("Spring 2025").build(),
                Enrollment.builder().student(carol).course(ma201).grade("A").semester("Spring 2025").build(),
                Enrollment.builder().student(dave).course(ma101).grade("C+").semester("Fall 2024").build(),
                Enrollment.builder().student(eve).course(cs101).grade("A-").semester("Fall 2024").build(),
                Enrollment.builder().student(eve).course(ma201).grade("B+").semester("Spring 2025").build()
            ));

            log.info("Seeded: {} depts, {} courses, {} students, {} enrollments",
                deptRepo.count(), courseRepo.count(), studentRepo.count(), enrollRepo.count());
            log.info("H2 Console → http://localhost:8080/h2-console  (JDBC URL: jdbc:h2:mem:academydb)");
        };
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  ENTITIES
// ═══════════════════════════════════════════════════════════════════════════

/**
 * Department — the "one" side of @OneToMany with Course.
 *
 * @OneToMany(mappedBy = "department")
 *   → mappedBy tells JPA that the 'department' field on Course owns the FK column.
 *   → Department is the INVERSE side; changes here are NOT automatically persisted.
 *   → Always set the owning side (course.setDepartment(dept)) to persist the link.
 *
 * cascade = ALL
 *   → Operations on Department propagate to its Courses (save, delete, etc.)
 *   → Deleting a Department will also delete all its Courses!
 *
 * fetch = LAZY (default for @OneToMany)
 *   → The courses list is NOT loaded from DB until it is accessed.
 *   → Spring Boot's Open Session In View (OSIV) keeps the session open during
 *     the HTTP request, so LAZY collections serialize without error by default.
 *   → In production, disable OSIV and use JOIN FETCH or @EntityGraph instead.
 *
 * @JsonIgnoreProperties("department") on the courses list
 *   → Prevents infinite recursion: Department → Course → Department → ...
 */
@Entity
@Table(name = "departments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "courses")
@ToString(exclude = "courses")
class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String building;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("department")
    @Builder.Default
    private List<Course> courses = new ArrayList<>();
}

/**
 * Course — the "many" side of @ManyToOne with Department (owns the FK),
 *           and participates in the @ManyToMany with Student via Enrollment.
 *
 * @ManyToOne(fetch = EAGER)
 *   → Department is ALWAYS loaded when a Course is fetched (default for @ManyToOne).
 *   → Generates a JOIN in the SQL: SELECT c.*, d.* FROM courses c JOIN departments d ...
 *
 * @JoinColumn(name = "department_id")
 *   → Specifies the FK column name in the courses table.
 *   → Course is the OWNING SIDE — its department field controls persistence.
 *
 * Alternative @ManyToMany approach (shown here as comment):
 *
 *   // On Course (inverse side — does NOT own the join table):
 *   @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
 *   private Set<Student> students = new HashSet<>();
 *
 *   // On Student (owning side — owns the join table):
 *   @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
 *   @JoinTable(
 *       name = "student_courses",
 *       joinColumns        = @JoinColumn(name = "student_id"),
 *       inverseJoinColumns = @JoinColumn(name = "course_id")
 *   )
 *   private Set<Course> courses = new HashSet<>();
 *
 * We use an explicit Enrollment entity instead (recommended when the join table
 * needs extra columns like grade and semester).
 */
@Entity
@Table(name = "courses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"department", "enrollments"})
@ToString(exclude = {"department", "enrollments"})
class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String title;

    private int credits;

    /**
     * OWNING SIDE of the Department ↔ Course relationship.
     * This entity owns the FK column 'department_id' in the courses table.
     * fetch = EAGER → Department loaded in the same query as Course.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    @JsonIgnoreProperties("courses")
    private Department department;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<Enrollment> enrollments = new ArrayList<>();
}

/**
 * Student — participates in the ManyToMany via the Enrollment entity.
 *
 * We use a separate Enrollment entity rather than a plain @ManyToMany
 * because enrollments carry extra data (grade, semester).
 *
 * When the join table needs ONLY the two FK columns, you can use:
 *   @ManyToMany + @JoinTable  (see comment block in Course above)
 * When the join table needs extra columns, use an explicit entity (this approach).
 */
@Entity
@Table(name = "students")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "enrollments")
@ToString(exclude = "enrollments")
class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private double gpa;

    /**
     * INVERSE SIDE: mappedBy = "student" → Enrollment.student owns the FK.
     * fetch = LAZY (default for @OneToMany) — enrollments loaded on demand.
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Enrollment> enrollments = new ArrayList<>();
}

/**
 * Enrollment — the explicit join entity for the Student ↔ Course many-to-many.
 *
 * Advantages over plain @ManyToMany:
 *   1. Can store extra columns (grade, semester, enrolledAt, etc.)
 *   2. Can be queried and managed independently
 *   3. Cascade behaviour is more explicit and predictable
 *
 * Both @ManyToOne fields use fetch = EAGER so a single JOIN fetches everything.
 * @JsonIgnoreProperties prevents circular references during serialisation.
 */
@Entity
@Table(name = "enrollments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** OWNING SIDE: has the FK column 'student_id' in enrollments table */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties("enrollments")
    private Student student;

    /** OWNING SIDE: has the FK column 'course_id' in enrollments table */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnoreProperties("enrollments")
    private Course course;

    private String grade;
    private String semester;
}

// ═══════════════════════════════════════════════════════════════════════════
//  REPOSITORIES
// ═══════════════════════════════════════════════════════════════════════════

interface DepartmentRepository extends JpaRepository<Department, Long> {}
interface CourseRepository      extends JpaRepository<Course, Long> {}
interface StudentRepository     extends JpaRepository<Student, Long> {}
interface EnrollmentRepository  extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);   // WHERE student_id = ?
    List<Enrollment> findByCourseId(Long courseId);     // WHERE course_id = ?
}

// ═══════════════════════════════════════════════════════════════════════════
//  SERVICE
// ═══════════════════════════════════════════════════════════════════════════

@Service
@Transactional(readOnly = true)
@Slf4j
class AcademyService {

    private final DepartmentRepository deptRepo;
    private final CourseRepository     courseRepo;
    private final StudentRepository    studentRepo;
    private final EnrollmentRepository enrollRepo;

    AcademyService(DepartmentRepository d, CourseRepository c,
                   StudentRepository s, EnrollmentRepository e) {
        this.deptRepo = d; this.courseRepo = c;
        this.studentRepo = s; this.enrollRepo = e;
    }

    public List<Department> findAllDepartments()       { return deptRepo.findAll(); }
    public List<Course>     findAllCourses()           { return courseRepo.findAll(); }
    public List<Student>    findAllStudents()          { return studentRepo.findAll(); }
    public List<Enrollment> findAllEnrollments()       { return enrollRepo.findAll(); }

    public List<Enrollment> findEnrollmentsByStudent(Long studentId) {
        return enrollRepo.findByStudentId(studentId);
    }

    @Transactional
    public Enrollment enroll(Long studentId, Long courseId) {
        Student student = studentRepo.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        Course course = courseRepo.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));
        Enrollment enrollment = Enrollment.builder()
            .student(student).course(course)
            .grade("Pending").semester("Current")
            .build();
        log.info("Enrolling student '{}' in course '{}'", student.getName(), course.getCode());
        return enrollRepo.save(enrollment);
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  CONTROLLERS
// ═══════════════════════════════════════════════════════════════════════════

@RestController
@RequestMapping("/api")
@Slf4j
class AcademyController {

    private final AcademyService service;
    AcademyController(AcademyService service) { this.service = service; }

    /**
     * GET /api/departments
     * Returns departments with their courses list.
     * Course.department is loaded EAGER so no extra queries.
     * Department.courses is LAZY — loaded here by OSIV or in-session access.
     */
    @GetMapping("/departments")
    public List<Department> getDepartments() {
        return service.findAllDepartments();
    }

    /** GET /api/courses — all courses (Department is EAGER — included automatically) */
    @GetMapping("/courses")
    public List<Course> getCourses() {
        return service.findAllCourses();
    }

    /** GET /api/students — all students */
    @GetMapping("/students")
    public List<Student> getStudents() {
        return service.findAllStudents();
    }

    /**
     * GET /api/students/{id}/courses
     * Shows courses a student is enrolled in via the Enrollment join entity.
     */
    @GetMapping("/students/{id}/courses")
    public ResponseEntity<Map<String, Object>> getStudentCourses(@PathVariable Long id) {
        List<Enrollment> enrollments = service.findEnrollmentsByStudent(id);
        if (enrollments.isEmpty()) return ResponseEntity.notFound().build();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("studentId",       id);
        result.put("enrollmentCount", enrollments.size());
        result.put("enrollments",     enrollments);
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/students/{studentId}/enroll/{courseId}
     * Creates a new Enrollment record linking the student to the course.
     */
    @PostMapping("/students/{studentId}/enroll/{courseId}")
    public ResponseEntity<?> enroll(
            @PathVariable Long studentId, @PathVariable Long courseId) {
        try {
            Enrollment e = service.enroll(studentId, courseId);
            return ResponseEntity.status(HttpStatus.CREATED).body(e);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
        }
    }

    /** GET /api/enrollments — all enrollment records */
    @GetMapping("/enrollments")
    public List<Enrollment> getAllEnrollments() {
        return service.findAllEnrollments();
    }

    // ── Reference ──────────────────────────────────────────────────────────

    @GetMapping("/relationships-reference")
    public Map<String, Object> reference() {
        return Map.of(
            "relationships", Map.of(
                "@OneToOne",   "Each entity instance relates to exactly one other. The owning side has the FK column.",
                "@OneToMany",  "One entity has a collection of related entities. Use mappedBy — the 'many' side owns the FK.",
                "@ManyToOne",  "Many entities point to one parent. This side owns the FK column. Use @JoinColumn to name it.",
                "@ManyToMany", "Many-to-many relationship requires a join table. Use @JoinTable on the owning side. " +
                               "If the join table needs extra columns, use an explicit entity (like Enrollment here)."
            ),
            "owningVsInverse", Map.of(
                "owning side",  "The entity with the FK column (or join table). Controls persistence of the relationship.",
                "inverse side", "Uses mappedBy = '<field on owning side>'. Changes here are NOT automatically persisted.",
                "rule",         "You MUST set the owning side to persist a relationship. Setting only the mappedBy side is silently ignored.",
                "example",      "course.setDepartment(dept) [owning] vs dept.getCourses().add(course) [inverse — not enough alone]"
            ),
            "fetchTypes", Map.of(
                "EAGER",       "Data is loaded immediately in the same query. Default for @ManyToOne and @OneToOne.",
                "LAZY",        "Data loaded via proxy on first access. Default for @OneToMany and @ManyToMany.",
                "N+1 Problem", "Fetching N entities lazily and accessing a LAZY collection triggers N extra SQL queries. " +
                               "Fix: use JOIN FETCH in JPQL: 'SELECT d FROM Department d JOIN FETCH d.courses'",
                "OSIV",        "Spring Boot's Open Session In View keeps the Hibernate session open for the entire HTTP request, " +
                               "allowing LAZY loading during JSON serialisation. Disable with spring.jpa.open-in-view=false in production."
            ),
            "cascadeTypes", Map.of(
                "ALL",     "Propagates PERSIST, MERGE, REMOVE, REFRESH, DETACH",
                "PERSIST", "Saving parent also saves new (transient) children",
                "MERGE",   "Merging parent also merges children",
                "REMOVE",  "Deleting parent also deletes children — use carefully!",
                "REFRESH", "Refreshing parent also refreshes children from DB",
                "DETACH",  "Detaching parent also detaches children from persistence context"
            ),
            "joinAnnotations", Map.of(
                "@JoinColumn(name=\"fk_col\")",                    "Specifies the FK column name in the owning entity's table",
                "@JoinTable(name, joinColumns, inverseJoinColumns)", "Specifies the join table details for @ManyToMany",
                "mappedBy = \"fieldOnOwner\"",                    "Marks the inverse side; value = field name on the owning entity"
            ),
            "jpqlExamples", Map.of(
                "basic select",     "SELECT d FROM Department d",
                "join (lazy load)", "SELECT e FROM Enrollment e JOIN e.student s WHERE s.gpa > 3.5",
                "JOIN FETCH",       "SELECT d FROM Department d JOIN FETCH d.courses  (loads LAZY collection eagerly in one query)",
                "named param",      "SELECT s FROM Student s WHERE s.gpa > :minGpa",
                "aggregation",      "SELECT d.name, AVG(s.gpa) FROM Department d JOIN d.courses c JOIN c.enrollments e JOIN e.student s GROUP BY d.name",
                "Criteria API",     "Programmatic query building: CriteriaBuilder cb = em.getCriteriaBuilder(); CriteriaQuery<Student> cq = ..."
            ),
            "schemaThisDemo", Map.of(
                "departments", "id, name, building",
                "courses",     "id, code, title, credits, department_id (FK → departments.id)",
                "students",    "id, name, email, gpa",
                "enrollments", "id, student_id (FK → students.id), course_id (FK → courses.id), grade, semester"
            )
        );
    }
}
