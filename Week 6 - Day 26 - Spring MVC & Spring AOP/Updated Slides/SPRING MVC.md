SECTION 1 — Introduction & Roadmap ⏱ 5 min

[SLIDE 1: What We're Covering Today]
SCRIPT:
"Welcome back everyone. Today is one of the most important sessions of this course — Spring MVC. This is the framework that powers the REST APIs you will build in virtually every Spring-based job. By the end of today you'll be able to build a fully structured, production-style REST API from scratch. We're going to cover the architecture, how requests flow through the framework, how to write controllers, how to validate data, how to handle errors properly, how to structure your code into layers, how to safely expose data with DTOs, and how to map between objects with MapStruct. There is a lot here, so let's not waste time."
SLIDE CONTENT:

Spring MVC Architecture & the DispatcherServlet
Controllers: @Controller & @RestController
Request Mapping: @GetMapping, @PostMapping, @PutMapping, @DeleteMapping
Extracting Data: @RequestParam, @PathVariable, @RequestBody
Responses: @ResponseBody, ResponseEntity, HTTP Status Codes
Layered Architecture: Controller → Service → Repository
DTOs vs Entities & Object Mapping (MapStruct / ModelMapper)
Validation with Bean Validation API & Custom Validators
Exception Handling & Error Responses
HTTP Headers & CORS
WebSocket Overview


SECTION 2 — Spring MVC Architecture ⏱ 10 min

[SLIDE 2: What Is Spring MVC?]
SCRIPT:
"Spring MVC is Spring's web framework for building web applications and REST APIs. The 'MVC' stands for Model-View-Controller. That's a design pattern that separates your application into three concerns: the Model holds data, the View renders it, and the Controller handles incoming requests and decides what to do. In traditional web apps, the View was an HTML template. In modern REST APIs, we skip the View entirely and just return data — usually JSON. But the separation of concerns still applies and is just as important. Under the hood, Spring MVC is built on top of the Java Servlet API, so it runs inside a servlet container like Apache Tomcat."
SLIDE CONTENT:

MVC = Model, View, Controller
A design pattern for separating concerns in web applications
Model — holds the data / response payload
View — the rendered output (HTML template, or JSON in REST)
Controller — receives the request, coordinates the response
In REST APIs: View is replaced by JSON/XML serialization
Built on the Java Servlet API → runs on Tomcat, Jetty, Undertow


[SLIDE 3: The Big Picture — Spring MVC Architecture]
SCRIPT:
"Here's the architecture diagram. Every single HTTP request that hits your Spring application is received by one central component called the DispatcherServlet. Everything flows through it. Think of it as the traffic controller for your entire web layer. It receives the request, delegates to the right components, and assembles the response. This is an implementation of the Front Controller design pattern. Let me walk you through each component in this diagram."
SLIDE CONTENT:
         HTTP Request
              │
              ▼
      [ DispatcherServlet ]  ← Front Controller
         /     |      \
        ▼      ▼       ▼
 Handler    Handler   View
 Mapping    Adapter  Resolver*
        \      |
         ▼     ▼
       [ @Controller / @RestController ]
              │
              ▼
          [ @Service ]
              │
              ▼
        [ @Repository ]
              │
              ▼
          [ Database ]
              │
       (back up the chain)
              │
              ▼
    [ HttpMessageConverter ]
       (Object → JSON)
              │
              ▼
         HTTP Response
*ViewResolver only used for traditional MVC (Thymeleaf etc.), not REST APIs

[SLIDE 4: DispatcherServlet — The Front Controller]
SCRIPT:
"The DispatcherServlet is the heart of Spring MVC. It is a single servlet — registered in your application context — that receives every HTTP request. It doesn't handle requests itself. Instead, it delegates to a set of collaborators. The HandlerMapping looks at the URL and HTTP method and figures out which controller method should handle this request. The HandlerAdapter then invokes that method, handling things like converting path variables, request parameters, and request bodies automatically. After the method returns, the HttpMessageConverter serializes the return value — usually using Jackson to produce JSON. In Spring Boot, the DispatcherServlet is auto-configured. You never need to register it manually."
SLIDE CONTENT:
DispatcherServlet responsibilities:

Receive every incoming HTTP request
Ask HandlerMapping → "Which controller method handles this?"
Ask HandlerAdapter → "Call that method, resolving all its parameters"
Receive the return value
Pass to HttpMessageConverter → serialize to JSON/XML
Write the HTTP response

Key collaborators:
ComponentRoleHandlerMappingMaps URL + HTTP method → controller methodHandlerAdapterInvokes the method with resolved argumentsHttpMessageConverterSerializes return value (Jackson → JSON)ViewResolverResolves view names (not used in REST APIs)HandlerExceptionResolverHandles exceptions thrown during processing

In Spring Boot: DispatcherServlet is auto-configured at /


[SLIDE 5: Full Request Processing Lifecycle]
SCRIPT:
"Let me walk you through the exact lifecycle of a request from browser to database and back. Step one: the client sends an HTTP request — let's say GET /api/users/5. Step two: Tomcat receives it and passes it to the DispatcherServlet. Step three: the DispatcherServlet consults the HandlerMapping, which looks at the URL and method and identifies that UserController.getById() should handle it. Step four: the HandlerAdapter calls the method. It sees a @PathVariable Long id parameter and extracts the value 5 from the URL and passes it in. Step five: your controller method runs — it calls the service, which calls the repository, which queries the database. Step six: a UserDTO object is returned. Step seven: Jackson serializes it to a JSON string. Step eight: the DispatcherServlet writes it into the HTTP response with a 200 OK status. That full cycle happens for every single request."
SLIDE CONTENT:
1. Client: GET /api/users/5

2. Tomcat receives request → passes to DispatcherServlet

3. HandlerMapping: "GET /api/users/{id}" → UserController.getById()

4. HandlerAdapter: calls getById(id=5)
   - Resolves @PathVariable id = 5
   - Resolves any @RequestParam, @RequestBody, etc.

5. Controller runs:
   UserController.getById(5)
     → UserService.findById(5)
       → UserRepository.findById(5)
         → SELECT * FROM users WHERE id = 5
       ← Optional<User>
     ← UserDTO
   ← UserDTO

6. Return value: UserDTO { id:5, name:"Alice" }

7. HttpMessageConverter (Jackson):
   UserDTO → { "id": 5, "name": "Alice" }

8. HTTP Response:
   Status: 200 OK
   Content-Type: application/json
   Body: { "id": 5, "name": "Alice" }

SECTION 3 — Controllers ⏱ 12 min

[SLIDE 6: The @Controller Annotation]
SCRIPT:
"To tell Spring that a class handles web requests, you annotate it with @Controller. This is a stereotype annotation — a specialization of @Component — so Spring will automatically detect it through component scanning and register it as a bean. When a method in a @Controller class returns a String, by default Spring interprets that string as the name of a view template to render — like an HTML file if you're using Thymeleaf. This is the traditional Spring MVC model for full-stack web apps. But we're building REST APIs, so we need something different."
SLIDE CONTENT:
java@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("username", "Alice");
        return "home";
        // Spring looks for: src/main/resources/templates/home.html
        // This is traditional MVC with a View
    }

    @GetMapping("/about")
    public String about() {
        return "about"; // renders about.html
    }
}

