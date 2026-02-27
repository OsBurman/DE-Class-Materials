SECTION 1: Opening & Overview (0:00 – 0:08)
SLIDE 1 — Title Slide
Content:

Title: "Spring Security – Authentication, Authorization & Configuration"
Subtitle: Your name, course name, date


SLIDE 2 — What We're Covering Today
Content:

Spring Security architecture overview
Authentication vs. Authorization
SecurityFilterChain configuration
Authentication mechanisms (in-memory & database)
Password encoding with BCrypt
Method-level security with @PreAuthorize
CSRF protection
JWT: what it is and when it changes the rules
Putting it all together

SCRIPT:
"Good morning everyone. Today we're going to do a deep dive into Spring Security — one of the most important and, honestly, one of the most misunderstood frameworks in the Spring ecosystem.
Security is not optional. Every real application you build in your career will need to control who can access it and what they're allowed to do. Spring Security is the standard way to handle that in the Java world.
We already touched on some foundational pieces in our earlier sessions. Today we're going to bring those threads together into a complete picture — how the framework is structured, how to configure it, how to authenticate users from memory and from a database, how to store passwords safely, how to protect individual endpoints with annotations, and how to protect against one of the most common web attacks.
By the end of this session, you should be able to look at a Spring Security configuration class and understand every line of it. Let's get into it."

SECTION 2: Spring Security Overview & Architecture (0:08 – 0:20)
SLIDE 3 — What Is Spring Security?
Content:

A powerful, highly customizable authentication and access control framework
The de-facto standard for securing Spring-based applications
Handles: authentication, authorization, protection against exploits
Integrates seamlessly with Spring Boot via auto-configuration

SCRIPT:
"So what exactly is Spring Security? At its core it's a framework that sits between incoming HTTP requests and your application code. Its job is to answer two questions: Who are you? And are you allowed to do this?
Spring Security is not something you bolt on at the end of a project. It's designed to be woven into the fabric of your application from the beginning. The good news is that Spring Boot makes the initial setup almost effortless — but you still need to understand what's happening under the hood, because the defaults will only take you so far."

SLIDE 4 — The Big Picture: How Spring Security Works
Content:
Incoming HTTP Request
        ↓
[Filter Chain — multiple security filters]
        ↓
[DispatcherServlet — your Spring MVC app]
        ↓
[Controller → Service → Repository]

Spring Security lives entirely in the Filter Chain
It intercepts requests BEFORE they reach your application code

SCRIPT:
"Here's the most important mental model you need to have. When an HTTP request comes in, it does not go straight to your controller. It passes through a chain of servlet filters first. Spring Security plugs its own filters into that chain.
This is powerful because it means Spring Security can inspect, reject, redirect, or modify a request before your application code ever runs. If a user isn't authenticated, they never get anywhere near your controllers.
Think of it like a series of security checkpoints at an airport. You have to pass through every checkpoint before you get to the gate. If you fail any one of them, you go no further."

SLIDE 5 — The Security Filter Chain (Architecture Detail)
Content:
Incoming HTTP Request
        ↓
[SecurityContextPersistenceFilter]   ← loads existing security context from session
        ↓
[UsernamePasswordAuthenticationFilter]  ← intercepts login form submissions
        ↓
[BasicAuthenticationFilter]          ← intercepts HTTP Basic auth headers
        ↓
[ExceptionTranslationFilter]         ← catches auth/access exceptions, sends error response
        ↓
[FilterSecurityInterceptor]          ← final access control check — allowed or denied?
        ↓
  DispatcherServlet (your app)
SCRIPT:
"Inside the filter chain there are many individual filters, each with a specific job. You don't usually interact with most of these directly — Spring Security wires them up for you — but it's important to know they exist.
The UsernamePasswordAuthenticationFilter is what handles your login form. When someone submits a username and password, this filter catches it, attempts to authenticate the user, and either grants them a security context or redirects them to an error page.
The ExceptionTranslationFilter is your safety net — if something down the chain throws an authentication or authorization exception, this filter catches it and decides what to show the user.
And finally FilterSecurityInterceptor is the last checkpoint. It looks at the URL being requested and asks: does this authenticated user have the right roles or authorities to access this resource?
You don't have to memorize all of these today. What I want you to take away is the concept: Spring Security is a chain of filters, each handling a specific concern, and they run in a specific order."

