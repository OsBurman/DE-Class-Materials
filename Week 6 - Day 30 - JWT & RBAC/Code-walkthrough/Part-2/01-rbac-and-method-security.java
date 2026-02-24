package com.bookstore.security.rbac;

// =============================================================================
// RBAC AND METHOD-LEVEL SECURITY — Day 30: JWT & RBAC
// =============================================================================
// This file demonstrates:
//   1. Role-Based Access Control (RBAC) concepts and design
//   2. @EnableMethodSecurity — enabling method-level security globally
//   3. @PreAuthorize — before-method authorization (most common)
//   4. @PostAuthorize — after-method authorization (filter response)
//   5. @Secured — legacy annotation for role checks
//   6. Custom authorization logic using Authentication parameter
//   7. SecurityContextHolder usage in service and controller layers
//   8. URL-based security patterns (requestMatchers)
// =============================================================================

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

// =============================================================================
// SECTION 1: RBAC DESIGN — Roles vs Authorities
// =============================================================================

/*
 * ROLE-BASED ACCESS CONTROL (RBAC) CONCEPTS
 * ==========================================
 *
 * RBAC assigns users to ROLES, and roles grant PERMISSIONS (authorities).
 *
 * Example bookstore RBAC model:
 *
 *   USER     → can browse catalog, borrow books, view own orders
 *   LIBRARIAN→ USER + can add/edit books, manage inventory
 *   ADMIN    → LIBRARIAN + can manage users, view all orders, purge data
 *
 * In Spring Security, "role" and "authority" are related but distinct:
 *
 *   ROLE:       "ROLE_ADMIN"    — a coarse-grained bucket of permissions
 *   AUTHORITY:  "books:write"   — a fine-grained specific permission
 *
 * Spring Security's hasRole("ADMIN") automatically prepends "ROLE_" prefix.
 * hasAuthority("ROLE_ADMIN") expects the full string — NO prefix added.
 *
 * Best practice: use roles for simple apps, authorities for fine-grained access control.
 *
 * Our bookstore roles stored in JWT payload (roles claim):
 *   "ROLE_USER"
 *   "ROLE_USER,ROLE_LIBRARIAN"
 *   "ROLE_USER,ROLE_ADMIN"
 */

// =============================================================================
// SECTION 2: @EnableMethodSecurity — Global Configuration
// =============================================================================

