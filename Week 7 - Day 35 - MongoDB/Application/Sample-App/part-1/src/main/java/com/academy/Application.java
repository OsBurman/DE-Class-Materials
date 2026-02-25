package com.academy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

// ============================================================
// EMBEDDED SUBDOCUMENT — not a @Document, just a POJO
// Stored inside Student documents, not in its own collection
// ============================================================
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
}

// ============================================================
// STUDENT DOCUMENT — maps to the "students" collection
// @Document tells Spring Data this is a MongoDB document
// @Id maps to MongoDB's _id field
// ============================================================
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "students")
class Student {

    @Id
    private String id;                      // MongoDB auto-generates ObjectId if null

    private String name;
    private String email;
    private String major;
    private Double gpa;

    private List<String> tags;              // e.g. ["honors", "scholarship", "part-time"]
    private Map<String, Object> metadata;   // flexible extra data: {"scholarship": true, "advisor": "Prof. Smith"}

    private Address address;               // embedded subdocument — no separate collection

    private LocalDateTime createdAt;
}

// ============================================================
// STUDENT REPOSITORY — Spring Data MongoDB repository
// Extend MongoRepository<Entity, IdType> for CRUD + pagination
// Spring generates queries from method names automatically
// ============================================================
interface StudentRepository extends MongoRepository<Student, String> {

    // Derived query — Spring generates: find({ major: ?0 })
    List<Student> findByMajor(String major);

    // Derived query — Spring generates: find({ gpa: { $gte: ?0 } })
    List<Student> findByGpaGreaterThanEqual(double gpa);

    // Derived query — Spring generates: find({ name: { $regex: ?0, $options: 'i' } })
    List<Student> findByNameContainingIgnoreCase(String name);

    // Derived query — combines two conditions
    List<Student> findByMajorAndGpaGreaterThan(String major, double gpa);

    // @Query with custom MongoDB query — positional parameter ?0
    @org.springframework.data.mongodb.repository.Query("{tags: {$in: ?0}}")
    List<Student> findByTagsContaining(List<String> tags);

    // @Query with field projection — only return name, gpa, major
    @org.springframework.data.mongodb.repository.Query(value = "{major: ?0}", fields = "{name: 1, gpa: 1, major: 1}")
    List<Student> findNameAndGpaByMajor(String major);

    // Count derived query
    long countByMajor(String major);

    // Top N + sort derived query
    List<Student> findTop3ByOrderByGpaDesc();
}

// ============================================================
// STUDENT SERVICE — business logic layer
// Shows both Repository pattern and MongoTemplate for advanced queries
// ============================================================
@Service
class StudentService {

    private final StudentRepository studentRepository;
    private final MongoTemplate mongoTemplate;

    StudentService(StudentRepository studentRepository, MongoTemplate mongoTemplate) {
        this.studentRepository = studentRepository;
        this.mongoTemplate = mongoTemplate;
    }

    // --- Basic CRUD via Repository ---

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(String id) {
        return studentRepository.findById(id);
    }

    public Student createStudent(Student student) {
        student.setCreatedAt(LocalDateTime.now());
        return studentRepository.save(student);
    }

    public Optional<Student> updateStudent(String id, Student updates) {
        return studentRepository.findById(id).map(existing -> {
            if (updates.getName()  != null) existing.setName(updates.getName());
            if (updates.getEmail() != null) existing.setEmail(updates.getEmail());
            if (updates.getMajor() != null) existing.setMajor(updates.getMajor());
            if (updates.getGpa()   != null) existing.setGpa(updates.getGpa());
            if (updates.getTags()  != null) existing.setTags(updates.getTags());
            if (updates.getMetadata() != null) existing.setMetadata(updates.getMetadata());
            if (updates.getAddress()  != null) existing.setAddress(updates.getAddress());
            return studentRepository.save(existing);
        });
    }

    public void deleteStudent(String id) {
        studentRepository.deleteById(id);
    }

    // --- Derived Query searches via Repository ---

    public List<Student> searchByGpa(double minGpa) {
        return studentRepository.findByGpaGreaterThanEqual(minGpa);
    }

