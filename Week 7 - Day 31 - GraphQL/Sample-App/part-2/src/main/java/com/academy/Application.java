package com.academy;

import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Day 31 — Part 2: Advanced GraphQL
 * ====================================
 * Run: mvn spring-boot:run
 * GraphiQL: http://localhost:8080/graphiql
 *
 * Topics:
 *   - Cursor-based pagination (Relay spec)
 *   - Offset pagination
 *   - Error types in mutations (StudentResult)
 *   - Aliases in queries
 *   - Fragments
 *   - @deprecated directive
 *   - Custom scalars (DateTime)
 *   - Introspection queries
 *
 * Try in GraphiQL:
 *
 * # Cursor pagination
 * query {
 *   studentsConnection(first: 2) {
 *     edges { cursor node { name gpa } }
 *     pageInfo { hasNextPage endCursor totalCount }
 *   }
 * }
 *
 * # Aliases (two queries in one)
 * query {
 *   csStudents: studentsByMajor(major: "CS") { name gpa }
 *   mathStudents: studentsByMajor(major: "Math") { name gpa }
 * }
 *
 * # Mutation with result type
 * mutation {
 *   createStudent(input: {name:"X" email:"x@e.com" major:"CS" gpa:3.5}) {
 *     success message
 *     student { id name }
 *     errors { field message }
 *   }
 * }
 *
 * # Introspection
 * query { __schema { types { name } } }
 * query { __type(name: "Student") { fields { name type { name } } } }
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║   Day 31 — Part 2: Advanced GraphQL                 ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  GraphiQL: http://localhost:8080/graphiql            ║");
        System.out.println("║  Reference: GET /api/graphql-advanced                ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");
    }

    @Bean
    CommandLineRunner seed(DataService svc) { return args -> svc.seedData(); }
}

// ── Domain ────────────────────────────────────────────────────────────────────

@Data @AllArgsConstructor @NoArgsConstructor
class Student {
    private Long id;
    private String name;
    private String email;
    private String major;
    private Double gpa;
    private Instant enrolledAt;
    private List<Long> courseIds = new ArrayList<>();
}

@Data @AllArgsConstructor @NoArgsConstructor
class Course {
    private Long id;
    private String title;
    private String instructor;
    private Integer credits;
}

// ── Pagination Types ──────────────────────────────────────────────────────────

record StudentEdge(String cursor, Student node) {}
record PageInfo(boolean hasNextPage, boolean hasPreviousPage,
                String startCursor, String endCursor, int totalCount) {}
record StudentConnection(List<StudentEdge> edges, PageInfo pageInfo) {}

// ── Mutation Result Types ─────────────────────────────────────────────────────

record FieldError(String field, String message) {}
record StudentResult(boolean success, String message, Student student, List<FieldError> errors) {}

// ── Input ─────────────────────────────────────────────────────────────────────

record CreateStudentInput(String name, String email, String major, Double gpa) {}

// ── Service ──────────────────────────────────────────────────────────────────

@Service
class DataService {
    final Map<Long, Student> students = new ConcurrentHashMap<>();
    final Map<Long, Course>  courses  = new ConcurrentHashMap<>();
    private final AtomicLong studentId = new AtomicLong(1);
    private final AtomicLong courseId  = new AtomicLong(1);

    public void seedData() {
        courses.put(1L, new Course(courseId.getAndIncrement(), "Java",   "Prof. Smith", 3));
        courses.put(2L, new Course(courseId.getAndIncrement(), "Spring", "Prof. Smith", 3));
        courses.put(3L, new Course(courseId.getAndIncrement(), "Algorithms", "Prof. Jones", 4));

        String[] names   = {"Alice Johnson","Bob Martinez","Carol White","David Kim","Emma Rodriguez","Frank Lee","Grace Chen","Henry Davis"};
        String[] emails  = {"alice","bob","carol","david","emma","frank","grace","henry"};
        String[] majors  = {"CS","Math","CS","CS","Math","CS","Math","CS"};
        double[] gpas    = {3.8,3.2,3.9,2.8,3.5,3.1,3.7,2.9};
        for (int i = 0; i < names.length; i++) {
            Long id = studentId.getAndIncrement();
            students.put(id, new Student(id, names[i], emails[i]+"@uni.edu", majors[i], gpas[i],
                Instant.now().minusSeconds((long)(i * 86400)), new ArrayList<>()));
        }
        System.out.println("✓ Seeded " + students.size() + " students");
    }

    public List<Student> getStudents(Integer limit, Integer offset) {
        List<Student> all = new ArrayList<>(students.values());
        all.sort(Comparator.comparing(Student::getId));
        int from = offset != null ? offset : 0;
        int to   = limit  != null ? Math.min(from + limit, all.size()) : all.size();
        return all.subList(Math.min(from, all.size()), to);
    }