@Controller = @Component + marks class as web request handler
Return value is a view name by default
Model carries data to the view template
Used for server-side rendered apps (Thymeleaf, JSP, etc.)
Not what we use for REST APIs


[SLIDE 7: @RestController — REST APIs]
SCRIPT:
"For REST APIs, we use @RestController. This annotation is a combination of two things: @Controller and @ResponseBody. The @ResponseBody part is the key — it tells Spring: don't look for a view, instead write the return value directly into the HTTP response body. Jackson takes your Java object and serializes it to JSON automatically. So when you return a UserDTO from a method, the client receives a JSON object. That's it. This is what you will use for virtually every controller you write in this course and in your career."
SLIDE CONTENT:
java@RestController                     // @Controller + @ResponseBody
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable Long id) {
        return userService.findById(id);
        // Spring serializes UserDTO to JSON automatically
        // → { "id": 5, "name": "Alice", "email": "alice@example.com" }
    }

    @GetMapping
    public List<UserDTO> getAll() {
        return userService.findAll();
        // → [ { "id": 1, ... }, { "id": 2, ... } ]
    }
}
@RestController = @Controller + @ResponseBody
@Controller@RestControllerReturn valueView name (String)Object serialized to JSONUse caseServer-side rendered HTMLREST APIsNeeds @ResponseBody?Yes (per method)No (applies to all methods)

[SLIDE 8: @RequestMapping — The Base Annotation]
SCRIPT:
"To map a URL to a controller or method, you use @RequestMapping. This is the base annotation that all the HTTP-verb-specific annotations are built on. You can put it at the class level to define a base URL path that applies to all methods in the class, and then put more specific mappings on each method. @RequestMapping accepts the path, the HTTP method, what media types it consumes, what it produces, and more. In practice though, you'll mostly use the shortcut annotations for specific HTTP methods — which is what the next slide covers."
SLIDE CONTENT:
java// Class-level: sets base path for all methods
@RestController
@RequestMapping("/api/users")
public class UserController {

    // Combined: GET /api/users
    @RequestMapping(method = RequestMethod.GET)
    public List<UserDTO> getAll() { ... }

    // Combined: POST /api/users, only accepts JSON
    @RequestMapping(
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserDTO create(@RequestBody CreateUserDTO dto) { ... }
}
@RequestMapping attributes:

value / path — URL pattern
method — HTTP method (GET, POST, PUT, DELETE, etc.)
consumes — expected request Content-Type
produces — response Content-Type
headers, params — header/param constraints


[SLIDE 9: HTTP Verb Shortcut Annotations]
SCRIPT:
"These are the annotations you'll use every day. Spring provides pre-configured versions of @RequestMapping for each HTTP method. @GetMapping is for reading data — it should never change server state. @PostMapping is for creating a new resource. @PutMapping is for replacing an existing resource entirely. @PatchMapping is for partial updates. @DeleteMapping is for deleting. These aren't just style preferences — they correspond to the HTTP specification and REST conventions. Using the right verb makes your API self-documenting and lets HTTP clients, browsers, and proxies behave correctly."
SLIDE CONTENT:
java@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping               // GET /api/users
    public List<UserDTO> getAll() { ... }

    @GetMapping("/{id}")      // GET /api/users/5
    public UserDTO getById(@PathVariable Long id) { ... }

    @PostMapping              // POST /api/users
    public UserDTO create(@RequestBody CreateUserDTO dto) { ... }

    @PutMapping("/{id}")      // PUT /api/users/5
    public UserDTO replace(@PathVariable Long id,
                           @RequestBody UpdateUserDTO dto) { ... }

    @PatchMapping("/{id}")    // PATCH /api/users/5
    public UserDTO partialUpdate(@PathVariable Long id,
                                 @RequestBody Map<String, Object> updates) { ... }

    @DeleteMapping("/{id}")   // DELETE /api/users/5
    public void delete(@PathVariable Long id) { ... }
}
AnnotationHTTP VerbPurposeChanges State?@GetMappingGETRead / fetchNo@PostMappingPOSTCreateYes@PutMappingPUTFull replaceYes@PatchMappingPATCHPartial updateYes@DeleteMappingDELETEDeleteYes

[SLIDE 10: @PathVariable — Extracting URL Segments]
SCRIPT:
"The first way data gets into your controller is through the URL path itself. When you define a mapping like /users/{id}, the value in the curly braces is a path variable. You extract it with @PathVariable. By default, the variable name in your method parameter must match the name in the curly braces exactly. If they don't match, you can specify the name explicitly. You can also have multiple path variables in a single mapping — for example, /departments/{deptId}/employees/{empId}."
SLIDE CONTENT:
java// Basic path variable — name must match
@GetMapping("/{id}")
public UserDTO getById(@PathVariable Long id) {
    return userService.findById(id);
}
// GET /api/users/42 → id = 42

// Explicit name mapping (when variable name differs)
@GetMapping("/{userId}")
public UserDTO getUser(@PathVariable("userId") Long id) { ... }

// Multiple path variables
@GetMapping("/{deptId}/employees/{empId}")
public EmployeeDTO getEmployee(
        @PathVariable Long deptId,
        @PathVariable Long empId) { ... }
// GET /api/departments/3/employees/7 → deptId=3, empId=7

// Required vs optional
@GetMapping({"/", "/{id}"})
public Object getOrList(
        @PathVariable(required = false) Long id) {
    return id == null ? userService.findAll()
                      : userService.findById(id);
}

Type conversion is automatic (String → Long, Integer, UUID, etc.)
Spring throws MethodArgumentTypeMismatchException on bad type


[SLIDE 11: @RequestParam — Query String Parameters]
SCRIPT:
"The second way data comes in is through query parameters — the key-value pairs after the question mark in a URL, like /users?page=2&size=10&search=alice. You extract these with @RequestParam. You can set a default value, and you can mark parameters as not required. @RequestParam is most commonly used for filtering, sorting, searching, and pagination — things that modify how you read data but don't identify a specific resource. For identifying a specific resource, you use path variables."
SLIDE CONTENT:
java// Required param — 400 error if missing
@GetMapping("/search")
public List<UserDTO> search(@RequestParam String query) { ... }
// GET /api/users/search?query=alice

