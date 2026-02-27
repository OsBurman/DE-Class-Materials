package com.exercise.securednotes.repository;

import com.exercise.securednotes.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByOwnerUsername(String ownerUsername);
    Optional<Note> findByIdAndOwnerUsername(Long id, String ownerUsername);
}
