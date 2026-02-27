# Exercise 02 — Request Handling & DTOs

## 🎯 Learning Objectives
By the end of this exercise you will be able to:
- Design **DTO (Data Transfer Object)** classes separate from your domain model
- Use `@RequestBody` to accept JSON payloads and map them to DTOs
- Use `@PathVariable` and `@RequestParam` with various types
- Return different response shapes using response DTOs
- Understand why DTOs are preferred over exposing entities directly
- Use proper HTTP status codes for different scenarios

---

## 📋 What You're Building
A **Student Registration API** — demonstrates the DTO pattern by keeping request/response models separate from the internal domain model.

### Endpoints
| Method | Path | Request Body | Response |
|--------|------|-------------|----------|
| `GET` | `/api/students` | — | `List<StudentResponse>` |
| `GET` | `/api/students/{id}` | — | `StudentResponse` |
| `POST` | `/api/students/register` | `StudentRegistrationRequest` | `StudentResponse` |
| `PUT` | `/api/students/{id}` | `StudentUpdateRequest` | `StudentResponse` |
| `DELETE` | `/api/students/{id}` | — | `MessageResponse` |
| `GET` | `/api/students/{id}/summary` | — | `StudentSummaryResponse` |
| `GET` | `/api/students/by-grade` | `?grade=A` | `List<StudentSummaryResponse>` |

---

## 🏗️ Project Setup
```bash
cd Exercise-02-Request-Handling-and-DTOs/starter-code
./mvnw spring-boot:run
```

---

## 📁 File Structure
```
src/main/java/com/exercise/studentregistration/
├── StudentRegistrationApplication.java
├── model/
│   └── Student.java                     ← Internal domain model
├── dto/
│   ├── StudentRegistrationRequest.java  ← ⭐ What the client SENDS to create
│   ├── StudentUpdateRequest.java        ← ⭐ What the client SENDS to update
│   ├── StudentResponse.java             ← ⭐ What the server RETURNS (full)
│   ├── StudentSummaryResponse.java      ← ⭐ What the server RETURNS (brief)
│   └── MessageResponse.java            ← ⭐ Generic message wrapper
├── repository/
│   └── StudentRepository.java          ← In-memory store (already done)
└── controller/
    └── StudentController.java          ← ⭐ YOUR MAIN TASK
```

---

## ✅ TODOs

### `dto/StudentRegistrationRequest.java`
- [ ] **TODO 1**: Create this class with fields: `firstName`, `lastName`, `email`, `major`, `yearLevel` (int)
- [ ] **TODO 2**: Add a no-arg constructor and a full constructor, plus all getters/setters

### `dto/StudentUpdateRequest.java`
- [ ] **TODO 3**: Create this class with optional update fields: `major`, `yearLevel`, `email`
  - Fields can be `null` meaning "don't update this field"

### `dto/StudentResponse.java`
- [ ] **TODO 4**: Create this class with all fields returned to the client: `id`, `firstName`, `lastName`, `email`, `major`, `yearLevel`, `gpa`, `enrolledAt`

### `dto/StudentSummaryResponse.java`
- [ ] **TODO 5**: Create this class with just: `id`, `fullName` (first + last), `major`, `gpa`

### `dto/MessageResponse.java`
- [ ] **TODO 6**: Create this class with a single `message` field

### `controller/StudentController.java`
- [ ] **TODO 7**: Add `@RestController` and `@RequestMapping("/api/students")`
- [ ] **TODO 8**: Implement `getAllStudents()` — map each Student to a `StudentResponse`
- [ ] **TODO 9**: Implement `getStudentById(@PathVariable Long id)` — return `StudentResponse` or `404`
- [ ] **TODO 10**: Implement `registerStudent(@RequestBody StudentRegistrationRequest req)` — convert request to Student, save, return `StudentResponse` with `201 Created`
- [ ] **TODO 11**: Implement `updateStudent(@PathVariable Long id, @RequestBody StudentUpdateRequest req)` — only update non-null fields
- [ ] **TODO 12**: Implement `deleteStudent(@PathVariable Long id)` — return `MessageResponse` with `"Student {id} deleted successfully"`
- [ ] **TODO 13**: Implement `getStudentSummary(@PathVariable Long id)` — return `StudentSummaryResponse`
- [ ] **TODO 14**: Implement `getStudentsByGrade(@RequestParam String grade)` — filter by letter grade, return `List<StudentSummaryResponse>`

---

## 💡 Why DTOs?

| Without DTOs | With DTOs |
|-------------|----------|
| Expose internal model structure | Control exactly what clients see |
| Password fields accidentally leaked | Sensitive fields kept private |
| Client tightly coupled to DB schema | Client/server can evolve independently |
| Must expose ALL fields | Return only what's needed (summary vs full) |

### Mapping Pattern
```java
// Entity → Response DTO (in controller or a mapper class)
private StudentResponse toResponse(Student student) {
    StudentResponse resp = new StudentResponse();
    resp.setId(student.getId());
    resp.setFirstName(student.getFirstName());
    // ... etc
    return resp;
}
```

---

## 🧪 Test Your Work

```http
### Register a student
POST http://localhost:8080/api/students/register
Content-Type: application/json

{
  "firstName": "Alice",
  "lastName": "Johnson",
  "email": "alice@university.edu",
  "major": "Computer Science",
  "yearLevel": 2
}

### Get student summary
GET http://localhost:8080/api/students/1/summary

### Filter by grade
GET http://localhost:8080/api/students/by-grade?grade=A

### Partial update
PUT http://localhost:8080/api/students/1
Content-Type: application/json

{
  "major": "Data Science"
}
```
