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

    @GetMapping("/notes")
    public ResponseEntity<List<NoteResponse>> getAllNotes() {
        return ResponseEntity.ok(noteService.getAllNotes());
    }
}
