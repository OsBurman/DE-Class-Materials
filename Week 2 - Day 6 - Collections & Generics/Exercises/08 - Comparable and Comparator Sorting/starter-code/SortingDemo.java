import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortingDemo {

    // ============================================================
    // TODO: Define a Student class that implements Comparable<Student>
    //   Fields:  String name, double gpa, int age
    //   Natural ordering: GPA DESCENDING (highest GPA first)
    //   toString(): "Student{name, gpa, age}"
    //
    //   Hint for compareTo: Double.compare(other.gpa, this.gpa)
    //   (reversed arguments = descending)
    // ============================================================


    public static void main(String[] args) {

        // Create the student list
        ArrayList<Student> students = new ArrayList<>();
        // TODO: Add 4 students:
        //   "Alice" gpa=3.8 age=22
        //   "Bob"   gpa=3.5 age=20
        //   "Carol" gpa=3.9 age=21
        //   "Dave"  gpa=3.5 age=23


        // ---- Natural order (Comparable) ----
        System.out.println("=== Natural Order (Comparable — GPA descending) ===");
        // TODO: Sort using Collections.sort(students)
        // TODO: Print each student


        System.out.println();

        // ---- Comparator by name ----
        System.out.println("=== Comparator by Name (alphabetical) ===");
        // TODO: Create Comparator<Student> byName using Comparator.comparing() and a method reference
        // TODO: Sort using list.sort(byName)
        // TODO: Print each student


        System.out.println();

        // ---- Chained Comparator: GPA desc then name asc ----
        System.out.println("=== Chained Comparator (GPA desc, then Name asc) ===");
        // TODO: Create Comparator<Student> byGpaThenName using:
        //       Comparator.comparingDouble(Student::getGpa).reversed().thenComparing(Student::getName)
        // TODO: Sort and print


        System.out.println();

        // ---- Age ascending ----
        System.out.println("=== Comparator by Age (ascending) ===");
        // TODO: Create a Comparator<Student> using a lambda: (a, b) -> a.getAge() - b.getAge()
        //       OR use Comparator.comparingInt(Student::getAge)
        // TODO: Sort and print

    }
}
