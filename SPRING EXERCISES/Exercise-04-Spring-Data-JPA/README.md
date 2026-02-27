# Exercise 04 — Spring Data JPA

## 🎯 Learning Objectives
By the end of this exercise you will be able to:
- Annotate a Java class as a JPA **entity** with `@Entity`, `@Table`, `@Id`, `@GeneratedValue`
- Map Java fields to database columns with `@Column`
- Use **H2 in-memory database** for development
- Extend `JpaRepository` to get free CRUD operations
- Use **derived query methods** (e.g. `findByStatus`, `findByPriorityAndStatus`)
- Use `@Query` for custom JPQL queries
- Configure `application.properties` for JPA/H2

---

## 📋 What You're Building
A **Task Manager API** — a to-do list API backed by a real (H2) database.

### Endpoints
| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/tasks` | Get all tasks (optional `?status=` filter) |
| `GET` | `/api/tasks/{id}` | Get task by ID |
| `GET` | `/api/tasks/priority/{level}` | Get tasks by priority (HIGH/MEDIUM/LOW) |
| `GET` | `/api/tasks/overdue` | Get all overdue incomplete tasks |
| `POST` | `/api/tasks` | Create a new task |
| `PUT` | `/api/tasks/{id}` | Update a task |
| `PATCH` | `/api/tasks/{id}/complete` | Mark a task as complete |
| `DELETE` | `/api/tasks/{id}` | Delete a task |

---

## 🏗️ Project Setup
```bash
cd Exercise-04-Spring-Data-JPA/starter-code
./mvnw spring-boot:run
# H2 Console: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:taskdb   User: sa   Password: (empty)
```

---

## 📁 File Structure
```
src/main/java/com/exercise/taskmanager/
├── TaskManagerApplication.java
├── entity/
│   └── Task.java                   ← ⭐ JPA Entity with TODO annotations
├── repository/
│   └── TaskRepository.java         ← ⭐ JpaRepository with derived queries
├── service/
│   ├── TaskService.java            ← Interface (already done)
│   └── TaskServiceImpl.java        ← ⭐ Business logic
└── controller/
    └── TaskController.java         ← ⭐ REST controller
src/main/resources/
└── application.properties          ← ⭐ H2 + JPA config
    data.sql                        ← Sample data (already done)
```

---

## ✅ TODOs

### `application.properties`
- [ ] **TODO 1**: Configure H2 in-memory database:
  ```properties
  spring.datasource.url=jdbc:h2:mem:taskdb
  spring.datasource.driver-class-name=org.h2.Driver
  spring.datasource.username=sa
  spring.datasource.password=
  ```
- [ ] **TODO 2**: Enable the H2 web console:
  ```properties
  spring.h2.console.enabled=true
  spring.h2.console.path=/h2-console
  ```
- [ ] **TODO 3**: Configure JPA/Hibernate:
  ```properties
  spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
  spring.jpa.hibernate.ddl-auto=create-drop
  spring.jpa.show-sql=true
  spring.jpa.properties.hibernate.format_sql=true
  ```

### `entity/Task.java`
- [ ] **TODO 4**: Add `@Entity` and `@Table(name = "tasks")` to the class
- [ ] **TODO 5**: Annotate `id` with `@Id` and `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- [ ] **TODO 6**: Add `@Column(nullable = false)` to the `title` field
- [ ] **TODO 7**: Add `@Enumerated(EnumType.STRING)` to both `status` and `priority` fields
- [ ] **TODO 8**: Add `@CreationTimestamp` to `createdAt` (auto-set on insert)
- [ ] **TODO 9**: Add `@UpdateTimestamp` to `updatedAt` (auto-set on update)

### `repository/TaskRepository.java`
- [ ] **TODO 10**: Make `TaskRepository` extend `JpaRepository<Task, Long>`
- [ ] **TODO 11**: Add a derived query method: `List<Task> findByStatus(TaskStatus status)`
- [ ] **TODO 12**: Add: `List<Task> findByPriority(Priority priority)`
- [ ] **TODO 13**: Add a `@Query` for overdue tasks:
  ```java
  @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_DATE AND t.status != 'COMPLETED'")
  List<Task> findOverdueTasks();
  ```
- [ ] **TODO 14**: Add: `long countByStatus(TaskStatus status)`

### `service/TaskServiceImpl.java`
- [ ] **TODO 15**: Implement all methods, delegating to `TaskRepository`

### `controller/TaskController.java`
- [ ] **TODO 16**: Implement all endpoints, delegating to `TaskService`

---

## 💡 Key Concepts

### JPA Entity Annotations
```java
@Entity                        // This class maps to a DB table
@Table(name = "tasks")         // Explicit table name (optional)
public class Task {

    @Id                                              // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment
    private Long id;

    @Column(nullable = false, length = 200)         // Column constraints
    private String title;

    @Enumerated(EnumType.STRING)                    // Store enum as "PENDING", not 0
    private TaskStatus status;
}
```

### Spring Data JPA Repository
```java
// Extend JpaRepository — you get 18+ methods for FREE:
// save(), findById(), findAll(), deleteById(), count(), existsById(), etc.
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Derived query — Spring generates the SQL automatically from the method name!
    List<Task> findByStatus(TaskStatus status);

    // Custom JPQL query
    @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_DATE")
    List<Task> findOverdueTasks();
}
```

### H2 Console
After starting the app, visit `http://localhost:8080/h2-console`:
- JDBC URL: `jdbc:h2:mem:taskdb`
- Username: `sa`
- Password: (leave empty)

You can run SQL queries directly to inspect your data!
