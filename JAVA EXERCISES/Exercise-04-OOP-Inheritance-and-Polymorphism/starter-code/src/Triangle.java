/**
 * Exercise 04 — OOP Part 2  (STARTER)
 * Triangle shape — three sides a, b, c.
 */
public class Triangle extends Shape {

    private double a, b, c;

    public Triangle(double a, double b, double c, String color) {
        super(color);
        this.a = a; this.b = b; this.c = c;
    }

    // TODO 5a: area() — use Heron's formula:
    //   s = (a + b + c) / 2
    //   area = sqrt(s * (s-a) * (s-b) * (s-c))
    @Override
    public double area() { return 0.0; }

    // TODO 5b: perimeter() → a + b + c
    @Override
    public double perimeter() { return 0.0; }

    // TODO 6: draw() — print "  /\  a=3.0 b=4.0 c=5.0"
    @Override
    public void draw() { }

    // TODO 7: toString() — "Triangle[3.0, 4.0, 5.0, color=Green]"
    @Override
    public String toString() { return "Triangle(not implemented)"; }
}
