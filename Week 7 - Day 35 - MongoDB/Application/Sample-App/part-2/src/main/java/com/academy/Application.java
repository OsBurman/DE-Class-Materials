package com.academy;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

// ============================================================
// DOMAIN MODELS
// ============================================================

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses")
class Course {
    @Id
    private String id;
    private String title;
    private String instructor;
    private String department;
    private int credits;
    private List<String> prerequisites;
    private double rating;
    private int enrollmentCount;
    private LocalDateTime createdAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
class Review {
    @Id
    private String id;
    private String courseId; // manual reference to Course — NOT @DBRef
    private String studentName;
    private int rating; // 1 to 5
    private String comment;
    private List<String> tags; // e.g. ["challenging","practical","great-instructor"]
    private LocalDateTime reviewedAt;
}

// ============================================================
// REPOSITORIES
// ============================================================

interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByDepartment(String department);
    List<Course> findByRatingGreaterThanEqual(double minRating);
    List<Course> findByInstructorContainingIgnoreCase(String instructor);

    @Query("{prerequisites: {$in: ?0}}")
    List<Course> findByPrerequisitesContaining(List<String> prereqs);

    List<Course> findTop5ByOrderByRatingDesc();
}

interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByCourseId(String courseId);
    List<Review> findByStudentName(String studentName);
    List<Review> findByCourseIdAndRatingGreaterThanEqual(String courseId, int minRating);
    long countByCourseId(String courseId);
}

// ============================================================
// AGGREGATION SERVICE
// ============================================================

@Service
class AggregationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /** GROUP BY department — avg rating, course count, total credits, top rating */
    public List<Map> getCourseStatsByDepartment() {
        Aggregation agg = Aggregation.newAggregation(
            Aggregation.group("department")
                .avg("rating").as("averageRating")
                .count().as("courseCount")
                .sum("credits").as("totalCredits")
                .max("rating").as("topRating"),
            Aggregation.sort(Sort.Direction.DESC, "averageRating")
        );
        return mongoTemplate.aggregate(agg, "courses", Map.class).getMappedResults();
    }

    /** MATCH + SORT + LIMIT — top-rated courses with rating >= 4.0 */
    public List<Course> getTopRatedCourses(int limit) {
        Aggregation agg = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("rating").gte(4.0)),
            Aggregation.sort(Sort.Direction.DESC, "rating"),
            Aggregation.limit(limit)
        );
        return mongoTemplate.aggregate(agg, "courses", Course.class).getMappedResults();
    }

    /** Aggregate review stats for a single course including rating distribution */
    public Map<String, Object> getReviewSummary(String courseId) {
        Aggregation agg = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("courseId").is(courseId)),
            Aggregation.group("courseId")
                .avg("rating").as("averageRating")
                .count().as("totalReviews")
                .push("rating").as("allRatings")
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(agg, "reviews", Map.class);
        Map<String, Object> summary = new HashMap<>();
        if (!results.getMappedResults().isEmpty()) {
            Map raw = results.getMappedResults().get(0);
            summary.put("courseId", courseId);
            summary.put("averageRating", raw.get("averageRating"));
            summary.put("totalReviews", raw.get("totalReviews"));
            // Calculate rating distribution
            List<Integer> allRatings = (List<Integer>) raw.get("allRatings");
            Map<Integer, Long> dist = new HashMap<>();
            for (int i = 1; i <= 5; i++) {
                final int r = i;
                dist.put(r, allRatings != null ? allRatings.stream().filter(x -> x.equals(r)).count() : 0L);
            }
            summary.put("ratingDistribution", dist);
        } else {
            summary.put("courseId", courseId);
            summary.put("averageRating", 0.0);
            summary.put("totalReviews", 0);
            summary.put("ratingDistribution", Map.of());
        }
        return summary;
    }

    /** Dynamic multi-criteria search using Query/Criteria (not a pipeline) */
    public List<Course> searchCourses(String keyword, String department, Double minRating) {
        List<Criteria> criteria = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            criteria.add(new Criteria().orOperator(
                Criteria.where("title").regex(keyword, "i"),
                Criteria.where("instructor").regex(keyword, "i")
            ));
        }
        if (department != null && !department.isBlank()) {
            criteria.add(Criteria.where("department").is(department));
        }
        if (minRating != null) {
            criteria.add(Criteria.where("rating").gte(minRating));
        }
        Query query = criteria.isEmpty()
            ? new Query()
            : new Query(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        return mongoTemplate.find(query, Course.class);
    }
}

