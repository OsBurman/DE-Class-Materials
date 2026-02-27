package com.exercise.taskmanager.repository;

import com.exercise.taskmanager.entity.Priority;
import com.exercise.taskmanager.entity.Task;
import com.exercise.taskmanager.entity.TaskStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// TODO 10: Make TaskRepository extend JpaRepository<Task, Long>.
//          This gives you 18+ free methods: save(), findById(), findAll(),
//          deleteById(), count(), existsById(), saveAll(), findAllById(), etc.
//          No implementation needed — Spring Data generates everything at runtime!
@Repository
public interface TaskRepository {

    // TODO 11: Add a derived query method: findByStatus(TaskStatus status)
    // Spring reads the method name and generates:
    // SELECT * FROM tasks WHERE status = ?
    // No @Query annotation needed!

    // TODO 12: Add a derived query method: findByPriority(Priority priority)

    // TODO 13: Add a custom JPQL query for overdue tasks.
    // Use @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_DATE AND t.status
    // != 'COMPLETED'")
    // Name the method: findOverdueTasks()

    // TODO 14: Add a derived count query: countByStatus(TaskStatus status)
    // Returns long — the number of tasks with that status.
}
