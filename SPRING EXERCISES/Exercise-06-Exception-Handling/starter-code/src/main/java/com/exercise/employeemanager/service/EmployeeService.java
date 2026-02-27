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
        // TODO 15: Find by id or throw ResourceNotFoundException("Employee", id)
        return null;
    }

    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }

    public Employee createEmployee(Employee employee) {
        // TODO 16: Check if email already exists using existsByEmail()
        //          If yes: throw new DuplicateResourceException("Email already in use: " + employee.getEmail())
        //          If no: save and return
        return null;
    }

    public Employee updateEmployee(Long id, Employee updated) {
        Employee existing = getEmployeeById(id); // reuse getEmployeeById — it throws if not found
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setDepartment(updated.getDepartment());
        existing.setPosition(updated.getPosition());
        existing.setSalary(updated.getSalary());
        return employeeRepository.save(existing);
    }

    public void deleteEmployee(Long id) {
        getEmployeeById(id); // throws if not found
        employeeRepository.deleteById(id);
    }

    public Employee promoteEmployee(Long id) {
        // TODO 17a: Find by id or throw ResourceNotFoundException("Employee", id)
        // TODO 17b: If employee.getLevel() >= 5:
        //           throw new BusinessRuleException("Employee is already at the maximum level (5)")
        // TODO 17c: Increment level by 1, save, return
        return null;
    }
}
