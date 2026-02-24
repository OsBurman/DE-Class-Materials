package com.jwt.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Minimal controller with two endpoints:
 *   GET /api/public  – accessible to any authenticated user (JWT or API key)
 *   GET /api/admin   – accessible only to users with ROLE_ADMIN
 *
 * No changes needed here.
 */
@RestController
public class SecureController {

    @GetMapping("/api/public")
    public ResponseEntity<String> publicData() {
        return ResponseEntity.ok("Public data – you are authenticated");
    }

    @GetMapping("/api/admin")
    public ResponseEntity<String> adminData() {
        return ResponseEntity.ok("Admin data – ROLE_ADMIN confirmed");
    }
}