/**
 * @EnableMethodSecurity must be added to a @Configuration class.
 * Without it, @PreAuthorize / @Secured annotations are IGNORED silently.
 *
 * prePostEnabled = true  → enables @PreAuthorize and @PostAuthorize
 * securedEnabled = true  → enables @Secured (legacy annotation)
 * jsr250Enabled = true   → enables @RolesAllowed (JSR-250 standard)
 *
 * In Spring Security 6.x, @EnableMethodSecurity replaces the older
 * @EnableGlobalMethodSecurity annotation. prePostEnabled is true by default.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    prePostEnabled = true,  // enable @PreAuthorize and @PostAuthorize (default: true)
    securedEnabled = true,  // enable @Secured annotation
    jsr250Enabled  = true   // enable @RolesAllowed (Java EE standard)
)
class RbacSecurityConfig {

    // -------------------------------------------------------------------------
    // URL-based security — complements method-level security
    // -------------------------------------------------------------------------
    /**
     * URL-based security and method-level security work at different layers:
     *
     * URL security (requestMatchers):
     *   - Enforced by Spring Security's filter chain
     *   - Checked BEFORE the request reaches any controller
     *   - Good for broad patterns (/admin/**, /api/public/**)
     *
     * Method security (@PreAuthorize):
     *   - Enforced via AOP proxy around the method
     *   - Checked INSIDE the service/controller layer
     *   - Good for fine-grained business logic
     *
     * Best practice: use BOTH for defense in depth.
     * URL rules block at the gate; method rules enforce inside the building.
     */
    @Bean
    public SecurityFilterChain rbacFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // --- URL-based security rules ---
            .authorizeHttpRequests(auth -> auth

                // 1. Public — no authentication required
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/books/public/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                // 2. Role-based URL patterns
                //    hasRole("ADMIN") === hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "LIBRARIAN")

                // 3. Specific HTTP method + path pattern
                //    DELETE requires ADMIN; GET is covered by the next rule
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/books/**")
                    .hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/books/**")
                    .hasAnyRole("ADMIN", "LIBRARIAN")

                // 4. Catch-all — any unmatched request requires authentication
                //    IMPORTANT: this must be LAST — rules are evaluated top-to-bottom
                //    The first matching rule wins
                .anyRequest().authenticated()
            );

        return http.build();
    }
}


// =============================================================================
// SECTION 3: @PreAuthorize — Before-Method Authorization
// =============================================================================

/**
 * BookService demonstrates the full range of @PreAuthorize expressions.
 *
 * @PreAuthorize evaluates a Spring Expression Language (SpEL) expression
 * BEFORE the method executes. If it returns false → AccessDeniedException → 403.
 *
 * The security check happens via AOP (Spring creates a proxy around this bean).
 * The method body is NOT executed if the check fails.
 */
@Service
class BookService {

    // -------------------------------------------------------------------------
    // 3a. hasRole() — single role check
    // -------------------------------------------------------------------------
    /**
     * hasRole("ADMIN") is equivalent to hasAuthority("ROLE_ADMIN").
     * Spring automatically prepends "ROLE_" when using hasRole().
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBook(Long bookId) {
        // Only ADMIN users reach this line
        System.out.println("Deleting book: " + bookId);
    }

    // -------------------------------------------------------------------------
    // 3b. hasAnyRole() — multiple roles accepted
    // -------------------------------------------------------------------------
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public void addBookToInventory(String isbn, int quantity) {
        // ADMIN or LIBRARIAN users can add inventory
        System.out.println("Adding " + quantity + " copies of " + isbn);
    }

    // -------------------------------------------------------------------------
    // 3c. hasAuthority() — fine-grained permission check
    // -------------------------------------------------------------------------
    /**
     * When using custom permissions/authorities (not roles), use hasAuthority().
     * hasAuthority() does NOT prepend "ROLE_" — it matches the string exactly.
     */
    @PreAuthorize("hasAuthority('books:publish')")
    public void publishBook(Long bookId) {
        // Only users with the specific 'books:publish' authority
        System.out.println("Publishing book: " + bookId);
    }

    // -------------------------------------------------------------------------
    // 3d. Compound expressions with AND / OR
    // -------------------------------------------------------------------------
    @PreAuthorize("hasRole('ADMIN') and isAuthenticated()")
    public List<String> getAllUserEmails() {
        // Must be authenticated AND have ADMIN role
        return List.of("alice@example.com", "bob@example.com");
    }

    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public List<String> getInventoryReport() {
        // LIBRARIAN OR ADMIN — equivalent to hasAnyRole()
        return List.of("report data");
    }

    // -------------------------------------------------------------------------
    // 3e. #parameterName — checking method arguments in the expression
    // -------------------------------------------------------------------------
    /**
     * #username refers to the method parameter named 'username'.
     * authentication.name is the currently logged-in user's username.
     *
     * This pattern enforces: "users can only read their own order history".
     * ADMIN can read anyone's history.
     */
    @PreAuthorize("#username == authentication.name or hasRole('ADMIN')")
    public List<String> getOrderHistory(String username) {
        // A user can only access their OWN orders, unless they're ADMIN
        // Example: alice can call getOrderHistory("alice") but not getOrderHistory("bob")
        //          ADMIN can call getOrderHistory("anyone")
        System.out.println("Fetching order history for: " + username);
        return List.of("Order #1001", "Order #1002");
    }

    // -------------------------------------------------------------------------
    // 3f. authentication — accessing the full Authentication object in SpEL
    // -------------------------------------------------------------------------
    /**
     * The 'authentication' variable in SpEL gives access to the full
     * Authentication object: .name, .authorities, .principal, .credentials
     */
    @PreAuthorize("authentication.name != null and authentication.name.length() > 0")
    public String getPersonalizedGreeting() {
        // Verify we have a valid non-empty username
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return "Welcome back, " + name + "!";
    }

    // -------------------------------------------------------------------------
    // 3g. @PreAuthorize with a custom SpEL bean reference
    // -------------------------------------------------------------------------
    /**
     * You can call a Spring bean from SpEL using @beanName.methodName(args).
     * This enables complex authorization logic without cluttering the annotation.
     *
     * Pattern: @PreAuthorize("@bookAuthorizationService.canEditBook(authentication, #bookId)")
     */
    @PreAuthorize("@bookAuthorizationService.canEditBook(authentication, #bookId)")
    public void editBook(Long bookId, String newTitle) {
        // Delegates to BookAuthorizationService.canEditBook() for the decision
        System.out.println("Editing book " + bookId + ": " + newTitle);
    }
}


// =============================================================================
// SECTION 4: @PostAuthorize — After-Method Authorization
// =============================================================================

@Service
class BookQueryService {

    // -------------------------------------------------------------------------
    // 4a. @PostAuthorize — check the RETURN VALUE before returning it
    // -------------------------------------------------------------------------
    /**
     * @PostAuthorize evaluates AFTER the method runs.
     * 'returnObject' is the SpEL variable that holds the method's return value.
     *
     * Use case: a user can call getBook(), but can only READ the result
     * if the book belongs to their bookstore branch.
     *
     * Note: the method ALWAYS executes — database query happens regardless.
     * PostAuthorize only controls whether the result is returned or a 403 is thrown.
     * Use @PreAuthorize when you want to prevent execution entirely.
     */
    @PostAuthorize("returnObject.ownerUsername == authentication.name or hasRole('ADMIN')")
    public BookRecord getBookRecord(Long bookId) {
        // This always runs — the DB query happens
        // But if the returned BookRecord.ownerUsername != current user, 403 is thrown
        return new BookRecord(bookId, "Clean Code", "alice@bookstore.com");
    }

    // -------------------------------------------------------------------------
    // 4b. @PostAuthorize with a null check
    // -------------------------------------------------------------------------
    @PostAuthorize("returnObject == null or returnObject.public == true or hasRole('ADMIN')")
    public BookRecord findDraftBook(Long bookId) {
        // Draft books (public=false) are admin-only
        return new BookRecord(bookId, "Draft Title", "editor@bookstore.com");
    }

    // Simple record to demonstrate PostAuthorize
    record BookRecord(Long id, String title, String ownerUsername) {
        boolean isPublic() { return true; } // simplified
    }
}


// =============================================================================
// SECTION 5: @Secured — Legacy Annotation
// =============================================================================

@Service
class LegacyBookService {

    // -------------------------------------------------------------------------
    // 5a. @Secured — older annotation, role names only (no SpEL)
    // -------------------------------------------------------------------------
    /**
     * @Secured is the older Spring Security annotation. It accepts role strings
     * and requires the full "ROLE_" prefix — does NOT auto-prepend it.
     *
     * Limitations vs @PreAuthorize:
     *   - No SpEL expressions — role strings only
     *   - Can't check method parameters or return values
     *   - Can't use AND/OR logic
     *
     * Use @PreAuthorize for new code. @Secured exists for legacy compatibility.
     */
    @Secured("ROLE_ADMIN")
    public void performAdminAction(String action) {
        System.out.println("Admin action: " + action);
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})  // OR semantics — any of these roles
    public void performLibrarianAction(String action) {
        System.out.println("Librarian action: " + action);
    }
}


