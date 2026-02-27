SEGMENT 1: OPENING & FRAMING (3 minutes)
SLIDE 1 – Title Slide
Title: "JWT & Role-Based Access Control" Subtitle: "Authentication, Authorization, and Securing REST APIs" Your name / date

SCRIPT: "Good morning everyone. Over the past few days we've been building up our understanding of web security, and today we're going to take a big step forward. We're going to talk about two of the most important concepts you will use in real production applications — JSON Web Tokens, and Role-Based Access Control. By the end of this class, you're going to understand exactly how a user logs in, gets a token, carries that token around, and how your application uses it to decide what that user is and isn't allowed to do. We're going to go from theory all the way down to Spring Boot code. Let's get into it."

SEGMENT 2: TOKEN-BASED vs SESSION-BASED AUTH (7 minutes)
SLIDE 2 – Two Approaches to Authentication
CONTENT:

Session-Based:

Server stores session in memory/DB
Client holds session ID (cookie)
Stateful — server must remember you
Scaling requires shared session store
Works well for monolithic apps
Token-Based:

Server stores nothing
Client holds signed token
Stateless — server verifies, not stores
Scales horizontally with ease
Ideal for REST APIs & microservices
SCRIPT: "Before we talk about JWTs specifically, I want to make sure everyone understands why they exist. To do that, we need to compare two different approaches to authentication. The older approach — and one you may still see in legacy apps — is session-based authentication. Here's how it works: a user logs in, the server creates a session, stores it in memory or a database, and gives the client a session ID as a cookie. Every time the client makes a request, it sends that cookie, the server looks up the session, verifies it, and says 'okay, I know who this is.' That works fine, but it has problems. First, the server has to store all those sessions. If you have a million users, you have a million sessions sitting in memory or in a database. Second, if you have multiple servers behind a load balancer, you have a problem — if server A created the session and the next request hits server B, server B has no idea who this user is. You can solve that with sticky sessions or a shared session store like Redis, but that adds complexity. Now the modern approach: token-based authentication. With this model, the server doesn't store anything. When a user logs in, the server creates a signed token that contains everything needed to identify the user, and hands it back. The client stores that token — usually in memory or local storage — and sends it with every request. The server just validates the token on the fly. No storage. No lookup. Completely stateless. This scales beautifully. It doesn't matter which server handles the request, because all the information is in the token itself."

SEGMENT 3: JWT STRUCTURE & CLAIMS (10 minutes)
SLIDE 3 – What is a JWT?
CONTENT:

"A compact, URL-safe, self-contained token for transmitting information between parties as a JSON object"
Three parts: Header . Payload . Signature
Each part is Base64URL encoded and separated by a dot
Not encrypted by default — anyone can decode it, but no one can forge the signature without the secret key
SCRIPT: "So what exactly is a JWT? JWT stands for JSON Web Token. The official definition says it's a compact, URL-safe means of representing claims to be transferred between two parties. Let's unpack that. A JWT is a string. It looks like a long blob of characters separated by two dots. Those two dots divide it into three parts: the header, the payload, and the signature. Each part is Base64URL encoded, which is why it looks like gibberish — but it's not encrypted by default. Anyone can decode and read the header and payload. What they cannot do is forge or tamper with the signature without knowing the secret key. Let's look at each part."

SLIDE 4 – JWT Header
CONTENT:

json
{
  "alg": "HS256",
  "typ": "JWT"
}
Base64URL encoded → eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9

SCRIPT: "The header is a small JSON object with two fields. typ is always JWT — it's just telling you what kind of token this is. alg tells you which signing algorithm was used. HS256 means HMAC with SHA-256, which is a symmetric algorithm — the same key is used to sign and verify. RS256 is an asymmetric algorithm using a private/public key pair, which is common in larger systems where different services need to verify tokens without knowing the signing secret. For today we'll focus on HS256. This header gets Base64URL encoded and becomes the first segment of your token."

SLIDE 5a – JWT Payload
CONTENT:

json
{
  "sub": "user123",
  "name": "Jane Doe",
  "roles": ["ROLE_ADMIN", "ROLE_USER"],
  "iat": 1700000000,
  "exp": 1700003600
}
This data is made up of claims — statements about the user and metadata
There are three categories: Registered, Public, and Private
SCRIPT: "The payload is where the actual data lives. This data is made up of what we call claims — statements about the user and additional metadata. You can see a typical payload here — it has a user ID, a name, their roles, and some timestamps. There are three categories of claims and we're going to go through each one."

