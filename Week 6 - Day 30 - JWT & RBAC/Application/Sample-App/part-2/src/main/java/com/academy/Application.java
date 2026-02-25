package com.academy;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Day 30 — Part 2: Role-Based Access Control (RBAC) & Method Security
 * =====================================================================
 * Run: mvn spring-boot:run
 *
 * HOW TO TEST:
 *   Step 1 — Login as different roles:
 *     POST /api/auth/login  {"username":"alice","password":"password"}   → STUDENT
 *     POST /api/auth/login  {"username":"bob","password":"password"}     → INSTRUCTOR
 *     POST /api/auth/login  {"username":"carol","password":"admin"}      → ADMIN
 *
 *   Step 2 — Try role-restricted endpoints:
 *     GET /api/courses          → all roles
 *     POST /api/courses         → INSTRUCTOR or ADMIN only
 *     DELETE /api/courses/1    → ADMIN only
 *
 * Topics: @PreAuthorize, @PostAuthorize, @Secured, @EnableMethodSecurity,
 *         RBAC, SecurityContextHolder, method-level security expressions
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// ── Model ─────────────────────────────────────────────────────────────────────
class Course {
    public Long id;
    public String title;
    public String instructorName;
    public int maxStudents;

    public Course(Long id, String title, String instructorName, int maxStudents) {
        this.id = id; this.title = title;
        this.instructorName = instructorName; this.maxStudents = maxStudents;
    }
}

// ── JWT Utility (same as Part 1) ──────────────────────────────────────────────
@Component
class JwtUtil {
    private final SecretKey secretKey;
    private final long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
            .subject(username).claim("roles", roles)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(secretKey).compact();
    }

    public boolean validateToken(String token) {
        try { Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token); return true; }
        catch (JwtException | IllegalArgumentException e) { return false; }
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
            .parseSignedClaims(token).getPayload().getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object roles = Jwts.parser().verifyWith(secretKey).build()
            .parseSignedClaims(token).getPayload().get("roles");
        return roles instanceof List ? (List<String>) roles : List.of();
    }
}

// ── JWT Filter (same as Part 1) ───────────────────────────────────────────────
@Component
class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService uds;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsService uds) {
        this.jwtUtil = jwtUtil; this.uds = uds;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                UserDetails user = uds.loadUserByUsername(jwtUtil.extractUsername(token));
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
            }
        }
        chain.doFilter(req, res);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api/auth");
    }
}

// ── Security Config with @EnableMethodSecurity ────────────────────────────────
@EnableWebSecurity
@EnableMethodSecurity  // Enables @PreAuthorize, @PostAuthorize, @Secured
class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) { this.jwtAuthFilter = jwtAuthFilter; }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/rbac-reference").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(c -> c.disable()).httpBasic(b -> b.disable()).formLogin(f -> f.disable())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder enc) {
        var m = new InMemoryUserDetailsManager();
        // STUDENT: can view courses
        m.createUser(User.withUsername("alice").password(enc.encode("password")).roles("STUDENT").build());
        // INSTRUCTOR: can create courses
        m.createUser(User.withUsername("bob").password(enc.encode("password")).roles("INSTRUCTOR").build());
        // ADMIN: full access
        m.createUser(User.withUsername("carol").password(enc.encode("admin")).roles("STUDENT","INSTRUCTOR","ADMIN").build());
        return m;
    }

    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}

// ── Course Service with Method-Level Security ─────────────────────────────────
@Service
class CourseService {
    private final Map<Long, Course> courses = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public CourseService() {
        courses.put(1L, new Course(1L, "Java Fundamentals", "bob", 30));
        courses.put(2L, new Course(2L, "Spring Boot",       "bob", 25));
        courses.put(3L, new Course(3L, "React Basics",      "carol", 20));
    }