// =============================================================================
// SECTION 6: Custom Authorization Logic
// =============================================================================

/**
 * BookAuthorizationService — a Spring bean used in @PreAuthorize SpEL expressions.
 * Encapsulates complex authorization decisions that don't fit in a one-liner annotation.
 *
 * Referenced as: @PreAuthorize("@bookAuthorizationService.canEditBook(authentication, #bookId)")
 */
@Service("bookAuthorizationService") // bean name must match the @beanName in SpEL
class BookAuthorizationService {

    // -------------------------------------------------------------------------
    // 6a. Complex authorization logic — own resource or admin
    // -------------------------------------------------------------------------
    /**
     * Returns true if the authenticated user is allowed to edit this book.
     * Rules:
     *   - ADMIN can always edit
     *   - The book's original author can edit
     *   - No one else can
     */
    public boolean canEditBook(Authentication authentication, Long bookId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Check if user has ADMIN role
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));

        if (isAdmin) {
            return true;
        }

        // Check if the book belongs to this user
        String currentUsername = authentication.getName();
        String bookAuthorUsername = findBookAuthor(bookId);
        return currentUsername.equals(bookAuthorUsername);
    }

    // -------------------------------------------------------------------------
    // 6b. Business-rule based authorization
    // -------------------------------------------------------------------------
    /**
     * Returns true if the user can access premium book content.
     * Rules:
     *   - ADMIN always has access
     *   - Users with "plan:premium" authority have access
     *   - Regular users don't
     */
    public boolean canAccessPremiumContent(Authentication authentication) {
        if (authentication == null) return false;

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return authorities.contains("ROLE_ADMIN") || authorities.contains("plan:premium");
    }

    private String findBookAuthor(Long bookId) {
        // In a real app, this would query the database
        return "alice@bookstore.com";
    }
}