SLIDE 5b – Registered Claims
CONTENT:

Claim	Meaning
sub	Subject — the user's unique identifier
iss	Issuer — who created the token
aud	Audience — who the token is intended for
iat	Issued At — Unix timestamp of creation
exp	Expiration — Unix timestamp of expiry (critical)
jti	JWT ID — unique token identifier, prevents replay attacks
SCRIPT: "Registered claims are standardized by the JWT spec. Let me walk through the important ones. sub is the subject — typically the user's unique ID. iss is the issuer — your application or auth server. aud is the audience — who this token is intended for. iat is issued-at — when the token was created. exp is expiration — and this one is critical. A token without an expiry is a security disaster because if it gets stolen it's valid forever. jti is a unique ID for the token itself, which is useful for preventing replay attacks. You will use sub, iat, and exp in virtually every JWT you ever create."

SLIDE 5c – Public & Private Claims + Warning
CONTENT:

Public claims — custom claims registered publicly to avoid naming collisions
Private claims — completely app-specific. Examples: roles, email, department
⚠️ The payload is NOT encrypted. It is Base64URL encoded — anyone who intercepts a token can read it. Never put passwords, SSNs, credit card numbers, or any sensitive data in a JWT payload.

SCRIPT: "Public claims are things you register publicly so there's no naming collision across systems. Private claims are completely custom to your application — that's where you'd put things like roles, email, department, or whatever your app needs to make decisions. And now the most important thing on this slide — that warning. The payload is not encrypted. It is only encoded. Anyone who gets hold of the token can base64-decode it and read every field. So never, ever put sensitive data in here. No passwords, no SSNs, no credit card numbers. The only thing that's protected is the signature — meaning they can read it, but they can't change it without breaking the token."

SLIDE 6 – JWT Signature
CONTENT:

HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
The signature proves the token hasn't been tampered with
Without the secret, you cannot forge a valid signature
A JWT is a signed document, not a locked box — readable by anyone, forgeable by no one without the key
SCRIPT: "The signature is what makes JWTs trustworthy. The server takes the encoded header, puts a dot, adds the encoded payload, then runs that string through the signing algorithm using a secret key. The result is the signature. When a token comes back to the server, the server does the same calculation again. If the result matches the signature on the token, the token is valid and untampered. If even a single character in the payload was changed, the signature won't match and the token gets rejected. So a JWT is not a locked box — it's a signed document. Anyone can read it, but only the server can verify it came from the right place, and only the server can create a valid one."

SEGMENT 4: JWT AUTHENTICATION FLOW (8 minutes)
SLIDE 7 – JWT Auth Flow
CONTENT:

1. Client  →  POST /login {username, password}
2. Server validates credentials against DB
3. Server generates JWT, returns it to client
4. Client stores token (memory / localStorage)
5. Client  →  GET /api/data
              Authorization: Bearer <token>
6. Server validates token signature + expiry
7. Server extracts user/roles from payload
8. Server processes request, returns response
SCRIPT: "Now let's talk about the actual flow — how this plays out in a real application. Step one: The user submits their credentials to a login endpoint. Something like POST /api/auth/login. Step two: The server checks those credentials against the database. If they match, we move forward. Step three: The server generates a JWT — it builds the header, builds the payload with the user's info and roles, signs it, and sends it back in the response body. Step four: The client receives the token and stores it somewhere. Step five: For every subsequent request to a protected endpoint, the client includes the token in the Authorization header, following the Bearer scheme. So the header looks like: Authorization: Bearer eyJhbG... Step six: The server receives that request, pulls out the token, validates the signature, and checks that it hasn't expired. Step seven: If everything checks out, the server extracts the user's identity and roles from the payload. Step eight: The server processes the request and returns a response. That's the complete loop. Notice that the server never stored anything. The token is the entire authentication record."

SEGMENT 5: GENERATING & VALIDATING JWTs (8 minutes)
SLIDE 8 – Java JWT with jjwt Library
CONTENT:

xml
<!-- pom.xml — all three artifacts are required -->
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>0.11.5</version>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-impl</artifactId>
  <version>0.11.5</version>
  <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-jackson</artifactId>
  <version>0.11.5</version>
  <scope>runtime</scope>
