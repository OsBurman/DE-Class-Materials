package com.exercise.jwtauth.service;

import com.exercise.jwtauth.dto.TaskRequest;
import com.exercise.jwtauth.dto.TaskResponse;
import com.exercise.jwtauth.entity.Task;
import com.exercise.jwtauth.exception.ResourceNotFoundException;
import com.exercise.jwtauth.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskResponse> getTasksForUser(String username) {
        return taskRepository.findByOwnerUsername(username).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public TaskResponse createTask(TaskRequest request, String username) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());
        task.setOwnerUsername(username);
        return toResponse(taskRepository.save(task));
    }

    public TaskResponse updateTask(Long id, TaskRequest request, String username) {
        Task task = taskRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());
        return toResponse(taskRepository.save(task));
    }

    public void deleteTask(Long id, String username) {
        Task task = taskRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
        taskRepository.delete(task);
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private TaskResponse toResponse(Task task) {
        TaskResponse r = new TaskResponse();
        r.setId(task.getId());
        r.setTitle(task.getTitle());
        r.setDescription(task.getDescription());
        r.setCompleted(task.isCompleted());
        r.setOwnerUsername(task.getOwnerUsername());
        r.setCreatedAt(task.getCreatedAt());
        return r;
    }
}
