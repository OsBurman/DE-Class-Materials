package com.exercise.taskmanager.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

// TODO 4: Add @Entity to tell JPA this class maps to a database table.
//         Add @Table(name = "tasks") to explicitly name the table "tasks".
public class Task {

    // TODO 5: Mark this as the primary key with @Id.
    //         Use @GeneratedValue(strategy = GenerationType.IDENTITY) for auto-increment.
    private Long id;

    // TODO 6: Add @Column(nullable = false) — title is required.
    private String title;

    private String description;

    // TODO 7: Add @Enumerated(EnumType.STRING) so the status is stored as
    //         the string "PENDING" rather than 0, 1, 2, 3.
    //         This makes the DB data human-readable.
    private TaskStatus status = TaskStatus.PENDING;

    // TODO 7 (continued): Same for priority.
    private Priority priority = Priority.MEDIUM;

    private LocalDate dueDate;

    // TODO 8: Add @CreationTimestamp — Hibernate will automatically set this
    //         to the current timestamp when the record is first inserted.
    //         Also add @Column(updatable = false) so it's never changed after insert.
    private LocalDateTime createdAt;

    // TODO 9: Add @UpdateTimestamp — Hibernate will automatically update this
    //         to the current timestamp every time the record is updated.
    private LocalDateTime updatedAt;

    public Task() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
