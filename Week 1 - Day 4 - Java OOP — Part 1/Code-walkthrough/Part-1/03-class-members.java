/**
 * 03-class-members.java
 * Day 4 — OOP Part 1
 *
 * Topics covered:
 *   - Instance fields (declared at class level, per-object state)
 *   - Class fields vs local variables
 *   - Instance methods (behaviors)
 *   - Method signatures: return types, parameters, void
 *   - Method overloading (same name, different params)
 *   - Getter and setter methods
 *   - Computed / derived methods
 *   - Methods calling other methods
 *   - Object composition (objects as fields)
 */
public class ClassMembers {

    // =========================================================
    // CLASS: Address — demonstrates object composition
    // =========================================================

    static class Address {
        String street;
        String city;
        String state;
        String zipCode;

        Address(String street, String city, String state, String zipCode) {
            this.street = street;
            this.city = city;
            this.state = state;
            this.zipCode = zipCode;
        }

        // Method: returns formatted address string
        String getFormatted() {
            return street + ", " + city + ", " + state + " " + zipCode;
        }

        @Override
        public String toString() {
            return getFormatted();
        }
    }

    // =========================================================
    // CLASS: Person — demonstrates full class member anatomy
    // =========================================================

    static class Person {

        // =========================================================
        // INSTANCE FIELDS (class-level variables)
        // =========================================================
        // These belong to EACH OBJECT — every Person gets its own copy.
        // Contrast with local variables (inside methods) which only
        // exist while the method is executing.

        String firstName;
        String lastName;
        int age;
        String email;
        Address homeAddress;        // field of type Address — object composition

        // =========================================================
        // CONSTRUCTOR
        // =========================================================
        Person(String firstName, String lastName, int age, String email, Address homeAddress) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.email = email;
            this.homeAddress = homeAddress;
        }

        // =========================================================
        // GETTER METHODS
        // =========================================================
        // Getters provide controlled READ access to fields.
        // Convention: getFieldName() — returns field's type.
        // (Access modifiers and encapsulation covered in Part 2.)

        String getFirstName()   { return firstName; }
        String getLastName()    { return lastName; }
        int    getAge()         { return age; }
        String getEmail()       { return email; }
        Address getHomeAddress(){ return homeAddress; }

        // =========================================================
        // SETTER METHODS
        // =========================================================
        // Setters provide controlled WRITE access.
        // Convention: setFieldName(value) — void, accepts new value.
        // Setters can include validation logic.

        void setEmail(String email) {
            // Validate before setting
            if (email != null && email.contains("@")) {
                this.email = email;
            } else {
                System.out.println("Invalid email format: " + email);
            }
        }

        void setAge(int age) {
            if (age >= 0 && age <= 150) {
                this.age = age;
            } else {
                System.out.println("Invalid age: " + age);
            }
        }

        // =========================================================
        // INSTANCE METHODS — void (no return value)
        // =========================================================

        void introduce() {
            System.out.printf("Hi, I'm %s %s, %d years old from %s.%n",
                    firstName, lastName, age, homeAddress.city);
        }

        void celebrateBirthday() {
            age++;
            System.out.println("Happy Birthday, " + firstName + "! You are now " + age);
        }

        void relocate(Address newAddress) {
            System.out.println(firstName + " is moving from " + homeAddress.city
                    + " to " + newAddress.city);
            this.homeAddress = newAddress;
        }

        // =========================================================
        // INSTANCE METHODS — with return values
        // =========================================================

        String getFullName() {
            return firstName + " " + lastName;    // computes from two fields
        }

        boolean isAdult() {
            return age >= 18;
        }

        String getAgeGroup() {
            if (age < 13)       return "Child";
            else if (age < 18)  return "Teenager";
            else if (age < 65)  return "Adult";
            else                return "Senior";
        }

        // =========================================================
        // METHOD OVERLOADING
        // =========================================================
        // Same method name, different parameter lists.
        // Java picks the right one based on arguments passed.

        // Overload 1: no parameters — uses defaults
        void sendNotification() {
            System.out.println("Sending notification to " + email + " with default subject.");
        }

        // Overload 2: custom subject
        void sendNotification(String subject) {
            System.out.println("Sending notification to " + email + ": [" + subject + "]");
        }

        // Overload 3: custom subject + message body
        void sendNotification(String subject, String body) {
            System.out.printf("Sending notification to %s: [%s] — %s%n", email, subject, body);
        }

        // =========================================================
        // METHOD CALLING ANOTHER METHOD (within the same class)
        // =========================================================

        String getSummary() {
            // This method calls other methods on the same object
            return String.format("[%s | Age: %d (%s) | Email: %s | Address: %s]",
                    getFullName(),      // calls our own method
                    age,
                    getAgeGroup(),      // calls our own method
                    email,
                    homeAddress.getFormatted());  // calls method on a field's object
        }

