package com.academy.tasks.service;

import com.academy.tasks.model.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Business logic for Task CRUD operations.
 * Uses an in-memory ConcurrentHashMap as the data store.
 */
@Slf4j
@Service
public class TaskService {

    // Thread-safe in-memory store: taskId → Task
    private final Map<Long, Task> taskStore = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    // TODO Task 3: Implement getAllTasks()
    // Return a new ArrayList of all values in taskStore
    public List<Task> getAllTasks() {
        // TODO
        return new ArrayList<>();
    }

    // TODO Task 3: Implement getTaskById(Long id)
    // Return Optional.ofNullable(taskStore.get(id))
    public Optional<Task> getTaskById(Long id) {
        // TODO
        return Optional.empty();
    }

    // TODO Task 3: Implement createTask(Task task)
    // 1. Generate a new ID using idSequence.getAndIncrement()
    // 2. Set createdAt and updatedAt to LocalDateTime.now()
    // 3. Set status to TODO if null
    // 4. Put in taskStore
    // 5. Log: "Created task #{id}: {title}"
    // 6. Return the saved task
    public Task createTask(Task task) {
        // TODO
        return task;
    }

    // TODO Task 3: Implement updateTask(Long id, Task updated)
    // 1. Check if task exists — if not, throw RuntimeException("Task not found: " + id)
    // 2. Update title, description, priority, status, dueDate from the updated object
    // 3. Set updatedAt = LocalDateTime.now()
    // 4. Put back in store and return
    public Task updateTask(Long id, Task updated) {
        // TODO
        return updated;
    }

    // TODO Task 3: Implement deleteTask(Long id)
    // 1. Check if task exists — if not, throw RuntimeException("Task not found: " + id)
    // 2. Remove from taskStore
    // 3. Log: "Deleted task #{id}"
    public void deleteTask(Long id) {
        // TODO
    }

    // TODO Task 4: Implement getTasksByStatus(Task.Status status)
    // Filter taskStore values where task.getStatus() == status
    // Return as a List
    public List<Task> getTasksByStatus(Task.Status status) {
        // TODO
        return new ArrayList<>();
    }

    // TODO Task 4: Implement getTasksByPriority(Task.Priority priority)
    // Similar to getTasksByStatus
    public List<Task> getTasksByPriority(Task.Priority priority) {
        // TODO
        return new ArrayList<>();
    }
}
