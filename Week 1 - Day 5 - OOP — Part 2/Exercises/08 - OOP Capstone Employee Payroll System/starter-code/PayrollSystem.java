import java.util.ArrayList;

// TODO: Define interface Benefitable with:
//       boolean isEligibleForBenefits()
//       String getBenefitsSummary()


// TODO: Define abstract class Employee with:
//       Private fields: String employeeId, String name, String department
//       Constructor taking all three fields
//       Getters: getEmployeeId(), getName(), getDepartment()
//       Abstract method: double calculateMonthlyPay()
//       Concrete method: String getPayStub() — returns:
//         "Employee: [name] ([employeeId])\nDepartment: [department]\nMonthly Pay: $[pay to 2 dp]"
//         Use String.format("$%.2f", calculateMonthlyPay()) for the pay line


// TODO: Create class SalariedEmployee extending Employee and implementing Benefitable
//       Field: double annualSalary
//       Constructor: SalariedEmployee(String id, String name, String dept, double annualSalary)
//       calculateMonthlyPay(): return annualSalary / 12
//       isEligibleForBenefits(): return true
//       getBenefitsSummary(): return "Full benefits: health, dental, vision, 401k"


// TODO: Create class HourlyEmployee extending Employee and implementing Benefitable
//       Fields: double hourlyRate, double hoursWorkedThisMonth
//       Constructor: HourlyEmployee(String id, String name, String dept, double hourlyRate, double hoursWorked)
//       calculateMonthlyPay(): return hourlyRate * hoursWorkedThisMonth
//       isEligibleForBenefits(): return true if hoursWorkedThisMonth >= 80, else false
//       getBenefitsSummary(): return "Partial benefits: health only" if eligible, "No benefits" if not


// TODO: Create class ContractEmployee extending Employee (does NOT implement Benefitable)
//       Fields: double contractFee, String projectName
//       Constructor: ContractEmployee(String id, String name, String dept, double contractFee, String projectName)
//       calculateMonthlyPay(): return contractFee
//       Override getPayStub(): call super.getPayStub() and append "\nProject: [projectName]"


public class PayrollSystem {
    public static void main(String[] args) {
        // TODO: Create an ArrayList<Employee>

        // TODO: Add employees:
        //   SalariedEmployee("E001", "Alice Johnson", "Engineering", 100000.0)
        //   SalariedEmployee("E002", "Bob Smith", "Marketing", 70000.0)
        //   HourlyEmployee("E003", "Carol White", "Engineering", 26.5, 80.0)
        //   HourlyEmployee("E004", "Dave Brown", "Support", 15.0, 52.0)
        //   ContractEmployee("E005", "Eve Davis", "Engineering", 5000.0, "Cloud Migration")

        // --- Payroll Report ---
        System.out.println("=== Payroll Report ===\n");
        // TODO: Loop through all employees and print getPayStub() for each
        //       Print "---" between each employee (not after the last one)

        // TODO: Compute total monthly payroll by summing calculateMonthlyPay() for all employees
        // TODO: Print: "Total monthly payroll: $" + String.format("%.2f", total)

        System.out.println();

        // --- Benefits Report ---
        System.out.println("=== Benefits Eligibility ===");
        // TODO: Loop through employees again
        //       Check if the employee instanceof Benefitable — if yes, downcast
        //       If isEligibleForBenefits() is true, print "[name]: [getBenefitsSummary()]"
        //       If isEligibleForBenefits() is false, print "[name] ([id]) is NOT eligible for benefits."
        //       ContractEmployee is NOT Benefitable — skip it silently

        // NOTE ON PACKAGES:
        // In a real multi-file Java project, each class would have a package declaration:
        //   package com.company.payroll;
        // Other files would import it:
        //   import com.company.payroll.Employee;
        //   import com.company.payroll.SalariedEmployee;
        // Packages organize classes into namespaces and control visibility.
        // In this single-file exercise all classes share the default (unnamed) package.
    }
}