// Optional with default value
@GetMapping
public Page<UserDTO> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy) { ... }
// GET /api/users → page=0, size=10, sortBy=id
// GET /api/users?page=2&size=5 → page=2, size=5, sortBy=id

// Truly optional (can be null)
@GetMapping
public List<UserDTO> getFiltered(
        @RequestParam(required = false) String role,
        @RequestParam(required = false) Boolean active) { ... }
// GET /api/users → role=null, active=null
// GET /api/users?role=ADMIN → role=ADMIN, active=null

// Multiple values for same key
@GetMapping("/batch")
public List<UserDTO> getMultiple(
        @RequestParam List<Long> ids) { ... }
// GET /api/users/batch?ids=1&ids=2&ids=3
Rule of thumb:

Use @PathVariable to identify which resource: /users/5
Use @RequestParam to filter/sort/paginate: /users?role=ADMIN&page=0


[SLIDE 12: @RequestBody — Deserializing JSON Input]
SCRIPT:
"The third way data comes in is through the request body. When a client sends a POST or PUT request with a JSON payload, you use @RequestBody to deserialize that JSON into a Java object. Jackson handles the deserialization automatically — it matches JSON field names to Java property names. You almost always pair @RequestBody with @Valid to trigger Bean Validation on the incoming object — we'll cover that in the validation section. The class you deserialize into should be a DTO, never an entity — and we'll talk about why in the DTO section."
SLIDE CONTENT:
java// The DTO that maps to the JSON
public class CreateUserDTO {
    private String name;
    private String email;
    private String password;
    // getters/setters or use Lombok @Data
}

// Controller method
@PostMapping
public ResponseEntity<UserDTO> create(
        @RequestBody CreateUserDTO dto) {   // @Valid added later
    UserDTO created = userService.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}

/*
Client sends:
POST /api/users
Content-Type: application/json

{
  "name": "Alice",
  "email": "alice@example.com",
  "password": "secret123"
}

Jackson deserializes this → CreateUserDTO { name="Alice", email="...", password="..." }
*/

Requires Content-Type: application/json header from client
Jackson auto-configured by Spring Boot
Unknown fields are ignored by default
Missing fields are set to null (or default value)


[SLIDE 13: @ResponseBody & Returning Data]
SCRIPT:
"On the output side, @ResponseBody is what tells Spring to write a method's return value directly into the HTTP response body rather than looking for a view. When you use @RestController, this is applied automatically to every method in the class — you don't need to write it yourself. Jackson serializes your return value to JSON. The serialization uses the field names and values of your object by default, but you can customize it with annotations like @JsonProperty, @JsonIgnore, and @JsonFormat."
SLIDE CONTENT:
java// With @RestController — @ResponseBody is implicit
@RestController
public class UserController {

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable Long id) {
        return userService.findById(id);
        // Automatically → { "id": 5, "name": "Alice", "email": "..." }
    }
}

// On a @Controller — must add @ResponseBody explicitly
@Controller
public class ApiController {

    @GetMapping("/data")
    @ResponseBody
    public Map<String, Object> getData() {
        return Map.of("status", "ok", "count", 42);
    }
}

// Jackson customization on DTO fields
public class UserDTO {
    private Long id;

    @JsonProperty("full_name")     // serializes as "full_name" not "name"
    private String name;

    @JsonIgnore                    // excluded from JSON output
    private String internalNote;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
}

[SLIDE 14: ResponseEntity — Full Control Over HTTP Responses]
SCRIPT:
"Returning a plain object from your controller method is fine for simple cases, but a proper REST API needs to control more than just the body. You need to control the HTTP status code, and sometimes you need to add response headers. ResponseEntity is a generic class that wraps your response body, lets you set any HTTP status code, and lets you add arbitrary headers. It's the most expressive way to return an HTTP response from a controller. You should make it a habit to use ResponseEntity in every controller method — it forces you to think about what status code you're returning, which makes your API much more correct and usable."
SLIDE CONTENT:
java// Static factory methods (preferred style)
@GetMapping("/{id}")
public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
    UserDTO user = userService.findById(id);
    return ResponseEntity.ok(user);                        // 200 OK
}

@PostMapping
public ResponseEntity<UserDTO> create(@RequestBody CreateUserDTO dto) {
    UserDTO created = userService.create(dto);
    URI location = URI.create("/api/users/" + created.getId());
    return ResponseEntity.created(location).body(created); // 201 Created + Location header
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();             // 204 No Content
}

// Builder for full control — add custom headers
@PutMapping("/{id}")
public ResponseEntity<UserDTO> update(@PathVariable Long id,
                                      @RequestBody UpdateUserDTO dto) {
    UserDTO updated = userService.update(id, dto);
    return ResponseEntity
            .status(HttpStatus.OK)
            .header("X-Updated-At", LocalDateTime.now().toString())
            .body(updated);
}

// Conditional: return 200 or 404 depending on whether resource exists
@GetMapping("/{id}")
public ResponseEntity<UserDTO> getByIdConditional(@PathVariable Long id) {
    return userService.findOptional(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());    // 404 Not Found
}

[SLIDE 15: HTTP Status Codes You Must Know]
SCRIPT:
"HTTP status codes are part of your API's contract with the client. The wrong status code is a bug. Here are the ones you'll use most often. 200 OK is the default success. 201 Created should be returned when you create a new resource — and you should include a Location header pointing to it. 204 No Content means the operation succeeded but there's nothing to return — typical for DELETE. 400 Bad Request means the client sent invalid data. 401 Unauthorized means the client isn't authenticated. 403 Forbidden means they're authenticated but don't have permission. 404 Not Found means the resource doesn't exist. 409 Conflict means there's a state conflict — like a duplicate email. 422 Unprocessable Entity is used when the request is well-formed but semantically invalid. 500 Internal Server Error means something went wrong on the server."
SLIDE CONTENT:
2xx — Success
CodeNameUse When200OKSuccessful GET, PUT, PATCH201CreatedSuccessful POST (resource created) — include Location header204No ContentSuccessful DELETE or PUT with no body
4xx — Client Error
CodeNameUse When400Bad RequestInvalid input, malformed JSON, failed validation401UnauthorizedNot authenticated403ForbiddenAuthenticated but not authorized404Not FoundResource doesn't exist409ConflictDuplicate resource (e.g. email already exists)422Unprocessable EntitySemantically invalid (business rule violation)
5xx — Server Error
CodeNameUse When500Internal Server ErrorUnexpected server-side failure503Service UnavailableServer temporarily down

Returning the wrong status code is a bug in your API.


SECTION 4 — HTTP Headers ⏱ 4 min