</dependency>
SCRIPT: "In Java and Spring Boot, the most common library for working with JWTs is called jjwt — the Java JWT library. You need all three of these artifacts in your pom.xml. jjwt-api is the interface you code against. jjwt-impl is the runtime implementation, and jjwt-jackson handles JSON serialization. If you only include the first one, you'll get runtime errors, so make sure all three are in there."

SLIDE 9 – Generating a JWT
CONTENT:

java
public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", userDetails.getAuthorities());

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
}
SCRIPT: "Here's a method to generate a token. We build a claims map and add the user's roles. Then we use the Jwts.builder() fluent API to set our custom claims, set the subject as the username, set the issued-at timestamp to right now, set the expiration to one hour from now — that's current time plus 1000 milliseconds times 60 seconds times 60 minutes — sign it with HS256 and our secret key, and call compact() which serializes everything into that three-part dot-separated string. The secretKey here should be stored securely in environment variables or a secrets manager. Never hardcode it in your source code."

SLIDE 10 – Validating a JWT
CONTENT:

java
public boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername())
        && !isTokenExpired(token);
}

public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
}

private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
}

private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
}
SCRIPT: "Validation is equally straightforward. We parse the token using Jwts.parserBuilder(), provide our signing key, and call parseClaimsJws(). If the signature is invalid or the token is malformed, this method throws an exception — which is how we detect tampered tokens. If the signature is fine, we get back the claims body and can extract anything we need. We also check that the username in the token matches the user we're expecting, and that the token hasn't expired. If all three conditions pass, the token is valid."

SLIDE 11 – User Entity & Role Model
CONTENT:

java
@Entity
public class User implements UserDetails {
    @Id @GeneratedValue
    private Long id;
    private String username;
    private String password; // stored as bcrypt hash

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles; // e.g. "ROLE_ADMIN", "ROLE_USER"

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }
    // ... other UserDetails methods
}

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
SCRIPT: "Before we can wire Spring Security together, we need a User entity it can work with. Our User class implements UserDetails — that's Spring Security's interface for representing a logged-in user. The most important method is getAuthorities(), which takes our role strings and converts them into GrantedAuthority objects that Spring Security understands. Roles are stored as strings in a separate collection — ROLE_ADMIN, ROLE_USER, and so on. The ROLE_ prefix is a Spring Security convention. Passwords are never stored in plain text — always as a bcrypt hash."

SLIDE 12 – UserDetailsService
CONTENT:

java
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() ->
                new UsernameNotFoundException("User not found: " + username));
    }
}
Spring Security calls loadUserByUsername() automatically during authentication
This is the bridge between your database and Spring Security
SCRIPT: "UserDetailsService is the interface Spring Security uses to load a user from your data source. You implement one method — loadUserByUsername — and return a UserDetails object. Spring Security calls this automatically when it needs to authenticate or verify a user. If the user isn't found, you throw UsernameNotFoundException and Spring Security handles the rest. This is the bridge between your database and Spring Security — without it, Spring has no idea your users exist."

SLIDE 13 – Login Endpoint
CONTENT:

java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private UserDetailsServiceImpl userDetailsService;
    @Autowired private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword())
        );

        UserDetails user = userDetailsService
            .loadUserByUsername(request.getUsername());

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(token);
    }
}
SCRIPT: "Here's the login endpoint — the thing that actually hands out tokens. A POST request comes in with a username and password. We pass those to the AuthenticationManager, which validates the credentials against the database. If the credentials are wrong it throws an exception automatically and the request fails with a 401. If they're correct, we load the full UserDetails, generate a JWT, and return it in the response body. The client stores that token and attaches it to every future request. This is the starting point of the entire JWT flow."

SEGMENT 6: SPRING SECURITY & CUSTOM AUTH FILTERS (8 minutes)
SLIDE 14 – Spring Security Request Pipeline
CONTENT:

HTTP Request
    ↓
[Filter Chain]
    ↓
JwtAuthenticationFilter  ← our custom filter
    ↓
UsernamePasswordAuthenticationFilter
    ↓
SecurityContextHolder
    ↓
