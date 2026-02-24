class Circle {

    public static final double PI = 3.14159;    // class-level constant — UPPER_SNAKE_CASE

    private final double radius;                // final — assigned once, never changed
    private       String color;                 // non-final — mutable via setColor()

    // Main constructor — 'this.' needed because parameter names match field names
    public Circle(double radius, String color) {
        this.radius = radius;
        this.color  = color;
    }

    // Copy constructor — delegates to main constructor; no code duplication
    public Circle(Circle other) {
        this(other.radius, other.color);
    }

    public double area()           { return PI * radius * radius; }
    public double circumference()  { return 2 * PI * radius; }

    public void   setColor(String color) { this.color = color; }   // 'this.' disambiguates
    public String getColor()             { return color; }
    public double getRadius()            { return radius; }

    @Override
    public String toString() {
        return "Circle{radius=" + radius + ", color='" + color + "', area="
               + String.format("%.2f", area()) + "}";
    }
}

public class CircleDemo {

    public static void main(String[] args) {

        System.out.println("PI constant: " + Circle.PI);   // accessed via class name

        Circle c1 = new Circle(5.0, "red");
        System.out.println(c1);

        Circle c2 = new Circle(c1);                         // copy constructor
        System.out.println(c2);                             // same values as c1

        c2.setColor("blue");                                // mutate only c2
        System.out.println(c1);                             // c1 still red — independent objects
        System.out.println(c2);                             // c2 now blue

        final double scale = 2.0;                           // final local variable — cannot reassign
        System.out.println("Scaled radius: " + (c1.getRadius() * scale));

        // c1.radius = 10.0;
        // ^^^ COMPILE ERROR: 'radius' is declared final — it can only be assigned in the constructor.
        // The compiler enforces this at compile time, not at runtime.
    }
}
