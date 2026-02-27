class Person {

    // private — no outside code can read or write these directly
    private String name;
    private int    age;
    private String email;

    public Person(String name, int age, String email) {
        this.name  = name;
        this.age   = age;
        this.email = email;
    }

    // --- Getters ---
    public String getName()  { return name; }
    public int    getAge()   { return age; }
    public String getEmail() { return email; }

    // --- Setters with validation ---
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            System.out.println("Name cannot be blank");
            return;
        }
        this.name = name;
    }

    public void setAge(int age) {
        if (age < 0 || age > 150) {
            System.out.println("Invalid age: " + age);
            return;
        }
        this.age = age;
    }

    public void setEmail(String email) {
        if (!email.contains("@")) {
            System.out.println("Invalid email: " + email);
            return;
        }
        this.email = email;
    }

    // Package-private (no modifier) — only accessible within the same package
    String formatForLog() {
        return name + "|" + age + "|" + email;
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + ", email='" + email + "'}";
    }
}

public class PersonDemo {

    public static void main(String[] args) {

        Person p = new Person("Carol", 30, "carol@example.com");
        System.out.println(p);                              // initial state

        // Valid updates
        p.setName("Carlos");
        p.setAge(31);
        p.setEmail("carlos@work.com");
        System.out.println(p);

        // Invalid updates — each prints an error and leaves the field unchanged
        p.setAge(-5);
        p.setEmail("not-an-email");
        p.setName("");

        System.out.println(p);                              // should be identical to previous print

        // formatForLog() is accessible here because PersonDemo is in the same package
        System.out.println("Log: " + p.formatForLog());
    }
}