Controller
SCRIPT: "Now let's talk about how this integrates with Spring Security. Spring Security operates as a chain of filters that every HTTP request passes through before it reaches your controller. What we need to do is insert our own custom filter — a JWT filter — into that chain. Our filter's job is to intercept every request, look for the Authorization header, pull out the token, validate it, and if it's valid, populate the Spring Security context with the user's identity."

SLIDE 15 – Custom JWT Filter
CONTENT:

java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder
                    .getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService
                    .loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                    SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Token expired\"}");
            return;
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid token\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}
SCRIPT: "Here's our custom filter. We extend OncePerRequestFilter which guarantees it runs exactly once per request. First, we grab the Authorization header. If it's missing or doesn't start with 'Bearer ', we skip this filter entirely and let the request continue — it'll be blocked later if the endpoint requires auth. If there is a token, we wrap everything in a try/catch. This is important — parseClaimsJws() will throw an ExpiredJwtException if the token is expired, and a JwtException for anything else malformed or tampered. Without this, an expired token causes an unhandled exception instead of a clean 401. We catch each case and write a proper JSON error response. If the token is valid, we extract the username, load the UserDetails, and set the authentication in the SecurityContextHolder. That's the key step — once we set it there, Spring Security considers this user authenticated for the rest of the request."

SLIDE 16 – Security Configuration
CONTENT:

java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthEntryPoint))
            .addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
SCRIPT: "Here's the security configuration that ties it all together. csrf.disable() — CSRF protection is designed for session-based apps with browser cookies. With JWTs we don't use cookies for auth, so CSRF isn't a concern. We disable it. SessionCreationPolicy.STATELESS — tells Spring Security not to create or use HTTP sessions. Fully stateless. authorizeHttpRequests — our URL-based rules. Let anything under /api/auth/ through without authentication. Anything under /api/admin/ requires ADMIN role. Everything else just needs the user to be authenticated. exceptionHandling — registers our custom 401 entry point for unauthenticated requests. addFilterBefore — registers our JWT filter and places it before the built-in username/password filter so it runs first and can populate the security context."

SEGMENT 7: RBAC & SECURITY CONTEXT (8 minutes)
SLIDE 17 – What is RBAC?
CONTENT:

Role-Based Access Control

Users are assigned ROLES
Roles are assigned PERMISSIONS
Access is granted based on ROLE, not individual user

Examples:
  ROLE_ADMIN    → full access
  ROLE_MANAGER  → read + write
  ROLE_USER     → read only
  ROLE_GUEST    → public endpoints only
SCRIPT: "Role-Based Access Control is the most common authorization model in enterprise applications. The idea is simple: instead of assigning permissions directly to individual users — which doesn't scale — you define roles, assign permissions to those roles, and then assign users to roles. So you might have an ADMIN role that can do everything. A MANAGER role that can read and write data but can't delete users or access system settings. A USER role that can read their own data. And a GUEST role that can only hit public endpoints. When a user logs in, their roles go into the JWT payload. When they make a request, Spring Security reads those roles out of the SecurityContextHolder and uses them to make authorization decisions."

SLIDE 18 – SecurityContextHolder
CONTENT:

java
// Reading the current user anywhere in your application
Authentication auth = SecurityContextHolder
    .getContext()
    .getAuthentication();

String username = auth.getName();
Collection<? extends GrantedAuthority> roles = auth.getAuthorities();

// Get your full User object
UserDetails user = (UserDetails) auth.getPrincipal();
Thread-local storage — holds "who is making this request"
Populated by the JWT filter, readable anywhere during that request
getPrincipal() returns your User object, giving access to any custom fields you've added
SCRIPT: "The SecurityContextHolder is Spring Security's thread-local storage for authentication information. Think of it as a request-scoped container that holds 'who is currently making this request.' Once our JWT filter populates it, any code that runs during that request — services, controllers, anywhere — can reach in and ask who is this. You get the username, their roles, or cast the principal to your UserDetails implementation to access any custom fields you've added to your User entity, like an ID or email."

SLIDE 19a – Method-Level Security: @PreAuthorize
CONTENT:

java
@RestController
@RequestMapping("/api/users")
public class UserController {

    // Simple role check
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() { ... }