// ============================================================
// COURSE SERVICE
// ============================================================

@Service
class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AggregationService aggregationService;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(String id) {
        return courseRepository.findById(id);
    }

    public Course createCourse(Course c) {
        c.setCreatedAt(LocalDateTime.now());
        return courseRepository.save(c);
    }

    public List<Course> getCoursesByDepartment(String dept) {
        return courseRepository.findByDepartment(dept);
    }

    public List<Course> getTopRatedCourses(int limit) {
        return aggregationService.getTopRatedCourses(limit);
    }

    public List<Map> getDepartmentStats() {
        return aggregationService.getCourseStatsByDepartment();
    }

    public List<Course> searchCourses(String keyword, String department, Double minRating) {
        return aggregationService.searchCourses(keyword, department, minRating);
    }

    /** Fetch course + its reviews + aggregated summary in one map */
    public Map<String, Object> getCourseWithReviews(String courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) return null;
        List<Review> reviews = reviewRepository.findByCourseId(courseId);
        Map<String, Object> summary = aggregationService.getReviewSummary(courseId);
        return Map.of("course", course.get(), "reviews", reviews, "summary", summary);
    }

    /** Save review, then recalculate and persist the course's average rating */
    public Review addReview(String courseId, Review review) {
        review.setReviewedAt(LocalDateTime.now());
        Review saved = reviewRepository.save(review);
        List<Review> allReviews = reviewRepository.findByCourseId(courseId);
        double newAvg = allReviews.stream().mapToInt(Review::getRating).average().orElse(review.getRating());
        courseRepository.findById(courseId).ifPresent(c -> {
            c.setRating(newAvg);
            c.setEnrollmentCount(c.getEnrollmentCount() + 1);
            courseRepository.save(c);
        });
        return saved;
    }
}

// ============================================================
// COURSE CONTROLLER
// ============================================================

@RestController
@RequestMapping("/api/courses")
class CourseController {

    @Autowired
    private CourseService courseService;

    // GET /api/courses
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    // GET /api/courses/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        return courseService.getCourseById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/courses
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(course));
    }

    // GET /api/courses/department/{dept}
    @GetMapping("/department/{dept}")
    public List<Course> getCoursesByDepartment(@PathVariable String dept) {
        return courseService.getCoursesByDepartment(dept);
    }

    // GET /api/courses/top-rated?limit=5
    @GetMapping("/top-rated")
    public List<Course> getTopRatedCourses(@RequestParam(defaultValue = "5") int limit) {
        return courseService.getTopRatedCourses(limit);
    }

    // GET /api/courses/stats
    @GetMapping("/stats")
    public List<Map> getDepartmentStats() {
        return courseService.getDepartmentStats();
    }

    // GET /api/courses/search?keyword=spring&department=CS&minRating=4.0
    @GetMapping("/search")
    public List<Course> searchCourses(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String department,
        @RequestParam(required = false) Double minRating
    ) {
        return courseService.searchCourses(keyword, department, minRating);
    }

    // GET /api/courses/{id}/reviews
    @GetMapping("/{id}/reviews")
    public ResponseEntity<Map<String, Object>> getCourseWithReviews(@PathVariable String id) {
        Map<String, Object> result = courseService.getCourseWithReviews(id);
        if (result == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(result);
    }

    // POST /api/courses/{id}/reviews
    @PostMapping("/{id}/reviews")
    public ResponseEntity<Review> addReview(@PathVariable String id, @RequestBody Review review) {
        review.setCourseId(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.addReview(id, review));
    }
}

// ============================================================
// REFERENCE CONTROLLER
// ============================================================

@RestController
@RequestMapping("/api")
class MongoAdvancedReferenceController {

