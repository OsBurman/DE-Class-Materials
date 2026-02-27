package com.exercise.employeemanager.repository;

import com.exercise.employeemanager.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByDepartment(String department);

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);
}
