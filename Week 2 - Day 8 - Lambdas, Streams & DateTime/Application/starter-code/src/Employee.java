import java.time.LocalDate;

/**
 * Represents an employee. This is a Java record — fields are final and
 * getters are auto-generated (name(), department(), salary(), etc.)
 *
 * No changes needed here — use this as your data model in EmployeeDashboard.
 */
public record Employee(
    int id,
    String name,
    String department,
    double salary,
    LocalDate hireDate,
    boolean isActive
) {
    @Override
    public String toString() {
        return String.format("Employee{id=%d, name='%s', dept='%s', salary=%.2f, hired=%s, active=%b}",
            id, name, department, salary, hireDate, isActive);
    }
}
