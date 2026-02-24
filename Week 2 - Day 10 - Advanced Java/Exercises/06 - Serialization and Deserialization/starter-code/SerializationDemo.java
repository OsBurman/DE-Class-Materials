import java.io.*;

// TODO: Make Employee implement Serializable
class Employee {

    // TODO: add serialVersionUID = 1L
    // TODO: add fields: String name, String department, transient String password

    // TODO: constructor(String name, String department, String password)

    @Override
    public String toString() {
        // TODO: return "Employee{name='...', department='...', password='...'}"
        return "";
    }
}

public class SerializationDemo {

    private static final String FILE_NAME = "employee.ser";

    public static void main(String[] args) {

        Employee emp = new Employee("Alice", "Engineering", "secret123");

        // ── Serialization ───────────────────────────────────────────────────
        System.out.println("=== Serialization ===");

        // TODO: print "Serialized: " + emp

        // TODO: use ObjectOutputStream inside try-with-resources to write emp to FILE_NAME
        //       print "Written to employee.ser" on success
        //       catch IOException and print the error message


        // ── Deserialization ─────────────────────────────────────────────────
        System.out.println("\n=== Deserialization ===");

        // TODO: use ObjectInputStream inside try-with-resources to read from FILE_NAME
        //       cast the result to Employee
        //       print "Deserialized: " + deserialized
        //       print "Note: 'password' is transient — it was NOT serialized"
        //       catch IOException and ClassNotFoundException
    }
}