[SLIDE 16: Working with HTTP Headers]
SCRIPT:
"HTTP headers carry metadata about requests and responses. They're separate from the body. Common request headers you'll work with include Authorization for security tokens, Content-Type to declare the format of the request body, and Accept to declare what format the client expects back. On the response side, you'll set Content-Type, Location after creating a resource, and sometimes custom headers. Spring gives you @RequestHeader to read individual request headers, and ResponseEntity to add response headers."
SLIDE CONTENT:
java// Reading a specific request header
@GetMapping("/me")
public UserDTO getMe(
        @RequestHeader("Authorization") String authHeader) {
    String token = authHeader.replace("Bearer ", "");
    return userService.findByToken(token);
}

// Optional header with default
@GetMapping
public List<UserDTO> getAll(
        @RequestHeader(
            value = "X-Api-Version",
            defaultValue = "1"
        ) String version) { ... }

// Reading all headers
@GetMapping("/debug")
public Map<String, String> debugHeaders(
        @RequestHeader Map<String, String> headers) {
    return headers;
}

// Setting response headers via ResponseEntity
@PostMapping
public ResponseEntity<UserDTO> create(@RequestBody CreateUserDTO dto) {
    UserDTO created = userService.create(dto);
    return ResponseEntity.created(URI.create("/api/users/" + created.getId()))
            .header("X-Created-By", "system")
            .body(created);
}
Common Headers:

Content-Type: application/json — body is JSON
Authorization: Bearer <token> — authentication
Accept: application/json — client wants JSON back
Location: /api/users/5 — where the new resource lives (201)


SECTION 5 — Layered Architecture ⏱ 7 min

[SLIDE 17: Why Layer Your Application?]
SCRIPT:
"Before we go further, I want to talk about how you should structure your code. This is not optional — this is one of the most important things to get right. Imagine putting your database query code directly in your controller method. Now try writing a unit test for it. You can't — you need a full HTTP request and a running database to test any logic. Now imagine putting your HTTP request parsing logic in a class that's supposed to contain business rules. When the business rule changes, you have to understand HTTP details to modify it. These are the problems that layered architecture solves. Each layer has exactly one responsibility."
SLIDE CONTENT:
Why layers?

Testability — each layer can be tested in isolation
Maintainability — changes to one layer don't ripple to others
Readability — developers know where to look for specific logic
Reusability — a service can be used by multiple controllers

The three layers:
┌─────────────────────────────────┐
│       Controller Layer          │  Handles HTTP: parsing, routing, responses
│   (@RestController)             │  Knows about: HTTP, DTOs, JSON
│   Knows NOTHING about: JPA      │
├─────────────────────────────────┤
│        Service Layer            │  Business logic: rules, orchestration
│      (@Service)                 │  Knows about: DTOs, Entities, business rules
│   Knows NOTHING about: HTTP     │
├─────────────────────────────────┤
│      Repository Layer           │  Data access: queries, persistence
│      (@Repository)              │  Knows about: Entities, JPA, SQL
│   Knows NOTHING about: HTTP     │
└─────────────────────────────────┘

[SLIDE 18: The Service Layer — @Service]
SCRIPT:
"The Service layer is where your business logic lives. Business logic means the rules of your application — things like: 'a user must have a unique email', 'an order can't be placed if the item is out of stock', 'an admin can delete any user but a regular user can only delete their own account'. None of that belongs in a controller or a repository. The @Service annotation marks a class as a Spring bean in the service layer. It's a semantic annotation that tells other developers — and Spring — what this class is for. Always inject dependencies through the constructor, not with field injection."
SLIDE CONTENT:
java@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // Constructor injection — always preferred
    public UserService(UserRepository userRepository,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("User not found: " + id));
        return userMapper.toDTO(user);
    }

    public UserDTO create(CreateUserDTO dto) {
        // Business rule: email must be unique
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email already in use");
        }
        User user = userMapper.toEntity(dto);
        user.setPasswordHash(hashPassword(dto.getPassword())); // business logic
        User saved = userRepository.save(user);
        return userMapper.toDTO(saved);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }
}

[SLIDE 19: The Repository Layer — @Repository]
SCRIPT:
"The Repository layer is responsible for all data access. Its only job is to read and write data — it contains no business logic. Spring Data JPA makes this incredibly simple. You create an interface that extends JpaRepository, specify your entity and its ID type, and Spring generates the full implementation at runtime — including all the standard CRUD operations. You can also define custom queries using method name conventions or @Query. The @Repository annotation enables Spring's exception translation — it converts database-specific exceptions into Spring's DataAccessException hierarchy."
SLIDE CONTENT:
java@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data generates the implementation from the method name
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(String role);
    List<User> findByActiveTrue();
    List<User> findByNameContainingIgnoreCase(String name);

    // Custom JPQL query
    @Query("SELECT u FROM User u WHERE u.createdAt > :since")
    List<User> findRecentUsers(@Param("since") LocalDateTime since);

    // Native SQL query
    @Query(value = "SELECT * FROM users WHERE email LIKE %:domain",
           nativeQuery = true)
    List<User> findByEmailDomain(@Param("domain") String domain);
}

// JpaRepository gives you for free:
// save(entity), findById(id), findAll(), deleteById(id),
// existsById(id), count(), saveAll(list), deleteAll(), ...

Never put business logic here — only queries
Never expose @Repository to controllers — only to @Service
@Repository enables exception translation (DB exceptions → Spring exceptions)


[SLIDE 20: Wiring the Layers Together]
SCRIPT:
"Let me show you how all three layers connect in a complete example. Notice that the controller only knows about the service and DTOs. It never touches the repository directly. The service knows about the repository and handles all the business rules. The repository only talks to the database. Each layer talks only to the layer directly below it. This is clean, testable architecture."
SLIDE CONTENT:
java// Repository
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}

// Service
@Service
public class UserService {
    private final UserRepository repo;
    private final UserMapper mapper;
    public UserService(UserRepository repo, UserMapper mapper) {
        this.repo = repo; this.mapper = mapper;
    }
    public UserDTO create(CreateUserDTO dto) {
        if (repo.existsByEmail(dto.getEmail()))
            throw new ConflictException("Email taken");
        return mapper.toDTO(repo.save(mapper.toEntity(dto)));
    }
    public UserDTO findById(Long id) {
        return mapper.toDTO(repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Not found")));
    }
}

// Controller
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    public UserController(UserService service) {
        this.service = service;
    }
    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody @Valid CreateUserDTO dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}

SECTION 6 — DTOs vs Entities & Object Mapping ⏱ 10 min

[SLIDE 21: What Are Entities?]
SCRIPT:
"Before we talk about DTOs, let's make sure we're clear on what an Entity is. An Entity is a Java class that maps directly to a database table. You annotate it with @Entity and each field maps to a column. Entities can have relationships to other entities — one-to-many, many-to-many, and so on. The state of an entity is managed by JPA — it can be in a managed state, detached state, and so on. Entities belong entirely in your persistence layer. They are database objects, not API objects."
SLIDE CONTENT:
java@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;   // NEVER expose this

    private String role;
    private boolean active;

    @CreatedDate
    private LocalDateTime createdAt;  // internal concern

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;       // lazy loading, serialization danger
}
Problems with exposing entities directly:

