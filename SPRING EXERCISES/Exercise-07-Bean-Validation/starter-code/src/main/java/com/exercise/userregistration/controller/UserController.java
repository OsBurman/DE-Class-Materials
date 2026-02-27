package com.exercise.userregistration.controller;

import com.exercise.userregistration.dto.LoginRequest;
import com.exercise.userregistration.dto.UserRegistrationRequest;
import com.exercise.userregistration.dto.UserResponse;
import com.exercise.userregistration.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // TODO 12: Add @Valid before @RequestBody on the register and login methods.
    // @Valid tells Spring MVC to run Bean Validation on the request body
    // BEFORE the method body executes. If validation fails, Spring throws
    // MethodArgumentNotValidException, which your GlobalExceptionHandler catches.
    //
    // Without @Valid: validation annotations are IGNORED!
    // With @Valid: invalid requests never reach service code.
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRegistrationRequest request) {
        // TODO 12: Add @Valid before @RequestBody above ↑
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        // TODO 12: Add @Valid before @RequestBody above ↑
        // Simplified login — just verify username exists (no real auth yet, that's
        // Exercise 08)
        return ResponseEntity.ok(Map.of("message", "Login successful (auth coming in Exercise 08!)"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
