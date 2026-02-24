import java.util.Objects;

/**
 * Represents a student. Implements Comparable for natural (alphabetical) ordering.
 * Complete all TODOs.
 */
public class Student implements Comparable<Student> {

    // TODO Task 1: Declare fields — studentId (int), name (String), gpa (double)


    // TODO: Parameterized constructor
    public Student(int studentId, String name, double gpa) {

    }

    // TODO: Getters for all fields


    // TODO: Implement compareTo() — natural order is alphabetical by name
    @Override
    public int compareTo(Student other) {
        return 0; // replace with: this.name.compareTo(other.name)
    }

    // TODO: Override equals() — equality based on studentId
    @Override
    public boolean equals(Object o) {
        return false;
    }

    // TODO: Override hashCode() — based on studentId
    @Override
    public int hashCode() {
        return 0;
    }

    // TODO: Override toString()
    @Override
    public String toString() {
        return "";
    }
}
