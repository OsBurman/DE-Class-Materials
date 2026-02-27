class Student {

    String name;
    int    studentId;
    double gpa;

    Student(String name, int studentId, double gpa) {
        this.name      = name;
        this.studentId = studentId;
        this.gpa       = gpa;
    }

    // Getters
    String getName()       { return name; }
    int    getStudentId()  { return studentId; }
    double getGpa()        { return gpa; }

    // Setters
    void setName(String name) {
        this.name = name;
    }

    void setGpa(double gpa) {
        if (gpa < 0.0 || gpa > 4.0) {
            System.out.println("Invalid GPA: " + gpa);  // guard clause — bail out early
            return;
        }
        this.gpa = gpa;
    }

    String getLetterGrade() {
        if      (gpa >= 3.7) return "A";
        else if (gpa >= 3.0) return "B";
        else if (gpa >= 2.0) return "C";
        else if (gpa >= 1.0) return "D";
        else                 return "F";
    }

    @Override
    public String toString() {
        // Calling getLetterGrade() keeps the grade calculation in one place
        return "Student{id=" + studentId + ", name='" + name + "', gpa=" + gpa
               + ", grade=" + getLetterGrade() + "}";
    }
}

public class StudentDemo {

    public static void main(String[] args) {

        Student s1 = new Student("Alice", 1001, 3.8);
        Student s2 = new Student("Bob",   1002, 2.5);

        System.out.println(s1);                         // toString() called automatically
        System.out.println(s2);

        s1.setName("Alicia");
        s1.setGpa(3.5);
        s1.setGpa(5.5);                                 // invalid — prints error, gpa stays 3.5

        System.out.println(s1);                         // name=Alicia, gpa=3.5, grade=B
        System.out.println("Bob's GPA: " + s2.getGpa());
    }
}
