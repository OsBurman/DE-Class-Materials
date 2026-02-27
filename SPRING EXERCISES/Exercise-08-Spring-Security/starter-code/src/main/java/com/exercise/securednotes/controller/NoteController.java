package com.exercise.securednotes.controller;

import com.exercise.securednotes.dto.NoteRequest;
import com.exercise.securednotes.dto.NoteResponse;
import com.exercise.securednotes.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // TODO 5: Spring automatically injects the Principal parameter — it represents the
    //         currently authenticated user. Call principal.getName() to get the username.
    //         Remove the placeholder "unknown" and use principal.getName() instead.
    @GetMapping
    public ResponseEntity<List<NoteResponse>> getNotes(Principal principal) {
        // TODO 5: Replace "unknown" with principal.getName()
        return ResponseEntity.ok(noteService.getNotesForUser("unknown"));
    }

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody NoteRequest request,
                                                   Principal principal) {
        // TODO 5: Replace "unknown" with principal.getName()
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noteService.createNote(request, "unknown"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(noteService.getNoteByIdForUser(id, principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(@PathVariable Long id,
                                                   @Valid @RequestBody NoteRequest request,
                                                   Principal principal) {
        return ResponseEntity.ok(noteService.updateNote(id, request, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id, Principal principal) {
        noteService.deleteNote(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
