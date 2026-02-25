package com.academy;

import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Day 31 — Part 1: GraphQL Server with Spring Boot
 * ==================================================
 * Run: mvn spring-boot:run
 *
 * GraphiQL Playground: http://localhost:8080/graphiql
 * GraphQL Endpoint:    POST http://localhost:8080/graphql
 *
 * Try these queries in GraphiQL:
 *
 * # 1. Get all students
 * query {
 *   students {
 *     id name major gpa
 *   }
 * }
 *
 * # 2. Get student with nested courses (N+1 prevention with @SchemaMapping)
 * query {
 *   student(id: "1") {
 *     name gpa
 *     courses { title instructor }
 *   }
 * }
 *
 * # 3. Filtered query
 * query {
 *   topStudents(minGpa: 3.5) {
 *     name major gpa
 *   }
 * }
 *
 * # 4. Create a student (mutation)
 * mutation {
 *   createStudent(input: {
 *     name: "Diana Prince"
 *     email: "diana@uni.edu"
 *     major: "CS"
 *     gpa: 3.9
 *   }) {
 *     id name email
 *   }
 * }
 *
 * Topics:
 *   @QueryMapping, @MutationMapping, @SchemaMapping,
 *   @Argument, GraphQL types vs REST, SDL schema
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("\n╔═════════════════════════════════════════════════════╗");
        System.out.println("║   Day 31 — Part 1: GraphQL Server                  ║");
        System.out.println("╠═════════════════════════════════════════════════════╣");
        System.out.println("║  GraphiQL UI:  http://localhost:8080/graphiql       ║");
        System.out.println("║  Endpoint:     POST /graphql                        ║");
        System.out.println("║  Reference:    GET  /api/graphql-reference          ║");
        System.out.println("╚═════════════════════════════════════════════════════╝\n");
    }

    @Bean
    CommandLineRunner seed(AcademyService svc) {
        return args -> svc.seedData();
    }
}

// ── Domain Models ─────────────────────────────────────────────────────────────

@Data @AllArgsConstructor @NoArgsConstructor
class Student {
    private Long id;
    private String name;
    private String email;
    private String major;
    private Double gpa;
    private List<Long> courseIds = new ArrayList<>();
}

@Data @AllArgsConstructor @NoArgsConstructor
class Course {
    private Long id;
    private String title;
    private String instructor;
    private Integer credits;
    private Integer maxStudents;
    private List<Long> studentIds = new ArrayList<>();

    public int getEnrolledCount() { return studentIds.size(); }
}

@Data @AllArgsConstructor @NoArgsConstructor
class Department {
    private Long id;
    private String name;
    private List<Long> courseIds = new ArrayList<>();
}

// ── Input Records ─────────────────────────────────────────────────────────────

record CreateStudentInput(String name, String email, String major, Double gpa) {}
record UpdateStudentInput(String name, String email, String major, Double gpa) {}
record CreateCourseInput(String title, String instructor, Integer credits, Integer maxStudents) {}

// ── Service ──────────────────────────────────────────────────────────────────

@Service
class AcademyService {
    final Map<Long, Student>    students    = new ConcurrentHashMap<>();
    final Map<Long, Course>     courses     = new ConcurrentHashMap<>();
    final Map<Long, Department> departments = new ConcurrentHashMap<>();

    private final AtomicLong studentIdGen = new AtomicLong(1);
    private final AtomicLong courseIdGen  = new AtomicLong(1);
    private final AtomicLong deptIdGen    = new AtomicLong(1);

    public void seedData() {
        // Departments
        Department cs   = new Department(deptIdGen.getAndIncrement(), "Computer Science", new ArrayList<>());
        Department math = new Department(deptIdGen.getAndIncrement(), "Mathematics", new ArrayList<>());
        departments.put(cs.getId(), cs);
        departments.put(math.getId(), math);

        // Courses
        Course java   = new Course(courseIdGen.getAndIncrement(), "Java Fundamentals", "Prof. Smith",  3, 30, new ArrayList<>());
        Course spring = new Course(courseIdGen.getAndIncrement(), "Spring Boot",       "Prof. Smith",  3, 25, new ArrayList<>());
        Course algo   = new Course(courseIdGen.getAndIncrement(), "Algorithms",        "Prof. Jones",  4, 20, new ArrayList<>());
        Course calc   = new Course(courseIdGen.getAndIncrement(), "Calculus",          "Prof. Davis",  4, 35, new ArrayList<>());
        courses.put(java.getId(), java);  courses.put(spring.getId(), spring);
        courses.put(algo.getId(), algo);  courses.put(calc.getId(), calc);
        cs.getCourseIds().addAll(List.of(java.getId(), spring.getId(), algo.getId()));
        math.getCourseIds().add(calc.getId());

        // Students
        createAndEnroll("Alice Johnson",  "alice@uni.edu",  "CS",   3.8, List.of(java.getId(), spring.getId()));
        createAndEnroll("Bob Martinez",   "bob@uni.edu",    "Math", 3.2, List.of(calc.getId()));
        createAndEnroll("Carol White",    "carol@uni.edu",  "CS",   3.9, List.of(java.getId(), algo.getId()));
        createAndEnroll("David Kim",      "david@uni.edu",  "CS",   2.8, List.of(java.getId()));
        createAndEnroll("Emma Rodriguez","emma@uni.edu",    "Math", 3.5, List.of(calc.getId(), algo.getId()));
        System.out.println("✓ Seeded GraphQL data");
    }

