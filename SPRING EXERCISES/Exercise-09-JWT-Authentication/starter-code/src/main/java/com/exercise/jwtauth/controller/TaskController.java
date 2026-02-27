package com.exercise.jwtauth.controller;

import com.exercise.jwtauth.dto.TaskRequest;
import com.exercise.jwtauth.dto.TaskResponse;
import com.exercise.jwtauth.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(Principal principal) {
        return ResponseEntity.ok(taskService.getTasksForUser(principal.getName()));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request,
                                                    Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(request, principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
                                                    @Valid @RequestBody TaskRequest request,
                                                    Principal principal) {
        return ResponseEntity.ok(taskService.updateTask(id, request, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Principal principal) {
        taskService.deleteTask(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
