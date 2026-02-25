package com.academy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Day 28 — Part 2: StudentController — @WebMvcTest with MockMvc
 * ===============================================================
 * @WebMvcTest loads ONLY the web layer.
 * Services/repositories are NOT loaded — replace with @MockBean.
 *
 * Run: mvn test
 *
 * Key concepts:
 *   MockMvc.perform()       — simulate HTTP requests
 *   andExpect(status())     — assert HTTP status code
 *   andExpect(jsonPath())   — assert JSON response fields
 *   @MockBean               — Spring-managed Mockito mock
 *   ObjectMapper            — convert Java objects to JSON string
 */
@WebMvcTest(StudentController.class)
@DisplayName("StudentController — MockMvc Tests")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService service;

    private Student alice;

    @BeforeEach
    void setup() {
        alice = new Student(1L, "Alice Johnson", "alice@uni.edu", "CS", 3.8);
    }

    @Test
    @DisplayName("GET /api/students — 200 OK with student list")
    void getAll() throws Exception {
        when(service.getAllStudents()).thenReturn(List.of(alice));

        mockMvc.perform(get("/api/students")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Alice Johnson"))
            .andExpect(jsonPath("$[0].gpa").value(3.8))
            .andExpect(jsonPath("$[0].major").value("CS"));
    }

    @Test
    @DisplayName("GET /api/students/1 — 200 OK with single student")
    void getById() throws Exception {
        when(service.getStudentById(1L)).thenReturn(Optional.of(alice));

        mockMvc.perform(get("/api/students/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Alice Johnson"))
            .andExpect(jsonPath("$.email").value("alice@uni.edu"));
    }

    @Test
    @DisplayName("GET /api/students/999 — 404 Not Found")
    void getByIdNotFound() throws Exception {
        when(service.getStudentById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/students/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/students — 201 Created")
    void create() throws Exception {
        when(service.createStudent(any(Student.class))).thenReturn(alice);

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alice)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Alice Johnson"));
    }

    @Test
    @DisplayName("DELETE /api/students/1 — 204 No Content")
    void delete() throws Exception {
        doNothing().when(service).deleteStudent(1L);

        mockMvc.perform(delete("/api/students/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/students/999 — 404 Not Found")
    void deleteNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Not found")).when(service).deleteStudent(999L);

        mockMvc.perform(delete("/api/students/999"))
            .andExpect(status().isNotFound());
    }
}
