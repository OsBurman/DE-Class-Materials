import java.util.ArrayList;

// Interface — marks types that can receive benefits; not all Employee subtypes will implement this
interface Benefitable {
    boolean isEligibleForBenefits();
    String getBenefitsSummary();
}

// Abstract base class — defines the shared employee contract
// Cannot be instantiated; each subclass must implement calculateMonthlyPay()
abstract class Employee {
    private String employeeId;
    private String name;
    private String department;

    public Employee(String employeeId, String name, String department) {
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
    }

    public String getEmployeeId()  { return employeeId; }
    public String getName()        { return name; }
    public String getDepartment()  { return department; }

    // Subclasses must define how to compute monthly pay
    public abstract double calculateMonthlyPay();

    // Concrete template method — uses calculateMonthlyPay() via runtime dispatch
    public String getPayStub() {
        return "Employee: " + name + " (" + employeeId + ")\n"
             + "Department: " + department + "\n"
             + "Monthly Pay: " + String.format("$%.2f", calculateMonthlyPay());
    }
}

// Salaried: fixed annual salary divided evenly across 12 months; always benefits-eligible
class SalariedEmployee extends Employee implements Benefitable {
    private double annualSalary;

    public SalariedEmployee(String id, String name, String dept, double annualSalary) {
        super(id, name, dept);
        this.annualSalary = annualSalary;
    }

    @Override
    public double calculateMonthlyPay() { return annualSalary / 12; }

    @Override public boolean isEligibleForBenefits() { return true; }
    @Override public String getBenefitsSummary()     { return "Full benefits: health, dental, vision, 401k"; }
}

// Hourly: pay depends on actual hours worked this month; benefits only if ≥80 hours
class HourlyEmployee extends Employee implements Benefitable {
    private double hourlyRate;
    private double hoursWorkedThisMonth;

    public HourlyEmployee(String id, String name, String dept,
                           double hourlyRate, double hoursWorkedThisMonth) {
        super(id, name, dept);
        this.hourlyRate = hourlyRate;
        this.hoursWorkedThisMonth = hoursWorkedThisMonth;
    }

    @Override
    public double calculateMonthlyPay() { return hourlyRate * hoursWorkedThisMonth; }

    @Override
    public boolean isEligibleForBenefits() { return hoursWorkedThisMonth >= 80; }

    @Override
    public String getBenefitsSummary() {
        return isEligibleForBenefits() ? "Partial benefits: health only" : "No benefits";
    }
}

// Contract: flat fee per project, NO benefits — does not implement Benefitable
class ContractEmployee extends Employee {
    private double contractFee;
    private String projectName;

    public ContractEmployee(String id, String name, String dept,
                             double contractFee, String projectName) {
        super(id, name, dept);
        this.contractFee = contractFee;
        this.projectName = projectName;
    }

    @Override
    public double calculateMonthlyPay() { return contractFee; }

    // Override getPayStub() to append the project name
    @Override
    public String getPayStub() {
        return super.getPayStub() + "\nProject: " + projectName;
    }
}

public class PayrollSystem {
    public static void main(String[] args) {
        ArrayList<Employee> employees = new ArrayList<>();
        employees.add(new SalariedEmployee("E001", "Alice Johnson", "Engineering", 100000.0));
        employees.add(new SalariedEmployee("E002", "Bob Smith",     "Marketing",   70000.0));
        employees.add(new HourlyEmployee(  "E003", "Carol White",   "Engineering", 26.5, 80.0));
        employees.add(new HourlyEmployee(  "E004", "Dave Brown",    "Support",     15.0, 52.0));
        employees.add(new ContractEmployee("E005", "Eve Davis",     "Engineering", 5000.0, "Cloud Migration"));

        // --- Payroll Report ---
        System.out.println("=== Payroll Report ===\n");
        double total = 0;
        for (int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            System.out.println(emp.getPayStub());  // runtime polymorphism — ContractEmployee's override is used
            total += emp.calculateMonthlyPay();
            if (i < employees.size() - 1) System.out.println("---");
        }
        System.out.println("\nTotal monthly payroll: " + String.format("$%.2f", total));

        System.out.println();

        // --- Benefits Eligibility Report ---
        System.out.println("=== Benefits Eligibility ===");
        for (Employee emp : employees) {
            if (emp instanceof Benefitable) {
                // ContractEmployee does NOT implement Benefitable — this block skips it
                Benefitable b = (Benefitable) emp;
                if (b.isEligibleForBenefits()) {
                    System.out.println(emp.getName() + ": " + b.getBenefitsSummary());
                } else {
                    System.out.println(emp.getName() + " (" + emp.getEmployeeId() + ") is NOT eligible for benefits.");
                }
            }
        }

        // --- Packages note ---
        // In a real project each class would declare: package com.company.payroll;
        // And other files would import: import com.company.payroll.Employee;
        // Packages prevent name collisions across libraries and control access via package-private visibility.
    }
}
