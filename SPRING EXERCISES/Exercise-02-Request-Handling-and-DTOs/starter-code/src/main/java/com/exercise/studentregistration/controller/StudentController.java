package com.exercise.studentregistration.controller;

import com.exercise.studentregistration.dto.*;
import com.exercise.studentregistration.model.Student;
import com.exercise.studentregistration.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// TODO 7: Add @RestController and @RequestMapping("/api/students") to this class
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // ─── Mapping Helper ───────────────────────────────────────────────────────
    // These helper methods convert between Student (domain model) and DTOs.
    // In larger projects you'd use a library like MapStruct, but for now we do it manually.

    private StudentResponse toResponse(Student student) {
        // Hint: Create a new StudentResponse, set all fields from student, return it.
        // Don't forget to set letterGrade using student.getLetterGrade()
        return null; // replace this
    }

    private StudentSummaryResponse toSummary(Student student) {
        // Hint: Create a new StudentSummaryResponse, combine first+last name for fullName
        return null; // replace this
    }

    // ─── Endpoints ────────────────────────────────────────────────────────────

    // TODO 8: Add @GetMapping. Return a list of ALL students mapped to StudentResponse.
    //         Use stream().map(this::toResponse).collect(Collectors.toList())
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        // your code here
        return null;
    }

    // TODO 9: Add @GetMapping("/{id}"). Return StudentResponse or 404.
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        // your code here
        return null;
    }

    // TODO 10: Add @PostMapping("/register"). Accept StudentRegistrationRequest.
    //           Create a new Student from the request data, set enrolledAt = LocalDateTime.now(),
    //           set an initial gpa of 0.0, save it, and return StudentResponse with 201 Created.
    public ResponseEntity<StudentResponse> registerStudent(@RequestBody StudentRegistrationRequest request) {
        // your code here
        return null;
    }

    // TODO 11: Add @PutMapping("/{id}"). Accept StudentUpdateRequest.
    //           Only update fields that are NOT null in the request.
    //           Return updated StudentResponse or 404.
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id,
                                                          @RequestBody StudentUpdateRequest request) {
        // your code here
        return null;
    }

    // TODO 12: Add @DeleteMapping("/{id}"). Delete the student.
    //           Return MessageResponse("Student {id} deleted successfully") with 200 OK.
    //           Return 404 if not found.
    public ResponseEntity<MessageResponse> deleteStudent(@PathVariable Long id) {
        // your code here
        return null;
    }

    // TODO 13: Add @GetMapping("/{id}/summary"). Return StudentSummaryResponse or 404.
    public ResponseEntity<StudentSummaryResponse> getStudentSummary(@PathVariable Long id) {
        // your code here
        return null;
    }

    // TODO 14: Add @GetMapping("/by-grade"). Accept @RequestParam String grade.
    //           Return all students with that letter grade as List<StudentSummaryResponse>.
    public ResponseEntity<List<StudentSummaryResponse>> getStudentsByGrade(@RequestParam String grade) {
        // your code here
        return null;
    }
}