    public List<Student> searchByMajor(String major) {
        return studentRepository.findByMajor(major);
    }

    public List<Student> searchByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Student> searchByTags(List<String> tags) {
        return studentRepository.findByTagsContaining(tags);
    }

    // --- Advanced queries via MongoTemplate + Criteria API ---

    // MongoTemplate: build a Query with Criteria — more flexible than derived queries
    public List<Student> findHonorsStudents() {
        // Criteria.where("gpa").gte(3.7) → { gpa: { $gte: 3.7 } }
        // .and("tags").in("honors")      → { tags: { $in: ["honors"] } }
        Query query = new Query(Criteria.where("gpa").gte(3.7).and("tags").in("honors"));
        return mongoTemplate.find(query, Student.class);
    }

    // MongoTemplate: query nested field using dot notation — metadata.scholarship
    public List<Student> getStudentsByScholarship(boolean scholarship) {
        Query query = new Query(Criteria.where("metadata.scholarship").is(scholarship));
        return mongoTemplate.find(query, Student.class);
    }

    // MongoTemplate: Aggregation pipeline — group by major, compute stats
    public List<Map> getGpaSummary() {
        // Aggregation pipeline stages:
        //   1. $group  — group by major, compute avg/count/max GPA
        //   2. $sort   — sort by averageGpa descending
        Aggregation agg = Aggregation.newAggregation(
            Aggregation.group("major")
                .avg("gpa").as("averageGpa")
                .count().as("studentCount")
                .max("gpa").as("highestGpa"),
            Aggregation.sort(Sort.Direction.DESC, "averageGpa")
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(agg, "students", Map.class);
        return results.getMappedResults();
    }
}

// ============================================================
// STUDENT CONTROLLER — REST API for student CRUD + search
// ============================================================
@RestController
@RequestMapping("/api/students")
class StudentController {

    private final StudentService studentService;

    StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // GET /api/students — retrieve all students
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // GET /api/students/{id} — retrieve one student by ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable String id) {
        return studentService.getStudentById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/students — create a new student (returns 201 Created)
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student created = studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/students/{id} — update an existing student
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable String id,
                                                  @RequestBody Student updates) {
        return studentService.updateStudent(id, updates)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/students/{id} — delete a student (returns 204 No Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/students/search?gpa=3.5 — filter by minimum GPA
    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchByGpa(@RequestParam double gpa) {
        return ResponseEntity.ok(studentService.searchByGpa(gpa));
    }

    // GET /api/students/search/major?major=Computer+Science
    @GetMapping("/search/major")
    public ResponseEntity<List<Student>> searchByMajor(@RequestParam String major) {
        return ResponseEntity.ok(studentService.searchByMajor(major));
    }

    // GET /api/students/search/name?name=alice
    @GetMapping("/search/name")
    public ResponseEntity<List<Student>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(studentService.searchByName(name));
    }

    // GET /api/students/search/tags?tags=honors,scholarship
    @GetMapping("/search/tags")
    public ResponseEntity<List<Student>> searchByTags(@RequestParam String tags) {
        // Parse comma-separated tags into a list
        List<String> tagList = Arrays.asList(tags.split(","));
        return ResponseEntity.ok(studentService.searchByTags(tagList));
    }

    // GET /api/students/honors — MongoTemplate query: gpa >= 3.7 AND tag "honors"
    @GetMapping("/honors")
    public ResponseEntity<List<Student>> findHonorsStudents() {
        return ResponseEntity.ok(studentService.findHonorsStudents());
    }

    // GET /api/students/scholarship?eligible=true — query nested metadata field
    @GetMapping("/scholarship")
    public ResponseEntity<List<Student>> getStudentsByScholarship(@RequestParam boolean eligible) {
        return ResponseEntity.ok(studentService.getStudentsByScholarship(eligible));
    }

    // GET /api/students/stats/gpa-by-major — aggregation pipeline results
    @GetMapping("/stats/gpa-by-major")
    public ResponseEntity<List<Map>> getGpaSummary() {
        return ResponseEntity.ok(studentService.getGpaSummary());
    }
}

// ============================================================
// MONGODB REFERENCE CONTROLLER — educational reference endpoint
// GET /api/mongodb-reference returns a structured reference map
// ============================================================
@RestController
@RequestMapping("/api")
class MongoReferenceController {

