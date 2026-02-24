/**
 * Represents a student's academic profile.
 *
 * TODO: Complete all method stubs below.
 * Each TODO is numbered to match the instructions.md tasks.
 */
public class StudentProfile {

    // TODO Task 1: Declare the following fields with correct types and access modifiers:
    // - name         (String)
    // - age          (int)
    // - gpa          (double)
    // - studentId    (long)
    // - isEnrolled   (boolean)
    // - grade        (char)  — letter grade e.g. 'A', 'B', 'C'


    // TODO Task 2: Build a parameterized constructor that accepts all 6 fields
    // and assigns them using the 'this' keyword.
    // public StudentProfile(String name, int age, double gpa, long studentId, boolean isEnrolled, char grade) {
    //     ...
    // }


    // Getter methods (needed by Main.java — implement after Task 2)
    public String getName()       { return ""; /* TODO: return name */ }
    public int getAge()           { return 0;  /* TODO: return age  */ }
    public double getGpa()        { return 0.0;/* TODO: return gpa  */ }
    public boolean isEnrolled()   { return false; /* TODO */ }


    // TODO Task 3: Implement displayProfile() using StringBuilder
    // Build a multi-line string showing all field values.
    // Return the final StringBuilder's toString().
    public String displayProfile() {
        StringBuilder sb = new StringBuilder();
        // Hint: sb.append("Name: ").append(name).append("\n");
        // ... add all fields
        return sb.toString();
    }


    // TODO Task 4: Implement getGpaLetterGrade()
    // Use if-else if to return the correct grade label based on the GPA ranges in instructions.md
    public String getGpaLetterGrade() {
        return ""; // replace with your logic
    }


    // TODO Task 5: Implement getAgeCategoryMessage()
    // Explicitly CAST age to byte first: byte ageAsByte = (byte) age;
    // Then use the ageAsByte in your conditions.
    public String getAgeCategoryMessage() {
        return ""; // replace with your logic
    }


    // TODO Task 6: Implement getWrappedId()
    // 1. Autobox studentId into a Long:  Long wrappedId = studentId;
    // 2. Unbox it back to long:          long unboxed = wrappedId;
    // 3. Add a comment explaining autoboxing vs unboxing
    // 4. Return wrappedId
    public Long getWrappedId() {
        return null; // replace with your logic
    }
}
