package com.exercise.securednotes.service;

import com.exercise.securednotes.dto.NoteRequest;
import com.exercise.securednotes.dto.NoteResponse;
import com.exercise.securednotes.entity.Note;
import com.exercise.securednotes.exception.ResourceNotFoundException;
import com.exercise.securednotes.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<NoteResponse> getNotesForUser(String username) {
        return noteRepository.findByOwnerUsername(username).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public NoteResponse createNote(NoteRequest request, String username) {
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setOwnerUsername(username);
        return toResponse(noteRepository.save(note));
    }

    public NoteResponse getNoteByIdForUser(Long id, String username) {
        Note note = noteRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Note", id));
        return toResponse(note);
    }

    public NoteResponse updateNote(Long id, NoteRequest request, String username) {
        Note note = noteRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Note", id));
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        return toResponse(noteRepository.save(note));
    }

    public void deleteNote(Long id, String username) {
        Note note = noteRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Note", id));
        noteRepository.delete(note);
    }

    public List<NoteResponse> getAllNotes() {
        return noteRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private NoteResponse toResponse(Note note) {
        NoteResponse r = new NoteResponse();
        r.setId(note.getId());
        r.setTitle(note.getTitle());
        r.setContent(note.getContent());
        r.setOwnerUsername(note.getOwnerUsername());
        r.setCreatedAt(note.getCreatedAt());
        return r;
    }
}