    @GetMapping("/mongodb-reference")
    public ResponseEntity<Map<String, Object>> getMongoReference() {
        Map<String, Object> reference = new LinkedHashMap<>();

        reference.put("title", "MongoDB & Spring Data MongoDB Reference");

        // SQL vs NoSQL comparison
        Map<String, Object> sqlVsNoSQL = new LinkedHashMap<>();
        Map<String, Object> sql = new LinkedHashMap<>();
        sql.put("strengths", List.of(
            "ACID transactions",
            "Complex joins",
            "Schema enforcement",
            "Mature tooling"
        ));
        sql.put("weaknesses", List.of(
            "Rigid schema (hard to change)",
            "Horizontal scaling is complex",
            "Object-relational impedance mismatch"
        ));
        Map<String, Object> nosql = new LinkedHashMap<>();
        nosql.put("strengths", List.of(
            "Flexible schema (documents can differ)",
            "Horizontal scaling",
            "Natural fit for OOP (objects as documents)",
            "Great for hierarchical/nested data"
        ));
        nosql.put("weaknesses", List.of(
            "Less mature transactions",
            "No standard query language",
            "Eventual consistency tradeoffs"
        ));
        sqlVsNoSQL.put("sql", sql);
        sqlVsNoSQL.put("nosql", nosql);
        reference.put("sqlVsNoSQL", sqlVsNoSQL);

        // Document model
        Map<String, Object> documentModel = new LinkedHashMap<>();
        documentModel.put("concept", "Documents are JSON-like objects (BSON) stored in collections");
        documentModel.put("embeddedDocuments", "Nest related data inside a document (Address inside Student) — read together, update together");
        documentModel.put("arrays", "Documents can contain arrays of primitives or embedded documents (tags, courses)");
        documentModel.put("flexibleSchema", "Different documents in same collection can have different fields");
        reference.put("documentModel", documentModel);

        // Mongo operations
        reference.put("mongoOperations", List.of(
            Map.of("operation", "find({})",
                   "description", "Find all documents",
                   "springEquivalent", "repository.findAll() or mongoTemplate.findAll(Class)"),
            Map.of("operation", "find({major:'CS'})",
                   "description", "Find with filter",
                   "springEquivalent", "repository.findByMajor('CS') or Criteria.where('major').is('CS')"),
            Map.of("operation", "insertOne({...})",
                   "description", "Insert one document",
                   "springEquivalent", "repository.save(entity)"),
            Map.of("operation", "updateOne({_id:id},{$set:{gpa:4.0}})",
                   "description", "Update specific fields",
                   "springEquivalent", "mongoTemplate.updateFirst(query, update, Class)"),
            Map.of("operation", "deleteOne({_id:id})",
                   "description", "Delete one document",
                   "springEquivalent", "repository.deleteById(id)"),
            Map.of("operation", "aggregate([...])",
                   "description", "Aggregation pipeline",
                   "springEquivalent", "mongoTemplate.aggregate(Aggregation, collection, Class)")
        ));

        // Query operators
        Map<String, Object> queryOperators = new LinkedHashMap<>();
        queryOperators.put("comparison", List.of(
            "$eq (equals)", "$ne (not equals)", "$gt (greater than)",
            "$lt (less than)", "$gte/$lte", "$in (in array)", "$nin (not in array)"
        ));
        queryOperators.put("logical", List.of("$and", "$or", "$not", "$nor"));
        queryOperators.put("array", List.of(
            "$in (element in array)", "$all (array contains all)",
            "$elemMatch (element matches criteria)", "$size (array length)"
        ));
        queryOperators.put("element", List.of("$exists (field exists)", "$type (field type)"));
        queryOperators.put("text", List.of(
            "$regex (pattern match)", "$text (full text search, requires text index)"
        ));
        reference.put("queryOperators", queryOperators);

        // Spring Data MongoDB
        Map<String, Object> springDataMongoDB = new LinkedHashMap<>();
        springDataMongoDB.put("atDocument", "@Document(collection='students') marks a class as a MongoDB document");
        springDataMongoDB.put("atId", "@Id maps to MongoDB _id field (can be String, ObjectId)");
        springDataMongoDB.put("mongoRepository", "Extends MongoRepository<Entity, IdType> — provides CRUD + pagination");
        springDataMongoDB.put("derivedQueries", "findByMajor(), findByGpaGreaterThan() — Spring generates MongoDB query from method name");
        springDataMongoDB.put("atQuery", "@Query('{field: ?0}') for custom MongoDB queries with positional parameters");
        springDataMongoDB.put("mongoTemplate", "More powerful than repository — supports complex queries, Criteria API, Aggregation");
        reference.put("springDataMongoDB", springDataMongoDB);

        // Aggregation pipeline
        Map<String, Object> aggregationPipeline = new LinkedHashMap<>();
        aggregationPipeline.put("description", "Process documents through stages in sequence");
        aggregationPipeline.put("stages", List.of(
            "$match — filter documents (like WHERE)",
            "$group — group and aggregate (like GROUP BY)",
            "$sort — sort results",
            "$project — select/transform fields (like SELECT)",
            "$limit/$skip — pagination",
            "$lookup — join with another collection",
            "$unwind — flatten array fields",
            "$addFields — add computed fields"
        ));
        reference.put("aggregationPipeline", aggregationPipeline);

        return ResponseEntity.ok(reference);
    }
}

// ============================================================
// APPLICATION — entry point + data seeder
// ============================================================
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner seedData(StudentRepository studentRepository) {
        return args -> {
            // Clear existing data for a clean run
            studentRepository.deleteAll();

            // Seed 8 students with varied majors, GPAs, tags, and metadata
            List<Student> students = List.of(

                Student.builder()
                    .name("Alice Johnson")
                    .email("alice@academy.edu")
                    .major("Computer Science")
                    .gpa(3.9)
                    .tags(List.of("honors", "scholarship"))
                    .metadata(Map.of("scholarship", true, "advisor", "Prof. Smith"))
                    .address(Address.builder()
                        .street("123 Main St").city("Boston").state("MA").zipCode("02101")
                        .build())
                    .createdAt(LocalDateTime.now().minusDays(30))
                    .build(),

                Student.builder()
                    .name("Bob Martinez")
                    .email("bob@academy.edu")
                    .major("Mathematics")
                    .gpa(3.5)
                    .tags(List.of("honors"))
                    .metadata(Map.of("scholarship", false, "advisor", "Prof. Jones"))
                    .address(Address.builder()
                        .street("456 Oak Ave").city("Cambridge").state("MA").zipCode("02139")
                        .build())
                    .createdAt(LocalDateTime.now().minusDays(25))
                    .build(),

                Student.builder()
                    .name("Carol Kim")
                    .email("carol@academy.edu")
                    .major("Data Science")
                    .gpa(4.0)
                    .tags(List.of("honors", "scholarship", "international"))
                    .metadata(Map.of("scholarship", true, "advisor", "Prof. Chen", "country", "South Korea"))
                    .address(Address.builder()
                        .street("789 Elm Rd").city("Somerville").state("MA").zipCode("02143")
                        .build())
                    .createdAt(LocalDateTime.now().minusDays(20))
                    .build(),

                Student.builder()
                    .name("David Okafor")
                    .email("david@academy.edu")
                    .major("Engineering")
                    .gpa(3.7)
                    .tags(List.of("honors", "part-time"))
                    .metadata(Map.of("scholarship", false, "advisor", "Prof. Patel", "employed", true))
                    .address(Address.builder()
                        .street("321 Pine St").city("Brookline").state("MA").zipCode("02445")
                        .build())
                    .createdAt(LocalDateTime.now().minusDays(18))
                    .build(),

                Student.builder()
                    .name("Eva Rodriguez")
                    .email("eva@academy.edu")
                    .major("Computer Science")
                    .gpa(3.2)
                    .tags(List.of("part-time"))
                    .metadata(Map.of("scholarship", false, "advisor", "Prof. Smith", "employed", true))
                    .address(Address.builder()
                        .street("654 Maple Dr").city("Waltham").state("MA").zipCode("02451")
                        .build())
                    .createdAt(LocalDateTime.now().minusDays(15))
                    .build(),

                Student.builder()
                    .name("Frank Lee")
                    .email("frank@academy.edu")
                    .major("Physics")
                    .gpa(3.8)
                    .tags(List.of("honors", "scholarship", "international"))
                    .metadata(Map.of("scholarship", true, "advisor", "Prof. Nguyen", "country", "Taiwan"))
                    .address(Address.builder()
                        .street("987 Birch Ln").city("Newton").state("MA").zipCode("02458")
                        .build())
                    .createdAt(LocalDateTime.now().minusDays(12))
                    .build(),

                Student.builder()
                    .name("Grace Patel")
                    .email("grace@academy.edu")
                    .major("Data Science")
                    .gpa(2.8)
                    .tags(List.of("part-time"))
                    .metadata(Map.of("scholarship", false, "advisor", "Prof. Chen", "onProbation", true))
                    .address(Address.builder()
                        .street("159 Cedar Ave").city("Malden").state("MA").zipCode("02148")
                        .build())
                    .createdAt(LocalDateTime.now().minusDays(10))
                    .build(),

                Student.builder()
                    .name("Henry Walsh")
                    .email("henry@academy.edu")
                    .major("Mathematics")
                    .gpa(3.6)
                    .tags(List.of("honors", "scholarship"))
                    .metadata(Map.of("scholarship", true, "advisor", "Prof. Jones", "transferStudent", true))
                    .address(Address.builder()
                        .street("753 Walnut St").city("Medford").state("MA").zipCode("02155")
                        .build())
                    .createdAt(LocalDateTime.now().minusDays(5))
                    .build()
            );

            studentRepository.saveAll(students);

            // Print startup banner
            System.out.println("""

                ╔══════════════════════════════════════════════════════════════════════╗
                ║         MongoDB Basics with Spring Data — Sample Application         ║
                ╠══════════════════════════════════════════════════════════════════════╣
                ║  Embedded MongoDB (Flapdoodle) is auto-configured — no install needed║
                ║  8 students seeded into the 'students' collection                    ║
                ╠══════════════════════════════════════════════════════════════════════╣
                ║  STUDENT CRUD                                                        ║
                ║    GET    /api/students                     — all students            ║
                ║    GET    /api/students/{id}                — student by ID           ║
                ║    POST   /api/students                     — create student          ║
                ║    PUT    /api/students/{id}                — update student          ║
                ║    DELETE /api/students/{id}                — delete student          ║
                ╠══════════════════════════════════════════════════════════════════════╣
                ║  SEARCH (Derived Queries & @Query)                                   ║
                ║    GET    /api/students/search?gpa=3.5      — min GPA filter         ║
                ║    GET    /api/students/search/major?major= — by major               ║
                ║    GET    /api/students/search/name?name=   — name contains (i-case) ║
                ║    GET    /api/students/search/tags?tags=   — tags (comma-separated) ║
                ╠══════════════════════════════════════════════════════════════════════╣
                ║  MONGOTEMPLATE QUERIES                                               ║
                ║    GET    /api/students/honors              — GPA≥3.7 + honors tag   ║
                ║    GET    /api/students/scholarship?eligible=true — metadata query   ║
                ╠══════════════════════════════════════════════════════════════════════╣
                ║  AGGREGATION                                                         ║
                ║    GET    /api/students/stats/gpa-by-major  — avg/max GPA by major   ║
                ╠══════════════════════════════════════════════════════════════════════╣
                ║  REFERENCE                                                           ║
                ║    GET    /api/mongodb-reference            — full concept reference  ║
                ╚══════════════════════════════════════════════════════════════════════╝
                """);
        };
    }
}
