# Exercise 08 — Lambdas & Streams

## Overview
Build an **Employee Analytics Dashboard** that processes HR data using the Java Streams API, lambdas, `Optional`, and functional interfaces.

## Learning Objectives
- Write lambda expressions and method references
- Build stream pipelines with `filter`, `map`, `collect`, `reduce`, `sorted`
- Use `Collectors.groupingBy`, `Collectors.averagingDouble`, `Collectors.toMap`
- Work with `Optional<T>` safely
- Use `Predicate<T>`, `Function<T,R>`, and `Comparator` as lambdas

## Setup
```bash
cd Exercise-08-Lambdas-Streams/starter-code/src
javac Main.java
java Main
```

## The Application
The `Employee` class has: `id`, `name`, `department`, `salary`, `yearsExperience`, `skills (List<String>)`.

You will implement a series of stream-based analytics methods on a sample dataset.

## Your TODOs

| # | Method | Stream concepts |
|---|--------|-----------------|
| 1 | `getRaisedSalaries` | `filter` + `map` + `collect` |
| 2 | `getSeniorDevs` | chained `filter` |
| 3 | `getTotalPayroll` | `mapToDouble` + `sum` |
| 4 | `getAvgSalaryByDept` | `Collectors.groupingBy` + `averagingDouble` |
| 5 | `getHighestPaid` | `max` + `Comparator` + `Optional` |
| 6 | `getAllSkills` | `flatMap` + `distinct` + `sorted` |
| 7 | `getEmployeeMap` | `Collectors.toMap` |
| 8 | `getSalaryReport` | `sorted` + formatted `map` + `collect(joining)` |
| 9 | `partitionBySalary` | `Collectors.partitioningBy` |

## Expected Output
```
Raised salaries (Engineering): 4 employees
Senior devs (5+ yrs, Engineering): 2 employees
Total payroll: $614,000.00
Avg salary by dept: {Engineering=95000.0, HR=62500.0, Marketing=71500.0}
Highest paid: Alice Johnson @ $120,000.00
All skills: [AWS, Docker, Excel, Git, Java, Kubernetes, Photoshop, Python, SQL, Scrum, TypeScript]
Employee #3 lookup: Carol White
Above avg salary (>$75k): [Alice, Bob, Carol, David]  Below avg: [Eve, Frank, Grace, Henry]
Salary report:
  Alice Johnson       Engineering  $120,000.00  (10 yrs)
  Bob Smith           Engineering  $110,000.00   (8 yrs)
  ...
```

## Hints
- `stream.mapToDouble(Employee::getSalary).sum()` for numeric aggregation
- `flatMap(e -> e.skills.stream())` to flatten nested collections
- `Optional.map(e -> e.name).orElse("None")` to safely unwrap
- Method references: `String::toLowerCase`, `Employee::getDept`
