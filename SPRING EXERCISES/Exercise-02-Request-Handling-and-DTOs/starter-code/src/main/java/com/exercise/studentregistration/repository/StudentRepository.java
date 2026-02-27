package com.exercise.studentregistration.repository;

import com.exercise.studentregistration.model.Student;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class StudentRepository {

    private final AtomicLong idCounter = new AtomicLong(1);
    private final Map<Long, Student> store = new HashMap<>();

    public StudentRepository() {
        save(new Student(null, "Alice", "Johnson", "alice@uni.edu", "Computer Science", 3, 3.8, LocalDateTime.now().minusYears(2)));
        save(new Student(null, "Bob", "Smith", "bob@uni.edu", "Mathematics", 2, 3.2, LocalDateTime.now().minusYears(1)));
        save(new Student(null, "Carol", "Williams", "carol@uni.edu", "Physics", 4, 3.9, LocalDateTime.now().minusYears(3)));
        save(new Student(null, "David", "Brown", "david@uni.edu", "Computer Science", 1, 2.8, LocalDateTime.now().minusMonths(6)));
    }

    public List<Student> findAll() { return new ArrayList<>(store.values()); }

    public Optional<Student> findById(Long id) { return Optional.ofNullable(store.get(id)); }

    public Student save(Student student) {
        if (student.getId() == null) student.setId(idCounter.getAndIncrement());
        store.put(student.getId(), student);
        return student;
    }

    public boolean deleteById(Long id) { return store.remove(id) != null; }

    public List<Student> findByLetterGrade(String grade) {
        return store.values().stream()
                .filter(s -> s.getLetterGrade().equalsIgnoreCase(grade))
                .collect(Collectors.toList());
    }
}