⏸️ INSTRUCTOR PAUSE — 60-second check-in before moving on
Ask the class: "Before we get into the individual components, can someone tell me — based on what we've just seen, at what point in this chain do you think a request gets rejected if the user isn't logged in? Which filter handles that?"
Expected answer: the UsernamePasswordAuthenticationFilter or FilterSecurityInterceptor — either is a reasonable answer and worth a brief discussion. This confirms students have the filter-as-checkpoint mental model before the component slides build on top of it.


SLIDE 6 — Core Authentication Components
Content:
ComponentRoleSecurityContextHolds the current user's authentication for the duration of a requestAuthenticationRepresents the user + their credentials + their authoritiesAuthenticationManagerOrchestrates the authentication processAuthenticationProviderDoes the actual credential validation
SCRIPT:
"Let me introduce the core players. I'm going to split these across two slides so we can give each one proper attention.
The SecurityContext is like a holder — it stores the currently authenticated user's information for the duration of a request. It's stored in a ThreadLocal so each thread has its own.
Authentication is the object inside that context. It represents the user who is logged in — their principal, their credentials, and their list of granted authorities.
AuthenticationManager is the central coordinator. When a login attempt comes in, it doesn't validate credentials itself — it delegates to one or more AuthenticationProviders.
AuthenticationProvider is what actually does the work. The default implementation is DaoAuthenticationProvider, which uses your database and password encoder to check credentials. You can register multiple providers if you need to support different login strategies."

SLIDE 7 — Core User & Authorization Components
Content:
ComponentRoleUserDetailsRepresents user data: username, password, enabled status, rolesUserDetailsServiceHas one job: load a UserDetails by usernameGrantedAuthorityRepresents a single permission — typically a role like ROLE_ADMIN
SCRIPT:
"On the other side of the equation we have the user-facing components.
UserDetails is an interface that represents the data about a user — their username, password, whether they're enabled, locked, expired, and their list of roles. When doing database authentication, you'll implement this yourself or use Spring's built-in User class.
UserDetailsService has one method: loadUserByUsername. You give it a username, it returns a UserDetails object. That's the entire contract. You'll implement this when connecting to a database.
GrantedAuthority represents a single permission. Typically you'll use roles — ROLE_ADMIN, ROLE_USER — and Spring Security makes authorization decisions based on these."

SLIDE 8 (NEW) — How These Components Connect
Content:
LOGIN ATTEMPT (username + password)
        ↓
  AuthenticationManager
        ↓ delegates to
  DaoAuthenticationProvider
        ↓ calls
  UserDetailsService.loadUserByUsername(username)
        ↓ returns
  UserDetails  ←  (loaded from DB / memory)
        ↓
  DaoAuthenticationProvider compares submitted password
  to UserDetails.getPassword() using PasswordEncoder
        ↓ if match
  Authentication object created (principal + authorities)
        ↓ stored in
  SecurityContext  (available for the rest of the request)
SCRIPT:
"This diagram ties the last two slides together. Let's walk through what actually happens when a user submits a login form.
The username and password come in and hit the AuthenticationManager. It delegates to the DaoAuthenticationProvider, which calls your UserDetailsService with the username. Your UserDetailsService goes to the database and returns a UserDetails object.
The DaoAuthenticationProvider then takes the raw password the user typed, runs it through the PasswordEncoder, and compares it to the encoded password on the UserDetails object. If they match — success. An Authentication object is created and stored in the SecurityContext for the rest of that request.
Every login that touches a database follows this exact flow. Once this mental model clicks, everything else in Spring Security becomes much easier to reason about."

SECTION 3: Authentication vs. Authorization (0:20 – 0:27)
SLIDE 9 — Authentication vs. Authorization
Content:
Authentication — Who are you?

Verifying the identity of a user
"Prove you are who you say you are"
Example: logging in with username + password

Authorization — What can you do?

