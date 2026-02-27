/**
 * DAY 5 — OOP Part 2 | Part 1, File 2
 * TOPIC: Method Overriding vs Method Overloading
 *
 * Topics covered:
 *  - Method overriding: same signature in subclass replaces parent behavior
 *  - Rules for overriding: same name, same params, same (or covariant) return type,
 *    equal/wider access modifier, cannot narrow checked exceptions
 *  - @Override annotation: compiler safety net
 *  - Method overloading: same name, different parameter list (resolved at compile time)
 *  - Side-by-side comparison: overriding vs overloading
 *  - Covariant return types
 *  - final methods cannot be overridden (cross-reference from Day 4)
 *
 * Key vocabulary:
 *  - override   : subclass replaces an inherited method at runtime
 *  - overload   : same class (or subclass) defines multiple methods with the same name
 *  - signature  : method name + parameter list (return type is NOT part of the signature)
 *  - covariant  : a return type that is a subtype of the parent's return type
 */
public class MethodOverridingVsOverloading {

    // ==========================================================
    // SECTION A: Method Overriding — base hierarchy
    // ==========================================================

    static class Shape {
        protected String color;

        public Shape(String color) {
            this.color = color;
        }

        // This method WILL be overridden by every subclass
        public double area() {
            return 0.0;   // base: no meaningful area without dimensions
        }

        public double perimeter() {
            return 0.0;
        }

        public void draw() {
            System.out.println("  Drawing a " + color + " shape.");
        }

        public String describe() {
            return String.format("%s Shape | area=%.2f | perimeter=%.2f", color, area(), perimeter());
        }

        @Override
        public String toString() {
            return "Shape{color='" + color + "'}";
        }
    }

    static class Circle extends Shape {
        private double radius;

        public Circle(String color, double radius) {
            super(color);
            this.radius = radius;
        }

        // ── OVERRIDING area() ─────────────────────────────────────
        // Same name ✅  Same params (none) ✅  Returns double ✅
        // Access is public (same as parent) ✅
        @Override
        public double area() {
            return Math.PI * radius * radius;
        }

        @Override
        public double perimeter() {
            return 2 * Math.PI * radius;
        }

        // Override draw() to add circle-specific output
        @Override
        public void draw() {
            System.out.printf("  Drawing a %s circle with radius %.1f ○%n", color, radius);
        }

        @Override
        public String toString() {
            return String.format("Circle{color='%s', radius=%.1f}", color, radius);
        }
    }

    static class Rectangle extends Shape {
        protected double width;
        protected double height;

        public Rectangle(String color, double width, double height) {
            super(color);
            this.width  = width;
            this.height = height;
        }

        @Override
        public double area() {
            return width * height;
        }

        @Override
        public double perimeter() {
            return 2 * (width + height);
        }

        @Override
        public void draw() {
            System.out.printf("  Drawing a %s rectangle %.1f × %.1f □%n", color, width, height);
        }

        @Override
        public String toString() {
            return String.format("Rectangle{color='%s', %.1f×%.1f}", color, width, height);
        }
    }

    static class Square extends Rectangle {
        // Square IS-A Rectangle — it just constrains width == height

        public Square(String color, double side) {
            super(color, side, side);  // width and height are the same
        }

        // COVARIANT RETURN TYPE example: describe() returns same String — standard override

        // We do NOT need to override area()/perimeter() — Rectangle's versions work correctly
        // (because we set width == height in the constructor).

        // But we CAN override draw() for a better message:
        @Override
        public void draw() {
            System.out.printf("  Drawing a %s square with side %.1f ■%n", color, width);
        }

        @Override
        public String toString() {
            return String.format("Square{color='%s', side=%.1f}", color, width);
        }
    }

    // ==========================================================
    // SECTION B: @Override annotation importance
    // ==========================================================

    static class Animal {
        public String speak() { return "..."; }
    }

    static class Dog extends Animal {

        // ✅ Correct override
        @Override
        public String speak() { return "Woof!"; }

        // ❌ WRONG — this does NOT override; it creates a new overload!
        // Without @Override you'd never know until runtime behavior confused you.
        //
        // public String Speak() { return "Woof!"; }    // capital S — typo!
        //
        // With @Override, Java gives a compile error:
        //   "method does not override or implement a method from a supertype"
        // That's the value of @Override: it's a safety net.
    }

    // ==========================================================
    // SECTION C: Rules for valid overriding
    // ==========================================================

    static class Printer {
        // Rule: subclass cannot NARROW visibility
        // parent has 'public' → child must be 'public' or 'protected' but NOT 'private'
        public void print(String document) {
            System.out.println("  [Printer] Printing: " + document);
        }

        // protected method — child can keep protected OR widen to public
        protected String getStatus() { return "Ready"; }
    }

    static class LaserPrinter extends Printer {

        @Override
        public void print(String document) {
            // ✅ Access is still public — allowed
            System.out.println("  [LaserPrinter] High-res laser print: " + document);
        }

        @Override
        public String getStatus() {
            // ✅ WIDENED from protected → public — allowed
            return "Laser Ready";
        }

        // ❌ The following would be a compile error:
        // @Override
        // private void print(String document) { }
        // Error: attempting to assign weaker access privileges; was public
    }

    // ==========================================================
    // SECTION D: Method Overloading
    // ==========================================================
    // Overloading = same method name, DIFFERENT parameter list.
    // Resolved entirely at COMPILE TIME based on argument types/count.

    static class Calculator {

        // Three overloads of add() — all in the SAME class

        // 1) Two ints
        public int add(int a, int b) {
            System.out.printf("  add(int, int): %d + %d = %d%n", a, b, a + b);
            return a + b;
        }

