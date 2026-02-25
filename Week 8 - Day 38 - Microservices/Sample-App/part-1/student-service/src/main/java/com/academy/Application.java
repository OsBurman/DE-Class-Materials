package com.academy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

// ─── Student Entity ─────────────────────────────────────────────────────────────

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Student {
    private Long id;
    private String name;
    private String email;
    private String major;
    private double gpa;
    private List<Long> enrolledCourseIds;
}

// ─── StudentRepository ──────────────────────────────────────────────────────────

@Component
class StudentRepository {
    private final Map<Long, Student> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @PostConstruct
    public void seed() {
        save(Student.builder()
                .name("Alice Johnson").email("alice@academy.com")
                .major("Computer Science").gpa(3.9)
                .enrolledCourseIds(List.of(1L, 2L, 3L)).build());
        save(Student.builder()
                .name("Bob Martinez").email("bob@academy.com")
                .major("Data Engineering").gpa(3.7)
                .enrolledCourseIds(List.of(1L, 4L)).build());
        save(Student.builder()
                .name("Carol Williams").email("carol@academy.com")
                .major("Software Engineering").gpa(3.8)
                .enrolledCourseIds(List.of(2L, 3L, 5L)).build());
        save(Student.builder()
                .name("David Brown").email("david@academy.com")
                .major("Computer Science").gpa(3.5)
                .enrolledCourseIds(List.of(1L, 6L)).build());
        save(Student.builder()
                .name("Eva Davis").email("eva@academy.com")
                .major("Data Engineering").gpa(3.6)
                .enrolledCourseIds(List.of(3L, 4L, 5L)).build());
    }

    public List<Student> findAll() { return new ArrayList<>(store.values()); }

    public Optional<Student> findById(Long id) { return Optional.ofNullable(store.get(id)); }

    public Student save(Student s) {
        if (s.getId() == null) s.setId(idGen.getAndIncrement());
        store.put(s.getId(), s);
        return s;
    }

    public void deleteById(Long id) { store.remove(id); }
}

// ─── CourseServiceClient ────────────────────────────────────────────────────────

@Component
class CourseServiceClient {

    @Value("${course.service.url}")
    private String courseServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    @SuppressWarnings("unchecked")
    public Map<String, Object> getCourseById(Long courseId) {
        try {
            return restTemplate.getForObject(
                    courseServiceUrl + "/api/courses/" + courseId, Map.class);
        } catch (RestClientException e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("id", courseId);
            error.put("name", "Course Service Unavailable");
            error.put("error", "Course service at " + courseServiceUrl + " is not running");
            return error;
        }
    }

    public List<Map<String, Object>> getCoursesByIds(List<Long> ids) {
        return ids.stream()
                .map(this::getCourseById)
                .collect(Collectors.toList());
    }
}

// ─── StudentService ─────────────────────────────────────────────────────────────

@Service
class StudentService {

    @Autowired private StudentRepository studentRepository;
    @Autowired private CourseServiceClient courseServiceClient;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Map<String, Object> getStudentWithCourses(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found: " + id));
        List<Map<String, Object>> courses =
                courseServiceClient.getCoursesByIds(student.getEnrolledCourseIds());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("student", student);
        result.put("enrolledCourses", courses);
        result.put("courseCount", courses.size());
        return result;
    }

    public Student createStudent(Student s) {
        return studentRepository.save(s);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}

// ─── StudentController ──────────────────────────────────────────────────────────

@RestController
@RequestMapping("/api/students")
class StudentController {

    @Autowired private StudentService studentService;

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ★ Inter-service communication demo — calls course-service!
    @GetMapping("/{id}/courses")
    public ResponseEntity<Map<String, Object>> getStudentWithCourses(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(studentService.getStudentWithCourses(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}

// ─── MicroservicesReferenceController ──────────────────────────────────────────

@RestController
@RequestMapping("/api")
class MicroservicesReferenceController {

    @GetMapping("/microservices-reference")
    public Map<String, Object> getMicroservicesReference() {
        Map<String, Object> ref = new LinkedHashMap<>();

        ref.put("title", "Microservices Architecture Reference");
        ref.put("whatAreMicroservices",
                "An architectural style where an application is structured as a collection of small, " +
                "independently deployable services, each responsible for a specific business capability");
        ref.put("principles", List.of(
                "Single Responsibility: each service owns one business capability",
                "Independent Deployability: services deploy without coordinating with others",
                "Decentralized Data: each service owns its own database",
                "Failure Isolation: one service failure shouldn't cascade",
                "Technology Diversity: services can use different tech stacks"
        ));

        // vsMonolith
        Map<String, Object> monolith = new LinkedHashMap<>();
        monolith.put("pros", List.of(
                "Simple to develop initially",
                "Easy to test",
                "Simple deployment (one thing to deploy)"
        ));
        monolith.put("cons", List.of(
                "Hard to scale individual components",
                "Technology lock-in",
                "Large team coordination issues",
                "Slow build/deploy as it grows"
        ));

        Map<String, Object> microservices = new LinkedHashMap<>();
        microservices.put("pros", List.of(
                "Independent scaling",
                "Technology flexibility",
                "Small focused teams",
                "Faster deployments per service"
        ));
        microservices.put("cons", List.of(
                "Distributed system complexity",
                "Network latency",
                "Data consistency challenges",
                "Operational overhead (multiple services to monitor)"
        ));

        Map<String, Object> vsMonolith = new LinkedHashMap<>();
        vsMonolith.put("monolith", monolith);
        vsMonolith.put("microservices", microservices);
        ref.put("vsMonolith", vsMonolith);

        // interServiceCommunication
        Map<String, Object> sync = new LinkedHashMap<>();
        sync.put("restHttp",
                "Service A calls Service B via HTTP. Simple but creates coupling and availability dependency.");
        sync.put("grpc", "High-performance binary protocol, great for internal services");

        Map<String, Object> async = new LinkedHashMap<>();
        async.put("messageBroker",
                "Service A publishes event to Kafka/RabbitMQ, Service B consumes when ready. Decoupled and resilient.");
        async.put("whenToUse",
                "Use async for notifications, order processing, audit logs. Use sync for real-time data needed immediately.");

        Map<String, Object> isc = new LinkedHashMap<>();
        isc.put("synchronous", sync);
        isc.put("asynchronous", async);
        ref.put("interServiceCommunication", isc);

        ref.put("thisDemo",
                "Student Service (port 8081) calls Course Service (port 8082) via RestTemplate. " +
                "Start both services to see inter-service communication. GET /api/students/{id}/courses");

        return ref;
    }
}

// ─── Application ───────────────────────────────────────────────────────────────

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CommandLineRunner startupBanner() {
        return args -> {
            System.out.println("\n================================================");
            System.out.println("  Student Service (port 8081)");
            System.out.println("================================================");
            System.out.println("  IMPORTANT: Also start course-service on port 8082 for full demo");
            System.out.println("  cd ../course-service && mvn spring-boot:run");
            System.out.println("------------------------------------------------");
            System.out.println("  Endpoints:");
            System.out.println("  GET    http://localhost:8081/api/students");
            System.out.println("  GET    http://localhost:8081/api/students/{id}");
            System.out.println("  GET    http://localhost:8081/api/students/{id}/courses  <- inter-service call!");
            System.out.println("  POST   http://localhost:8081/api/students");
            System.out.println("  DELETE http://localhost:8081/api/students/{id}");
            System.out.println("  GET    http://localhost:8081/api/microservices-reference");
            System.out.println("================================================\n");
        };
    }
}