    private void createAndEnroll(String name, String email, String major, double gpa, List<Long> cIds) {
        Long id = studentIdGen.getAndIncrement();
        Student s = new Student(id, name, email, major, gpa, new ArrayList<>(cIds));
        students.put(id, s);
        cIds.forEach(cId -> { Course c = courses.get(cId); if (c != null) c.getStudentIds().add(id); });
    }

    public List<Student> getAllStudents()                  { return new ArrayList<>(students.values()); }
    public Optional<Student> getStudentById(Long id)      { return Optional.ofNullable(students.get(id)); }
    public List<Student> getStudentsByMajor(String major) { return students.values().stream().filter(s -> major.equalsIgnoreCase(s.getMajor())).toList(); }
    public List<Student> getTopStudents(double minGpa)    { return students.values().stream().filter(s -> s.getGpa() >= minGpa).toList(); }

    public List<Course> getAllCourses()               { return new ArrayList<>(courses.values()); }
    public Optional<Course> getCourseById(Long id)   { return Optional.ofNullable(courses.get(id)); }
    public List<Department> getAllDepartments()       { return new ArrayList<>(departments.values()); }

    public Student createStudent(CreateStudentInput input) {
        Long id = studentIdGen.getAndIncrement();
        Student s = new Student(id, input.name(), input.email(), input.major(), input.gpa(), new ArrayList<>());
        students.put(id, s);
        return s;
    }

    public Optional<Student> updateStudent(Long id, UpdateStudentInput input) {
        Student s = students.get(id);
        if (s == null) return Optional.empty();
        if (input.name()  != null) s.setName(input.name());
        if (input.email() != null) s.setEmail(input.email());
        if (input.major() != null) s.setMajor(input.major());
        if (input.gpa()   != null) s.setGpa(input.gpa());
        return Optional.of(s);
    }

    public boolean deleteStudent(Long id) {
        if (!students.containsKey(id)) return false;
        students.remove(id);
        return true;
    }

    public Course createCourse(CreateCourseInput input) {
        Long id = courseIdGen.getAndIncrement();
        Course c = new Course(id, input.title(), input.instructor(), input.credits(), input.maxStudents(), new ArrayList<>());
        courses.put(id, c);
        return c;
    }

    public boolean enrollStudent(Long studentId, Long courseId) {
        Student s = students.get(studentId);
        Course  c = courses.get(courseId);
        if (s == null || c == null) return false;
        if (!s.getCourseIds().contains(courseId)) s.getCourseIds().add(courseId);
        if (!c.getStudentIds().contains(studentId)) c.getStudentIds().add(studentId);
        return true;
    }
}

// ── GraphQL Controller (Resolver) ─────────────────────────────────────────────

@Controller
class AcademyGraphQLController {

    private final AcademyService svc;
    public AcademyGraphQLController(AcademyService svc) { this.svc = svc; }

    // ── Queries ──────────────────────────────────────────────────────

    /** @QueryMapping = @SchemaMapping(typeName="Query", field="students") */
    @QueryMapping
    public List<Student> students() { return svc.getAllStudents(); }

    @QueryMapping
    public Student student(@Argument String id) {
        return svc.getStudentById(Long.parseLong(id)).orElse(null);
    }

    @QueryMapping
    public List<Student> studentsByMajor(@Argument String major) {
        return svc.getStudentsByMajor(major);
    }

    @QueryMapping
    public List<Student> topStudents(@Argument double minGpa) {
        return svc.getTopStudents(minGpa);
    }

    @QueryMapping
    public List<Course> courses() { return svc.getAllCourses(); }

    @QueryMapping
    public Course course(@Argument String id) {
        return svc.getCourseById(Long.parseLong(id)).orElse(null);
    }

    @QueryMapping
    public List<Department> departments() { return svc.getAllDepartments(); }

    // ── Nested Field Resolvers (@SchemaMapping) ──────────────────────

