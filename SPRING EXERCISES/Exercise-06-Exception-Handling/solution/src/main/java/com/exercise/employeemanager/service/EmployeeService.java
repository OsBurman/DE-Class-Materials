package com.exercise.employeemanager.service;

import com.exercise.employeemanager.entity.Employee;
import com.exercise.employeemanager.exception.BusinessRuleException;
import com.exercise.employeemanager.exception.DuplicateResourceException;
import com.exercise.employeemanager.exception.ResourceNotFoundException;
import com.exercise.employeemanager.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }

    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }

    public Employee createEmployee(Employee employee) {
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new DuplicateResourceException("Email already in use: " + employee.getEmail());
        }
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee updated) {
        Employee existing = getEmployeeById(id);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setDepartment(updated.getDepartment());
        existing.setPosition(updated.getPosition());
        existing.setSalary(updated.getSalary());
        return employeeRepository.save(existing);
    }

    public void deleteEmployee(Long id) {
        getEmployeeById(id);
        employeeRepository.deleteById(id);
    }

    public Employee promoteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        if (employee.getLevel() >= 5) {
            throw new BusinessRuleException("Employee is already at the maximum level (5)");
        }
        employee.setLevel(employee.getLevel() + 1);
        return employeeRepository.save(employee);
    }
}
