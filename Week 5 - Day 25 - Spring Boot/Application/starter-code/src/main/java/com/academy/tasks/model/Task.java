package com.academy.tasks.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a Task in the system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    private Long id;

    // TODO Task 1: Add @NotBlank validation annotation
    // @NotBlank(message = "Title must not be blank")
    @Size(min = 1, max = 200)
    private String title;

    @Size(max = 1000)
    private String description;

    // TODO Task 1: Add @NotNull validation annotation
    private Priority priority;

    // TODO Task 1: Add @NotNull validation annotation
    private Status status;

    private LocalDate dueDate;

    // Auto-set by the service layer — not provided by the client
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // TODO Task 2: Define the Priority enum as a nested enum inside this class
    //   Values: LOW, MEDIUM, HIGH, CRITICAL
    public enum Priority {
        // TODO
    }

    // TODO Task 2: Define the Status enum as a nested enum inside this class
    //   Values: TODO, IN_PROGRESS, DONE, CANCELLED
    public enum Status {
        // TODO
    }
}