    @PreAuthorize("hasAnyRole('STUDENT','INSTRUCTOR','ADMIN')")
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses.values());
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public Course createCourse(Course course) {
        long id = idGen.incrementAndGet();
        course.id = id;
        courses.put(id, course);
        return course;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCourse(Long id) {
        if (!courses.containsKey(id)) throw new IllegalArgumentException("Course not found: " + id);
        courses.remove(id);
    }

    // @PostAuthorize — runs AFTER method, checks returned value
    @PostAuthorize("returnObject.instructorName == authentication.name or hasRole('ADMIN')")
    public Course getCourseSecure(Long id) {
        return courses.getOrDefault(id, null);
    }

    // @PreAuthorize with parameter — user can only view their own profile
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public Map<String, Object> getProfile(String username) {
        return Map.of("username", username, "message", "Profile for " + username);
    }

    // @Secured — older, simpler annotation
    @Secured({"ROLE_ADMIN"})
    public Map<String, Object> getSystemStats() {
        return Map.of("totalCourses", courses.size(), "activeUsers", 3);
    }
}

// ── Auth Controller ────────────────────────────────────────────────────────────
@RestController
@RequestMapping("/api/auth")
class AuthController {
    private final JwtUtil jwtUtil;
    private final UserDetailsService uds;
    private final PasswordEncoder enc;

    public AuthController(JwtUtil jwtUtil, UserDetailsService uds, PasswordEncoder enc) {
        this.jwtUtil = jwtUtil; this.uds = uds; this.enc = enc;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> creds) {
        try {
            UserDetails user = uds.loadUserByUsername(creds.get("username"));
            if (!enc.matches(creds.get("password"), user.getPassword()))
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            List<String> roles = user.getAuthorities().stream().map(a -> a.getAuthority()).toList();
            String token = jwtUtil.generateToken(user.getUsername(), roles);
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("token", token); r.put("username", user.getUsername());
            r.put("roles", roles); r.put("type", "Bearer");
            return ResponseEntity.ok(r);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }
}

// ── Course Controller ─────────────────────────────────────────────────────────
@RestController
@RequestMapping("/api")
class CourseController {
    private final CourseService courseService;
    public CourseController(CourseService courseService) { this.courseService = courseService; }

    @GetMapping("/courses")
    public ResponseEntity<?> getAll() {
        try { return ResponseEntity.ok(courseService.getAllCourses()); }
        catch (AccessDeniedException e) { return ResponseEntity.status(403).body(Map.of("error","Access denied")); }
    }

    @PostMapping("/courses")
    public ResponseEntity<?> create(@RequestBody Course course) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            course.instructorName = auth.getName();
            return ResponseEntity.status(201).body(courseService.createCourse(course));
        } catch (AccessDeniedException e) { return ResponseEntity.status(403).body(Map.of("error","Requires INSTRUCTOR or ADMIN role")); }
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try { courseService.deleteCourse(id); return ResponseEntity.noContent().build(); }
        catch (AccessDeniedException e) { return ResponseEntity.status(403).body(Map.of("error","Requires ADMIN role")); }
        catch (IllegalArgumentException e) { return ResponseEntity.notFound().build(); }
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<?> profile(@PathVariable String username) {
        try { return ResponseEntity.ok(courseService.getProfile(username)); }
        catch (AccessDeniedException e) { return ResponseEntity.status(403).body(Map.of("error","Can only view your own profile")); }
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<?> stats() {
        try { return ResponseEntity.ok(courseService.getSystemStats()); }
        catch (AccessDeniedException e) { return ResponseEntity.status(403).body(Map.of("error","Admin only")); }
    }
}

// ── RBAC Reference Controller ──────────────────────────────────────────────────
@RestController
@RequestMapping("/api")
class RbacReferenceController {

