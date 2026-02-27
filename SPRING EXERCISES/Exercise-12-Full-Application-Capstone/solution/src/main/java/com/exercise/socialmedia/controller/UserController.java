package com.exercise.socialmedia.controller;

import com.exercise.socialmedia.dto.UserProfileResponse;
import com.exercise.socialmedia.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<Map<String, String>> follow(@PathVariable Long id, Principal principal) {
        userService.follow(id, principal.getName());
        return ResponseEntity.ok(Map.of("message", "Now following user " + id));
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Map<String, String>> unfollow(@PathVariable Long id, Principal principal) {
        userService.unfollow(id, principal.getName());
        return ResponseEntity.ok(Map.of("message", "Unfollowed user " + id));
    }
}