    // SpEL expression — admin OR requesting their own data
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public User getUser(@PathVariable Long id) { ... }
}
@PreAuthorize uses Spring Expression Language (SpEL)
authentication is automatically available in SpEL expressions
Enables complex rules: role checks, ownership checks, any Java logic
SCRIPT: "Method-level security lets you put authorization logic directly on your controller or service methods — often cleaner than expressing every rule as a URL pattern. @PreAuthorize is the most powerful option. It takes a SpEL expression. The first example is simple — hasRole('ADMIN'). The second is where the real power is: hasRole('ADMIN') or #id == authentication.principal.id. This says allow access if the user is an admin OR if the ID they're requesting matches their own ID. That's how you let users see their own data but block them from seeing anyone else's. The #id references the method parameter by name, and authentication.principal is the logged-in user from the SecurityContextHolder. You need @EnableMethodSecurity on your config class for this to work — you saw that in the SecurityConfig slide."

SLIDE 19b – Method-Level Security: @Secured & Authentication Injection
CONTENT:

java
    // Simpler annotation for straightforward role checks
    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public void deleteUser(@PathVariable Long id) { ... }

    // Spring injects Authentication automatically as a parameter
    @GetMapping("/me")
    public User getCurrentUser(Authentication authentication) { ... }
@Secured — simpler syntax, no SpEL, just a list of allowed roles
Authentication injected as a method parameter — Spring populates it automatically, no manual SecurityContextHolder call needed
SCRIPT: "@Secured is the simpler alternative to @PreAuthorize. No SpEL, just a list of role names. It's perfectly fine for straightforward cases where you just need to check a role. And that last example is a handy pattern — you can add Authentication as a parameter to any controller method and Spring will inject it automatically. No need to manually call SecurityContextHolder.getContext().getAuthentication() — Spring handles it for you. Useful for endpoints like /me where you just need to return the currently logged-in user's data."

SEGMENT 8: HANDLING AUTH FAILURES & CUSTOM LOGIC (5 minutes)
SLIDE 20 – Handling Authentication Failures
CONTENT:

java
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(
            "{\"error\": \"Unauthorized\", \"message\": \""
            + authException.getMessage() + "\"}");
    }
}
Called when an unauthenticated request hits a protected endpoint → 401
For authenticated users hitting endpoints their role can't access → 403 (handle with AccessDeniedHandler)
SCRIPT: "When authentication fails, by default Spring Security redirects to a login page — which is useless for a REST API. We need it to return a proper JSON error response. We do that by implementing AuthenticationEntryPoint. This is called whenever an unauthenticated request hits a protected endpoint. We override commence, set the Content-Type to JSON, set the HTTP status to 401, and write a JSON error body. You register this in your SecurityConfig — you saw the exceptionHandling line a few slides back. For authorization failures — authenticated users trying to do something their role doesn't allow — the status code is 403 Forbidden. You handle that the same way with an AccessDeniedHandler."

SLIDE 21 – Custom Authorization Logic
CONTENT:

java
@Component
public class DocumentAuthorizationService {

