package com.academy.tasks.controller;

import com.academy.tasks.model.Task;
import com.academy.tasks.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST Controller for Task CRUD operations.
 *
 * Base URL: /api/tasks
 */
@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // TODO Task 5: Implement GET /api/tasks
    // - Optional query params: ?status=TODO and ?priority=HIGH
    // - If status param is present, return filtered list
    // - If priority param is present, return filtered list
    // - Otherwise return all tasks
    // - Return 200 OK with List<Task>
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(required = false) Task.Status status,
            @RequestParam(required = false) Task.Priority priority) {
        // TODO
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    // TODO Task 5: Implement GET /api/tasks/{id}
    // - Return 200 with the task if found
    // - Return 404 if not found
    // Hint: use taskService.getTaskById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build())
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        // TODO
        return ResponseEntity.notFound().build();
    }

    // TODO Task 5: Implement POST /api/tasks
    // - Use @Valid to trigger validation
    // - Return 201 Created with a Location header pointing to /api/tasks/{newId}
    // Hint:
    //   URI location = ServletUriComponentsBuilder.fromCurrentRequest()
    //       .path("/{id}").buildAndExpand(created.getId()).toUri();
    //   return ResponseEntity.created(location).body(created);
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        // TODO
        return ResponseEntity.ok().build();
    }

    // TODO Task 5: Implement PUT /api/tasks/{id}
    // - Return 200 with updated task
    // - Return 404 if not found (catch RuntimeException from service)
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        // TODO
        return ResponseEntity.ok().build();
    }

    // TODO Task 5: Implement DELETE /api/tasks/{id}
    // - Return 204 No Content on success
    // - Return 404 if not found
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        // TODO
        return ResponseEntity.noContent().build();
    }
}
