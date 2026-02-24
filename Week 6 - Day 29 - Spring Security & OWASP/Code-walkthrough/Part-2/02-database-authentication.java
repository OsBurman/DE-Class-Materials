package com.bookstore.security;

// =============================================================================
// DATABASE AUTHENTICATION WITH UserDetailsService
// =============================================================================
// In-memory authentication is fine for demos, but real applications need users
// stored in a database so:
//   - Users can be added/removed without redeploying
//   - Passwords can be reset
//   - User data persists across restarts
//   - You can store additional user attributes (email, profile, etc.)
//
// Spring Security's database authentication uses three key interfaces:
//
//   UserDetailsService  — "Where do I load a user from?" (the data source adapter)
//   UserDetails         — "What does a user look like to Spring Security?"
//   AuthenticationProvider — "How do I verify credentials?"
//
// Flow:
//   1. User submits username + password
//   2. UsernamePasswordAuthenticationFilter creates an unauthenticated token
//   3. AuthenticationManager delegates to DaoAuthenticationProvider
//   4. DaoAuthenticationProvider calls UserDetailsService.loadUserByUsername()
//   5. UserDetailsService queries the database, returns UserDetails
//   6. DaoAuthenticationProvider calls passwordEncoder.matches(submitted, stored)
//   7. If match: creates authenticated token, stores in SecurityContextHolder
//   8. If no match: throws BadCredentialsException → 401 response
// =============================================================================

import com.bookstore.entity.User;
import com.bookstore.repository.UserRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;


// =============================================================================
// SECTION 1: JPA Entity — User (the database table)
// =============================================================================
// This is your application's user entity — what you control and store.
// It is NOT the Spring Security interface — we'll bridge the two next.
// =============================================================================

@Entity
@Table(name = "users")
class BookstoreUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    // Stored as a bcrypt hash — NEVER plain text
    // The column must be at least 60 characters long to hold the bcrypt hash
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    // Roles stored as a collection — could also be a separate Role entity
    // For simplicity here: comma-separated string "ROLE_USER,ROLE_ADMIN"
    // In production: use a separate join table (users ↔ roles)
    @Column(nullable = false)
    private String roles = "ROLE_USER";

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked = true;

    // Getters and setters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isAccountNonLocked() { return accountNonLocked; }
    public void setAccountNonLocked(boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; }
}


// =============================================================================
// SECTION 2: UserRepository — JPA Repository for fetching users
// =============================================================================

interface BookstoreUserRepository extends JpaRepository<BookstoreUser, Long> {
    // Spring Data JPA derives: SELECT * FROM users WHERE username = ?
    Optional<BookstoreUser> findByUsername(String username);

    // Used for registration — check if username/email already taken
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}


// =============================================================================
// SECTION 3: UserDetails — Spring Security's view of a user
// =============================================================================
// UserDetails is the interface Spring Security uses internally.
// Your JPA entity doesn't know about Spring Security.
// We need an adapter class that WRAPS your entity and IMPLEMENTS UserDetails.
//
// This is a very common pattern: create a class called BookstoreUserDetails (or
// BookstoreUserPrincipal) that holds a reference to your entity and implements
// all the UserDetails methods by delegating to it.
// =============================================================================

class BookstoreUserDetails implements UserDetails {

    // Holds the JPA entity
    private final BookstoreUser user;

    public BookstoreUserDetails(BookstoreUser user) {
        this.user = user;
    }

    // GrantedAuthority is Spring Security's representation of a permission or role.
    // Our roles column is a comma-separated string like "ROLE_USER,ROLE_ADMIN".
    // We split it and convert each role string into a SimpleGrantedAuthority.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(user.getRoles().split(","))
            .map(String::trim)
            .map(SimpleGrantedAuthority::new)  // "ROLE_USER" → SimpleGrantedAuthority("ROLE_USER")
            .collect(Collectors.toList());
    }

    // Spring Security calls this to get the stored password hash for comparison
    @Override
    public String getPassword() {
        return user.getPasswordHash();
        // This is the bcrypt hash — Spring Security's PasswordEncoder.matches()
        // will be called to compare the submitted password against this hash
    }

    // Spring Security calls this to identify the user
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // These methods allow fine-grained account status control.
    // Return false to prevent login even if credentials are correct.
    @Override
    public boolean isAccountNonExpired() {
        return true;  // implement expiry logic if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();  // delegate to the entity flag
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // implement password expiry if needed
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();  // disabled users cannot log in even with correct password
    }

    // Convenience: expose the underlying entity if needed elsewhere
    public BookstoreUser getUser() {
        return user;
    }
}


// =============================================================================
// SECTION 4: UserDetailsService — Loading Users from the Database
// =============================================================================
// UserDetailsService has ONE method: loadUserByUsername(String username).
// Spring Security calls this method when authenticating.
// You query the database, wrap the result in a UserDetails, and return it.
// If the user is not found, throw UsernameNotFoundException.
//
// This is the adapter between Spring Security and your data source.
// =============================================================================

@Service
class BookstoreUserDetailsService implements UserDetailsService {

    private final BookstoreUserRepository userRepository;

    // Constructor injection — best practice
    @Autowired
    public BookstoreUserDetailsService(BookstoreUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Query the database for the user
        BookstoreUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                // SECURITY NOTE: Do NOT say "username not found" vs "wrong password" — that
                // leaks information. Always use a generic message.
                // Spring Security handles this — it catches the exception and returns 401.
                return new UsernameNotFoundException("Authentication failed for: " + username);
            });

        // 2. Wrap in UserDetails and return
        // Spring Security will then call passwordEncoder.matches(submittedPassword, user.getPassword())
        return new BookstoreUserDetails(user);
    }
}


