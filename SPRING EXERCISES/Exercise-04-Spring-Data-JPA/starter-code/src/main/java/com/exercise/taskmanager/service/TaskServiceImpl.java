package com.exercise.taskmanager.service;

import com.exercise.taskmanager.entity.Priority;
import com.exercise.taskmanager.entity.Task;
import com.exercise.taskmanager.entity.TaskStatus;
import com.exercise.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

// TODO 15: Implement all TaskService methods below.
//          Inject TaskRepository via constructor injection.
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getAllTasks(TaskStatus status) {
        // If status is null, return all tasks (taskRepository.findAll())
        // Otherwise, return tasks filtered by status (taskRepository.findByStatus(status))
        // your code here
        return null;
    }

    @Override
    public Optional<Task> getTaskById(Long id) {
        // your code here
        return null;
    }

    @Override
    public List<Task> getTasksByPriority(Priority priority) {
        // your code here
        return null;
    }

    @Override
    public List<Task> getOverdueTasks() {
        // your code here
        return null;
    }

    @Override
    public Task createTask(Task task) {
        // Reset id (don't trust client-provided IDs), save and return
        task.setId(null);
        // your code here
        return null;
    }

    @Override
    public Task updateTask(Long id, Task updated) {
        // Find existing task or throw NoSuchElementException("Task not found: " + id)
        // Copy fields: title, description, status, priority, dueDate
        // Save and return
        // your code here
        return null;
    }

    @Override
    public Task completeTask(Long id) {
        // Find task or throw NoSuchElementException
        // Set status to COMPLETED
        // Save and return
        // your code here
        return null;
    }

    @Override
    public boolean deleteTask(Long id) {
        // Check existsById, then deleteById, return true
        // If not found, return false
        // your code here
        return false;
    }
}
