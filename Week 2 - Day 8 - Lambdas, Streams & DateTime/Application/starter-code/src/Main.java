import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        List<Employee> employees = List.of(
                new Employee(1, "Alice Chen", "Engineering", 95000, LocalDate.of(2020, 3, 15), true),
                new Employee(2, "Bob Torres", "Marketing", 72000, LocalDate.of(2022, 7, 1), true),
                new Employee(3, "Carol Pham", "Engineering", 105000, LocalDate.of(2019, 1, 20), true),
                new Employee(4, "David Kim", "HR", 65000, LocalDate.of(2021, 11, 5), false),
                new Employee(5, "Eve Nakamura", "Engineering", 98000, LocalDate.of(2018, 6, 10), true),
                new Employee(6, "Frank Rivera", "Marketing", 78000, LocalDate.of(2023, 2, 28), true));

        EmployeeDashboard dashboard = new EmployeeDashboard();

        // TODO: Call each dashboard method and print the results
        // Example:
        // System.out.println("--- Active Senior Employees ---");
        // dashboard.getActiveSeniorEmployees(employees).forEach(System.out::println);

        // System.out.println("\n--- Employee Names ---");
        // System.out.println(dashboard.getEmployeeNames(employees));

        // System.out.println("\n--- Total Salary ---");
        // System.out.println(dashboard.getTotalSalary(employees));

        // System.out.println("\n--- Average Salary ---");
        // dashboard.getAverageSalary(employees).ifPresent(avg ->
        // System.out.printf("$%.2f%n", avg));

        // System.out.println("\n--- Grouped by Department ---");
        // dashboard.groupByDepartment(employees).forEach((dept, emps) -> { ... });

        // System.out.println("\n--- Highest Paid ---");
        // dashboard.findHighestPaid(employees).ifPresent(e ->
        // System.out.println(e.name()));
        // // orElse: dashboard.findHighestPaid(List.of()).orElse(new Employee(...))

        // System.out.println("\n--- Tenures ---");
        // employees.forEach(e -> System.out.println(e.name() + ": " +
        // dashboard.getTenure(e)));

        // System.out.println("\n--- Formatted Hire Dates ---");
        // employees.forEach(e -> System.out.println(e.name() + ": " +
        // dashboard.getFormattedHireDate(e)));

        // System.out.println("\n--- Hired in 2021 ---");
        // dashboard.getEmployeesHiredIn(employees, 2021).forEach(System.out::println);
    }
}
