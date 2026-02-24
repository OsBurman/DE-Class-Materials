package com.jwt.security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
