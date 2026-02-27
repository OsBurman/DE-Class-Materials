package com.exercise.taskmanager.service;

import com.exercise.taskmanager.entity.Priority;
import com.exercise.taskmanager.entity.Task;
import com.exercise.taskmanager.entity.TaskStatus;
import com.exercise.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getAllTasks(TaskStatus status) {
        if (status == null) return taskRepository.findAll();
        return taskRepository.findByStatus(status);
    }

    @Override
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
    public List<Task> getTasksByPriority(Priority priority) {
        return taskRepository.findByPriority(priority);
    }

    @Override
    public List<Task> getOverdueTasks() {
        return taskRepository.findOverdueTasks();
    }

    @Override
    public Task createTask(Task task) {
        task.setId(null);
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long id, Task updated) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found: " + id));
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());
        existing.setPriority(updated.getPriority());
        existing.setDueDate(updated.getDueDate());
        return taskRepository.save(existing);
    }

    @Override
    public Task completeTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found: " + id));
        task.setStatus(TaskStatus.COMPLETED);
        return taskRepository.save(task);
    }

    @Override
    public boolean deleteTask(Long id) {
        if (!taskRepository.existsById(id)) return false;
        taskRepository.deleteById(id);
        return true;
    }
}
