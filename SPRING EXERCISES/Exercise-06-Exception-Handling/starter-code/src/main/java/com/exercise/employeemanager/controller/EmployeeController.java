package com.exercise.employeemanager.controller;

import com.exercise.employeemanager.entity.Employee;
import com.exercise.employeemanager.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO 18: Add @RestController and @RequestMapping("/api/employees")
//          Implement all 7 endpoints CLEANLY — no try/catch needed here!
//          The GlobalExceptionHandler will catch all exceptions automatically.
//
//          Endpoints:
//            GET    /api/employees              → getAllEmployees()
//            GET    /api/employees/{id}         → getEmployeeById(id)
//            GET    /api/employees/department/{dept} → getEmployeesByDepartment(dept)
//            POST   /api/employees              → createEmployee(@RequestBody Employee)  → 201
//            PUT    /api/employees/{id}         → updateEmployee(id, @RequestBody Employee)
//            DELETE /api/employees/{id}         → deleteEmployee(id)                    → 204
//            PUT    /api/employees/{id}/promote → promoteEmployee(id)
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // your endpoints here
}