// =============================================================================
// SECTION 7: SecurityContextHolder in Controllers and Services
// =============================================================================

/**
 * Demonstrates multiple ways to access the authenticated principal
 * from within a controller or service.
 */
@RestController
@RequestMapping("/api/rbac-demo")
class RbacDemoController {

    // -------------------------------------------------------------------------
    // 7a. SecurityContextHolder — direct access (verbose, but always works)
    // -------------------------------------------------------------------------
    @GetMapping("/context-holder")
    public ResponseEntity<?> usingContextHolder() {
        // Get the SecurityContext for the current thread
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        String username     = auth.getName();                  // "sub" claim
        Object principal    = auth.getPrincipal();             // UserDetails object
        var    authorities  = auth.getAuthorities();           // roles/permissions

        return ResponseEntity.ok(Map.of(
                "username",    username,
                "authorities", authorities.toString(),
                "principalType", principal.getClass().getSimpleName()
        ));
    }

    // -------------------------------------------------------------------------
    // 7b. @AuthenticationPrincipal — Spring MVC injects UserDetails automatically
    // -------------------------------------------------------------------------
    @GetMapping("/principal")
    public ResponseEntity<?> usingPrincipal(
            @AuthenticationPrincipal UserDetails currentUser) {
        // Spring MVC extracts currentUser from SecurityContextHolder automatically
        // Much cleaner than calling SecurityContextHolder directly
        return ResponseEntity.ok(Map.of(
                "username",  currentUser.getUsername(),
                "enabled",   currentUser.isEnabled(),
                "roles",     currentUser.getAuthorities()
        ));
    }

    // -------------------------------------------------------------------------
    // 7c. Authentication parameter — Spring MVC injects it directly
    // -------------------------------------------------------------------------
    @GetMapping("/authentication")
    public ResponseEntity<?> usingAuthentication(Authentication authentication) {
        // Spring MVC also injects the Authentication object directly
        // Same data as SecurityContextHolder, just cleaner syntax
        return ResponseEntity.ok(Map.of(
                "name",         authentication.getName(),
                "authenticated", authentication.isAuthenticated(),
                "details",      authentication.getDetails()
        ));
    }

    // -------------------------------------------------------------------------
    // 7d. @PreAuthorize on controller methods
    // -------------------------------------------------------------------------
    /**
     * Method security works on controllers too — not just services.
     * But best practice is to put authorization on the service layer
     * so it's enforced even if called from non-web contexts.
     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminStats() {
        return ResponseEntity.ok(Map.of(
                "totalBooks", 1500,
                "totalUsers", 320,
                "activeLoans", 47
        ));
    }

    // -------------------------------------------------------------------------
    // 7e. Ownership check — user can only access their own data
    // -------------------------------------------------------------------------
    @GetMapping("/users/{username}/profile")
    @PreAuthorize("#username == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<?> getUserProfile(@PathVariable String username) {
        // A user can only access their own profile
        // ADMIN can access any profile
        return ResponseEntity.ok(Map.of(
                "username", username,
                "profile",  "Profile data for " + username
        ));
    }

    // -------------------------------------------------------------------------
    // 7f. Checking roles programmatically inside a method
    // -------------------------------------------------------------------------
    @GetMapping("/role-check")
    public ResponseEntity<?> checkRolesProgrammatically() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));

        boolean isLibrarian = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_LIBRARIAN"));

        // Business logic based on role
        if (isAdmin) {
            return ResponseEntity.ok(Map.of("view", "full admin dashboard"));
        } else if (isLibrarian) {
            return ResponseEntity.ok(Map.of("view", "librarian inventory view"));
        } else {
            return ResponseEntity.ok(Map.of("view", "standard user view"));
        }
    }
}
