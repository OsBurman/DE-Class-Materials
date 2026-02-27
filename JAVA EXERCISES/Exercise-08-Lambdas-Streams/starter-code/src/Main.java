import java.util.*;
import java.util.stream.*;
import java.util.function.*;

/**
 * Exercise 08 — Lambdas & Streams
 * Employee Analytics Dashboard — STARTER CODE
 *
 * Implement all TODO methods using the Java Streams API.
 * Do NOT use for-loops or if-statements inside the methods — streams only!
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

    // -----------------------------------------------------------------
    // TODO 1 — getRaisedSalaries
    // Return a List<String> of names of Engineering employees
    // whose salary is >= $90,000, each name prefixed with "★ ".
    // Use: filter → map → collect(Collectors.toList())
    // -----------------------------------------------------------------
    static List<String> getRaisedSalaries(List<Employee> employees) {
        // TODO: implement with stream pipeline
        return new ArrayList<>();
    }

    // -----------------------------------------------------------------
    // TODO 2 — getSeniorDevs
    // Return employees who are in "Engineering" AND have >= 5 years exp.
    // Use: chained filter → collect
    // -----------------------------------------------------------------
    static List<Employee> getSeniorDevs(List<Employee> employees) {
        // TODO: implement with stream pipeline
        return new ArrayList<>();
    }

    // -----------------------------------------------------------------
    // TODO 3 — getTotalPayroll
    // Return the sum of ALL employees' salaries.
    // Use: mapToDouble → sum
    // -----------------------------------------------------------------
    static double getTotalPayroll(List<Employee> employees) {
        // TODO: implement with stream pipeline
        return 0.0;
    }

    // -----------------------------------------------------------------
    // TODO 4 — getAvgSalaryByDept
    // Return a Map<String, Double> of department → average salary.
    // Use: Collectors.groupingBy + Collectors.averagingDouble
    // -----------------------------------------------------------------
    static Map<String, Double> getAvgSalaryByDept(List<Employee> employees) {
        // TODO: implement with stream pipeline
        return new HashMap<>();
    }

    // -----------------------------------------------------------------
    // TODO 5 — getHighestPaid
    // Return an Optional<Employee> of the highest-paid employee.
    // Use: max(Comparator.comparingDouble(...))
    // -----------------------------------------------------------------
    static Optional<Employee> getHighestPaid(List<Employee> employees) {
        // TODO: implement with stream pipeline
        return Optional.empty();
    }

    // -----------------------------------------------------------------
    // TODO 6 — getAllSkills
    // Return a sorted List<String> of ALL unique skills across all employees.
    // Use: flatMap → distinct → sorted → collect
    // -----------------------------------------------------------------
    static List<String> getAllSkills(List<Employee> employees) {
        // TODO: implement with stream pipeline
        return new ArrayList<>();
    }

    // -----------------------------------------------------------------
    // TODO 7 — getEmployeeMap
    // Return a Map<Integer, String> of employee id → employee name.
    // Use: Collectors.toMap
    // -----------------------------------------------------------------
    static Map<Integer, String> getEmployeeMap(List<Employee> employees) {
        // TODO: implement with stream pipeline
        return new HashMap<>();
    }

    // -----------------------------------------------------------------
    // TODO 8 — getSalaryReport
    // Return a single String with one line per employee,
    // sorted by salary descending. Each line:
    // " Name Dept $salary (N yrs)"
    // Use: sorted(Comparator.comparingDouble(...).reversed()) → map →
    // collect(Collectors.joining("\n"))
    // -----------------------------------------------------------------
    static String getSalaryReport(List<Employee> employees) {
        // TODO: implement with stream pipeline
        return "";
    }

    // -----------------------------------------------------------------
    // TODO 9 — partitionBySalary
    // Partition employees into two groups using a threshold salary.
    // Return Map<Boolean, List<Employee>>
    // true → salary >= threshold
    // false → salary < threshold
    // Use: Collectors.partitioningBy
    // -----------------------------------------------------------------
    static Map<Boolean, List<Employee>> partitionBySalary(List<Employee> employees, double threshold) {
        // TODO: implement with stream pipeline
        return new HashMap<>();
    }

    // -----------------------------------------------------------------
    // Main — do not modify
    // -----------------------------------------------------------------
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
