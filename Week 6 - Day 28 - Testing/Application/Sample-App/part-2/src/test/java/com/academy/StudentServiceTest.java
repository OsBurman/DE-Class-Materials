package com.academy;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Day 28 — Part 2: StudentService — Mockito Unit Tests
 * ======================================================
 * Run: mvn test
 *
 * Key concepts:
 *   @Mock         — mock the repository (no real DB needed)
 *   @InjectMocks  — real StudentService with mock injected
 *   when/thenReturn — stub method behaviour
 *   verify        — assert interactions with the mock
 *   ArgumentCaptor — inspect arguments passed to mocks
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService — Mockito Unit Tests")
class StudentServiceTest {

    @Mock
    private StudentRepository repository;

    @InjectMocks
    private StudentService service;

    private Student alice;
    private Student bob;

    @BeforeEach
    void setup() {
        alice = new Student(1L, "Alice Johnson", "alice@uni.edu", "CS", 3.8);
        bob   = new Student(2L, "Bob Smith",     "bob@uni.edu",   "Math", 3.2);
    }

    @Test
    @DisplayName("getAllStudents — returns list from repository")
    void getAllStudents() {
        // Arrange: stub the mock
        when(repository.findAll()).thenReturn(List.of(alice, bob));

        // Act
        List<Student> result = service.getAllStudents();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Alice Johnson", result.get(0).getName());

        // Verify the repository was called exactly once
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("getStudentById — returns student when found")
    void getStudentByIdFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(alice));

        Optional<Student> result = service.getStudentById(1L);

        assertTrue(result.isPresent());
        assertEquals("Alice Johnson", result.get().getName());
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("getStudentById — returns empty when not found")
    void getStudentByIdNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Student> result = service.getStudentById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("createStudent — saves and returns new student")
    void createStudent() {
        when(repository.findAll()).thenReturn(List.of()); // no existing students
        when(repository.save(any(Student.class))).thenReturn(alice);

        Student result = service.createStudent(alice);

        assertNotNull(result);
        assertEquals("Alice Johnson", result.getName());
        verify(repository, times(1)).save(alice);
    }

    @Test
    @DisplayName("createStudent — throws when email already exists")
    void createStudentDuplicateEmail() {
        when(repository.findAll()).thenReturn(List.of(alice)); // alice already exists

        Student duplicate = new Student(null, "Alice Copy", "alice@uni.edu", "CS", 3.0);

        assertThrows(IllegalArgumentException.class,
            () -> service.createStudent(duplicate));

        // Verify save was NEVER called — validation rejected the request
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("deleteStudent — calls deleteById when student exists")
    void deleteStudent() {
        when(repository.findById(1L)).thenReturn(Optional.of(alice));
        doNothing().when(repository).deleteById(1L);

        service.deleteStudent(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteStudent — throws when student not found")
    void deleteStudentNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.deleteStudent(99L));
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("ArgumentCaptor — capture the Student passed to save()")
    void captureArgument() {
        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        when(repository.findAll()).thenReturn(List.of());
        when(repository.save(any())).thenReturn(alice);

        service.createStudent(alice);

        verify(repository).save(captor.capture());
        Student captured = captor.getValue();
        assertEquals("alice@uni.edu", captured.getEmail());
        System.out.println("Captured argument email: " + captured.getEmail());
    }
}

class StudentServiceTest {

    @Mock
    private StudentRepository repository;   // Mock — no real DB interaction

    @InjectMocks
    private StudentService service;         // Real service, mocked repository injected

    private Student alice;
    private Student bob;

    @BeforeEach
    void setup() {
        alice = new Student(1L, "Alice Johnson", "alice@uni.edu", "CS", 3.8);
        bob   = new Student(2L, "Bob Martinez",  "bob@uni.edu",  "Math", 3.2);
    }

    // ── getAllStudents ─────────────────────────────────────────────────

    @Test
    @DisplayName("getAllStudents — returns list from repository")
    void getAllStudents() {
        // Arrange: stub the mock
        when(repository.findAll()).thenReturn(List.of(alice, bob));

        // Act
        List<Student> result = service.getAllStudents();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Alice Johnson", result.get(0).getName());

        // Verify repository was called exactly once
        verify(repository, times(1)).findAll();
    }

    // ── getStudentById ─────────────────────────────────────────────────

    @Test
    @DisplayName("getStudentById — returns student when found")
    void getStudentByIdFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(alice));

        Optional<Student> result = service.getStudentById(1L);

        assertTrue(result.isPresent());
        assertEquals("Alice Johnson", result.get().getName());
        verify(repository).findById(1L);  // times(1) is the default
    }

    @Test
    @DisplayName("getStudentById — returns empty when not found")
    void getStudentByIdNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Student> result = service.getStudentById(99L);

        assertFalse(result.isPresent());
    }

    // ── createStudent ──────────────────────────────────────────────────

    @Test
    @DisplayName("createStudent — saves and returns student")
    void createStudent() {
        when(repository.findAll()).thenReturn(List.of()); // no duplicates
        when(repository.save(any(Student.class))).thenReturn(alice);

        Student result = service.createStudent(alice);

        assertNotNull(result);
        assertEquals("Alice Johnson", result.getName());
        verify(repository, times(1)).save(alice);
    }

    @Test
    @DisplayName("createStudent — throws when email already exists")
    void createStudentDuplicateEmail() {
        when(repository.findAll()).thenReturn(List.of(alice));

        Student duplicate = new Student(null, "Duplicate", "alice@uni.edu", "Art", 2.5);

        assertThrows(IllegalArgumentException.class,
            () -> service.createStudent(duplicate));

        // save should NEVER be called when validation fails
        verify(repository, never()).save(any());
    }

    // ── deleteStudent ──────────────────────────────────────────────────

    @Test
    @DisplayName("deleteStudent — calls deleteById when student exists")
    void deleteStudent() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        service.deleteStudent(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteStudent — throws when student not found")
    void deleteStudentNotFound() {
        when(repository.existsById(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
            () -> service.deleteStudent(999L));

        verify(repository, never()).deleteById(any());
    }

    // ── ArgumentCaptor ─────────────────────────────────────────────────

    @Test
    @DisplayName("ArgumentCaptor — inspect what was passed to save()")
    void captureArgument() {
        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        when(repository.findAll()).thenReturn(List.of());
        when(repository.save(any())).thenReturn(alice);

        service.createStudent(alice);

        verify(repository).save(captor.capture());
        Student captured = captor.getValue();
        assertEquals("alice@uni.edu", captured.getEmail());
        assertEquals("CS", captured.getMajor());
    }
}