// =============================================================================
// SECTION 5: User Registration Service — Creating Users Securely
// =============================================================================
// When a new user registers, you MUST hash their password before saving.
// The PasswordEncoder is injected and used here.
// =============================================================================

@Service
class UserRegistrationService {

    private final BookstoreUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRegistrationService(BookstoreUserRepository userRepository,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public BookstoreUser registerUser(String username, String email, String rawPassword) {
        // 1. Validate — check for duplicates
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }

        // 2. Create user entity
        BookstoreUser newUser = new BookstoreUser();
        newUser.setUsername(username);
        newUser.setEmail(email);

        // 3. HASH the password — NEVER store the plain text
        //    passwordEncoder.encode() calls BCryptPasswordEncoder.encode()
        //    which generates a salt, applies 2^12 rounds, returns a 60-char hash
        String hashedPassword = passwordEncoder.encode(rawPassword);
        newUser.setPasswordHash(hashedPassword);

        System.out.println("Storing hashed password (NOT plain text): " + hashedPassword.substring(0, 20) + "...");

        // 4. Save to database
        return userRepository.save(newUser);
    }

    public BookstoreUser registerAdmin(String username, String email, String rawPassword) {
        BookstoreUser admin = registerUser(username, email, rawPassword);
        admin.setRoles("ROLE_USER,ROLE_ADMIN");
        return userRepository.save(admin);
    }
}


// =============================================================================
// SECTION 6: DaoAuthenticationProvider — The Authentication Provider
// =============================================================================
// AuthenticationProvider is responsible for verifying credentials.
// DaoAuthenticationProvider is Spring Security's standard implementation.
// It uses:
//   - UserDetailsService (to load the user from the database)
//   - PasswordEncoder (to verify the submitted password against the stored hash)
//
// You register it as a @Bean and wire in your UserDetailsService and PasswordEncoder.
// Then you register it with the AuthenticationManager.
// =============================================================================

@Configuration
@EnableWebSecurity
class DatabaseSecurityConfig {

    // DaoAuthenticationProvider — wires UserDetailsService + PasswordEncoder together
    // Spring Security calls:
    //   1. userDetailsService.loadUserByUsername(username)
    //   2. passwordEncoder.matches(submittedPassword, loadedUser.getPassword())
    //   3. If match: return authenticated token
    //   4. Check isEnabled(), isAccountNonLocked(), etc.
    @Bean
    public AuthenticationProvider daoAuthenticationProvider(
            BookstoreUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        // hideUserNotFoundExceptions: default true — converts UsernameNotFoundException
        // to BadCredentialsException. This prevents attackers from enumerating valid usernames
        // by observing different error messages for "user not found" vs "wrong password".
        provider.setHideUserNotFoundExceptions(true);

        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // The SecurityFilterChain wires it all together
    @Bean
    public SecurityFilterChain databaseAuthFilterChain(
            HttpSecurity http,
            AuthenticationProvider authProvider) throws Exception {
        http
            // Register our custom authentication provider
            .authenticationProvider(authProvider)

            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/register", "/login", "/", "/home").permitAll()
                .requestMatchers("/api/v1/books").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}


// =============================================================================
// SECTION 7: SecurityContextHolder — Accessing the Authenticated User
// =============================================================================
// After authentication, Spring Security stores the authenticated user's details
// in the SecurityContextHolder for the duration of the request.
// You can access it from any part of your application.
// =============================================================================

@Component
class SecurityContextHelper {

    // Get the currently authenticated user's username
    public static String getCurrentUsername() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder
            .getContext()
            .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    // Get the full UserDetails object (includes authorities)
    public static BookstoreUserDetails getCurrentUserDetails() {
        var auth = org.springframework.security.core.context.SecurityContextHolder
            .getContext()
            .getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof BookstoreUserDetails)) {
            return null;
        }
        return (BookstoreUserDetails) auth.getPrincipal();
    }

    // Check if the current user has a specific role
    public static boolean hasRole(String role) {
        var auth = org.springframework.security.core.context.SecurityContextHolder
            .getContext()
            .getAuthentication();

        if (auth == null) return false;
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}


// =============================================================================
// SECTION 8: Summary of the Complete Authentication Flow
// =============================================================================
/*
  COMPLETE FLOW DIAGRAM:

  User POST /login (username=alice, password=secret)
      ↓
  UsernamePasswordAuthenticationFilter
      → extracts username and password from request
      → creates UsernamePasswordAuthenticationToken(alice, secret) [unauthenticated]
      ↓
  AuthenticationManager.authenticate(token)
      ↓
  DaoAuthenticationProvider.authenticate(token)
      → calls BookstoreUserDetailsService.loadUserByUsername("alice")
          → SELECT * FROM users WHERE username = 'alice'
          → wraps result in BookstoreUserDetails
      → calls BCryptPasswordEncoder.matches("secret", storedHash)
          → re-hashes "secret" with the salt from storedHash
          → compares with stored hash (constant time)
      → checks isEnabled(), isAccountNonLocked(), etc.
      ↓
  If success:
      → creates UsernamePasswordAuthenticationToken(userDetails, null, authorities) [authenticated]
      → stores in SecurityContextHolder
      → triggers session creation / JWT generation
      → redirects to defaultSuccessUrl
  If failure:
      → throws BadCredentialsException
      → ExceptionTranslationFilter catches it
      → redirects to failureUrl with error parameter
*/
