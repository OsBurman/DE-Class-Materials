package exceptions;

/**
 * Thrown when a student cannot be found by name.
 * This is an UNCHECKED exception — callers are not required to catch it.
 *
 * TODO Task 2: Extend RuntimeException.
 * Accept a studentName and pass "Student not found: [studentName]" to super().
 */
public class StudentNotFoundException extends RuntimeException {

    // TODO: public StudentNotFoundException(String studentName) { super("Student not found: " + studentName); }
}