    // GET /api/mongodb-advanced-reference
    @GetMapping("/mongodb-advanced-reference")
    public Map<String, Object> getReference() {
        return Map.of(
            "title", "MongoDB Advanced Reference",
            "aggregationPipeline", Map.of(
                "overview", "Pipeline processes documents through sequential stages, each stage transforms the output",
                "stages", List.of(
                    Map.of("stage", "$match",    "description", "Filter documents — like SQL WHERE. Place early to reduce data processed.",   "example", "{$match: {rating: {$gte: 4.0}}}"),
                    Map.of("stage", "$group",    "description", "Group documents and compute aggregates — like SQL GROUP BY",                  "example", "{$group: {_id: '$department', avgRating: {$avg: '$rating'}, count: {$sum: 1}}}"),
                    Map.of("stage", "$sort",     "description", "Sort results by field(s)",                                                   "example", "{$sort: {avgRating: -1}}"),
                    Map.of("stage", "$project",  "description", "Select/transform fields — like SQL SELECT",                                  "example", "{$project: {title: 1, rating: 1, _id: 0}}"),
                    Map.of("stage", "$limit",    "description", "Limit number of documents",                                                   "example", "{$limit: 10}"),
                    Map.of("stage", "$skip",     "description", "Skip N documents — used with $limit for pagination",                         "example", "{$skip: 20}"),
                    Map.of("stage", "$unwind",   "description", "Flatten an array field — creates one doc per array element",                 "example", "{$unwind: '$tags'}"),
                    Map.of("stage", "$lookup",   "description", "Join with another collection — like SQL LEFT JOIN",                           "example", "{$lookup: {from:'reviews', localField:'_id', foreignField:'courseId', as:'reviews'}}"),
                    Map.of("stage", "$addFields","description", "Add computed fields without removing existing ones",                          "example", "{$addFields: {fullTitle: {$concat: ['$title', ' - ', '$instructor']}}}")
                ),
                "groupAccumulators", Map.of(
                    "$avg",        "Average of values",
                    "$sum",        "Sum (use 1 to count: {$sum:1})",
                    "$min/$max",   "Minimum/Maximum value",
                    "$push",       "Build array of all values",
                    "$addToSet",   "Build array of unique values",
                    "$first/$last","First/last value in group"
                )
            ),
            "embeddingVsReferencing", Map.of(
                "embed", Map.of(
                    "when", List.of(
                        "Data is accessed together (course + instructor info)",
                        "1-to-few relationships (student → addresses)",
                        "Data doesn't change independently",
                        "Need atomic updates"
                    ),
                    "example", "Student document embeds Address — they're always read together"
                ),
                "reference", Map.of(
                    "when", List.of(
                        "Many-to-many relationships (students ↔ courses)",
                        "Large subdocuments that could exceed 16MB limit",
                        "Data is frequently updated independently",
                        "Subdocument needs its own query patterns"
                    ),
                    "example", "Review stores courseId string — course and reviews are queried independently"
                ),
                "mongoLookup", "$lookup stage performs a JOIN equivalent to LEFT OUTER JOIN in SQL"
            ),
            "springDataAggregation", Map.of(
                "pattern", "Aggregation.newAggregation(stage1, stage2, stage3, ...)",
                "stages", Map.of(
                    "match",   "Aggregation.match(Criteria.where('rating').gte(4.0))",
                    "group",   "Aggregation.group('department').avg('rating').as('avgRating').count().as('total')",
                    "sort",    "Aggregation.sort(Sort.Direction.DESC, 'avgRating')",
                    "project", "Aggregation.project('title','rating').andExclude('_id')",
                    "limit",   "Aggregation.limit(10)",
                    "unwind",  "Aggregation.unwind('tags')"
                ),
                "execute", "mongoTemplate.aggregate(aggregation, 'collectionName', OutputClass.class).getMappedResults()"
            ),
            "indexes", Map.of(
                "why", "Without indexes, MongoDB scans every document (collection scan = slow for large datasets)",
                "types", List.of(
                    "Single field: db.courses.createIndex({rating:-1})",
                    "Compound: db.courses.createIndex({department:1, rating:-1})",
                    "Text: db.courses.createIndex({title:'text', description:'text'})",
                    "Unique: db.students.createIndex({email:1},{unique:true})"
                ),
                "springDataAnnotation", "@Indexed on field (single) or @CompoundIndex on class"
            )
        );
    }
}