    @GetMapping("/rbac-reference")
    public Map<String, Object> reference() {
        Map<String, Object> ref = new LinkedHashMap<>();
        ref.put("topic", "RBAC & Method-Level Security");

        Map<String, String> annotations = new LinkedHashMap<>();
        annotations.put("@EnableMethodSecurity", "Enable on @Configuration class — activates method-level security");
        annotations.put("@PreAuthorize",  "Runs BEFORE method — most flexible, supports SpEL expressions");
        annotations.put("@PostAuthorize", "Runs AFTER method — can check return value");
        annotations.put("@Secured",       "Simpler, older — just lists roles, no SpEL");
        ref.put("annotations", annotations);

        Map<String, String> spel = new LinkedHashMap<>();
        spel.put("hasRole('ADMIN')",              "User has ROLE_ADMIN");
        spel.put("hasAnyRole('USER','ADMIN')",     "User has either role");
        spel.put("isAuthenticated()",             "Any logged-in user");
        spel.put("isAnonymous()",                 "Not logged in");
        spel.put("#param == authentication.name", "#param is a method parameter name");
        spel.put("returnObject.owner == auth.name","@PostAuthorize: check returned object field");
        ref.put("spelExpressions", spel);

        Map<String, String> comparison = new LinkedHashMap<>();
        comparison.put("URL_security",    "requestMatchers().hasRole() — coarse-grained, in SecurityConfig");
        comparison.put("method_security", "@PreAuthorize on service methods — fine-grained, closer to business logic");
        comparison.put("recommendation",  "Use BOTH: URL security for broad rules + method security for fine-grained");
        ref.put("urlVsMethodSecurity", comparison);

        ref.put("testUsers", Map.of(
            "alice / password", "ROLE_STUDENT — GET /api/courses only",
            "bob / password",   "ROLE_INSTRUCTOR — GET + POST /api/courses",
            "carol / admin",    "ROLE_ADMIN — all endpoints including DELETE and /api/admin/stats"
        ));

        ref.put("howToTest", List.of(
            "1. Login: POST /api/auth/login  {\"username\":\"alice\",\"password\":\"password\"}",
            "2. Copy token from response",
            "3. GET /api/courses  header: Authorization: Bearer <token>  ← works for alice",
            "4. POST /api/courses  with alice token  ← 403 Forbidden (student can't create)",
            "5. Login as bob (INSTRUCTOR) and POST /api/courses  ← 201 Created",
            "6. DELETE /api/courses/1 with bob token  ← 403 (not admin)",
            "7. Login as carol (ADMIN) and DELETE /api/courses/1  ← 204 No Content"
        ));

        return ref;
    }
}


// ── Domain ────────────────────────────────────────────────────────────────────

class Course {
    private Long id;
    private String title;
    private String instructorName;
    private int maxStudents;

    public Course(Long id, String title, String instructorName, int maxStudents) {
        this.id = id; this.title = title;
        this.instructorName = instructorName; this.maxStudents = maxStudents;
    }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getInstructorName() { return instructorName; }
    public int getMaxStudents() { return maxStudents; }
    public void setTitle(String title) { this.title = title; }
}

// ── Service with Method Security ──────────────────────────────────────────────

@Service
class CourseService {

    private final Map<Long, Course> courses = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public void seedData() {
        courses.put(1L, new Course(1L, "Java Fundamentals",  "bob",   30));
        courses.put(2L, new Course(2L, "Spring Boot",        "bob",   25));
        courses.put(3L, new Course(3L, "React Essentials",   "carol", 20));
    }

