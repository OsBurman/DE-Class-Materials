import java.util.ArrayList;

class Course {
    // TODO: Declare private fields:
    //       String courseCode, String title, int maxEnrollment, ArrayList<String> enrolledStudents

    // TODO: Write the constructor Course(String courseCode, String title, int maxEnrollment):
    //       1. Validate courseCode is not null or blank:
    //          if (courseCode == null || courseCode.isBlank()) throw new IllegalArgumentException("Course code cannot be blank");
    //       2. Validate title is not null or blank (same pattern)
    //       3. Validate maxEnrollment is between 1 and 500:
    //          if (maxEnrollment < 1 || maxEnrollment > 500) throw new IllegalArgumentException("Max enrollment must be between 1 and 500");
    //       4. Assign all valid values
    //       5. Initialize enrolledStudents as a new empty ArrayList<String>

    // TODO: Write getters getCourseCode() and getTitle()

    // TODO: Write setter setMaxEnrollment(int max) with the same 1-500 validation

    // TODO: Write void enroll(String studentName):
    //       - Validate studentName is not null/blank (throw IllegalArgumentException)
    //       - If enrolledStudents.size() >= maxEnrollment, throw new IllegalStateException("Course is full")
    //       - Otherwise add studentName to enrolledStudents

    // TODO: Write void drop(String studentName):
    //       - Call enrolledStudents.remove(studentName) — it returns a boolean (true if removed)
    //       - If it returns false, throw new IllegalArgumentException("Student not enrolled: " + studentName)

    // TODO: Write ArrayList<String> getEnrolledStudents():
    //       Return a COPY: return new ArrayList<>(enrolledStudents);
    //       DO NOT return the original list — that would let callers bypass enroll()/drop()

    // TODO: Write int getEnrollmentCount() — returns enrolledStudents.size()

    // TODO: Write boolean isFull() — returns enrolledStudents.size() >= maxEnrollment
}

public class EncapsulationDemo {
    public static void main(String[] args) {
        System.out.println("=== Course Enrollment System ===\n");

        // TODO: Create a Course("CS101", "Intro to Java", 3)
        // TODO: Print "Course created: CS101 - Intro to Java (max: 3)"

        System.out.println();

        // TODO: Enroll "Alice" — catch IllegalArgumentException/IllegalStateException if thrown
        //       Print "Enrolled Alice. Students: " + course.getEnrolledStudents()
        // TODO: Enroll "Bob" and "Carol" similarly

        System.out.println();

        // --- Defensive copy demonstration ---
        System.out.println("--- Defensive copy demonstration ---");
        // TODO: Call getEnrolledStudents() and store in a variable 'externalList'
        // TODO: Print "External list (copy): " + externalList
        // TODO: Add "Eve" to externalList (externalList.add("Eve"))
        // TODO: Print "Adding Eve to the external copy..."
        // TODO: Call getEnrolledStudents() again and print "Course's internal list (unchanged): " + ...
        // TODO: Print "Course enrollment count: " + course.getEnrollmentCount()

        System.out.println();

        // --- Try to enroll when full ---
        System.out.println("--- Attempting to enroll a 4th student ---");
        // TODO: Try to enroll "Dave"
        //       Catch IllegalStateException and print "Caught: " + e.getMessage()

        // TODO: Drop "Bob", print "Dropped Bob. Updated list: " + course.getEnrolledStudents()

        System.out.println();

        // --- Invalid course creation ---
        System.out.println("--- Invalid course creation ---");
        // TODO: Try to create a Course("BAD", "Test", 600) — maxEnrollment is out of range
        //       Catch IllegalArgumentException and print "Caught: " + e.getMessage()
    }
}