// ============================================================
// APPLICATION ENTRY POINT
// ============================================================

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner seedData(CourseRepository courseRepository,
                               ReviewRepository reviewRepository) {
        return args -> {
            // Only seed if collections are empty
            if (courseRepository.count() > 0) {
                System.out.println("=== Data already seeded — skipping ===");
                return;
            }

            // ── Seed Courses ─────────────────────────────────────────────
            Course dsa = courseRepository.save(Course.builder()
                .title("Data Structures & Algorithms")
                .instructor("Prof. Rodriguez")
                .department("CS")
                .credits(3)
                .prerequisites(List.of("CS101"))
                .rating(4.7)
                .enrollmentCount(45)
                .createdAt(LocalDateTime.now())
                .build());

            Course ml = courseRepository.save(Course.builder()
                .title("Machine Learning Fundamentals")
                .instructor("Prof. Chen")
                .department("CS")
                .credits(3)
                .prerequisites(List.of("Linear Algebra", "CS201"))
                .rating(4.5)
                .enrollmentCount(38)
                .createdAt(LocalDateTime.now())
                .build());

            Course calc2 = courseRepository.save(Course.builder()
                .title("Calculus II")
                .instructor("Prof. Thompson")
                .department("Mathematics")
                .credits(4)
                .prerequisites(List.of("Calculus I"))
                .rating(4.2)
                .enrollmentCount(60)
                .createdAt(LocalDateTime.now())
                .build());

            Course linalg = courseRepository.save(Course.builder()
                .title("Linear Algebra")
                .instructor("Prof. Thompson")
                .department("Mathematics")
                .credits(3)
                .prerequisites(List.of("Calculus I"))
                .rating(4.4)
                .enrollmentCount(50)
                .createdAt(LocalDateTime.now())
                .build());

            Course react = courseRepository.save(Course.builder()
                .title("React & Modern JavaScript")
                .instructor("Prof. Patel")
                .department("Web Development")
                .credits(3)
                .prerequisites(List.of("HTML/CSS basics"))
                .rating(4.8)
                .enrollmentCount(72)
                .createdAt(LocalDateTime.now())
                .build());

            Course nodejs = courseRepository.save(Course.builder()
                .title("Node.js & APIs")
                .instructor("Prof. Kim")
                .department("Web Development")
                .credits(3)
                .prerequisites(List.of("JavaScript fundamentals"))
                .rating(4.6)
                .enrollmentCount(65)
                .createdAt(LocalDateTime.now())
                .build());

            // ── Seed Reviews (2 per course) ───────────────────────────────
            // Data Structures & Algorithms
            reviewRepository.save(Review.builder()
                .courseId(dsa.getId())
                .studentName("Alice Johnson")
                .rating(5)
                .comment("Fantastic course! Prof. Rodriguez explains complex topics with crystal clarity. The problem sets are tough but very rewarding.")
                .tags(List.of("challenging", "well-structured", "great-instructor"))
                .reviewedAt(LocalDateTime.now().minusDays(10))
                .build());

            reviewRepository.save(Review.builder()
                .courseId(dsa.getId())
                .studentName("Bob Martinez")
                .rating(4)
                .comment("Solid fundamentals course. Covers trees, graphs, and dynamic programming thoroughly. Highly recommend brushing up on recursion first.")
                .tags(List.of("practical", "rigorous", "must-know"))
                .reviewedAt(LocalDateTime.now().minusDays(8))
                .build());

            // Machine Learning Fundamentals
            reviewRepository.save(Review.builder()
                .courseId(ml.getId())
                .studentName("Clara Nguyen")
                .rating(5)
                .comment("Prof. Chen makes ML approachable without dumbing it down. Loved the hands-on projects with real datasets.")
                .tags(List.of("hands-on", "practical", "great-instructor"))
                .reviewedAt(LocalDateTime.now().minusDays(14))
                .build());

            reviewRepository.save(Review.builder()
                .courseId(ml.getId())
                .studentName("David Lee")
                .rating(4)
                .comment("Great introduction to supervised and unsupervised learning. Linear algebra background is a must — be prepared!")
                .tags(List.of("intermediate", "math-heavy", "insightful"))
                .reviewedAt(LocalDateTime.now().minusDays(5))
                .build());

            // Calculus II
            reviewRepository.save(Review.builder()
                .courseId(calc2.getId())
                .studentName("Emma Wilson")
                .rating(4)
                .comment("Prof. Thompson is patient and thorough. Integration techniques section was particularly well-paced. Office hours are very helpful.")
                .tags(List.of("well-paced", "supportive", "foundational"))
                .reviewedAt(LocalDateTime.now().minusDays(20))
                .build());

            reviewRepository.save(Review.builder()
                .courseId(calc2.getId())
                .studentName("Frank Okafor")
                .rating(4)
                .comment("Challenging but fair. The series and sequences unit was tricky — make sure to do all practice problems.")
                .tags(List.of("challenging", "fair-grading", "practice-heavy"))
                .reviewedAt(LocalDateTime.now().minusDays(15))
                .build());

            // Linear Algebra
            reviewRepository.save(Review.builder()
                .courseId(linalg.getId())
                .studentName("Grace Park")
                .rating(5)
                .comment("Beautifully taught. Understanding eigenvalues finally clicked for me here. Essential for anyone going into ML or data science.")
                .tags(List.of("essential", "clear-explanations", "great-instructor"))
                .reviewedAt(LocalDateTime.now().minusDays(12))
                .build());

            reviewRepository.save(Review.builder()
                .courseId(linalg.getId())
                .studentName("Henry Brooks")
                .rating(4)
                .comment("Very useful for understanding ML algorithms at a deeper level. Visualizations in class really helped with the abstract concepts.")
                .tags(List.of("visual", "insightful", "practical"))
                .reviewedAt(LocalDateTime.now().minusDays(7))
                .build());

            // React & Modern JavaScript
            reviewRepository.save(Review.builder()
                .courseId(react.getId())
                .studentName("Isabelle Thomas")
                .rating(5)
                .comment("Best web dev course I've taken! Prof. Patel keeps everything up-to-date with modern practices. Built three real projects by the end.")
                .tags(List.of("project-based", "modern", "great-instructor"))
                .reviewedAt(LocalDateTime.now().minusDays(3))
                .build());

            reviewRepository.save(Review.builder()
                .courseId(react.getId())
                .studentName("James Carter")
                .rating(5)
                .comment("Hooks, context, and routing all covered deeply. The final capstone project is challenging but you'll feel very confident afterward.")
                .tags(List.of("comprehensive", "hands-on", "job-ready"))
                .reviewedAt(LocalDateTime.now().minusDays(1))
                .build());

            // Node.js & APIs
            reviewRepository.save(Review.builder()
                .courseId(nodejs.getId())
                .studentName("Karen Liu")
                .rating(5)
                .comment("Excellent coverage of REST API design, Express, and MongoDB integration. Prof. Kim's real-world examples made everything stick.")
                .tags(List.of("real-world", "backend-focused", "great-instructor"))
                .reviewedAt(LocalDateTime.now().minusDays(6))
                .build());

            reviewRepository.save(Review.builder()
                .courseId(nodejs.getId())
                .studentName("Liam Stevenson")
                .rating(4)
                .comment("Great course for learning the full backend stack. Authentication and middleware sections were the highlights for me.")
                .tags(List.of("practical", "backend-focused", "security"))
                .reviewedAt(LocalDateTime.now().minusDays(4))
                .build());

            // ── Startup Banner ────────────────────────────────────────────
            System.out.println("""
                ╔══════════════════════════════════════════════════════════════════╗
                ║               MongoDB Advanced Demo                             ║
                ║        Embedded MongoDB auto-configured — no external            ║
                ║                  MongoDB needed                                 ║
                ╠══════════════════════════════════════════════════════════════════╣
                ║  ENDPOINTS                                                       ║
                ║  ─────────────────────────────────────────────────────────────  ║
                ║  GET    /api/courses                  — all courses              ║
                ║  GET    /api/courses/{id}             — course by id             ║
                ║  POST   /api/courses                  — create course            ║
                ║  GET    /api/courses/department/{d}   — by department            ║
                ║  GET    /api/courses/top-rated        — top rated (?limit=5)     ║
                ║  GET    /api/courses/stats            — department aggregation   ║
                ║  GET    /api/courses/search           — search (?keyword=        ║
                ║                                         &department=            ║
                ║                                         &minRating=)            ║
                ║  GET    /api/courses/{id}/reviews     — course + reviews + stats ║
                ║  POST   /api/courses/{id}/reviews     — add review to course     ║
                ║  GET    /api/mongodb-advanced-reference — concepts reference      ║
                ╠══════════════════════════════════════════════════════════════════╣
                ║  Run: mvn spring-boot:run                                        ║
                ╚══════════════════════════════════════════════════════════════════╝
                """);

            System.out.println("Seeded: 6 courses, 12 reviews across CS / Mathematics / Web Development");
        };
    }
}