Exposes passwordHash — security vulnerability
orders causes lazy loading exception or infinite recursion in JSON
Exposes internal fields (createdAt, role) clients shouldn't see
Creates tight coupling between API shape and database schema
Any DB change breaks your API contract


[SLIDE 22: DTOs — Data Transfer Objects]
SCRIPT:
"A DTO — Data Transfer Object — is a plain Java class designed specifically for the shape of your API. It contains only the fields the client needs to send or receive. Nothing more. You can have different DTOs for different operations — a CreateUserDTO for creating, an UpdateUserDTO for updating, and a UserDTO for reading. This gives you complete control over what the API exposes. The entity is your internal representation. The DTO is your public contract. Changing the database schema doesn't automatically break your API — you just update the mapping between entity and DTO."
SLIDE CONTENT:
java// DTO for reading a user (response)
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    // No passwordHash, no orders list, no internal fields
}

// DTO for creating a user (request)
public class CreateUserDTO {
    private String name;
    private String email;
    private String password;   // raw password, not the hash
    // No id — that's assigned by the server
}

// DTO for updating a user (request)
public class UpdateUserDTO {
    private String name;       // only the fields that can change
    // No email (not allowed to change), no password (separate flow)
}
Golden Rule: Entities go IN, DTOs come OUT
Client → JSON → CreateUserDTO → [map] → User (Entity) → Database
Database → User (Entity) → [map] → UserDTO → JSON → Client
Benefits:

Security: never accidentally expose sensitive fields
Flexibility: API and DB can evolve independently
Clarity: DTO shape documents your API exactly


[SLIDE 23: Object Mapping — The Problem]
SCRIPT:
"Once you have DTOs and entities, you need code to convert between them. This is called object mapping. The problem is that this code is tedious and error-prone to write by hand for every class. If your entity has 20 fields and your DTO has 15 of them, you need 15 setter calls. Multiply that by every entity in your application and you have hundreds of lines of repetitive code. There are three main strategies to deal with this: manual mapping, MapStruct, and ModelMapper. Let me compare them."
SLIDE CONTENT:
java// The mapping problem — you need this for every entity in your app

public UserDTO toDTO(User user) {
    UserDTO dto = new UserDTO();
    dto.setId(user.getId());
    dto.setName(user.getName());
    dto.setEmail(user.getEmail());
    // ... and so on for every field ...
    return dto;
}

public User toEntity(CreateUserDTO dto) {
    User user = new User();
    user.setName(dto.getName());
    user.setEmail(dto.getEmail());
    // ... and so on ...
    return user;
}
// And you need this for Product, Order, Customer, Invoice...
3 strategies:
ManualMapStructModelMapperCode generationNoneCompile-time ✅Runtime (reflection)PerformanceFastFastest ✅SlowestType safetyCompile-time ✅Compile-time ✅RuntimeError detectionCompile-timeCompile-time ✅Runtime (silent!)VerbosityHighLow ✅LowestRecommended forTiny projects✅ ProductionRapid prototyping

[SLIDE 24: Manual Mapping]
SCRIPT:
"Manual mapping is simple: you write a mapper class with methods that copy field values from one object to another. The advantage is total transparency — anyone reading the code sees exactly what happens. The disadvantage is verbosity and the risk of forgetting to update the mapper when you add a field. For small projects or one-off mappings, it's perfectly acceptable. For anything larger, you want MapStruct."
SLIDE CONTENT:
java@Component   // Make it a Spring bean so you can inject it
public class UserMapper {

    // Entity → Response DTO
    public UserDTO toDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    // Create DTO → Entity
    public User toEntity(CreateUserDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        // Note: passwordHash set separately in service (hashing logic)
        return user;
    }

    // List mapping
    public List<UserDTO> toDTOList(List<User> users) {
        return users.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
    }
}
Pros: transparent, no dependencies, easy to debug
Cons: verbose, error-prone (forgetting new fields), lots of boilerplate

[SLIDE 25: MapStruct — How It Works]
SCRIPT:
"MapStruct is the industry standard solution for object mapping in Java. Here's the core idea: you write an interface with the mapping method signatures you need. At compile time, MapStruct's annotation processor reads that interface and generates the full implementation — the same tedious setter code you'd write by hand, but done for you automatically. Because the generation happens at compile time, if a field doesn't exist or a type is incompatible, you get a compile error — not a runtime surprise. The generated code is plain Java with no reflection, so it's as fast as hand-written mapping."
SLIDE CONTENT:
xml<!-- pom.xml dependency -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
java// Step 1 — You write the interface:
@Mapper(componentModel = "spring")   // makes it a Spring bean automatically
public interface UserMapper {

    UserDTO toDTO(User user);
    User toEntity(CreateUserDTO dto);
    List<UserDTO> toDTOList(List<User> users);
}

// Step 2 — MapStruct GENERATES this at compile time:
// public class UserMapperImpl implements UserMapper {
//     public UserDTO toDTO(User user) {
//         if (user == null) return null;
//         UserDTO dto = new UserDTO();
//         dto.setId(user.getId());
//         dto.setName(user.getName());
//         dto.setEmail(user.getEmail());
//         return dto;
//     }
//     // ... toEntity and toDTOList also generated ...
// }

// Step 3 — Inject and use it like any Spring bean:
@Service
public class UserService {
    private final UserRepository repo;
    private final UserMapper mapper;   // Spring injects the generated impl

    public UserService(UserRepository repo, UserMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public UserDTO findById(Long id) {
        User user = repo.findById(id).orElseThrow();
        return mapper.toDTO(user);   // one line — no boilerplate
    }

    public List<UserDTO> findAll() {
        return mapper.toDTOList(repo.findAll());
    }
}

You write 3 method signatures. MapStruct writes the 30 lines of setter code.


[SLIDE 26: MapStruct — Customizing Field Mappings]
SCRIPT:
"For simple cases where field names match, MapStruct needs zero configuration. But you'll regularly hit situations where the names differ, you want to ignore a field, or you need a calculated value. MapStruct handles all of this with annotations on the interface methods. @Mapping is the workhorse — it lets you rename fields, ignore them, or inject any Java expression as the value. You can have as many @Mapping annotations as you need on a single method."
SLIDE CONTENT:
java@Mapper(componentModel = "spring")
public interface UserMapper {

    // Ignore a field — don't try to map passwordHash from the DTO
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(CreateUserDTO dto);

    // Rename: source field "fullName" → target field "name"
    @Mapping(source = "fullName", target = "name")
    UserDTO toDTO(User user);

