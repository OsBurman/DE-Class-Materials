# Day 8 Application — Lambdas, Streams & DateTime: Employee Analytics Dashboard

## Overview

You'll build an **Employee Analytics Dashboard** — a Java app that processes a list of employees using the Stream API, lambda expressions, functional interfaces, and the Java DateTime API. No loops — only streams!

---

## Learning Goals

- Write lambda expressions for concise, functional-style code
- Use functional interfaces: `Predicate`, `Function`, `Consumer`, `Supplier`
- Use method references
- Apply Stream operations: `filter`, `map`, `reduce`, `collect`, `sorted`, `groupingBy`
- Use `Optional` to handle nullable values safely
- Work with `LocalDate`, `LocalDateTime`, `DateTimeFormatter`, `Period`

---

## Project Structure

```
starter-code/
└── src/
    ├── Main.java
    ├── Employee.java               ← provided (record class)
    └── EmployeeDashboard.java      ← TODO: complete all methods using streams/lambdas
```

---

## Part 1 — Stream Operations in `EmployeeDashboard.java`

You are given a `List<Employee>` where `Employee` has: `id`, `name`, `department`, `salary`, `hireDate` (`LocalDate`), `isActive` (`boolean`).

**Task 1 — Filter with `Predicate`**  
`getActiveSeniorEmployees(List<Employee> employees)` — return employees who are active AND have been employed for more than 3 years. Use a `Predicate<Employee>` variable, then pass it to `.filter()`.

**Task 2 — Transform with `Function`**  
`getEmployeeNames(List<Employee> employees)` — use `.map()` with a `Function<Employee, String>` to return a `List<String>` of all names.

**Task 3 — Consume with `Consumer`**  
`printEmployeeSummaries(List<Employee> employees)` — use `.forEach()` with a `Consumer<Employee>` lambda that prints each employee's name, department, and formatted salary.

**Task 4 — Supply with `Supplier`**  
`getDefaultEmployee()` — return a `Supplier<Employee>` that supplies a default `Employee` object when called.

**Task 5 — Method references**  
`sortByName(List<Employee> employees)` — use `.sorted()` with `Comparator.comparing(Employee::getName)`.  
`printAll(List<Employee> employees)` — use `.forEach(System.out::println)`.

**Task 6 — Aggregate with `reduce` and `collect`**  
`getTotalSalary(List<Employee> employees)` — use `.mapToDouble().sum()`.  
`getAverageSalary(List<Employee> employees)` — use `.mapToDouble().average()` — return an `OptionalDouble`.  
`groupByDepartment(List<Employee> employees)` — use `Collectors.groupingBy()` — return `Map<String, List<Employee>>`.

**Task 7 — `Optional`**  
`findHighestPaid(List<Employee> employees)` — use `.max()` returning `Optional<Employee>`.  
In `Main.java`, use `.ifPresent()` and `.orElse()` to handle the result.

---

## Part 2 — DateTime API in `EmployeeDashboard.java`

**Task 8 — Calculate tenure**  
`getTenure(Employee e)` — use `Period.between(e.getHireDate(), LocalDate.now())` and return `"[X] years, [Y] months"`.

**Task 9 — Format dates**  
`getFormattedHireDate(Employee e)` — use `DateTimeFormatter.ofPattern("MMMM dd, yyyy")` to return the hire date as a readable string.

**Task 10 — Filter by hire year**  
`getEmployeesHiredIn(List<Employee> employees, int year)` — use `.filter()` with the DateTime API to return employees hired in the given year.

---

## Stretch Goals

1. Use `flatMap()` to flatten a `List<List<Employee>>` into a single stream.
2. Use `Collectors.toUnmodifiableList()` and explain why immutability matters.
3. Chain multiple `Predicate` objects using `.and()` and `.or()`.

---

## Submission Checklist

- [ ] `Predicate`, `Function`, `Consumer`, `Supplier` all used
- [ ] Method references used (at least 2)
- [ ] `filter`, `map`, `reduce`/`sum`, `collect` all used
- [ ] `groupingBy` used
- [ ] `Optional` handled with `ifPresent` and `orElse`
- [ ] `LocalDate` and `Period` used
- [ ] `DateTimeFormatter` used for formatting
- [ ] No traditional `for` loops used in Dashboard methods
