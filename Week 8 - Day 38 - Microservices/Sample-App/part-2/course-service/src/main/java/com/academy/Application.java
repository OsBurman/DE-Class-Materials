package com.academy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

// ─── Course Entity ──────────────────────────────────────────────────────────────

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Course {
    private Long id;
    private String title;
    private String instructor;
    private String department;
    private int credits;
    private double rating;
}

// ─── CourseRepository ───────────────────────────────────────────────────────────

@Component
class CourseRepository {
    private final Map<Long, Course> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @PostConstruct
    public void seed() {
        save(Course.builder()
                .title("Introduction to Java").instructor("Dr. Smith")
                .department("Computer Science").credits(3).rating(4.8).build());
        save(Course.builder()
                .title("Data Structures & Algorithms").instructor("Dr. Johnson")
                .department("Computer Science").credits(4).rating(4.7).build());
        save(Course.builder()
                .title("Database Systems").instructor("Prof. Lee")
                .department("Data Engineering").credits(3).rating(4.5).build());
        save(Course.builder()
                .title("Big Data Processing").instructor("Dr. Williams")
                .department("Data Engineering").credits(4).rating(4.6).build());
        save(Course.builder()
                .title("Web Development").instructor("Prof. Davis")
                .department("Software Engineering").credits(3).rating(4.4).build());
        save(Course.builder()
                .title("Cloud Architecture").instructor("Dr. Brown")
                .department("Software Engineering").credits(3).rating(4.9).build());
    }

    public List<Course> findAll() { return new ArrayList<>(store.values()); }

    public Optional<Course> findById(Long id) { return Optional.ofNullable(store.get(id)); }

    public List<Course> findByDepartment(String dept) {
        return store.values().stream()
                .filter(c -> c.getDepartment().equalsIgnoreCase(dept))
                .collect(Collectors.toList());
    }

    public Course save(Course c) {
        if (c.getId() == null) c.setId(idGen.getAndIncrement());
        store.put(c.getId(), c);
        return c;
    }

    public void deleteById(Long id) { store.remove(id); }
}

// ─── CourseController ───────────────────────────────────────────────────────────

@RestController
@RequestMapping("/api/courses")
class CourseController {

    @Autowired private CourseRepository courseRepository;

    @GetMapping
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/department/{dept}")
    public List<Course> getCoursesByDepartment(@PathVariable String dept) {
        return courseRepository.findByDepartment(dept);
    }
}

// ─── Application ───────────────────────────────────────────────────────────────

@SpringBootApplication
@EnableDiscoveryClient
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner startupBanner() {
        return args -> {
            System.out.println("\n================================================");
            System.out.println("  Course Service (port 8082) — Discovery Edition");
            System.out.println("================================================");
            System.out.println("  This service will register with Eureka at:");
            System.out.println("  http://localhost:8761/eureka/");
            System.out.println("  Student Service will discover this service via Eureka");
            System.out.println("------------------------------------------------");
            System.out.println("  Endpoints:");
            System.out.println("  GET http://localhost:8082/api/courses");
            System.out.println("  GET http://localhost:8082/api/courses/{id}");
            System.out.println("  GET http://localhost:8082/api/courses/department/{dept}");
            System.out.println("================================================\n");
        };
    }
}
