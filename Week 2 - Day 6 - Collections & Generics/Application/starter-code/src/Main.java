import java.util.*;

public class Main {
    public static void main(String[] args) {
        EnrollmentSystem system = new EnrollmentSystem();

        // Sample students
        Student s1 = new Student(1, "Alice Chen", 3.9);
        Student s2 = new Student(2, "Bob Torres", 3.4);
        Student s3 = new Student(3, "Carol Pham", 3.7);
        Student s4 = new Student(4, "David Kim", 2.8);
        Student s5 = new Student(5, "Eve Nakamura", 3.5);

        // TODO Task 2: Add all students, then remove one by id using the system
        // system.addStudent(s1); ... system.removeStudent(4);

        // TODO Task 3: Add course codes to the HashSet, check if a course is offered

        // TODO Task 4: Add students to the waitlist (TreeSet), print it sorted

        // TODO Task 5: Enroll students into courses, retrieve enrolled list

        // TODO Task 6: Add courses to the catalog (TreeMap), print it

        // TODO Task 7: Sort students by GPA descending, then print
        List<Student> all = new ArrayList<>(List.of(s1, s2, s3, s4, s5));
        // system.sortByGpaDescending(all);
        // system.printCollection(all);  // Task 8 generic method

        System.out.println("Done!");
    }
}
