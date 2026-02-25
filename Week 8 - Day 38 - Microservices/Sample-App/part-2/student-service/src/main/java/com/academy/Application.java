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
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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
    private String major;
    private double gpa;
}

// ─── StudentRepository ──────────────────────────────────────────────────────────

@Component
class StudentRepository {
    private final Map<Long, Student> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @PostConstruct
    public void seed() {
        save(Student.builder().name("Alice Johnson").major("Computer Science").gpa(3.9).build());
        save(Student.builder().name("Bob Martinez").major("Data Engineering").gpa(3.7).build());
        save(Student.builder().name("Carol Williams").major("Software Engineering").gpa(3.8).build());
        save(Student.builder().name("David Brown").major("Computer Science").gpa(3.5).build());
        save(Student.builder().name("Eva Davis").major("Data Engineering").gpa(3.6).build());
    }

    public List<Student> findAll() { return new ArrayList<>(store.values()); }

    public Optional<Student> findById(Long id) { return Optional.ofNullable(store.get(id)); }

    public Student save(Student s) {
        if (s.getId() == null) s.setId(idGen.getAndIncrement());
        store.put(s.getId(), s);
        return s;
    }
}

// ─── CourseServiceClient ────────────────────────────────────────────────────────

@Component
class CourseServiceClient {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${course.service.name}")
    private String courseServiceName;

    @Autowired
    private RestTemplate restTemplate;

    // Looks up the actual URL of a running course-service instance via Eureka
    String getCourseServiceUrl() {
        List<ServiceInstance> instances = discoveryClient.getInstances(courseServiceName);
        if (instances.isEmpty()) return null;
        ServiceInstance instance = instances.get(0);
        return instance.getUri().toString();
    }

    // Uses the lb:// scheme so Spring Cloud LoadBalancer resolves service name via Eureka
    @SuppressWarnings("unchecked")
    public Object getCourses() {
        List<ServiceInstance> instances = discoveryClient.getInstances(courseServiceName);
        if (instances.isEmpty()) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "Course Service not found in Eureka registry");
            error.put("hint", "Make sure course-service is running and registered with Eureka at http://localhost:8761");
            return error;
        }
        try {
            // lb:// scheme → Spring Cloud LoadBalancer picks an instance from Eureka
            List<Map<String, Object>> courses = restTemplate.getForObject(
                    "lb://" + courseServiceName + "/api/courses", List.class);
            return courses;
        } catch (RestClientException e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "Failed to call course-service: " + e.getMessage());
            return error;
        }
    }

    // Returns discovery metadata — useful for the /service-status endpoint
    public Map<String, Object> getServiceStatus() {
        List<ServiceInstance> instances = discoveryClient.getInstances(courseServiceName);
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("serviceFound", !instances.isEmpty());
        status.put("instanceCount", instances.size());
        status.put("url", instances.isEmpty() ? "NOT_FOUND" : instances.get(0).getUri().toString());

        List<Map<String, Object>> instanceList = instances.stream().map(i -> {
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("host", i.getHost());
            info.put("port", i.getPort());
            info.put("serviceId", i.getServiceId());
            return info;
        }).collect(Collectors.toList());

        status.put("instances", instanceList);
        return status;
    }
}

// ─── StudentController ──────────────────────────────────────────────────────────

@RestController
@RequestMapping("/api/students")
class StudentController {

    @Autowired private StudentRepository studentRepository;
    @Autowired private CourseServiceClient courseServiceClient;

    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ★ Uses Eureka discovery to find course-service — no hardcoded URL!
    @GetMapping("/with-courses")
    public Map<String, Object> getStudentsWithCourses() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("students", studentRepository.findAll());
        result.put("allCourses", courseServiceClient.getCourses());
        result.put("discoveryNote",
                "Courses fetched using Eureka service discovery — no hardcoded URL needed!");
        return result;
    }

    @GetMapping("/service-status")
    public Map<String, Object> getServiceStatus() {
        return courseServiceClient.getServiceStatus();
    }
}

// ─── ServiceDiscoveryReferenceController ────────────────────────────────────────

@RestController
@RequestMapping("/api")
class ServiceDiscoveryReferenceController {

