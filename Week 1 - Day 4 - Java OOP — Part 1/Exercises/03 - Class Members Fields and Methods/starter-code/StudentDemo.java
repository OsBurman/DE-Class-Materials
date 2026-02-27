class Student {

    // TODO: Declare three fields: String name, int studentId, double gpa

    // TODO: Write a parameterized constructor  Student(String name, int studentId, double gpa)
    //       Assign all three parameters to the corresponding fields

    // TODO: Write getter  String getName()
    // TODO: Write getter  int getStudentId()
    // TODO: Write getter  double getGpa()

    // TODO: Write setter  void setName(String name)
    //       Assign the parameter to the field

    // TODO: Write setter  void setGpa(double gpa)
    //       If gpa < 0.0 OR gpa > 4.0:
    //           Print "Invalid GPA: [gpa]"
    //           return without updating the field
    //       Otherwise assign the new value

    // TODO: Write method  String getLetterGrade()
    //       Return "A" if gpa >= 3.7
    //       Return "B" if gpa >= 3.0
    //       Return "C" if gpa >= 2.0
    //       Return "D" if gpa >= 1.0
    //       Return "F" otherwise

    // TODO: Override toString()
    //       Annotate with @Override
    //       Return "Student{id=[studentId], name='[name]', gpa=[gpa], grade=[letterGrade]}"
    //       Hint: call getLetterGrade() inside the return statement
}

public class StudentDemo {

    public static void main(String[] args) {

        // TODO: Create Student s1 with name="Alice", id=1001, gpa=3.8
        // TODO: Create Student s2 with name="Bob",   id=1002, gpa=2.5

        // TODO: Print s1 using System.out.println(s1)  (calls toString() automatically)
        // TODO: Print s2 using System.out.println(s2)

        // TODO: Update s1: setName("Alicia"), setGpa(3.5)
        // TODO: Try setting s1's gpa to 5.5 (should print error and NOT update)
        // TODO: Print s1 again (should show name=Alicia, gpa=3.5)

        // TODO: Print "Bob's GPA: " + s2.getGpa()
    }
}