    public boolean canAccessDocument(Authentication auth, Long documentId) {
        UserDetails user = (UserDetails) auth.getPrincipal();
        Document doc = documentRepository.findById(documentId).orElseThrow();

        return doc.getOwnerId().equals(user.getId())
            || auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}

// In controller:
@GetMapping("/documents/{id}")
@PreAuthorize("@documentAuthService.canAccessDocument(authentication, #id)")
public Document getDocument(@PathVariable Long id) { ... }
SCRIPT: "Sometimes your authorization logic is too complex for a one-liner in @PreAuthorize. In that case, you can move it to a Spring bean and call the bean method directly from the annotation. The @ symbol references a Spring bean by name. authentication is automatically available in SpEL and gives you the current user. This way you can write arbitrary Java logic for your authorization checks — database lookups, complex business rules, whatever you need."

SEGMENT 9: SECURITY BEST PRACTICES (8 minutes)
SLIDE 22a – Token Expiry & Short-Lived Access Tokens
CONTENT:

✅ Always set an expiration (exp claim) — a token with no expiry is valid forever
✅ Use short-lived access tokens — 15 to 60 minutes is typical
Since JWTs are stateless, you cannot invalidate them once issued — short expiry limits the window if a token is stolen
🚫 Never issue tokens with no expiry in production
SCRIPT: "Always set an expiration. This cannot be overstated. A token without an expiry is valid forever — if it gets stolen, it's a permanent breach. Short-lived access tokens limit your exposure. If a token is compromised, it's only valid for 15 minutes. The attacker can't do much in that window. The tradeoff is that users would need to log in again every 15 minutes, which is where refresh tokens come in."

SLIDE 22b – Refresh Tokens
CONTENT:

Access Token:   short-lived (15–60 min), stateless JWT
Refresh Token:  long-lived (days/weeks), stored in DB, httpOnly cookie

Refresh Flow:
1. Access token expires → client gets 401
2. Client sends refresh token to POST /auth/refresh
3. Server validates refresh token against DB
4. Server issues new access token
5. Client continues with new token

Revocation: delete refresh token from DB → user effectively logged out
SCRIPT: "Refresh tokens solve the usability problem that short-lived access tokens create. The access token is your regular short-lived JWT — stateless, fast to verify. The refresh token is a separate long-lived credential stored in your database, typically in an httpOnly cookie so JavaScript can't touch it. When the access token expires and the client gets a 401, it automatically sends the refresh token to a dedicated refresh endpoint. The server looks it up in the database, validates it, and issues a new access token. The user never has to log in again. The key benefit beyond convenience is revocation. Because the refresh token lives in the database, you can delete it at any time to log the user out — something you can't do with a stateless JWT alone."

SLIDE 22c – Token Storage
CONTENT:

Storage	XSS Risk	CSRF Risk	Survives Refresh
localStorage	🔴 High	🟢 None	✅ Yes
httpOnly cookie	🟢 None	🔴 Yes (mitigable)	✅ Yes
In-memory (JS var)	🟢 None	🟢 None	❌ No
localStorage — accessible by any JavaScript, including injected malicious scripts
httpOnly cookie — browser blocks JS access; CSRF mitigated with SameSite and CSRF tokens
In-memory — safest against both attacks; lost on page refresh, best for access tokens paired with httpOnly refresh token
SCRIPT: "Where you store the token in the browser matters a lot. localStorage is convenient but accessible by any JavaScript running on the page — including code injected by an XSS attack. httpOnly cookies can't be read by JavaScript at all, which defeats XSS, but they're automatically sent with every request which opens up CSRF exposure — mitigated with the SameSite attribute. In-memory is the safest option — nothing is persisted, so there's nothing to steal. The downside is that a page refresh loses the token, which is why you pair in-memory access tokens with an httpOnly refresh token cookie. Understand your threat model and pick accordingly."

SLIDE 22d – The alg:none Vulnerability & Strong Secrets
CONTENT:

🚫 alg:none attack — some older libraries accepted tokens with "alg": "none", meaning no signature required. Attackers could forge any token.
Always validate the algorithm. Always reject none.
✅ Strong secrets — for HS256, use at least 256 bits (32 random bytes)
Generate with a cryptographic RNG
Store in environment variables or a secrets manager — never in source code
Rotate secrets periodically
SCRIPT: "Two more things before we move on. The alg:none vulnerability — some older JWT libraries accepted tokens where the algorithm was set to none, effectively removing signature validation. Attackers could craft any payload they wanted and the server would accept it. Always ensure your library validates the algorithm and hard-reject none. And strong secrets. For HS256, use at least 256 bits — 32 random bytes — generated by a cryptographic random number generator. A weak or short secret can be brute-forced. Never hardcode your secret in source code. Store it in an environment variable or a secrets manager, and rotate it periodically."

SLIDE 23 – HTTPS & Transport Security
CONTENT:

✅ Always use HTTPS in production — a JWT over HTTP is plain text on the wire
✅ Enforce HTTPS with HSTS (HTTP Strict Transport Security)
✅ TLS 1.2 minimum — prefer TLS 1.3
🚫 Never send tokens over HTTP
java
http.headers(h -> h.httpStrictTransportSecurity(
    hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000)
));
SCRIPT: "HTTPS is non-negotiable. A JWT in an HTTP request is transmitted in plain text — anyone on the network can read it. TLS encryption is the envelope that protects your tokens in transit. In Spring Security you can configure HSTS — HTTP Strict Transport Security — which tells browsers to never make an HTTP connection to your domain. Always use it in production. TLS 1.2 is the minimum acceptable version today. TLS 1.3 is faster and more secure — prefer it where you can."

SLIDE 24 – API Keys (Overview)
CONTENT:

Long-lived credentials typically used for server-to-server or third-party integration auth
Stored hashed in DB, scoped to minimum permissions, rotated regularly
JWT vs API Key — when to use which:

