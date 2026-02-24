package com.academy.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Protected resource — demonstrates RBAC with JWT.
 *
 * TODO Task 5: Add @PreAuthorize annotations to each endpoint.
 * The roles are: ROLE_VIEWER, ROLE_USER, ROLE_ADMIN (see Role.java).
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ResourceController {

    private final List<Map<String, Object>> resources = new ArrayList<>(List.of(
        Map.of("id", 1L, "name", "Public Report",     "owner", "admin"),
        Map.of("id", 2L, "name", "User Dashboard",    "owner", "user"),
        Map.of("id", 3L, "name", "Admin Console",     "owner", "admin")
    ));
    private final AtomicLong nextId = new AtomicLong(4);

    // TODO Task 5a: accessible by VIEWER, USER, and ADMIN
    @GetMapping("/resources")
    public ResponseEntity<List<Map<String, Object>>> getAllResources(Authentication authentication) {
        log.info("User '{}' is accessing all resources", authentication.getName());
        return ResponseEntity.ok(resources);
    }

    // TODO Task 5b: accessible only by USER and ADMIN
    @PostMapping("/resources")
    public ResponseEntity<Map<String, Object>> createResource(
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        // TODO: create resource and add to list
        Map<String, Object> resource = Map.of(
            "id",    nextId.getAndIncrement(),
            "name",  body.getOrDefault("name", "Unnamed"),
            "owner", authentication.getName()
        );
        resources.add(resource);
        return ResponseEntity.status(201).body(resource);
    }

    // TODO Task 5c: accessible only by ADMIN
    @DeleteMapping("/resources/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id, Authentication authentication) {
        log.info("Admin '{}' deleting resource {}", authentication.getName(), id);
        resources.removeIf(r -> r.get("id").equals(id));
        return ResponseEntity.noContent().build();
    }

    // TODO Task 5d: accessible only by ADMIN
    @GetMapping("/admin/users")
    public ResponseEntity<List<String>> listUsers() {
        return ResponseEntity.ok(List.of("admin", "user", "viewer"));
    }

    // Any authenticated user can see their own info
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
            "username",    authentication.getName(),
            "authorities", authentication.getAuthorities().toString()
        ));
    }
}