    public StudentConnection getStudentsConnection(Integer first, String after) {
        List<Student> all = new ArrayList<>(students.values());
        all.sort(Comparator.comparing(Student::getId));
        int totalCount = all.size();
        int pageSize   = first != null ? first : 5;

        // Decode cursor (base64 of index)
        int startIndex = 0;
        if (after != null) {
            try {
                startIndex = Integer.parseInt(new String(Base64.getDecoder().decode(after))) + 1;
            } catch (Exception ignored) {}
        }

        List<Student> page = all.subList(Math.min(startIndex, all.size()),
            Math.min(startIndex + pageSize, all.size()));

        List<StudentEdge> edges = new ArrayList<>();
        for (int i = 0; i < page.size(); i++) {
            Student s = page.get(i);
            String cursor = Base64.getEncoder().encodeToString(String.valueOf(startIndex + i).getBytes());
            edges.add(new StudentEdge(cursor, s));
        }

        String startCursor = edges.isEmpty() ? null : edges.get(0).cursor();
        String endCursor   = edges.isEmpty() ? null : edges.get(edges.size()-1).cursor();
        boolean hasNext    = (startIndex + pageSize) < totalCount;
        boolean hasPrev    = startIndex > 0;

        return new StudentConnection(edges, new PageInfo(hasNext, hasPrev, startCursor, endCursor, totalCount));
    }
}

// ── GraphQL Controller ────────────────────────────────────────────────────────

@Controller
class AdvancedGraphQLController {

    private final DataService svc;
    public AdvancedGraphQLController(DataService svc) { this.svc = svc; }

    @QueryMapping
    public List<Student> students(@Argument Integer limit, @Argument Integer offset) {
        return svc.getStudents(limit, offset);
    }

    @QueryMapping
    public StudentConnection studentsConnection(@Argument Integer first, @Argument String after) {
        return svc.getStudentsConnection(first, after);
    }

    @QueryMapping
    public Student student(@Argument String id) {
        return svc.students.get(Long.parseLong(id));
    }

    @QueryMapping
    public List<Student> studentsByMajor(@Argument String major) {
        return svc.students.values().stream()
            .filter(s -> major.equalsIgnoreCase(s.getMajor())).toList();
    }

    @QueryMapping
    public List<Course> courses() { return new ArrayList<>(svc.courses.values()); }

    @SchemaMapping(typeName = "Student", field = "courses")
    public List<Course> studentCourses(Student student) {
        return student.getCourseIds().stream()
            .map(id -> svc.courses.get(id)).filter(Objects::nonNull).toList();
    }

    @MutationMapping
    public StudentResult createStudent(@Argument CreateStudentInput input) {
        List<FieldError> errors = new ArrayList<>();
        if (input.name() == null || input.name().isBlank())
            errors.add(new FieldError("name", "Name is required"));
        if (input.email() == null || !input.email().contains("@"))
            errors.add(new FieldError("email", "Valid email required"));
        if (input.gpa() != null && (input.gpa() < 0 || input.gpa() > 4.0))
            errors.add(new FieldError("gpa", "GPA must be between 0.0 and 4.0"));

        if (!errors.isEmpty())
            return new StudentResult(false, "Validation failed", null, errors);

        boolean duplicate = svc.students.values().stream()
            .anyMatch(s -> s.getEmail().equalsIgnoreCase(input.email()));
        if (duplicate)
            return new StudentResult(false, "Email already registered", null,
                List.of(new FieldError("email", "Email already in use")));

        Long id = svc.studentId.getAndIncrement();
        Student s = new Student(id, input.name(), input.email(), input.major(), input.gpa(),
            Instant.now(), new ArrayList<>());
        svc.students.put(id, s);
        return new StudentResult(true, "Student created", s, List.of());
    }

    @MutationMapping
    public StudentResult deleteStudent(@Argument String id) {
        Long sid = Long.parseLong(id);
        Student removed = svc.students.remove(sid);
        if (removed == null)
            return new StudentResult(false, "Student not found: " + id, null, List.of());
        return new StudentResult(true, "Student deleted", removed, List.of());
    }
}

// ── REST Reference ────────────────────────────────────────────────────────────

@RestController @RequestMapping("/api")
class AdvancedReferenceController {
    @GetMapping("/graphql-advanced")
    public Map<String, Object> reference() {
        Map<String, Object> ref = new LinkedHashMap<>();
        ref.put("title", "Advanced GraphQL — Day 31 Part 2");

        Map<String, String> pagination = new LinkedHashMap<>();
        pagination.put("Offset_pagination", "students(limit:5, offset:10) — simple, but inconsistent if data changes");
        pagination.put("Cursor_pagination", "studentsConnection(first:5, after:\"cursor\") — stable, Relay spec");
        pagination.put("PageInfo_fields", "hasNextPage, hasPreviousPage, startCursor, endCursor, totalCount");
        pagination.put("Cursor", "Opaque string (usually Base64 of position/id) — clients pass as after/before");
        ref.put("pagination", pagination);

        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("Approach_1_exceptions", "Throw exception → GraphQL error in 'errors' array (partial success possible)");
        errors.put("Approach_2_result_type", "Return result type with success/errors fields — explicit, type-safe, preferred");
        errors.put("GraphQL_errors_array", "GraphQL spec: errors and data can coexist in same response");
        ref.put("errorHandling", errors);

        Map<String, String> advanced = new LinkedHashMap<>();
        advanced.put("Aliases", "csStudents: studentsByMajor(major:\"CS\") { ... } — rename result field");
        advanced.put("Fragments", "fragment StudentFields on Student { id name gpa } — reuse field sets");
        advanced.put("Variables", "query Get($id: ID!) { student(id: $id) { name } } — typed query params");
        advanced.put("Directives", "@include(if: $showEmail) @skip(if: $hide) — conditional fields");
        advanced.put("Introspection", "__schema, __type(name:\"Student\") — query the schema itself");
        advanced.put("@deprecated", "@deprecated(reason: \"Use newField instead\") — mark fields as deprecated");
        ref.put("advancedFeatures", advanced);

        return ref;
    }
}
