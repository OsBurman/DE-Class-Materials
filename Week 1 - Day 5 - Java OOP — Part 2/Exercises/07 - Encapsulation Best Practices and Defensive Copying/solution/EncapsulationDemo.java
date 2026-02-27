import java.util.ArrayList;

class Course {
    private String courseCode;
    private String title;
    private int maxEnrollment;
    private ArrayList<String> enrolledStudents;  // mutable field — must be defensively copied

    public Course(String courseCode, String title, int maxEnrollment) {
        // Validate all inputs before assigning — fail fast with clear messages
        if (courseCode == null || courseCode.isBlank())
            throw new IllegalArgumentException("Course code cannot be blank");
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Title cannot be blank");
        if (maxEnrollment < 1 || maxEnrollment > 500)
            throw new IllegalArgumentException("Max enrollment must be between 1 and 500");

        this.courseCode = courseCode;
        this.title = title;
        this.maxEnrollment = maxEnrollment;
        this.enrolledStudents = new ArrayList<>();  // always starts empty
    }

    public String getCourseCode() { return courseCode; }
    public String getTitle()      { return title; }

    public void setMaxEnrollment(int max) {
        if (max < 1 || max > 500)
            throw new IllegalArgumentException("Max enrollment must be between 1 and 500");
        this.maxEnrollment = max;
    }

    public void enroll(String studentName) {
        if (studentName == null || studentName.isBlank())
            throw new IllegalArgumentException("Student name cannot be blank");
        if (enrolledStudents.size() >= maxEnrollment)
            throw new IllegalStateException("Course is full");
        enrolledStudents.add(studentName);
    }

    public void drop(String studentName) {
        // remove() returns false if the element was not found
        boolean removed = enrolledStudents.remove(studentName);
        if (!removed)
            throw new IllegalArgumentException("Student not enrolled: " + studentName);
    }

    // Defensive copy — the caller gets a snapshot; modifying it does NOT affect enrolledStudents
    public ArrayList<String> getEnrolledStudents() {
        return new ArrayList<>(enrolledStudents);
    }

    public int getEnrollmentCount() { return enrolledStudents.size(); }
    public boolean isFull()         { return enrolledStudents.size() >= maxEnrollment; }
}

public class EncapsulationDemo {
    public static void main(String[] args) {
        System.out.println("=== Course Enrollment System ===\n");

        Course course = new Course("CS101", "Intro to Java", 3);
        System.out.println("Course created: CS101 - Intro to Java (max: 3)");

        System.out.println();

        // Enroll three students; each succeeds because max is 3
        course.enroll("Alice");
        System.out.println("Enrolled Alice. Students: " + course.getEnrolledStudents());
        course.enroll("Bob");
        System.out.println("Enrolled Bob. Students: " + course.getEnrolledStudents());
        course.enroll("Carol");
        System.out.println("Enrolled Carol. Students: " + course.getEnrolledStudents());

        System.out.println();

        // --- Defensive copy demonstration ---
        System.out.println("--- Defensive copy demonstration ---");
        ArrayList<String> externalList = course.getEnrolledStudents();  // returns a copy
        System.out.println("External list (copy): " + externalList);
        externalList.add("Eve");   // modifying the COPY
        System.out.println("Adding Eve to the external copy...");
        // The internal list is unchanged — Eve was added to externalList, not enrolledStudents
        System.out.println("Course's internal list (unchanged): " + course.getEnrolledStudents());
        System.out.println("Course enrollment count: " + course.getEnrollmentCount());

        System.out.println();

        // --- Attempt to enroll when full ---
        System.out.println("--- Attempting to enroll a 4th student ---");
        try {
            course.enroll("Dave");
        } catch (IllegalStateException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // Drop a student and verify
        course.drop("Bob");
        System.out.println("Dropped Bob. Updated list: " + course.getEnrolledStudents());

        System.out.println();

        // --- Invalid course creation ---
        System.out.println("--- Invalid course creation ---");
        try {
            new Course("BAD", "Test", 600);  // maxEnrollment exceeds limit
        } catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e.getMessage());
        }
    }
}
