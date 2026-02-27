package com.exercise.taskmanager.service;

import com.exercise.taskmanager.entity.Priority;
import com.exercise.taskmanager.entity.Task;
import com.exercise.taskmanager.entity.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    List<Task> getAllTasks(TaskStatus status);
    Optional<Task> getTaskById(Long id);
    List<Task> getTasksByPriority(Priority priority);
    List<Task> getOverdueTasks();
    Task createTask(Task task);
    Task updateTask(Long id, Task updated);
    Task completeTask(Long id);
    boolean deleteTask(Long id);
}