        // 2) Three ints
        public int add(int a, int b, int c) {
            System.out.printf("  add(int,int,int): %d + %d + %d = %d%n", a, b, c, a + b + c);
            return a + b + c;
        }

        // 3) Two doubles
        public double add(double a, double b) {
            System.out.printf("  add(double,double): %.2f + %.2f = %.2f%n", a, b, a + b);
            return a + b;
        }

        // 4) String concatenation — completely different behavior, same method name
        public String add(String a, String b) {
            String result = a + b;
            System.out.printf("  add(String,String): \"%s\" + \"%s\" = \"%s\"%n", a, b, result);
            return result;
        }

        // NOTE: You CANNOT overload by return type alone — the signature is name + params.
        // These two would be a compile error (duplicate method):
        //   public int  getValue() { return 0; }
        //   public long getValue() { return 0L; }   // same params → NOT an overload → error
    }

    // ==========================================================
    // SECTION E: Overriding vs Overloading — side by side
    // ==========================================================
    // A subclass can BOTH override AND overload inherited methods.

    static class Notification {
        public void send(String message) {
            System.out.println("  [Notification] Sending: " + message);
        }
    }

    static class EmailNotification extends Notification {

        // ── OVERRIDE ─────────────────────────────────────────────
        // Same signature as parent → replaces the parent's behavior
        @Override
        public void send(String message) {
            System.out.println("  [Email] Sending email: " + message);
        }

        // ── OVERLOADS ─────────────────────────────────────────────
        // Different parameter lists → NEW methods in this class
        public void send(String message, String recipient) {
            System.out.printf("  [Email] Sending \"%s\" to %s%n", message, recipient);
        }

        public void send(String message, String recipient, boolean highPriority) {
            String prefix = highPriority ? "🔴 URGENT: " : "";
            System.out.printf("  [Email] Sending %s\"%s\" to %s%n", prefix, message, recipient);
        }
    }

    // ==========================================================
    // MAIN — DEMONSTRATIONS
    // ==========================================================
    public static void main(String[] args) {

        System.out.println("============================================================");
        System.out.println("SECTION 1 — Method Overriding with @Override");
        System.out.println("============================================================");

        Circle  c = new Circle("Red",   5.0);
        Rectangle r = new Rectangle("Blue", 4.0, 6.0);
        Square  s = new Square("Green", 3.0);

        System.out.println(c.describe());   // calls overridden area() and perimeter()
        System.out.println(r.describe());
        System.out.println(s.describe());
        System.out.println();

        c.draw();
        r.draw();
        s.draw();
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 2 — @Override annotation catches typos");
        System.out.println("============================================================");

        Dog dog = new Dog();
        System.out.println("dog.speak(): " + dog.speak());
        System.out.println("Without @Override, a typo like 'Speak()' creates a NEW method");
        System.out.println("silently instead of overriding — a runtime behavior bug.");
        System.out.println("With @Override, Java rejects it at compile time.");
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 3 — Overriding rules: access, return type");
        System.out.println("============================================================");

        LaserPrinter lp = new LaserPrinter();
        lp.print("Q4 Report.pdf");            // uses LaserPrinter.print(), not Printer.print()
        System.out.println("Status: " + lp.getStatus());   // widened to public — works
        System.out.println();

        // ── Rules summary (in comments — these would be compile errors) ──────────
        // 1. Same name + same parameter list required
        // 2. Return type must be same OR a subtype (covariant)
        // 3. Access modifier: same or WIDER (public > protected > default > private)
        // 4. Cannot throw NEW or BROADER checked exceptions
        // 5. final methods cannot be overridden
        // 6. static methods cannot be overridden (they are hidden, not overridden)
        System.out.println("Rules for valid overriding:");
        System.out.println("  ✅ Same method name");
        System.out.println("  ✅ Same parameter list");
        System.out.println("  ✅ Same or covariant (subtype) return type");
        System.out.println("  ✅ Same or wider access modifier");
        System.out.println("  ❌ Cannot override final methods");
        System.out.println("  ❌ Cannot narrow access (public → private is illegal)");
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 4 — Method Overloading (resolved at compile time)");
        System.out.println("============================================================");

        Calculator calc = new Calculator();
        calc.add(3, 4);                   // → add(int, int)
        calc.add(1, 2, 3);               // → add(int, int, int)
        calc.add(3.14, 2.71);            // → add(double, double)
        calc.add("Hello, ", "World!");   // → add(String, String)
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 5 — Override AND Overload in same subclass");
        System.out.println("============================================================");

        EmailNotification email = new EmailNotification();

        // Overridden version (replaces parent)
        email.send("Server is down!");

        // Overloaded versions (new methods in EmailNotification)
        email.send("Welcome to the platform", "alice@example.com");
        email.send("Security alert", "bob@example.com", true);
        System.out.println();

        System.out.println("============================================================");
        System.out.println("OVERRIDING vs OVERLOADING — COMPARISON TABLE");
        System.out.println("============================================================");
        System.out.println("  Feature          │ Overriding               │ Overloading");
        System.out.println("  ─────────────────┼──────────────────────────┼─────────────────────────");
        System.out.println("  Where?           │ Subclass                 │ Same class (or subclass)");
        System.out.println("  Signature        │ SAME                     │ DIFFERENT parameters");
        System.out.println("  Resolved         │ Runtime (dynamic)        │ Compile time (static)");
        System.out.println("  @Override needed?│ Best practice: YES       │ N/A");
        System.out.println("  Return type      │ Same or covariant        │ Can be anything");
        System.out.println("  Purpose          │ Specialise inherited beh │ Multiple input variants");
    }
}