    /**
     * Any authenticated user with STUDENT, INSTRUCTOR, or ADMIN role can read.
     * @PreAuthorize runs BEFORE the method.
     */
    @PreAuthorize("hasAnyRole('STUDENT','INSTRUCTOR','ADMIN')")
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses.values());
    }

    /**
     * Only INSTRUCTORS and ADMINs can create courses.
     */
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public Course createCourse(Course course) {
        Long id = idGen.incrementAndGet();
        Course newCourse = new Course(id, course.getTitle(), course.getInstructorName(), course.getMaxStudents());
        courses.put(id, newCourse);
        return newCourse;
    }

    /**
     * Only ADMIN can delete courses.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCourse(Long id) {
        if (!courses.containsKey(id)) throw new IllegalArgumentException("Course not found: " + id);
        courses.remove(id);
    }

    /**
     * Admin can see anyone's profile; users can only see their own.
     * #username refers to the method parameter — SpEL expression.
     */
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public Map<String, Object> getProfile(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Map.of(
            "username", username,
            "requestedBy", auth.getName(),
            "roles", auth.getAuthorities().stream().map(a -> a.getAuthority()).toList()
        );
    }

    /**
     * @PostAuthorize — method runs first, THEN Spring checks the return value.
     * Only the instructor of the course (or admin) can access it.
     */
    @PostAuthorize("returnObject.instructorName == authentication.name or hasRole('ADMIN')")
    public Course getCourseSecure(Long id) {
        return courses.getOrDefault(id, new Course(id, "Unknown", "nobody", 0));
    }

    /**
     * @Secured — older style, equivalent to hasRole().
     */
    @Secured({"ROLE_ADMIN"})
    public Map<String, Object> getAdminStats() {
        return Map.of("totalCourses", courses.size(), "adminNote", "Only ADMIN can see this");
    }
}

// ── JWT Utilities (same as Part 1) ────────────────────────────────────────────

@Component
class JwtUtil {
    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expirationMs) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        byte[] paddedKey = Arrays.copyOf(keyBytes, Math.max(keyBytes.length, 32));
        this.secretKey = Keys.hmacShaKeyFor(paddedKey);
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
            .subject(username)
            .claim("roles", roles)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(secretKey)
            .compact();
    }

    public boolean validateToken(String token) {
        try { Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token); return true; }
        catch (Exception e) { return false; }
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object roles = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("roles");
        return roles instanceof List ? (List<String>) roles : List.of();
    }
}

@Component
class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    public JwtAuthFilter(JwtUtil jwtUtil) { this.jwtUtil = jwtUtil; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws ServletException, IOException {
        String path = req.getRequestURI();
        if (path.startsWith("/api/auth/") || path.startsWith("/api/rbac-reference")) {
            chain.doFilter(req, res); return;
        }
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                List<String> roles = jwtUtil.extractRoles(token);
                List<SimpleGrantedAuthority> auths = roles.stream()
                    .map(r -> new SimpleGrantedAuthority(r.startsWith("ROLE_") ? r : "ROLE_" + r))
                    .toList();
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(username, null, auths));
            }
        }
        chain.doFilter(req, res);
    }
}

// ── Security Config ───────────────────────────────────────────────────────────

@EnableWebSecurity
class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    public SecurityConfig(JwtAuthFilter f) { this.jwtAuthFilter = f; }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/rbac-reference").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .httpBasic(b -> b.disable())
            .formLogin(f -> f.disable())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder enc) {
        return new InMemoryUserDetailsManager(
            User.withUsername("alice").password(enc.encode("password")).roles("STUDENT").build(),
            User.withUsername("bob").password(enc.encode("password")).roles("INSTRUCTOR").build(),
            User.withUsername("carol").password(enc.encode("admin")).roles("STUDENT","INSTRUCTOR","ADMIN").build()
        );
    }

    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    AuthenticationManager authManager(UserDetailsService uds, PasswordEncoder enc) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds); p.setPasswordEncoder(enc);
        return new ProviderManager(p);
    }
}

// ── Controllers ───────────────────────────────────────────────────────────────

@RestController @RequestMapping("/api/auth")
class AuthController {
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    public AuthController(AuthenticationManager am, JwtUtil ju) { authManager = am; jwtUtil = ju; }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(body.get("username"), body.get("password")));
        List<String> roles = auth.getAuthorities().stream().map(a -> a.getAuthority()).toList();
        String token = jwtUtil.generateToken(body.get("username"), roles);
        return Map.of("token", token, "type", "Bearer", "username", body.get("username"), "roles", roles);
    }
}

