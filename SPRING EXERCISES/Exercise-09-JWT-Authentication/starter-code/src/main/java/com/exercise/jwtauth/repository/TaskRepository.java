package com.exercise.jwtauth.repository;

import com.exercise.jwtauth.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwnerUsername(String ownerUsername);
    Optional<Task> findByIdAndOwnerUsername(Long id, String ownerUsername);
}