JWT	API Key
Use case	User sessions, web/mobile apps	Service integrations, programmatic access
Lifetime	Short (minutes)	Long (months/indefinite)
Stateless	✅	❌ (stored in DB)
Tools for later exercises: Spring Security, custom filter chains, API gateway key validation (Kong, AWS API Gateway)

SCRIPT: "API keys are a related concept you'll encounter. They're typically used for server-to-server communication or developer integrations rather than user sessions. Unlike JWTs they're stored in the database. JWTs are the right tool for user auth. API keys are better for long-lived programmatic access. We won't go deep on this today — you'll work with these tools in later exercises."

SLIDE 25 – Rate Limiting (Overview)
CONTENT:

Prevents brute force on /login, token enumeration, and abuse of expensive endpoints
Example: limit /api/auth/login to 5 attempts per minute per IP → return 429 Too Many Requests
Tools for later exercises:

Bucket4j — token bucket algorithm, integrates with Spring
Redis-backed rate limiting — shared limits across multiple instances
Resilience4j @RateLimiter
API Gateway (Kong, AWS API Gateway) — infrastructure-level rate limiting
SCRIPT: "Rate limiting is often the last thing developers add and the first thing attackers exploit. At minimum, protect your login endpoint. If you allow unlimited attempts, an attacker can try millions of passwords. We won't implement this today, but you'll use these tools in later exercises — Bucket4j is the most common Spring-native approach, and in cloud environments you'd typically push this to an API gateway."

SEGMENT 10: IMPLEMENTATION CHECKLIST (3 minutes)
SLIDE 26 – JWT + RBAC Implementation Checklist
CONTENT:

□ Add all 3 jjwt dependencies to pom.xml
□ Create User entity implementing UserDetails
□ Implement UserDetailsService (load from DB)
□ Create JwtService (generate + validate tokens)
□ Create login endpoint (POST /api/auth/login)
□ Create JwtAuthenticationFilter (OncePerRequestFilter)
    □ Include try/catch for ExpiredJwtException and JwtException
□ Configure SecurityFilterChain
    □ Disable CSRF
    □ Set STATELESS session policy
    □ Configure URL-based authorization rules
    □ Register JWT filter before UsernamePasswordAuthenticationFilter
    □ Register AuthenticationEntryPoint for 401 responses
□ Add @EnableMethodSecurity to config
□ Use @PreAuthorize on sensitive endpoints
□ Store JWT secret in environment variable
□ Set token expiration (access: 15–60 min)
□ Implement refresh token flow
□ Rate limit /auth endpoints
□ Enforce HTTPS
SCRIPT: "Before I take questions, here's your implementation checklist. Every one of these boxes needs to be checked in a production application. Use this as your review list when you're working on your projects. Notice that security is not one thing you add at the end — it spans dependencies, filter chain, configuration, annotations, and infrastructure. The most common mistakes I see are: no expiration on tokens, secrets hardcoded in source code, missing HTTPS, no exception handling in the JWT filter, and no rate limiting. Those five things alone are responsible for a huge percentage of real-world JWT-related breaches."

SEGMENT 11: Q&A BUFFER (3 minutes)
SLIDE 27 – Key Takeaways
CONTENT:

1. JWTs are signed, not encrypted — never put secrets in the payload
2. Three parts: Header (algorithm) . Payload (claims) . Signature
3. Authentication → who are you? | Authorization → what can you do?
4. Spring Security filter chain → JWT filter → SecurityContextHolder
5. RBAC: roles in JWT → @PreAuthorize / URL rules enforce them
6. UserDetailsService is the bridge between your DB and Spring Security
7. Always handle ExpiredJwtException in your filter — clean 401, not a crash
8. Short-lived tokens + refresh tokens + HTTPS + strong secrets + rate limiting
   = production-ready security
SCRIPT: "Let's bring it all together. JWT is your authentication mechanism — proving identity. RBAC is your authorization mechanism — enforcing what that identity can do. Spring Security is the framework that wires them together through a filter chain and security context. UserDetailsService connects your database to Spring Security. Your JWT filter must handle exceptions properly or expired tokens will crash instead of returning a 401. Use short-lived tokens paired with refresh tokens for real sessions. And always: HTTPS, strong secrets, token expiry, rate limiting on auth endpoints. What questions do you have?"