    /**
     * @SchemaMapping resolves a field on a GraphQL type.
     * Called for EACH Student when "courses" field is requested.
     * Spring BatchMapping can be used to prevent N+1 queries.
     */
    @SchemaMapping(typeName = "Student", field = "courses")
    public List<Course> studentCourses(Student student) {
        return student.getCourseIds().stream()
            .map(id -> svc.courses.get(id))
            .filter(Objects::nonNull)
            .toList();
    }

    @SchemaMapping(typeName = "Course", field = "students")
    public List<Student> courseStudents(Course course) {
        return course.getStudentIds().stream()
            .map(id -> svc.students.get(id))
            .filter(Objects::nonNull)
            .toList();
    }

    @SchemaMapping(typeName = "Department", field = "courses")
    public List<Course> departmentCourses(Department dept) {
        return dept.getCourseIds().stream()
            .map(id -> svc.courses.get(id))
            .filter(Objects::nonNull)
            .toList();
    }

    // ── Mutations ─────────────────────────────────────────────────────

    @MutationMapping
    public Student createStudent(@Argument CreateStudentInput input) {
        return svc.createStudent(input);
    }

    @MutationMapping
    public Student updateStudent(@Argument String id, @Argument UpdateStudentInput input) {
        return svc.updateStudent(Long.parseLong(id), input).orElse(null);
    }

    @MutationMapping
    public boolean deleteStudent(@Argument String id) {
        return svc.deleteStudent(Long.parseLong(id));
    }

    @MutationMapping
    public Course createCourse(@Argument CreateCourseInput input) {
        return svc.createCourse(input);
    }

    @MutationMapping
    public boolean enrollStudent(@Argument String studentId, @Argument String courseId) {
        return svc.enrollStudent(Long.parseLong(studentId), Long.parseLong(courseId));
    }
}

// ── REST Reference Endpoint ───────────────────────────────────────────────────

@RestController
@RequestMapping("/api")
class GraphQLReferenceController {

    @GetMapping("/graphql-reference")
    public Map<String, Object> reference() {
        Map<String, Object> ref = new LinkedHashMap<>();
        ref.put("title", "GraphQL Reference — Day 31");
        ref.put("graphiql", "http://localhost:8080/graphiql");

        Map<String, String> vs = new LinkedHashMap<>();
        vs.put("REST_multiple_endpoints", "GET /students, GET /courses, GET /students/1/courses");
        vs.put("GraphQL_single_endpoint", "POST /graphql — all operations through one endpoint");
        vs.put("REST_over_under_fetch", "Response shape fixed — may get too much or too little data");
        vs.put("GraphQL_precise", "Client specifies EXACTLY what fields it needs");
        vs.put("REST_versioning", "v1/v2 in URL");
        vs.put("GraphQL_evolution", "Add fields without breaking clients; deprecate old fields");
        ref.put("graphqlVsRest", vs);

        Map<String, String> concepts = new LinkedHashMap<>();
        concepts.put("Schema", "SDL file (.graphqls) — defines types and operations — contract between server and client");
        concepts.put("Query", "Read operation — like HTTP GET");
        concepts.put("Mutation", "Write operation — like HTTP POST/PUT/DELETE");
        concepts.put("Subscription", "Real-time stream — like WebSocket");
        concepts.put("Resolver", "@QueryMapping / @MutationMapping / @SchemaMapping — Java method that fetches data for a field");
        concepts.put("@Argument", "Bind GraphQL argument to Java method parameter");
        concepts.put("@QueryMapping", "Maps method to Query.fieldName in schema");
        concepts.put("@MutationMapping", "Maps method to Mutation.fieldName in schema");
        concepts.put("@SchemaMapping", "Maps method to any field on any type — for nested resolvers");
        concepts.put("N+1 Problem", "Fetching courses for 100 students = 101 queries. Use @BatchMapping to batch.");
        concepts.put("input type", "Separate type for mutation arguments — cannot be used in queries");
        concepts.put("Non-null (!)", "field: String! — guaranteed non-null. field: String — may be null.");
        ref.put("concepts", concepts);

        Map<String, String> queries = new LinkedHashMap<>();
        queries.put("Get all students", "query { students { id name gpa } }");
        queries.put("Nested query", "query { student(id: \"1\") { name courses { title } } }");
        queries.put("Filtered", "query { topStudents(minGpa: 3.5) { name gpa } }");
        queries.put("Create mutation", "mutation { createStudent(input: {name:\"X\" email:\"x@e.com\" major:\"CS\" gpa:3.5}) { id } }");
        queries.put("Enroll mutation", "mutation { enrollStudent(studentId: \"1\", courseId: \"2\") }");
        queries.put("Named query", "query GetStudents { students { id name } }");
        queries.put("Variables", "query Get($id: ID!) { student(id: $id) { name } }  — then pass variables: {\"id\": \"1\"}");
        ref.put("exampleQueries", queries);

        return ref;
    }
}
