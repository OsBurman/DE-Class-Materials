import java.util.*;
import java.util.stream.*;
import java.util.function.*;

/**
 * Exercise 08 — Lambdas & Streams (SOLUTION)
 */
public class Main {

    static class Employee {
        final int id;
        final String name;
        final String department;
        final double salary;
        final int yearsExperience;
        final List<String> skills;

        Employee(int id, String name, String dept, double salary, int yrs, String... skills) {
            this.id = id;
            this.name = name;
            this.department = dept;
            this.salary = salary;
            this.yearsExperience = yrs;
            this.skills = List.of(skills);
        }

        String getName() {
            return name;
        }

        String getDept() {
            return department;
        }

        double getSalary() {
            return salary;
        }

        int getYrs() {
            return yearsExperience;
        }

        @Override
        public String toString() {
            return String.format("%-20s %-14s $%,.0f  (%d yrs)", name, department, salary, yearsExperience);
        }
    }

    static List<String> getRaisedSalaries(List<Employee> employees) {
        return employees.stream()
                .filter(e -> e.department.equals("Engineering") && e.salary >= 90_000)
                .map(e -> "★ " + e.name)
                .collect(Collectors.toList());
    }

    static List<Employee> getSeniorDevs(List<Employee> employees) {
        return employees.stream()
                .filter(e -> e.department.equals("Engineering"))
                .filter(e -> e.yearsExperience >= 5)
                .collect(Collectors.toList());
    }

    static double getTotalPayroll(List<Employee> employees) {
        return employees.stream()
                .mapToDouble(Employee::getSalary)
                .sum();
    }

    static Map<String, Double> getAvgSalaryByDept(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDept,
                        Collectors.averagingDouble(Employee::getSalary)));
    }

    static Optional<Employee> getHighestPaid(List<Employee> employees) {
        return employees.stream()
                .max(Comparator.comparingDouble(Employee::getSalary));
    }

    static List<String> getAllSkills(List<Employee> employees) {
        return employees.stream()
                .flatMap(e -> e.skills.stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    static Map<Integer, String> getEmployeeMap(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.toMap(e -> e.id, Employee::getName));
    }

    static String getSalaryReport(List<Employee> employees) {
        return employees.stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                .map(e -> "  " + e.toString())
                .collect(Collectors.joining("\n"));
    }

    static Map<Boolean, List<Employee>> partitionBySalary(List<Employee> employees, double threshold) {
        return employees.stream()
                .collect(Collectors.partitioningBy(e -> e.salary >= threshold));
    }

    public static void main(String[] args) {
        List<Employee> employees = List.of(
                new Employee(1, "Alice Johnson", "Engineering", 120_000, 10, "Java", "Python", "AWS", "Kubernetes"),
                new Employee(2, "Bob Smith", "Engineering", 110_000, 8, "Java", "TypeScript", "Docker", "Git"),
                new Employee(3, "Carol White", "Engineering", 95_000, 6, "Java", "Python", "SQL"),
                new Employee(4, "David Brown", "Engineering", 80_000, 3, "Java", "Git"),
                new Employee(5, "Eve Davis", "HR", 70_000, 5, "Excel", "Scrum"),
                new Employee(6, "Frank Garcia", "HR", 55_000, 2, "Excel", "Scrum"),
                new Employee(7, "Grace Lee", "Marketing", 75_000, 4, "Photoshop", "Scrum", "SQL"),
                new Employee(8, "Henry Martinez", "Marketing", 68_000, 1, "Photoshop", "Excel"));

        System.out.println("=== Employee Analytics Dashboard ===\n");

        List<String> raised = getRaisedSalaries(employees);
        System.out.println("Raised salaries (Engineering ≥ $90k): " + raised);

        List<Employee> seniors = getSeniorDevs(employees);
        System.out.println("Senior devs (5+ yrs, Engineering): " + seniors.size());

        System.out.printf("Total payroll: $%,.2f%n", getTotalPayroll(employees));

        System.out.println("Avg salary by dept: " + getAvgSalaryByDept(employees));

        Optional<Employee> top = getHighestPaid(employees);
        System.out.println(
                "Highest paid: " + top.map(e -> e.name + " @ $" + String.format("%,.2f", e.salary)).orElse("None"));

        System.out.println("All skills: " + getAllSkills(employees));

        Map<Integer, String> empMap = getEmployeeMap(employees);
        System.out.println("Employee #3 lookup: " + empMap.get(3));

        Map<Boolean, List<Employee>> parts = partitionBySalary(employees, 75_000);
        System.out.println("Above $75k threshold: "
                + parts.get(true).stream().map(Employee::getName).collect(Collectors.joining(", ")));
        System.out.println("Below $75k threshold: "
                + parts.get(false).stream().map(Employee::getName).collect(Collectors.joining(", ")));

        System.out.println("\nSalary Report (descending):");
        System.out.println(getSalaryReport(employees));
    }
}
