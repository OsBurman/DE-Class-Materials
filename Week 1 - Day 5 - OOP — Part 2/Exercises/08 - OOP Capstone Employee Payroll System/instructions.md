# Exercise 08: OOP Capstone — Employee Payroll System

## Objective
Synthesize all Day 5 OOP concepts — inheritance, abstract classes, interfaces, method overriding, polymorphism, and encapsulation — into a complete, multi-class program that models a real payroll system.

## Background
A company needs a payroll system that handles multiple employee types: salaried employees (fixed monthly pay), hourly employees (pay based on hours worked), and contract employees (fixed fee per project). All employees share common data, but their pay calculations differ. The system also needs to flag employees that are eligible for benefits and generate a payroll report. This is the kind of hierarchy Java OOP was designed for.

## Requirements

1. Define interface `Benefitable` with:
   - `boolean isEligibleForBenefits()` — returns whether the employee qualifies
   - `String getBenefitsSummary()` — returns a string describing their benefits package

2. Create abstract class `Employee` with:
   - Private fields: `String employeeId`, `String name`, `String department`
   - Constructor taking all three fields
   - Getters for all three fields
   - Abstract method `double calculateMonthlyPay()` — each type computes this differently
   - Concrete method `String getPayStub()` that returns a formatted string:
     ```
     Employee: [name] ([employeeId])
     Department: [department]
     Monthly Pay: $[calculateMonthlyPay() to 2 dp]
     ```

3. Create class `SalariedEmployee` extending `Employee` and implementing `Benefitable`:
   - Additional field: `double annualSalary`
   - Constructor: `SalariedEmployee(String id, String name, String dept, double annualSalary)`
   - Override `calculateMonthlyPay()`: `annualSalary / 12`
   - Implement `isEligibleForBenefits()`: always `true`
   - Implement `getBenefitsSummary()`: `"Full benefits: health, dental, vision, 401k"`

4. Create class `HourlyEmployee` extending `Employee` and implementing `Benefitable`:
   - Additional fields: `double hourlyRate`, `double hoursWorkedThisMonth`
   - Constructor: takes id, name, dept, hourlyRate, hoursWorkedThisMonth
   - Override `calculateMonthlyPay()`: `hourlyRate * hoursWorkedThisMonth`
   - Implement `isEligibleForBenefits()`: return `true` if `hoursWorkedThisMonth >= 80`, else `false`
   - Implement `getBenefitsSummary()`: return `"Partial benefits: health only"` if eligible, `"No benefits"` if not

5. Create class `ContractEmployee` extending `Employee`:
   - Additional fields: `double contractFee`, `String projectName`
   - Constructor: takes id, name, dept, contractFee, projectName
   - Override `calculateMonthlyPay()`: returns `contractFee`
   - Override `getPayStub()` to also include `"Project: [projectName]"` as the last line
   - Does **not** implement `Benefitable` — contractors have no benefits

6. In `main`:
   - Create an `ArrayList<Employee>` with at least:
     - 2 `SalariedEmployee` objects
     - 2 `HourlyEmployee` objects (one with ≥80 hours, one with <80 hours)
     - 1 `ContractEmployee`
   - **Payroll report**: loop through the list and print each employee's `getPayStub()` separated by `---`
   - **Total payroll**: sum all `calculateMonthlyPay()` values and print
   - **Benefits report**: iterate the list, use `instanceof Benefitable` to downcast, and list only employees eligible for benefits and their summary
   - **Packages and imports note**: Print a comment-style message at the end explaining how packages work: in a real application, each class would live in a package (e.g., `package com.company.payroll;`) and other classes would `import com.company.payroll.Employee;`

## Hints
- `Employee` is abstract — you cannot instantiate it directly, only its concrete subclasses
- `ContractEmployee` does NOT implement `Benefitable`, so it will be skipped in the benefits loop
- `String.format("$%.2f", amount)` formats a double to a currency-style string
- When looping with `ArrayList<Employee>`, calling `getPayStub()` uses runtime polymorphism — `ContractEmployee`'s overridden version is called automatically

## Expected Output

```
=== Payroll Report ===

Employee: Alice Johnson (E001)
Department: Engineering
Monthly Pay: $8333.33
---
Employee: Bob Smith (E002)
Department: Marketing
Monthly Pay: $5833.33
---
Employee: Carol White (E003)
Department: Engineering
Monthly Pay: $2120.00
---
Employee: Dave Brown (E004)
Department: Support
Monthly Pay: $780.00
---
Employee: Eve Davis (E005)
Department: Engineering
Monthly Pay: $5000.00
Project: Cloud Migration

Total monthly payroll: $22066.67

=== Benefits Eligibility ===
Alice Johnson: Full benefits: health, dental, vision, 401k
Bob Smith: Full benefits: health, dental, vision, 401k
Carol White: Partial benefits: health only
Dave Brown (E004) is NOT eligible for benefits.
```
