package com.academy;

import java.util.*;
import java.util.function.*;

/**
 * Day 8 Part 1 — Lambdas, Functional Interfaces, Method References, Optional
 *
 * Theme: Employee Management System
 * Run: mvn compile exec:java
 */
public class Main {

    record Employee(String name, String dept, double salary, int yearsExp) {}

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  Day 8 Part 1 — Lambdas & Functional Interfaces Demo    ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");

        List<Employee> employees = List.of(
            new Employee("Alice",   "Engineering", 95_000, 5),
            new Employee("Bob",     "Marketing",   65_000, 3),
            new Employee("Carol",   "Engineering", 82_000, 7),
            new Employee("Dave",    "HR",          55_000, 2),
            new Employee("Eve",     "Engineering", 110_000, 10),
            new Employee("Frank",   "Marketing",   72_000, 4)
        );

        demoLambdas(employees);
        demoFunctionalInterfaces(employees);
        demoMethodReferences(employees);
        demoOptional(employees);
    }

    static void demoLambdas(List<Employee> employees) {
        System.out.println("=== 1. Lambda Expressions ===");

        // Without lambda (anonymous class)
        Comparator<Employee> oldWay = new Comparator<Employee>() {
            @Override public int compare(Employee a, Employee b) { return Double.compare(a.salary(), b.salary()); }
        };

        // With lambda — much cleaner!
        Comparator<Employee> bySalary = (a, b) -> Double.compare(a.salary(), b.salary());
        Comparator<Employee> byName   = (a, b) -> a.name().compareTo(b.name());

        List<Employee> sorted = new ArrayList<>(employees);
        sorted.sort(bySalary);
        System.out.println("  Sorted by salary: " + sorted.stream().map(e -> e.name() + "($" + (int)e.salary()/1000 + "k)").toList());

        // Runnable with lambda
        Runnable greeting = () -> System.out.println("  Hello from lambda Runnable!");
        greeting.run();
        System.out.println();
    }

    static void demoFunctionalInterfaces(List<Employee> employees) {
        System.out.println("=== 2. Built-in Functional Interfaces ===");

        // Predicate<T> — test a condition, returns boolean
        Predicate<Employee> isEngineer = e -> e.dept().equals("Engineering");
        Predicate<Employee> highEarner = e -> e.salary() > 80_000;
        Predicate<Employee> seniorEngineer = isEngineer.and(highEarner);

        System.out.println("  Engineers:        " + employees.stream().filter(isEngineer).map(Employee::name).toList());
        System.out.println("  High earners:     " + employees.stream().filter(highEarner).map(Employee::name).toList());
        System.out.println("  Senior engineers: " + employees.stream().filter(seniorEngineer).map(Employee::name).toList());

        // Function<T,R> — transform T to R
        Function<Employee, String> summarize = e ->
            String.format("%s (%s) — $%.0f", e.name(), e.dept(), e.salary());
        System.out.println("  First employee:   " + summarize.apply(employees.get(0)));

        // Function chaining
        Function<Double, Double> applyBonus   = salary -> salary * 1.10;
        Function<Double, String> formatSalary = salary -> String.format("$%.0f", salary);
        Function<Double, String> bonusFormatted = applyBonus.andThen(formatSalary);
        System.out.println("  $95,000 + 10% bonus: " + bonusFormatted.apply(95_000.0));

        // Consumer<T> — accept T, return nothing (side effects)
        Consumer<Employee> printEmployee = e -> System.out.println("    → " + e.name() + " earns $" + (int)e.salary());
        System.out.println("  All employees:");
        employees.forEach(printEmployee);

        // Supplier<T> — supply a value without input
        Supplier<Employee> defaultEmployee = () -> new Employee("NewHire", "TBD", 50_000, 0);
        System.out.println("  Default employee: " + defaultEmployee.get().name());
        System.out.println();
    }

    static void demoMethodReferences(List<Employee> employees) {
        System.out.println("=== 3. Method References ===");

        // Static method reference
        List<String> names = List.of("carol", "alice", "bob");
        names.stream().map(String::toUpperCase).forEach(System.out::println);  // instance method ref

        // Instance method on specific object
        String prefix = "DEPT:";
        Function<String, String> addPrefix = prefix::concat;
        System.out.println("  " + addPrefix.apply("Engineering"));

        // Constructor reference
        Function<String, StringBuilder> sbFactory = StringBuilder::new;
        StringBuilder sb = sbFactory.apply("Hello");
        System.out.println("  StringBuilder via constructor ref: " + sb);
        System.out.println();
    }

    static void demoOptional(List<Employee> employees) {
        System.out.println("=== 4. Optional — Avoiding NullPointerException ===");

        // findFirst returns Optional
        Optional<Employee> topEarner = employees.stream()
            .max(Comparator.comparingDouble(Employee::salary));

        topEarner.ifPresent(e -> System.out.println("  Top earner: " + e.name() + " ($" + (int)e.salary() + ")"));

        // orElse — provide default when empty
        Optional<Employee> hrManager = employees.stream()
            .filter(e -> e.dept().equals("HR") && e.yearsExp() > 5)
            .findFirst();
        Employee found = hrManager.orElse(new Employee("No one", "HR", 0, 0));
        System.out.println("  Senior HR manager: " + found.name());

        // orElseThrow
        try {
            Employee ceo = employees.stream()
                .filter(e -> e.dept().equals("C-Suite"))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No CEO found"));
        } catch (NoSuchElementException e) {
            System.out.println("  " + e.getMessage());
        }

        // map + orElse
        String topName = topEarner.map(Employee::name).orElse("Unknown");
        System.out.println("  Top earner name via Optional.map: " + topName);

        System.out.println("\n✓ Lambdas & Functional Interfaces demo complete.");
    }
}
