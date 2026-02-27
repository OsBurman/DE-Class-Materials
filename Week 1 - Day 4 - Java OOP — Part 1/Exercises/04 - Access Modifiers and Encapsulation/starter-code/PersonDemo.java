class Person {

    // TODO: Declare three PRIVATE fields: String name, int age, String email

    // TODO: Write a PUBLIC parameterized constructor Person(String name, int age, String email)
    //       Assign all three fields using 'this.field = parameter'

    // TODO: Write PUBLIC getters: getName(), getAge(), getEmail()

    // TODO: Write PUBLIC setter  void setName(String name)
    //       If name is null OR name.isBlank() → print "Name cannot be blank" and return
    //       Otherwise assign the new value

    // TODO: Write PUBLIC setter  void setAge(int age)
    //       If age < 0 OR age > 150 → print "Invalid age: [age]" and return
    //       Otherwise assign the new value

    // TODO: Write PUBLIC setter  void setEmail(String email)
    //       If the email does NOT contain "@" → print "Invalid email: [email]" and return
    //       Otherwise assign the new value
    //       Hint: use email.contains("@")

    // TODO: Write a PACKAGE-PRIVATE (no modifier) method  String formatForLog()
    //       Return "[name]|[age]|[email]"

    // TODO: Override toString() (PUBLIC)
    //       Return "Person{name='[name]', age=[age], email='[email]'}"
}

public class PersonDemo {

    public static void main(String[] args) {

        // TODO: Create a Person: name="Carol", age=30, email="carol@example.com"
        //       Print the person

        // TODO: Use setters to update: name="Carlos", age=31, email="carlos@work.com"
        //       Print the person after valid updates

        // TODO: Try three INVALID updates (each should print an error):
        //       setAge(-5)
        //       setEmail("not-an-email")
        //       setName("")  or  setName("   ")

        // TODO: Print the person again (should be unchanged from valid-update state)

        // TODO: Call formatForLog() and print "Log: [result]"
    }
}