    // Computed value: combine two source fields into one target field
    @Mapping(
        expression = "java(user.getFirstName() + ' ' + user.getLastName())",
        target = "name"
    )
    UserDTO toDTOCombined(User user);

    // Multiple @Mapping annotations on one method
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(source = "dto.phoneNumber", target = "phone")
    User toEntity(CreateUserDTO dto);
}
Key rules:

Fields with matching names are mapped automatically — no annotation needed
Fields that exist in source but not in target are silently ignored
Fields that exist in target but not in source will be null unless you map them
MapStruct reports unmapped target properties as warnings — treat them as errors in production


[SLIDE 27: ModelMapper — Awareness & Trade-offs]
SCRIPT:
"You should also be aware of ModelMapper. ModelMapper uses reflection at runtime to automatically match fields between source and destination objects by name. The appeal is obvious — you just call mapper.map(source, Destination.class) and it does everything. No interface to write. No setup per class. The problem is that it does this matching at runtime, which means if fields don't match up, you won't find out until your application is running. It can silently skip fields with no warning. It's also slower because of the reflection overhead. ModelMapper is fine for prototyping or very simple projects. For anything production, MapStruct's compile-time safety is worth the minimal extra setup."
SLIDE CONTENT:
java// ModelMapper dependency
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>3.1.1</version>
</dependency>

// Configuration
@Bean
public ModelMapper modelMapper() {
    return new ModelMapper();
}

// Usage — minimal code
@Service
public class UserService {
    @Autowired
    private ModelMapper modelMapper;

    public UserDTO findById(Long id) {
        User user = repo.findById(id).orElseThrow();
        return modelMapper.map(user, UserDTO.class);  // one call, no interface
    }
}
ModelMapper vs MapStruct:
ModelMapperMapStructSetup per classNoneInterface methodError detectionRuntime (silent!)Compile-time ✅PerformanceSlower (reflection)Fast (generated code) ✅DebuggingHarderEasier (generated code is readable)Field mismatchSilently skipped ⚠️Compile error ✅

Recommendation: Use MapStruct in production. Be aware of ModelMapper. Don't choose it just because it's less code upfront — a silent field mismatch is a hard bug to track down.


SECTION 7 — Validation ⏱ 8 min

[SLIDE 28: Why Validate at the Boundary?]
SCRIPT:
"Never trust data from a client. The client can be anyone — a browser, a mobile app, another service, or a malicious actor. Before your business logic even runs, you should validate that the data you received is structurally correct — required fields are present, strings aren't too long, numbers are in range, emails look like emails. Spring integrates with the Bean Validation API — also known as Jakarta Validation — which lets you put constraint annotations directly on your DTO fields. Then adding @Valid to your @RequestBody parameter tells Spring: validate this object before passing it to my method. If validation fails, Spring throws a MethodArgumentNotValidException and returns a 400 automatically."
SLIDE CONTENT:
java// Add to pom.xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

// Annotate the DTO
public class CreateUserDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be 2–50 characters")
    private String name;

    @NotBlank
    @Email(message = "Must be a valid email address")
    private String email;

    @NotNull
    @Size(min = 8, max = 128, message = "Password must be 8–128 characters")
    private String password;

    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 150, message = "Age seems unrealistic")
    private Integer age;
}

// Add @Valid in controller
@PostMapping
public ResponseEntity<UserDTO> create(
        @RequestBody @Valid CreateUserDTO dto) {
    // Only reached if ALL constraints pass
    return ResponseEntity.status(201).body(service.create(dto));
}
// If validation fails → 400 Bad Request (automatic, no extra code needed)

[SLIDE 29: Bean Validation Annotations — Null & String Checks]
SCRIPT:
"Let me give you a reference of the Bean Validation annotations you'll use most often. First, the most common source of confusion: the difference between @NotNull, @NotEmpty, and @NotBlank. These are not interchangeable. @NotNull only checks that the value isn't null — an empty string passes. @NotEmpty rejects null and empty strings, but a string of spaces passes. @NotBlank is the strictest — it rejects null, empty, and whitespace-only strings. For user-facing text fields, @NotBlank is almost always what you want. For strings, @Size controls length, @Email validates the format, and @Pattern lets you write your own regex."
SLIDE CONTENT:
Null / Blank checks — know the difference:
AnnotationNull?Empty String ""?Whitespace "   "?@NotNull❌ Fails✅ Passes✅ Passes@NotEmpty❌ Fails❌ Fails✅ Passes@NotBlank❌ Fails❌ Fails❌ Fails

For text fields entered by users, always use @NotBlank

String constraints:
java@Size(min = 2, max = 50)            // controls character length
@Email                               // validates email format
@Pattern(regexp = "^[A-Z]{2}$")     // custom regex match

[SLIDE 30: Bean Validation Annotations — Numbers, Dates & Collections]
SCRIPT:
"For numbers, @Min and @Max check that a value is within a range. @Positive and @PositiveOrZero are shortcuts for values greater than zero or at least zero. For dates, @Past ensures the date is before today — useful for birthdays — and @Future ensures it's after today — useful for booking dates. For collections and lists, @Size works the same way it does for strings, and @NotEmpty ensures the list has at least one item. Finally, if you have a nested DTO as a field, you must put @Valid on that field to tell Spring to recursively validate it — otherwise the nested constraints are ignored."
SLIDE CONTENT:
Number constraints:
java@Min(1)                  // value must be >= 1
@Max(100)                // value must be <= 100
@Positive                // value must be > 0
@PositiveOrZero          // value must be >= 0
@Negative                // value must be < 0
@DecimalMin("0.01")      // minimum for BigDecimal
Date constraints:
java@Past            // date must be before today (e.g. birthDate)
@PastOrPresent   // date must be today or earlier
@Future          // date must be after today (e.g. bookingDate)
@FutureOrPresent // date must be today or later
Collection constraints:
java@Size(min = 1, max = 10)   // collection must have 1–10 elements
@NotEmpty                   // collection must not be empty
Nested DTO validation:
javapublic class OrderDTO {

    @Valid       // ← REQUIRED to trigger validation on the nested object
    @NotNull
    private AddressDTO shippingAddress;
}
// Without @Valid, constraints on AddressDTO fields are silently ignored

