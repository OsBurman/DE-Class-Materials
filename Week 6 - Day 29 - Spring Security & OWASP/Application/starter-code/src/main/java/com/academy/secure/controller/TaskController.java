package com.academy.secure.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Task controller with method-level security.
 *
 * TODO Task 6: Add @PreAuthorize annotations to the appropriate methods.
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    // In-memory tasks for simplicity
    private final List<Map<String, String>> tasks = new java.util.ArrayList<>(List.of(
            Map.of("id", "1", "title", "Set up project", "status", "DONE"),
            Map.of("id", "2", "title", "Write unit tests", "status", "IN_PROGRESS"),
            Map.of("id", "3", "title", "Deploy to production", "status", "TODO")));

    // GET /api/tasks — any authenticated user
    @GetMapping
    public List<Map<String, String>> getAllTasks() {
        return tasks;
    }

    // POST /api/tasks — requires ROLE_USER or ROLE_ADMIN
    // TODO Task 6: Add @PreAuthorize
    @PostMapping
    public Map<String, String> createTask(@RequestBody Map<String, String> task) {
        tasks.add(task);
        return task;
    }

    // DELETE /api/tasks/{id} — requires ROLE_ADMIN only
    // TODO Task 6: Add @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public Map<String, String> deleteTask(@PathVariable String id) {
        tasks.removeIf(t -> t.get("id").equals(id));
        return Map.of("deleted", id);
    }
}
