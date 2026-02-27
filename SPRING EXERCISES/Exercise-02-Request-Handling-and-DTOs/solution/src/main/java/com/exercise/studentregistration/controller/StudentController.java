package com.exercise.studentregistration.controller;

import com.exercise.studentregistration.dto.*;
import com.exercise.studentregistration.model.Student;
import com.exercise.studentregistration.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // ─── Mapping Helpers ─────────────────────────────────────────────────────

    private StudentResponse toResponse(Student s) {
        StudentResponse r = new StudentResponse();
        r.setId(s.getId());
        r.setFirstName(s.getFirstName());
        r.setLastName(s.getLastName());
        r.setEmail(s.getEmail());
        r.setMajor(s.getMajor());
        r.setYearLevel(s.getYearLevel());
        r.setGpa(s.getGpa());
        r.setLetterGrade(s.getLetterGrade());
        r.setEnrolledAt(s.getEnrolledAt());
        return r;
    }

    private StudentSummaryResponse toSummary(Student s) {
        return new StudentSummaryResponse(
                s.getId(),
                s.getFirstName() + " " + s.getLastName(),
                s.getMajor(),
                s.getGpa()
        );
    }

    // ─── Endpoints ────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        List<StudentResponse> responses = studentRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(s -> ResponseEntity.ok(toResponse(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<StudentResponse> registerStudent(@RequestBody StudentRegistrationRequest request) {
        Student student = new Student();
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setMajor(request.getMajor());
        student.setYearLevel(request.getYearLevel());
        student.setGpa(0.0);
        student.setEnrolledAt(LocalDateTime.now());

        Student saved = studentRepository.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id,
                                                          @RequestBody StudentUpdateRequest request) {
        return studentRepository.findById(id)
                .map(student -> {
                    if (request.getMajor() != null)     student.setMajor(request.getMajor());
                    if (request.getYearLevel() != null) student.setYearLevel(request.getYearLevel());
                    if (request.getEmail() != null)     student.setEmail(request.getEmail());
                    return ResponseEntity.ok(toResponse(studentRepository.save(student)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteStudent(@PathVariable Long id) {
        if (studentRepository.deleteById(id)) {
            return ResponseEntity.ok(new MessageResponse("Student " + id + " deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<StudentSummaryResponse> getStudentSummary(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(s -> ResponseEntity.ok(toSummary(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-grade")
    public ResponseEntity<List<StudentSummaryResponse>> getStudentsByGrade(@RequestParam String grade) {
        List<StudentSummaryResponse> results = studentRepository.findByLetterGrade(grade)
                .stream().map(this::toSummary).collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }
}