[SLIDE 31: Custom Validators]
SCRIPT:
"Sometimes the built-in constraints aren't enough. Maybe you need to validate that a username doesn't contain profanity, or that a product code follows a specific internal format, or that a phone number is valid for a specific country. For this, you create a custom validator. It's a two-part process. First, you create a custom annotation that carries the constraint. Second, you create a class that implements ConstraintValidator — which contains the actual validation logic. Then you use your annotation just like any other Bean Validation annotation."
SLIDE CONTENT:
java// Step 1: Create the annotation
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface ValidPhone {
    String message() default "Invalid phone number format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// Step 2: Implement the validator
@Component
public class PhoneNumberValidator
        implements ConstraintValidator<ValidPhone, String> {

    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^\\+?[1-9]\\d{1,14}$");

    @Override
    public boolean isValid(String phone,
                           ConstraintValidatorContext context) {
        if (phone == null) return true; // null handled by @NotNull
        return PHONE_PATTERN.matcher(phone).matches();
    }
}

// Step 3: Use it just like any other constraint
public class CreateUserDTO {
    @NotBlank
    private String name;

    @Email
    private String email;

    @ValidPhone
    private String phoneNumber;
}

Custom validators can be Spring beans — you can inject a repository to check database uniqueness (e.g. @UniqueEmail).


SECTION 8 — Exception Handling ⏱ 7 min

[SLIDE 32: The Problem with Unhandled Exceptions]
SCRIPT:
"What happens right now if a user requests a user that doesn't exist? Without custom exception handling, Spring returns a generic 500 error or a Whitelabel Error Page with a Java stack trace. That is completely unacceptable for a production API. The client receives no useful information, and you've potentially leaked internal implementation details. A good API returns a structured, consistent error response with the right HTTP status code and a meaningful message. Let's build that."
SLIDE CONTENT:
Without exception handling — what the client actually receives:
json{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/api/users/999"
}
// Or worse — a full Java stack trace in the response body
What we want for resource errors:
json{
  "status": 404,
  "message": "User not found with id: 999",
  "timestamp": "2024-01-15T10:30:00",
  "path": "/api/users/999"
}
What we want for validation errors:
json{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "name": "Name is required",
    "email": "Must be a valid email address"
  }
}

Both formats are consistent — clients write one error-handling routine that handles everything.


[SLIDE 33: Custom Exception Classes]
SCRIPT:
"The first step is to define meaningful exception classes. Rather than throwing a generic RuntimeException everywhere, create specific exception types that describe the problem. This makes your service code readable — when you throw ResourceNotFoundException, anyone reading the code knows exactly what's happening. It also lets your exception handler treat different exceptions differently — a ResourceNotFoundException should produce a 404, a ConflictException should produce a 409."
SLIDE CONTENT:
java// 404 — resource not found
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    // Convenience constructor
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id);
    }
}

// 409 — duplicate / conflict
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

// 422 — business rule violation
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
Used in service — reads clearly:
javapublic UserDTO findById(Long id) {
    return userRepository.findById(id)
        .map(userMapper::toDTO)
        .orElseThrow(() -> new ResourceNotFoundException("User", id));
        // throws: "User not found with id: 5"
}

[SLIDE 34: The Error Response DTO]
SCRIPT:
"Next, create a consistent structure for all error responses. Every error your API returns should look the same — same fields, same format. Clients can then write one piece of error-handling code that works for every endpoint. Include the HTTP status, a human-readable message, a timestamp, the request path, and optionally a map of field-level errors for validation failures."
SLIDE CONTENT:
javapublic class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, String> fieldErrors;  // for validation errors only

    // General error constructor
    public ErrorResponse(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    // Validation error constructor
    public ErrorResponse(int status, String message, String path,
                         Map<String, String> fieldErrors) {
        this(status, message, path);
        this.fieldErrors = fieldErrors;
    }
    // getters...
}
What both error types look like to the client:
json// 404
{ "status": 404, "message": "User not found: 5",
  "timestamp": "2024-01-15T10:30:00", "path": "/api/users/5" }

// 400 Validation
{ "status": 400, "message": "Validation failed",
  "timestamp": "2024-01-15T10:30:00", "path": "/api/users",
  "fieldErrors": { "email": "Must be valid", "name": "Required" } }

[SLIDE 35: @ControllerAdvice — Global Exception Handler]
SCRIPT:
"Now we wire it all together with @ControllerAdvice. This annotation tells Spring: this class handles exceptions thrown from any controller in the application. It's a global catch block for your entire web layer. Combined with @ExceptionHandler, you map specific exception types to specific handler methods. Each handler builds an ErrorResponse and returns a ResponseEntity. Use @RestControllerAdvice which combines @ControllerAdvice with @ResponseBody so you don't need to add that annotation separately."
SLIDE CONTENT:
java@RestControllerAdvice  // = @ControllerAdvice + @ResponseBody
public class GlobalExceptionHandler {

    // 404 — resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, ex.getMessage(),
                                        request.getRequestURI()));
    }

    // 409 — conflict / duplicate
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, ex.getMessage(),
                                        request.getRequestURI()));
    }

    // 400 — @Valid validation failures
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
          .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(400, "Validation failed",
                                        request.getRequestURI(), errors));
    }

    // 500 — catch-all for anything unexpected
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {
        // Log the full exception internally — never expose the stack trace
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "An unexpected error occurred",
                                        request.getRequestURI()));
    }
}

SECTION 9 — CORS ⏱ 4 min

[SLIDE 36: What Is CORS and Why Does It Matter?]
SCRIPT:
"CORS stands for Cross-Origin Resource Sharing. Here's the problem it addresses: browsers have a security policy called the Same-Origin Policy, which blocks JavaScript code on one origin from making requests to a different origin. An 'origin' is the combination of protocol, hostname, and port. So if your frontend is at http://localhost:3000 and your API is at http://localhost:8080, the browser blocks the request because the ports are different — they're different origins. This is a browser enforcement, not a server enforcement — curl and Postman ignore it. To allow the browser to make the request, your server needs to respond with specific CORS headers telling the browser it's allowed."
SLIDE CONTENT:
What triggers CORS:

Frontend: http://localhost:3000 → API: http://localhost:8080 ❌ (different port)
Frontend: https://myapp.com → API: https://api.myapp.com ❌ (different subdomain)
Frontend: http://myapp.com → API: https://myapp.com ❌ (different protocol)
Frontend: https://myapp.com → API: https://myapp.com ✅ (same origin)

How it works:

Browser sends a preflight OPTIONS request to the API
Server responds with Access-Control-Allow-Origin header
If the origin is allowed, browser proceeds with the real request
If not, browser blocks the request (your JS code gets an error)


CORS is browser enforcement only — Postman and curl are unaffected


[SLIDE 37: Configuring CORS in Spring MVC]
SCRIPT:
"Spring gives you two levels of CORS configuration. At the method or class level, you can use @CrossOrigin to allow specific origins for that controller. For a global configuration that applies to all controllers, implement WebMvcConfigurer and override addCorsMappings. The global approach is almost always what you want — you don't want to remember to add @CrossOrigin to every controller. In development, you might allow all origins with a wildcard, but in production, always specify exactly which origins are allowed. Never use allowedOrigins("*") with allowCredentials(true) — the browser will block it anyway because those two settings are incompatible."
SLIDE CONTENT:
java// Option 1: Class or method level
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/users")
public class UserController { ... }