@RestController @RequestMapping("/api/courses")
class CourseController {
    private final CourseService courseService;
    public CourseController(CourseService cs) { courseService = cs; }

    @GetMapping
    public List<Course> getAll() { return courseService.getAllCourses(); }

    @GetMapping("/{id}")
    public Course getById(@PathVariable Long id) { return courseService.getCourseSecure(id); }

    @PostMapping
    public Course create(@RequestBody Course course) { return courseService.createCourse(course); }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return Map.of("message", "Deleted course " + id);
    }
}

@RestController @RequestMapping("/api/profile")
class ProfileController {
    private final CourseService courseService;
    public ProfileController(CourseService cs) { courseService = cs; }

    @GetMapping("/{username}")
    public Map<String, Object> getProfile(@PathVariable String username) {
        return courseService.getProfile(username);
    }
}

@RestController @RequestMapping("/api/admin")
class AdminController {
    private final CourseService courseService;
    public AdminController(CourseService cs) { courseService = cs; }

    @GetMapping("/stats")
    public Map<String, Object> stats() { return courseService.getAdminStats(); }
}

@RestController @RequestMapping("/api")
class RbacReferenceController {

    @GetMapping("/rbac-reference")
    public Map<String, Object> reference() {
        Map<String, Object> ref = new LinkedHashMap<>();
        ref.put("title", "RBAC & Method-Level Security Reference");

        Map<String, String> annotations = new LinkedHashMap<>();
        annotations.put("@EnableMethodSecurity", "Put on @SpringBootApplication or @Configuration — enables all method security annotations");
        annotations.put("@PreAuthorize(\"expr\")", "Runs BEFORE method — blocks if expression is false");
        annotations.put("@PostAuthorize(\"expr\")", "Runs AFTER method — can inspect returnObject");
        annotations.put("@Secured({\"ROLE_ADMIN\"})", "Older style — simple role check, no SpEL");
        annotations.put("@PreFilter", "Filter a collection INPUT parameter before method runs");
        annotations.put("@PostFilter", "Filter a collection RETURN value after method runs");
        ref.put("annotations", annotations);

        Map<String, String> spel = new LinkedHashMap<>();
        spel.put("hasRole('ADMIN')", "Has ROLE_ADMIN authority (Spring adds ROLE_ prefix)");
        spel.put("hasAnyRole('USER','ADMIN')", "Has any of the specified roles");
        spel.put("hasAuthority('ROLE_ADMIN')", "Exact authority match (no prefix added)");
        spel.put("isAuthenticated()", "Any authenticated user");
        spel.put("isAnonymous()", "Not authenticated");
        spel.put("authentication.name", "Current username");
        spel.put("#param == authentication.name", "#param refers to method parameter by name");
        spel.put("returnObject.field == val", "PostAuthorize — inspect returned object field");
        ref.put("spelExpressions", spel);

        Map<String, String> comparison = new LinkedHashMap<>();
        comparison.put("URL Security (SecurityFilterChain)", "Coarse-grained — based on URL patterns. Good for broad rules.");
        comparison.put("Method Security (@PreAuthorize)", "Fine-grained — based on method call + parameters. Good for business rules.");
        comparison.put("Recommendation", "Use both: URL security for broad auth, method security for business logic");
        ref.put("urlVsMethodSecurity", comparison);

        Map<String, String> accessPatterns = new LinkedHashMap<>();
        accessPatterns.put("Get current user", "SecurityContextHolder.getContext().getAuthentication()");
        accessPatterns.put("Get username", "authentication.getName()");
        accessPatterns.put("Get roles", "authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()");
        accessPatterns.put("Check role in code", "authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(\"ROLE_ADMIN\"))");
        accessPatterns.put("In controller param", "@AuthenticationPrincipal UserDetails user");
        ref.put("accessingSecurityContext", accessPatterns);

        return ref;
    }
}
