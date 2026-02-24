import java.io.*;

class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String department;
    private transient String password;   // transient — excluded from serialization

    public Employee(String name, String department, String password) {
        this.name = name;
        this.department = department;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Employee{name='" + name + "', department='" + department + "', password='" + password + "'}";
    }
}

public class SerializationDemo {

    private static final String FILE_NAME = "employee.ser";

    public static void main(String[] args) {

        Employee emp = new Employee("Alice", "Engineering", "secret123");

        // ── Serialization ───────────────────────────────────────────────────
        System.out.println("=== Serialization ===");
        System.out.println("Serialized: " + emp);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(emp);
            System.out.println("Written to " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Serialization error: " + e.getMessage());
        }

        // ── Deserialization ─────────────────────────────────────────────────
        System.out.println("\n=== Deserialization ===");

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            Employee deserialized = (Employee) ois.readObject();
            System.out.println("Deserialized: " + deserialized);
            System.out.println("Note: 'password' is transient — it was NOT serialized");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Deserialization error: " + e.getMessage());
        }
    }
}