Verifying what an authenticated user is allowed to access
"You're logged in — but are you allowed here?"
Example: only ADMIN can access /admin/**


Authentication always comes before Authorization

SCRIPT:
"This distinction is absolutely fundamental and I want to make sure it's crystal clear before we go further.
Authentication is the process of proving identity. When you type in your username and password, you're authenticating. You're saying 'I am this person, and here's my proof.'
Authorization is what happens after that. Once we know who you are, the system checks what you're allowed to do. Can you view this page? Can you delete this record? Can you access the admin dashboard?
A user can be fully authenticated — logged in successfully — and still be unauthorized to do certain things. That's not a bug, that's by design.
Spring Security handles both, but they are separate concerns and they happen in that order. You cannot authorize someone whose identity you haven't verified. Keep this distinction in your head as we configure everything today."

SLIDE 10 — How Spring Security Represents This
Content:

Authentication object contains:

principal → who the user is (UserDetails)
credentials → password (cleared after login for security)
authorities → list of GrantedAuthority (roles/permissions)
isAuthenticated() → boolean


Authorization decisions use authorities to check access rules

SCRIPT:
"In code, both concepts live inside the Authentication object. After a successful login, Spring Security creates an Authentication object and stores it in the SecurityContext. The authorities list on that object is what authorization decisions are based on.
When you later say 'only users with ROLE_ADMIN can access this URL', Spring Security looks at the authorities in the current user's Authentication object and checks if ROLE_ADMIN is in there. That's the entire mechanism."

SECTION 4: @EnableWebSecurity & SecurityFilterChain Configuration (0:27 – 0:38)
SLIDE 11 — Configuring Spring Security: The Old Way vs. The New Way
Content:
Old way (deprecated in Spring Security 5.7+):

Extend WebSecurityConfigurerAdapter
Override configure() methods

New way (current standard):

Annotate a @Configuration class with @EnableWebSecurity
Declare a SecurityFilterChain bean
Declare an AuthenticationManager or UserDetailsService bean

SCRIPT:
"Before we write any configuration, I want to address something you'll inevitably see when googling Spring Security — a lot of old tutorials that show you extending a class called WebSecurityConfigurerAdapter. That approach is deprecated. It still works for now but it's being removed, and you should not write new code that way.
The modern approach, which is what we're going to use, is to create a plain configuration class, annotate it, and declare beans. It's more explicit and honestly more readable once you know the pattern."

SLIDE 12 — @EnableWebSecurity
Content:
java@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // security beans go here
}

@Configuration — marks this as a Spring config class
@EnableWebSecurity — activates Spring Security's web security support

Registers the DelegatingFilterProxy
Enables Spring Security annotations like @PreAuthorize
Allows you to override default auto-configuration



SCRIPT:
"The @EnableWebSecurity annotation does a lot of behind-the-scenes work. Most importantly, it tells Spring to register the DelegatingFilterProxy — which is the bridge between the standard servlet filter mechanism and Spring's application context. This is what connects your security configuration to the actual filter chain.
In a Spring Boot app, auto-configuration will actually apply a default security setup even without this annotation. But as soon as you add it with your own configuration class, you're taking control. Your configuration overrides the defaults."

SLIDE 13 — SecurityFilterChain Bean
Content:
java@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) 
        throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public/**").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .permitAll()
        )
        .logout(logout -> logout
            .permitAll()
        );

    return http.build();
}
SCRIPT:
"This is your primary security configuration. The SecurityFilterChain bean is where you define the rules for your entire application.
The HttpSecurity object is a builder — you chain methods on it to define behavior. Let me walk through this example.
authorizeHttpRequests is where you define access rules for URLs. Inside, you call requestMatchers with a URL pattern and then a method like permitAll(), hasRole(), or authenticated().
Rules are evaluated in order from top to bottom — the first matching rule wins. This is critical. If you put anyRequest().authenticated() before a specific permit rule, the specific rule will never be reached. Always put more specific rules first.
formLogin configures the login page. Here we're pointing to a custom /login endpoint and saying anyone can access it — which makes sense, because you need to be able to reach the login page without being logged in.
logout configures the logout functionality. By default, Spring Security creates a /logout endpoint for you.
At the end you call http.build() and return the result. That's your filter chain."

SLIDE 14 — Common Access Control Methods
Content:
MethodMeaning.permitAll()Anyone can access, authenticated or not.authenticated()Must be logged in.hasRole("ADMIN")Must have ROLE_ADMIN.hasAnyRole("ADMIN","USER")Must have at least one of these roles.hasAuthority("READ_PRIVILEGES")Must have this exact authority string.denyAll()No one can access

hasRole("ADMIN") automatically prepends ROLE_ — the stored value must be ROLE_ADMIN

SCRIPT:
"A quick but important note on roles versus authorities. When you call hasRole('ADMIN'), Spring Security automatically prepends ROLE_ to the string it's looking for. So the actual value in your database or user object must be ROLE_ADMIN, not just ADMIN.
hasAuthority does no such transformation — you pass the exact string it needs to match.
In practice, roles are typically coarse-grained — ADMIN, USER, MANAGER — while authorities can be more fine-grained like READ_PRIVILEGES or WRITE_PRIVILEGES. You can use either pattern, just be consistent."

SECTION 5: Authentication Mechanisms (0:38 – 0:50)
SLIDE 15 — In-Memory Authentication
Content:
java@Bean
public UserDetailsService userDetailsService() {
    UserDetails user = User.builder()
        .username("alice")
        .password(passwordEncoder().encode("password123"))
        .roles("USER")
        .build();

    UserDetails admin = User.builder()
        .username("bob")
        .password(passwordEncoder().encode("admin123"))
        .roles("ADMIN")
        .build();

    return new InMemoryUserDetailsManager(user, admin);
}
SCRIPT:
"In-memory authentication is exactly what it sounds like — you define users directly in your configuration, and they're stored in memory. This is not for production. You would use this for development, testing, prototyping, or demos.
InMemoryUserDetailsManager implements UserDetailsService and stores users in a simple map. When Spring Security needs to look up a user by username, it calls loadUserByUsername on your UserDetailsService, and the in-memory manager just does a map lookup.
Notice that we're already encoding the password here — we'll talk about that in a moment, but the important thing is: you never store or compare plain text passwords. Ever. Even in a test configuration."

SLIDE 16 — Database Authentication with UserDetailsService
Content:
java@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {
        
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> 
                new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User
            .builder()
            .username(user.getUsername())
            .password(user.getPassword()) // already encoded in DB
            .roles(user.getRole())
            .build();
    }
}
SCRIPT:
"For any real application, users live in a database. Database authentication means that when someone tries to log in, Spring Security calls your UserDetailsService, which queries the database and returns a UserDetails object.
Your job is to implement that one method: loadUserByUsername. You take a username string, you look it up in the database, and you return a populated UserDetails object. If the user doesn't exist, you throw UsernameNotFoundException.
Notice that the password coming back from the database should already be encoded — we stored it encoded, so we return it encoded. Spring Security will handle comparing the raw submitted password against the encoded stored password using the password encoder. You don't do that comparison yourself."

SLIDE 17 — Wiring UserDetailsService Into Security Config
Content:
java@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) 
            throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

Spring Boot auto-detects a UserDetailsService bean — you often don't need to wire it manually

SCRIPT:
"When you declare your CustomUserDetailsService as a Spring @Service bean, Spring Boot's auto-configuration is smart enough to pick it up automatically and use it for authentication. You don't always need to explicitly wire it.
However, if you need an AuthenticationManager bean to inject elsewhere — for example, in a custom authentication endpoint — you can expose it by declaring it as a bean and getting it from AuthenticationConfiguration as shown here. This is the modern way to do it without the deprecated adapter."

SLIDE 18 — Authentication Providers
Content:

AuthenticationProvider is the interface that actually validates credentials
DaoAuthenticationProvider — the default, uses UserDetailsService + PasswordEncoder
You can register multiple providers for different auth strategies

java@Bean
public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
}

AuthenticationManager delegates to registered providers in order until one succeeds

SCRIPT:
"The AuthenticationProvider is the piece that actually does the work of verifying credentials. The default one you'll use is DaoAuthenticationProvider — DAO standing for Data Access Object, because it uses your UserDetailsService to load the user from a data store.
You give it your UserDetailsService and your PasswordEncoder, and it handles the rest: load the user, check if the account is enabled and non-expired, encode the submitted password and compare it to the stored one.
You can register multiple providers if you need to support different authentication methods — for example, username/password for some users and LDAP for others. The AuthenticationManager tries each provider in sequence until one succeeds or all fail."

SECTION 6: Password Encoding (0:50 – 0:55)
SLIDE 19 — Why You Must Encode Passwords
Content:

Never store plain text passwords — ever
If your database is compromised, plain text = immediate exposure of all passwords
Password encoding converts plain text into a one-way hash
BCrypt is the recommended algorithm — it's slow by design (brute force resistance)
BCrypt automatically includes a salt (prevents rainbow table attacks)

SCRIPT:
"I want to be emphatic about this: storing plain text passwords is a career-ending mistake if it leads to a breach. You will see it in old codebases. You will see tutorials that skip encoding for 'simplicity.' Ignore them.
BCrypt is the standard choice for Spring applications. What makes BCrypt special is that it is intentionally slow. Most hashing algorithms are optimized to be fast. BCrypt is designed to take a noticeable amount of time — milliseconds — which is fine for a legitimate login but makes brute force attacks enormously expensive.
BCrypt also automatically generates and stores a salt with each hash, which means even if two users have the same password, their stored hashes will be completely different. This defeats rainbow table attacks."

SLIDE 20 — BCryptPasswordEncoder in Code
Content:
java// Declare the bean
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Encoding a password (e.g., when registering a user)
String rawPassword = "mypassword123";
String encoded = passwordEncoder.encode(rawPassword);
// Store 'encoded' in the database

// Verifying a password (Spring Security does this for you during login)
boolean matches = passwordEncoder.matches(rawPassword, encoded);
// returns true if rawPassword matches the encoded hash
BCrypt output example:
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

$2a$ = algorithm version | 10 = cost factor | remainder = salt + hash

SCRIPT:
"Using BCryptPasswordEncoder in your application is straightforward. You declare it as a bean, inject it where needed, and use two methods: encode for hashing a new password when a user registers, and matches for comparing a raw password to a stored hash.
During the login flow, you don't call matches yourself — DaoAuthenticationProvider does this for you when it validates credentials. You just make sure the PasswordEncoder is wired into the provider.
Look at that BCrypt hash example. The 2a2a
2a is the algorithm version, the 10 is the cost factor — higher means slower and more secure — and the rest is the salt concatenated with the hash. Everything Spring Security needs to verify the password is right there in that one string."


SECTION 7: Method-Level Security (NEW)
SLIDE 21 — Method-Level Security: Protecting Individual Endpoints
Content:

URL-based rules in SecurityFilterChain protect routes broadly
Method-level security lets you protect individual methods with annotations
Must be enabled explicitly:

java@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // ← enables @PreAuthorize and @Secured
public class SecurityConfig {
    // ...
}
Two main annotations:
AnnotationSourceWhat it supports@PreAuthorizeSpring SecuritySpEL expressions — very flexible@SecuredSpring SecuritySimple role names only
SCRIPT:
"Up to now, all of our access control has been at the URL level — we define rules about which paths require which roles. But what if you want to protect a specific service method regardless of how it was called? That's where method-level security comes in.
By adding @EnableMethodSecurity to your config class, you unlock the ability to put security annotations directly on your service or controller methods. This is extremely useful because it ties authorization to the business logic itself, not just to a URL pattern.
There are two annotations you'll use. @PreAuthorize is the more powerful one — it accepts Spring Expression Language, or SpEL, expressions that give you a lot of flexibility. @Secured is simpler and only works with role names. We'll look at both."

SLIDE 22 — @PreAuthorize and @Secured Examples
Content:
java// Only ADMIN can call this method
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long userId) { ... }

// User can only access their own data (or admin can access anyone's)
@PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
public UserProfile getProfile(String username) { ... }

// Must have ALL of these conditions
@PreAuthorize("isAuthenticated() and hasAuthority('WRITE_PRIVILEGES')")
public void publishPost(Post post) { ... }

// @Secured — simpler, role names only (ROLE_ prefix required)
@Secured("ROLE_ADMIN")
public List<User> getAllUsers() { ... }

@PreAuthorize runs before the method — access denied before execution
#username in SpEL refers to the method parameter named username
authentication.name refers to the currently logged-in user's name

SCRIPT:
"Here's where @PreAuthorize gets powerful. The first example is straightforward — only an ADMIN can delete a user.
The second example is much more interesting. The condition says: either you're an ADMIN, or the username you're requesting is your own username. Notice #username — that's SpEL referencing the method parameter by name. And authentication.name is the username of whoever is currently logged in. This means a user can always fetch their own profile, but can't fetch anyone else's unless they're an admin. One annotation, smart access control.
The third example chains conditions — you must both be authenticated and have the WRITE_PRIVILEGES authority.
For @Secured, the syntax is simpler — you just pass the exact role string, ROLE_ prefix included. Use @PreAuthorize when you need logic or expressions. Use @Secured when it's just a simple role check and you want minimal noise.
These annotations can go on controller methods, service methods, or repository methods — anywhere in your Spring bean layer."

SECTION 8: CSRF Protection (0:55 – 1:05)
SLIDE 23 — What is CSRF?
Content:
Cross-Site Request Forgery (CSRF):

An attack where a malicious site tricks a user's browser into making an unwanted request to your application
The browser automatically sends cookies (including session cookies) with every request to a domain
If the user is logged in, the attacker's forged request will be authenticated

Example attack flow:

User logs into your bank app — session cookie is set
User visits evil.com without logging out
evil.com contains a hidden form that submits to your bank's /transfer endpoint
User's browser sends the request with the bank session cookie
Your server can't tell it wasn't intentional

SCRIPT:
"Cross-Site Request Forgery is one of the classic web security vulnerabilities and it's included in the OWASP Top 10. The attack exploits the fact that browsers automatically attach cookies to requests for a given domain.
Think about it: if you're logged into your bank and then you visit a malicious site that has a hidden form targeting your bank's transfer endpoint, your browser will helpfully include your session cookie when submitting that form. Your bank's server receives a perfectly valid authenticated request — it just wasn't you who initiated it.
Spring Security enables CSRF protection by default for all state-changing requests — POST, PUT, DELETE, PATCH. This is important to know because it can catch you off guard when you first set up a form and wonder why it keeps returning 403."

SLIDE 24 — How Spring Security's CSRF Protection Works
Content:
The Synchronizer Token Pattern:

Server generates a unique, random CSRF token per session
Token is embedded in every HTML form as a hidden field
On state-changing requests, server verifies the token is present and correct
An attacker's site cannot read the token (same-origin policy) — so cannot forge a valid request

In Thymeleaf — token is added automatically:
html<form th:action="@{/transfer}" method="post">
    <!-- Thymeleaf auto-inserts: -->
    <!-- <input type="hidden" name="_csrf" value="...token..."> -->
</form>
SCRIPT:
"The defense is elegantly simple. The server generates a secret random token and embeds it in every form. When the form is submitted, the server checks that the token is there and that it matches. A malicious site on a different origin cannot read your forms due to the browser's same-origin policy, so it cannot obtain the token and cannot forge a valid request.
If you're using Thymeleaf, this is completely automatic — it injects the hidden token field for you. If you're using plain HTML forms or a JavaScript frontend you'll need to handle it yourself."

SLIDE 25 — Configuring CSRF
Content:
Default behavior: CSRF is ENABLED
Disabling CSRF (for stateless REST APIs — where you use JWT tokens instead):
javahttp.csrf(csrf -> csrf.disable());
Customizing CSRF (e.g., ignoring specific endpoints):
javahttp.csrf(csrf -> csrf
    .ignoringRequestMatchers("/api/webhook/**")
);
Accessing the CSRF token in JavaScript:
javascript// Available in a meta tag or cookie (if CookieCsrfTokenRepository is used)
const token = document.querySelector('meta[name="_csrf"]').content;
const header = document.querySelector('meta[name="_csrf_header"]').content;
// Include in AJAX requests as a header
SCRIPT:
"The most common question I get about CSRF is: 'when should I disable it?' The short answer is: only for stateless REST APIs where clients authenticate via tokens like JWT rather than session cookies. If your app doesn't use session cookies for authentication, CSRF doesn't apply as an attack vector — because the attack requires the browser to silently attach a cookie.
But — and this is important — if you have a traditional web application with session-based login and HTML forms, CSRF protection must stay on. Don't disable it just because it's causing your forms to fail. Fix your forms.
For single-page applications that use session cookies, you can use CookieCsrfTokenRepository which stores the token in a cookie that JavaScript can read, allowing your frontend to include it in AJAX request headers.
This brings up JWT, which I want to touch on briefly before we move on."

SLIDE 26 (NEW) — JWT: Stateless Authentication Overview
Content:
What is JWT (JSON Web Token)?

A self-contained token that encodes the user's identity and authorities
Stored client-side (typically in memory or a header), not in a session cookie
Sent with every request as a Bearer token in the Authorization header

Why it changes the security rules:
Session-based (Cookie)Token-based (JWT)State storedServer (session store)Client (token itself)CSRF risk?Yes — browser auto-sends cookiesNo — attacker can't forge the headerCSRF protection needed?YesNo — disable it

⚠️ JWT authentication, token generation, and RBAC integration are covered in depth in the dedicated JWT & RBAC session. This slide exists so you understand why the rules change.

SCRIPT:
"I want to give you just enough context on JWT to make sense of why you'd disable CSRF.
JWT stands for JSON Web Token. Instead of the server maintaining a session that maps to a cookie, with JWT the server issues a signed token that contains the user's identity and roles. The client stores this token — usually in memory or localStorage — and includes it manually in the Authorization header of every request, like 'Bearer eyJ...'.
Because the token is in a header that a browser never automatically attaches — unlike cookies — an attacker can't exploit the same vulnerability. The malicious site would need to steal the token, not just trigger a request. So for JWT-based APIs, CSRF protection is irrelevant and you turn it off.
The full implementation of JWT — generating tokens, validating them, building a filter for it, wiring it into your filter chain — is its own session. What I need you to take away today is the conceptual split: session cookies require CSRF protection, bearer tokens do not."

SECTION 9: Putting It All Together
SLIDE 27 — Complete Configuration Example
Content:
java@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) 
            throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .csrf(Customizer.withDefaults()); // enabled by default
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

⏱️ INSTRUCTOR TIMING NOTE — Budget 4–5 minutes on this slide.
Do not read this code top to bottom. Instead, point to each section by annotation or method name and ask students what it does before you explain it. E.g., "What does @EnableMethodSecurity unlock for us?" or "Why is /login in the permitAll list?" This keeps the class engaged and confirms retention before closing out the session.

SCRIPT:
"Here's a complete, real-world configuration that brings together everything we covered today. Let's do a quick walkthrough.
We've added @EnableMethodSecurity so our @PreAuthorize annotations work throughout the application.
We have our access rules: public pages are open, the admin area requires ADMIN role, everything else just needs you to be logged in.
We have form login configured with a custom login page and a redirect after success.
We have logout configured with a redirect back to the login page.
CSRF is using the default — which means it's on.
We have a DaoAuthenticationProvider wired up with our custom UserDetailsService and our BCryptPasswordEncoder.
And we have the PasswordEncoder bean declared.
This is the pattern. This is what a solid, production-ready Spring Security configuration looks like for a traditional web application. Everything has a reason. Nothing is there by accident."

SLIDE 28 — Key Takeaways
Content:

Spring Security works as a filter chain — intercepting requests before they reach your app
Authentication = who you are | Authorization = what you can do
Configure with @EnableWebSecurity + SecurityFilterChain bean (not the deprecated adapter)
In-memory authentication: good for testing only
Database authentication: implement UserDetailsService, return UserDetails
Always encode passwords with BCryptPasswordEncoder — never store plain text
DaoAuthenticationProvider wires UserDetailsService + PasswordEncoder together
Use @PreAuthorize on methods for fine-grained, expression-based access control
CSRF is on by default — disable only for stateless token-based (JWT) APIs
JWT = stateless, bearer token, no CSRF needed — full coverage in the JWT session
Access rules are evaluated in order — most specific rules first

SCRIPT:
"Let me leave you with the key things to remember from today.
Spring Security is a filter chain. Know this. Everything else builds on it.
Authentication and Authorization are different concepts that happen in sequence. Never confuse them.
Use the modern configuration style — @EnableWebSecurity with a SecurityFilterChain bean. The old WebSecurityConfigurerAdapter is dead.
In-memory auth is for development only. Real applications use UserDetailsService against a database.
BCrypt is non-negotiable. Every password that touches a database must be encoded.
@PreAuthorize gives you fine-grained control right on your methods. Don't neglect it just because you have URL rules.
CSRF protection is your default friend for web apps. For JWT-based APIs, turn it off and we'll cover the whole JWT setup next session.
Access rules are evaluated top to bottom — always put specific rules before general ones.
In our next session we'll build on this foundation with JWT and role-based access control. Before then, make sure you can write a complete SecurityConfig from scratch — that's your benchmark for today. Any questions?"