// Option 2: Global configuration (preferred)
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:3000",    // local dev
                    "https://www.myapp.com"     // production
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Location", "X-Custom-Header")
                .allowCredentials(true)         // allows cookies/auth headers
                .maxAge(3600);                  // cache preflight for 1hr
    }
}
Rules:

Development: allowedOrigins("*") is acceptable
Production: always list specific origins explicitly
allowCredentials(true) + allowedOrigins("*") = invalid combination


SECTION 10 — WebSocket Overview ⏱ 1 min

[SLIDE 38: WebSocket — When HTTP Isn't Enough]
SCRIPT:
"Quick mention of WebSocket before we wrap up, because you'll hit it eventually. Standard HTTP is request-response — the client always initiates. WebSocket is a persistent, bidirectional channel where either side can send messages at any time without waiting. This is what you use for live chat, real-time notifications, collaborative editing, or live dashboards. Spring has full WebSocket and STOMP support. We're not going into it today — it gets a dedicated session. Just know what problem it solves: if you ever find yourself polling an endpoint every second to check for updates, that's a sign you need WebSocket instead."
SLIDE CONTENT:
HTTP (Request-Response) — client always initiates:
Client → GET /notifications → Server
Client ← 200 OK (empty)    ← Server
(connection closed — client must ask again)
WebSocket (Persistent Bidirectional):
Client → HTTP Upgrade → Server
Client ← 101 Switching ← Server
[connection stays open]
Client →  "hello"         → Server
Server → "new message"    → Client  ← server can push any time
Use when you need:

💬 Live chat
🔔 Real-time push notifications
📊 Live dashboards
🤝 Collaborative editing (Google Docs-style)


Full deep-dive in a dedicated session.


SECTION 11 — Putting It All Together ⏱ 5 min

[SLIDE 39: Complete Request Flow Diagram]
SCRIPT:
"Let's put everything we've covered today into one complete picture. Trace a POST request to create a new user from start to finish. The client sends a JSON body. The DispatcherServlet receives it and routes it to UserController.create(). Spring deserializes the JSON into a CreateUserDTO and runs Bean Validation. If validation fails, GlobalExceptionHandler returns a 400 with field error details. If validation passes, the controller calls UserService.create(). The service applies business rules — checking for duplicate email — then uses MapStruct to map the DTO to a User entity. It calls UserRepository.save(), gets the saved entity back, maps it to a UserDTO, and returns it. The controller wraps it in a ResponseEntity with status 201 and a Location header. Jackson serializes it to JSON. Done."
SLIDE CONTENT:
POST /api/users
Body: { "name": "Alice", "email": "alice@ex.com", "password": "secret1!" }
      │
      ▼
DispatcherServlet
      │
      ▼
HandlerMapping → UserController.create()
      │
      ▼
@RequestBody → CreateUserDTO
@Valid → Bean Validation
   │
   ├─ FAIL → MethodArgumentNotValidException
   │          → GlobalExceptionHandler
   │          → 400 { fieldErrors: {...} }
   │
   └─ PASS
         │
         ▼
   UserController.create(dto)
         │
         ▼
   UserService.create(dto)
         ├─ Check: email unique?
         │    └─ No → ConflictException → GlobalExceptionHandler → 409
         ├─ UserMapper.toEntity(dto) → User
         ├─ Hash password
         ├─ UserRepository.save(user) → User (with id=5)
         ├─ UserMapper.toDTO(saved) → UserDTO
         └─ return UserDTO
         │
         ▼
   ResponseEntity.created("/api/users/5").body(userDTO)
         │
         ▼
   Jackson → { "id": 5, "name": "Alice", "email": "alice@ex.com" }
         │
         ▼
HTTP 201 Created
Location: /api/users/5
Body: { "id": 5, "name": "Alice", "email": "alice@ex.com" }

[SLIDE 40: Package Structure — How to Organize Your Code]
SCRIPT:
"I want to leave you with the recommended package structure. This is what production Spring MVC projects look like. Organize by feature, not by layer. Each feature — users, products, orders — gets its own package containing its controller, service, repository, entities, DTOs, and mapper. Common things like exception handling, configuration, and shared utilities live in their own packages. This makes it easy to find everything related to a feature in one place, and easy to work on a feature without navigating all over the project."
SLIDE CONTENT:
src/main/java/com/example/myapp/
│
├── user/
│   ├── UserController.java         (@RestController)
│   ├── UserService.java            (@Service)
│   ├── UserRepository.java         (@Repository)
│   ├── User.java                   (@Entity)
│   ├── UserDTO.java                (response DTO)
│   ├── CreateUserDTO.java          (request DTO)
│   ├── UpdateUserDTO.java          (request DTO)
│   └── UserMapper.java             (@Mapper - MapStruct)
│
├── product/
│   ├── ProductController.java
│   ├── ProductService.java
│   ├── ProductRepository.java
│   ├── Product.java
│   ├── ProductDTO.java
│   └── ...
│
├── exception/
│   ├── GlobalExceptionHandler.java (@RestControllerAdvice)
│   ├── ResourceNotFoundException.java
│   ├── ConflictException.java
│   └── ErrorResponse.java
│
├── config/
│   └── WebConfig.java              (CORS, MVC config)
│
└── MyAppApplication.java           (@SpringBootApplication)

[SLIDE 41: Key Takeaways]
SCRIPT:
"Here are the things I need you to walk away knowing today. First: every request goes through the DispatcherServlet — understand that flow. Second: use @RestController for REST APIs. Third: use the right HTTP verb annotation and the right status code — they're part of your API's contract. Fourth: always use DTOs — never expose entities directly. Fifth: use MapStruct for object mapping in any serious project. Sixth: validate at the boundary with Bean Validation before your business logic runs. Seventh: handle all exceptions globally with @ControllerAdvice and return structured error responses. Eighth: keep your layers clean — controllers handle HTTP, services handle business logic, repositories handle data. For your lab today, you're going to build a complete CRUD API applying every one of these concepts."
SLIDE CONTENT:
Must remember:

Every request flows through DispatcherServlet
@RestController = @Controller + @ResponseBody
Use correct HTTP verb annotations and status codes (201, 204, 400, 404, 409...)
Never expose entities from your API — always use DTOs
MapStruct for compile-time safe, fast object mapping
@Valid triggers Bean Validation on request bodies
@RestControllerAdvice for centralized, consistent error responses
Controller → Service → Repository — keep layers pure
ResponseEntity for full control over the HTTP response
Configure CORS globally in WebMvcConfigurer

Lab Assignment:
Build a full CRUD REST API for a Product resource:

Entity, DTOs (response + create + update), MapStruct mapper
Controller with all 5 endpoints + correct status codes
Service with business logic (unique SKU check)
Repository with a custom query
Bean Validation on create/update DTOs
GlobalExceptionHandler with structured error responses
CORS configuration for localhost:3000