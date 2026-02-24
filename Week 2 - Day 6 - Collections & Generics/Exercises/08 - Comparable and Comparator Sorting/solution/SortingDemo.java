import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortingDemo {

    // ============================================================
    // Student implements Comparable<Student> for natural ordering
    // Natural order = GPA descending (highest first)
    // ============================================================
    static class Student implements Comparable<Student> {
        private final String name;
        private final double gpa;
        private final int age;

        public Student(String name, double gpa, int age) {
            this.name = name;
            this.gpa = gpa;
            this.age = age;
        }

        public String getName() { return name; }
        public double getGpa()  { return gpa; }
        public int getAge()     { return age; }

        // Natural order: GPA descending — swap argument order vs ascending
        // Double.compare avoids floating-point subtraction pitfalls
        @Override
        public int compareTo(Student other) {
            return Double.compare(other.gpa, this.gpa);  // other first = descending
        }

        @Override
        public String toString() {
            return "Student{" + name + ", " + gpa + ", " + age + "}";
        }
    }

    // Helper: prints every student in the list on its own line
    static void printAll(List<Student> list) {
        for (Student s : list) {
            System.out.println(s);
        }
    }

    public static void main(String[] args) {

        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student("Alice", 3.8, 22));
        students.add(new Student("Bob",   3.5, 20));
        students.add(new Student("Carol", 3.9, 21));
        students.add(new Student("Dave",  3.5, 23));

        // ---- Natural order (Comparable) ----
        System.out.println("=== Natural Order (Comparable — GPA descending) ===");
        Collections.sort(students);   // uses Student.compareTo()
        printAll(students);
        System.out.println();

        // ---- Comparator by name ----
        System.out.println("=== Comparator by Name (alphabetical) ===");
        // Comparator.comparing() extracts a Comparable key — here: Student::getName returns String
        Comparator<Student> byName = Comparator.comparing(Student::getName);
        students.sort(byName);
        printAll(students);
        System.out.println();

        // ---- Chained Comparator: GPA desc then name asc ----
        System.out.println("=== Chained Comparator (GPA desc, then Name asc) ===");
        // reversed() flips comparingDouble(gpa) to descending
        // thenComparing() is only evaluated when the first comparator returns 0 (a tie in GPA)
        Comparator<Student> byGpaThenName =
                Comparator.comparingDouble(Student::getGpa)
                          .reversed()
                          .thenComparing(Student::getName);
        students.sort(byGpaThenName);
        printAll(students);
        System.out.println();

        // ---- Age ascending ----
        System.out.println("=== Comparator by Age (ascending) ===");
        // comparingInt() is preferred over a subtraction lambda — avoids int overflow edge cases
        Comparator<Student> byAge = Comparator.comparingInt(Student::getAge);
        students.sort(byAge);
        printAll(students);
    }
}