        @Override
        public String toString() {
            return getSummary();
        }
    }

    // =========================================================
    // CLASS: Rectangle — demonstrates computed/derived methods
    // =========================================================

    static class Rectangle {

        // Fields — the minimal information needed to describe a rectangle
        double width;
        double height;

        Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }

        // ----- Getters -----
        double getWidth()  { return width; }
        double getHeight() { return height; }

        // ----- Setters with validation -----
        void setWidth(double width) {
            if (width > 0) this.width = width;
            else System.out.println("Width must be positive.");
        }

        void setHeight(double height) {
            if (height > 0) this.height = height;
            else System.out.println("Height must be positive.");
        }

        // ----- COMPUTED / DERIVED methods -----
        // These return values calculated FROM the fields — not stored separately
        double getArea()      { return width * height; }
        double getPerimeter() { return 2 * (width + height); }
        double getDiagonal()  { return Math.sqrt(width * width + height * height); }
        boolean isSquare()    { return width == height; }

        // Overloaded scale method
        void scale(double factor) {
            width  *= factor;
            height *= factor;
        }

        void scale(double widthFactor, double heightFactor) {
            width  *= widthFactor;
            height *= heightFactor;
        }

        @Override
        public String toString() {
            return String.format("Rectangle{w=%.1f, h=%.1f, area=%.2f, perimeter=%.2f, square=%b}",
                    width, height, getArea(), getPerimeter(), isSquare());
        }
    }

    // =========================================================
    // MAIN METHOD
    // =========================================================

    public static void main(String[] args) {

        // =========================================================
        // SECTION 1: Instance fields — each object has its own
        // =========================================================
        System.out.println("=== Instance Fields ===");

        Address addr1 = new Address("123 Main St", "Austin", "TX", "78701");
        Address addr2 = new Address("456 Oak Ave", "Seattle", "WA", "98101");

        Person person1 = new Person("Alice", "Johnson", 28, "alice@example.com", addr1);
        Person person2 = new Person("Bob", "Martinez", 17, "bob@example.com", addr2);

        // Each person has completely independent field values
        System.out.println(person1);
        System.out.println(person2);

        // =========================================================
        // SECTION 2: Getters and setters
        // =========================================================
        System.out.println("\n=== Getters ===");
        System.out.println("Full name:  " + person1.getFullName());
        System.out.println("Age group:  " + person1.getAgeGroup());
        System.out.println("Is adult:   " + person1.isAdult());
        System.out.println("Address:    " + person1.getHomeAddress().getFormatted());

        System.out.println("\n=== Setters with validation ===");
        person1.setEmail("alice.johnson@company.com");  // valid
        person1.setEmail("not-an-email");               // invalid — prints error
        person1.setAge(200);                             // invalid — prints error
        System.out.println("Updated email: " + person1.getEmail());

        // =========================================================
        // SECTION 3: Void methods (side effects)
        // =========================================================
        System.out.println("\n=== Void Methods ===");
        person1.introduce();
        person2.celebrateBirthday();  // increments age internally
        System.out.println("Bob's age after birthday: " + person2.getAge());

        person1.relocate(new Address("789 Pine Rd", "Denver", "CO", "80201"));
        System.out.println("Alice's new address: " + person1.getHomeAddress());

        // =========================================================
        // SECTION 4: Method overloading
        // =========================================================
        System.out.println("\n=== Method Overloading ===");
        person1.sendNotification();
        person1.sendNotification("Welcome to the platform!");
        person1.sendNotification("Order Shipped", "Your order #12345 is on the way.");

        // =========================================================
        // SECTION 5: Methods calling other methods
        // =========================================================
        System.out.println("\n=== Methods Calling Other Methods ===");
        System.out.println(person1.getSummary());

        // =========================================================
        // SECTION 6: Computed / derived methods (Rectangle)
        // =========================================================
        System.out.println("\n=== Computed Methods (Rectangle) ===");
        Rectangle r1 = new Rectangle(5.0, 3.0);
        System.out.println(r1);
        System.out.printf("Area: %.2f | Perimeter: %.2f | Diagonal: %.2f%n",
                r1.getArea(), r1.getPerimeter(), r1.getDiagonal());

        // Overloaded scale
        r1.scale(2.0);           // uniform
        System.out.println("After uniform scale(2): " + r1);

        Rectangle r2 = new Rectangle(4.0, 4.0);
        System.out.println("\n" + r2);
        System.out.println("Is square: " + r2.isSquare());

        r2.scale(1.5, 2.0);      // non-uniform
        System.out.println("After scale(1.5, 2.0): " + r2);
        System.out.println("Is square after non-uniform scale: " + r2.isSquare());

        // =========================================================
        // SECTION 7: Fields vs local variables — scope comparison
        // =========================================================
        System.out.println("\n=== Fields vs Local Variables ===");
        // person1.firstName — instance field: lives on the heap, tied to the object,
        //                     accessible across ALL methods of the class.
        // Local vars (like 'addr1', 'addr2' in this main method) — stack-allocated,
        //             exist only while this method is executing.
        System.out.println("person1.firstName is an instance field: " + person1.firstName);
        System.out.println("(local 'addr1' only exists in this stack frame)");
    }
}
