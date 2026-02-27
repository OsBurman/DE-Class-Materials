package com.exercise.taskmanager.controller;

import com.exercise.taskmanager.entity.Priority;
import com.exercise.taskmanager.entity.Task;
import com.exercise.taskmanager.entity.TaskStatus;
import com.exercise.taskmanager.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO 16: Add @RestController and @RequestMapping("/api/tasks")
//          Inject TaskService via constructor.
//          Implement all 8 endpoints delegating to taskService.
//          For 404 cases, return ResponseEntity.notFound().build()
//          For NoSuchElementException from service, handle in the controller method
//          (we'll learn @ControllerAdvice for this in Exercise 06!)
public class TaskController {

    // TODO: inject TaskService

    // GET /api/tasks?status=PENDING (status is optional)
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(required = false) TaskStatus status) {
        // your code here
        return null;
    }

    // GET /api/tasks/{id}
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        // your code here
        return null;
    }

    // GET /api/tasks/priority/{level} e.g. /priority/HIGH
    public ResponseEntity<List<Task>> getTasksByPriority(@PathVariable Priority priority) {
        // your code here
        return null;
    }

    // GET /api/tasks/overdue
    public ResponseEntity<List<Task>> getOverdueTasks() {
        // your code here
        return null;
    }

    // POST /api/tasks
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        // your code here
        return null;
    }

    // PUT /api/tasks/{id}
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        // your code here
        return null;
    }

    // PATCH /api/tasks/{id}/complete
    public ResponseEntity<Task> completeTask(@PathVariable Long id) {
        // your code here
        return null;
    }

    // DELETE /api/tasks/{id}
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        // your code here
        return null;
    }
}