    @GetMapping("/service-discovery-reference")
    public Map<String, Object> getServiceDiscoveryReference() {
        Map<String, Object> ref = new LinkedHashMap<>();

        ref.put("title", "Service Discovery & API Gateway Reference");

        // serviceDiscovery
        Map<String, Object> sd = new LinkedHashMap<>();
        sd.put("problem",
                "In a dynamic cloud environment, service instances start/stop and get new IP addresses. " +
                "You can't hardcode URLs.");
        sd.put("solution",
                "Service registry (Eureka) tracks all running instances. Services look up each other by name, not IP.");
        sd.put("eurekaFlow", List.of(
                "1. Course Service starts, registers with Eureka ('I am course-service at 10.0.0.5:8082')",
                "2. Student Service wants to call Course Service",
                "3. Student Service asks Eureka: 'Where is course-service?'",
                "4. Eureka returns list of available instances",
                "5. Student Service picks an instance (client-side load balancing)",
                "6. Student Service calls the instance directly"
        ));

        Map<String, Object> components = new LinkedHashMap<>();
        components.put("eurekaServer",
                "The registry — services register here. UI at http://localhost:8761");
        components.put("eurekaClient",
                "Each service registers itself and queries for other services");
        components.put("heartbeat",
                "Services send heartbeat every 30s. Eureka removes services that stop heartbeating.");
        components.put("selfPreservation",
                "Eureka holds registrations during network issues to prevent mass deregistration");
        sd.put("components", components);
        ref.put("serviceDiscovery", sd);

        // apiGateway
        Map<String, Object> gw = new LinkedHashMap<>();
        gw.put("what",
                "Single entry point for all client requests. Routes to appropriate microservice.");

        Map<String, Object> scg = new LinkedHashMap<>();
        scg.put("dependency", "spring-cloud-starter-gateway");
        scg.put("features", List.of(
                "Route requests to services by path pattern",
                "Load balancing across service instances",
                "Authentication/authorization filter",
                "Rate limiting",
                "Request/response transformation",
                "Circuit breaker integration"
        ));
        scg.put("routeExample",
                "spring.cloud.gateway.routes[0].id=student-route\n" +
                "spring.cloud.gateway.routes[0].uri=lb://student-service\n" +
                "spring.cloud.gateway.routes[0].predicates[0]=Path=/students/**");
        gw.put("springCloudGateway", scg);
        ref.put("apiGateway", gw);

        // circuitBreaker
        Map<String, Object> cb = new LinkedHashMap<>();
        cb.put("problem",
                "If Course Service is down, Student Service waits and eventually times out. " +
                "Under load this cascades.");
        cb.put("solution",
                "Circuit Breaker pattern: after N failures, 'open' the circuit and return a fallback immediately");

        Map<String, Object> r4j = new LinkedHashMap<>();
        r4j.put("dependency", "spring-cloud-starter-circuitbreaker-resilience4j");
        r4j.put("states", List.of(
                "CLOSED: Normal operation, requests pass through",
                "OPEN: Too many failures, return fallback immediately",
                "HALF_OPEN: Test if service recovered with a few requests"
        ));
        cb.put("resilience4j", r4j);
        ref.put("circuitBreaker", cb);

        // loadBalancing
        Map<String, Object> lb = new LinkedHashMap<>();
        lb.put("clientSide",
                "Client picks which instance to call (Spring Cloud LoadBalancer with Eureka)");
        lb.put("strategies", List.of(
                "Round robin (default): distribute requests evenly",
                "Random: pick random instance",
                "Weighted: favor faster instances"
        ));
        ref.put("loadBalancing", lb);

        return ref;
    }
}

// ─── Application ───────────────────────────────────────────────────────────────

@SpringBootApplication
@EnableDiscoveryClient
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // @LoadBalanced enables the lb:// URI scheme — Spring Cloud resolves service names via Eureka
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CommandLineRunner startupBanner() {
        return args -> {
            System.out.println("\n================================================");
            System.out.println("  Student Service (port 8081) — Discovery Edition");
            System.out.println("================================================");
            System.out.println("  All 3 services must run for full demo:");
            System.out.println("  1. eureka-server   (port 8761)  <- start first");
            System.out.println("  2. course-service  (port 8082)");
            System.out.println("  3. student-service (port 8081)  <- this service");
            System.out.println("------------------------------------------------");
            System.out.println("  Endpoints:");
            System.out.println("  GET http://localhost:8081/api/students");
            System.out.println("  GET http://localhost:8081/api/students/{id}");
            System.out.println("  GET http://localhost:8081/api/students/with-courses  <- discovery!");
            System.out.println("  GET http://localhost:8081/api/students/service-status");
            System.out.println("  GET http://localhost:8081/api/service-discovery-reference");
            System.out.println("  Eureka Dashboard: http://localhost:8761");
            System.out.println("================================================\n");
        };
    }
}
