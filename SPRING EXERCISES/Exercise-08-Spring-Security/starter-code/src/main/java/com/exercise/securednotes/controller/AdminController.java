package com.exercise.securednotes.controller;

import com.exercise.securednotes.dto.NoteResponse;
import com.exercise.securednotes.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final NoteService noteService;

    public AdminController(NoteService noteService) {
        this.noteService = noteService;
    }

    // TODO 6: This endpoint is at /api/admin/notes.
    //         The SecurityFilterChain already restricts /api/admin/** to ADMIN role only.
    //         No additional annotations needed here — just implement the method to return all notes.
    //
    //         Notice: Security is CENTRALIZED in SecurityConfig, not scattered across controllers.
    //         This is the advantage of Spring Security's filter-based approach.
    @GetMapping("/notes")
    public ResponseEntity<List<NoteResponse>> getAllNotes() {
        // TODO 6: Call noteService.getAllNotes() and return it with ResponseEntity.ok()
        return null;
    }
}
